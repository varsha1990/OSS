/*
 * The contents of this file are subject to the terms 
 * of the Common Development and Distribution License 
 * (the License).  You may not use this file except in
 * compliance with the License.
 * 
 * You can obtain a copy of the license at 
 * https://glassfish.dev.java.net/public/CDDLv1.0.html or
 * glassfish/bootstrap/legal/CDDLv1.0.txt.
 * See the License for the specific language governing 
 * permissions and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL 
 * Header Notice in each file and include the License file 
 * at glassfish/bootstrap/legal/CDDLv1.0.txt.  
 * If applicable, add the following below the CDDL Header, 
 * with the fields enclosed by brackets [] replaced by
 * you own identifying information: 
 * "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
 */
package org.glassfish.deployment.autodeploy;

import com.sun.enterprise.util.io.FileUtils;
import com.sun.enterprise.config.serverbeans.Applications;
import com.sun.enterprise.config.serverbeans.Application;
import org.glassfish.deployment.admin.UndeployCommand;
import java.io.File;
import java.util.Properties;
import java.util.logging.Level;
import java.util.List;
import org.glassfish.api.admin.AdminCommand;
import org.glassfish.deployment.autodeploy.AutoDeployer.AutodeploymentStatus;
import org.glassfish.deployment.common.DeploymentProperties;
import org.jvnet.hk2.annotations.Inject;
import org.jvnet.hk2.annotations.Scoped;
import org.jvnet.hk2.annotations.Service;
import org.jvnet.hk2.component.Habitat;
import org.jvnet.hk2.component.PerLookup;

/**
 * Performs a single auto-undeploy operation for a single file.
 * <p>
 * Note - Use the newInstance static method to obtain a fully-injected operation;
 * it is safer and more convenient than using the no-arg constructor and then 
 * invoking init yourself.
 * 
 * @author tjquinn
 */
@Service
@Scoped(PerLookup.class)
public class AutoUndeploymentOperation extends AutoOperation {

    @Inject
    Applications apps;

    /**
     * Creates a new, injected, and initialized AutoUndeploymentOperation object.
     * 
     * @param habitat
     * @param appFile
     * @param name
     * @param target
     * @return the AutoUndeploymentOperation object
     */
    static AutoUndeploymentOperation newInstance(
            Habitat habitat,
            File appFile, 
            String name,
            String target) {
        AutoUndeploymentOperation o = 
                (AutoUndeploymentOperation) habitat.getComponent(AutoUndeploymentOperation.class);
        
        o.init(appFile, name, target);
        return o;
    }

    private String name;
    
    private static final String COMMAND_NAME = "undeploy";
    
    @Inject
    private AutodeployRetryManager retryManager;

    @Inject(name=COMMAND_NAME)
    private AdminCommand undeployCommand;
    
    /**
     * Completes the intialization of the object.
     * @param appFile
     * @param name
     * @param target
     * @return the AutoUndeployOperation for convenience
     */
    protected AutoUndeploymentOperation init(
            File appFile, 
            String name,
            String target) {
        super.init(
                appFile, 
                prepareUndeployActionProperties(name, target), 
                COMMAND_NAME, 
                undeployCommand);
        this.name = name;
        return this;
    }
    
    private Properties prepareUndeployActionProperties(String archiveName, String target) {
        DeploymentProperties dProps = new DeploymentProperties();

        // we need to find the application registration name
        // which is not always the same as archive name
        String appName = archiveName;
        List<Application> applications = apps.getApplications();
        for (Application app : applications) {
            if (app.getDeployProperties().getProperty
                (DeploymentProperties.DEFAULT_APP_NAME).equals(archiveName)) {
                appName = app.getName();
            }
        }

        dProps.setName(appName);
//        dProps.setResourceAction(DeploymentProperties.RES_UNDEPLOYMENT);
//        dProps.setResourceTargetList(target);
        return (Properties)dProps;
    }

    
    /**
     * {@inheritDoc}
     */
    protected String getMessageString(AutodeploymentStatus ds, File file) {
        return localStrings.getLocalString(
                ds.undeploymentMessageKey, 
                ds.undeploymentDefaultMessage, 
                file);
    }

    /**
     * {@inheritDoc}
     */
    protected void markFiles(AutodeploymentStatus ds, File file) {
        /*
         * Before managing the marker file for the app, see if 
         * the autodeployer is responsible for deleting this app
         * file and, if so, delete it.
         * 
         * Normally users will delete the application file themselves.  Especially
         * in the case of directories, though, users may create the file
         * ${fileName}_undeployRequested and have the autodeployer delete the
         * file.  
         * <p>
         * This avoids problems if the user-initiated deletion of a large
         * file or directory takes longer than the autodeployer cycle time.  If
         * a file has been removed from the top-level directory, the autodeployer
         * will see the updated timestamp on the directory and can only decide
         * that this is a new file - at least a newer file - to be autodeployed.
         * <p>
         * By allowing the auto-deployer to manage the deletion of the file the 
         * user can avoid this whole scenario and, thereby, avoid accidental
         * attempts to deploy an application that the user wants gone.
         * 
         */
        if (undeployedByRequestFile(file)) {
            cleanupAppAndRequest(file);
        }

        if (ds.status) {
            markUndeployed(file);
            retryManager.recordSuccessfulUndeployment(file);
        } else {
            markUndeployFailed(file);
            retryManager.recordFailedUndeployment(file);
        }
    }
    
    private void markUndeployed(File f) {
        try {
            deleteAllMarks(f);
            getUndeployedFile(f).createNewFile();
        } catch (Exception e) { 
            //ignore 
        }
    }
    
    private void markUndeployFailed(File f) {
        try {
            deleteAllMarks(f);
            getUndeployFailedFile(f).createNewFile();
        } catch (Exception e) { 
            //ignore 
        }
    }
    
    private boolean undeployedByRequestFile(File f) {
        return f instanceof AutoDeployedFilesManager.UndeployRequestedFile;
    }
    
    private void cleanupAppAndRequest(File f) {
        boolean logFine = sLogger.isLoggable(Level.FINE);

        /*
         * Clean up the application file or directory.
         */
        if (f.isDirectory()) {
            if (logFine) {
                sLogger.fine("Deleting autodeployed directory " + f.getAbsolutePath() + " by request");
            }
            FileUtils.liquidate(f);
        } else {
            if (logFine) {
                sLogger.fine("Deleting autodeployed file " + f.getAbsolutePath() + " by request");
            }
            FileUtils.deleteFile(f);
        }
        
        /*
         * Remove the undeploy request file.
         */
        File requestFile = AutoDeployedFilesManager.appToUndeployRequestFile(f);
        if (logFine) {
            sLogger.fine("Deleting autodeploy request file " + requestFile.getAbsolutePath());
        }
        FileUtils.deleteFile(requestFile);
    }
    
    
}
