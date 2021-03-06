/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 * 
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 * or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 * 
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 * 
 * Contributor(s):
 * 
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package com.sun.enterprise.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;
import java.util.logging.*;
import javax.naming.*;
import org.apache.catalina.Authenticator;
import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.Pipeline;
import org.apache.catalina.Realm;
import org.apache.catalina.core.ContainerBase;
import org.apache.catalina.deploy.ApplicationParameter;
import org.apache.catalina.deploy.ContextEnvironment;
import org.apache.catalina.deploy.ContextResource;
import org.apache.catalina.deploy.LoginConfig;
import org.apache.catalina.deploy.SecurityConstraint;
import org.apache.catalina.startup.ContextConfig;
import org.apache.catalina.core.StandardEngine;

import org.jvnet.hk2.component.Habitat;
import com.sun.enterprise.container.common.spi.util.ComponentEnvManager;
import org.glassfish.web.valve.GlassFishValve;

import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;

import com.sun.enterprise.deployment.EnvironmentProperty;
import com.sun.enterprise.deployment.ResourcePrincipal;
import com.sun.enterprise.deployment.ResourceReferenceDescriptor;
import com.sun.enterprise.deployment.WebBundleDescriptor;
import com.sun.enterprise.deployment.EjbBundleDescriptor;
import com.sun.enterprise.deployment.runtime.common.DefaultResourcePrincipal;
import com.sun.enterprise.deployment.runtime.common.ResourceRef;
import com.sun.enterprise.deployment.runtime.web.SunWebApp;
import com.sun.enterprise.deployment.web.ContextParameter;
import com.sun.logging.LogDomains;

/**
 * Startup event listener for a <b>Context</b> that configures the properties
 * of that Context, and the associated defined servlets.
 *
 * @author Jean-Francois Arcand
 */

public class WebModuleContextConfig extends ContextConfig {

    private static final Logger logger = LogDomains.getLogger(
        WebModuleContextConfig.class, LogDomains.WEB_LOGGER);
    
    protected static final ResourceBundle rb = logger.getResourceBundle();

    public final static int CHILDREN = 0;
    public final static int SERVLET_MAPPINGS = 1;
    public final static int LOCAL_EJBS = 2;
    public final static int EJBS = 3;
    public final static int ENVIRONMENTS = 4;
    public final static int ERROR_PAGES = 5;
    public final static int FILTER_DEFS = 6;
    public final static int FILTER_MAPS = 7;
    public final static int APPLICATION_LISTENERS = 8;
    public final static int RESOURCES = 9;
    public final static int APPLICATION_PARAMETERS = 10;
    public final static int MESSAGE_DESTINATIONS = 11;
    public final static int MESSAGE_DESTINATION_REFS = 12;
    public final static int MIME_MAPPINGS = 13;
    
    protected Habitat habitat;
    
    /**
     * The <code>File</code> reffering to the default-web.xml 
     */
    protected File file; 
        
    
    /**
     * The DOL object representing the web.xml content.
    */
    private WebBundleDescriptor webBundleDescriptor;


    /**
     * Resource references from outside the .war
     */
    private Collection<ResourceReferenceDescriptor> resRefs =
                new HashSet<ResourceReferenceDescriptor>();

    /**
     * Environment properties from outside the .war
     */
    private Collection<EnvironmentProperty> envProps =
            new HashSet<EnvironmentProperty>();

    
    /**
     * Customized <code>ContextConfig</code> which use the DOL for deployment.
     */
    public WebModuleContextConfig(){
    }
    
    
    /**
     * Set the DOL object associated with this class.
     */
    public void setDescriptor(WebBundleDescriptor wbd){
        webBundleDescriptor = wbd;
    }
   
    /**
     * Set the DOL object associated with this class.
     */
    public void setHabitat(Habitat habitat){
        this.habitat = habitat;
    }
    
    
    /**
     * Process the START event for an associated Context.
     *
     * @param event The lifecycle event that has occurred
     */
    public void lifecycleEvent(LifecycleEvent event)
            throws LifecycleException {
        
        // Identify the context we are associated with
        context = (Context) event.getLifecycle();

        // Called from ContainerBase.addChild() -> StandardContext.start()
        // Process the event that has occurred
        if (event.getType().equals(Lifecycle.START_EVENT)) {
            configureResource();
            start();
        } else if (event.getType().equals(Lifecycle.STOP_EVENT)) {
            stop();
        } else if (event.getType().equals(Lifecycle.INIT_EVENT)) {
            super.init();
        }
    }
    
    
    protected synchronized void configureResource()
            throws LifecycleException {
        
        List<ApplicationParameter> appParams = 
            context.findApplicationParameters();
        ContextParameter contextParam;
        synchronized (appParams) {
            Iterator<ApplicationParameter> i = appParams.iterator(); 
            while (i.hasNext()) {
                ApplicationParameter appParam = i.next();
                contextParam = new EnvironmentProperty(
                    appParam.getName(), appParam.getValue(),
                    appParam.getDescription());
                webBundleDescriptor.addContextParameter(contextParam);
            }
        }

        ContextEnvironment[] envs = context.findEnvironments();
        EnvironmentProperty envEntry;

        for (int i=0; i<envs.length; i++) {
            envEntry = new EnvironmentProperty(
                    envs[i].getName(), envs[i].getValue(),
                    envs[i].getDescription(), envs[i].getType()); 
            if (envs[i].getValue()!=null) {
                envEntry.setValue(envs[i].getValue());
            }
            webBundleDescriptor.addEnvironmentProperty(envEntry);
            envProps.add(envEntry);
        }

        ContextResource[] resources = context.findResources();
        ResourceReferenceDescriptor resourceReference;
        SunWebApp iasBean = webBundleDescriptor.getSunDescriptor();
        ResourceRef[] rr = iasBean.getResourceRef();
        DefaultResourcePrincipal drp;
        ResourcePrincipal rp;


        for (int i=0; i<resources.length; i++) {
            resourceReference = new ResourceReferenceDescriptor(
                    resources[i].getName(), resources[i].getDescription(),
                    resources[i].getType());
            resourceReference.setJndiName(resources[i].getName());
            if (rr!=null) {
                for (int j=0; j<rr.length; j++) {
                    if (resources[i].getName().equals(rr[j].getResRefName())) {
                        resourceReference.setJndiName(rr[i].getJndiName());
                        drp = rr[i].getDefaultResourcePrincipal();
                        if (drp!=null) {
                            rp = new ResourcePrincipal(drp.getName(), drp.getPassword());
                            resourceReference.setResourcePrincipal(rp);
                        }
                    }
                }
            }
            resourceReference.setAuthorization(resources[i].getAuth());
            webBundleDescriptor
                    .addResourceReferenceDescriptor(resourceReference);
            resRefs.add(resourceReference);
        }    
    }


    /**
     * Process a "start" event for this Context - in background
     */
    protected synchronized void start() throws LifecycleException {
        context.setConfigured(false);

        ComponentEnvManager namingMgr = habitat.getComponent(
            com.sun.enterprise.container.common.spi.util.ComponentEnvManager.class);
        if (namingMgr != null) {
            try {
                boolean webBundleContainsEjbs =
                    (webBundleDescriptor.getExtensionsDescriptors(EjbBundleDescriptor.class).size() > 0);

                // If .war contains EJBs, .war-defined dependencies have already been bound by
                // EjbDeployer, so just add the dependencies from outside the .war
                if( webBundleContainsEjbs ) {
                    namingMgr.addToComponentNamespace(webBundleDescriptor, envProps, resRefs);
                } else {
                    namingMgr.bindToComponentNamespace(webBundleDescriptor);
                }

                String componentId = namingMgr.getComponentEnvId(webBundleDescriptor);
                ((WebModule) context).setComponentId(componentId);
            } catch (NamingException ne) {
                throw new LifecycleException(ne);
            }
        }
        
        TomcatDeploymentConfig.configureWebModule(
            (WebModule)context, webBundleDescriptor);
        authenticatorConfig();
        managerConfig();

        context.setConfigured(true);
    }
    
    
    /**
     * Always sets up an Authenticator regardless of any security constraints.
     */
    protected synchronized void authenticatorConfig()
            throws LifecycleException {
        
        LoginConfig loginConfig = context.getLoginConfig();
        if (loginConfig == null) {
            loginConfig = new LoginConfig("NONE", null, null, null);
            context.setLoginConfig(loginConfig);
        }

        // Has an authenticator been configured already?
        if (context instanceof Authenticator)
            return;
        if (context instanceof ContainerBase) {
            Pipeline pipeline = ((ContainerBase) context).getPipeline();
            if (pipeline != null) {
                GlassFishValve basic = pipeline.getBasic();
                if ((basic != null) && (basic instanceof Authenticator))
                    return;
                GlassFishValve valves[] = pipeline.getValves();
                for (int i = 0; i < valves.length; i++) {
                    if (valves[i] instanceof Authenticator)
                        return;
                }
            }
        } else {
            return;     // Cannot install a Valve even if it would be needed
        }

        // Has a Realm been configured for us to authenticate against?
        /* START IASRI 4856062
        if (context.getRealm() == null) {
        */
        // BEGIN IASRI 4856062
        Realm rlm = context.getRealm();
        if (rlm == null) {
        // END IASRI 4856062
            String realmName = (context.getLoginConfig() != null) ?
                context.getLoginConfig().getRealmName() : null;
            if (realmName != null && !realmName.isEmpty()) {
                String msg = rb.getString(
                    "webModuleContextConfig.missingRealm");
                throw new LifecycleException(
                    MessageFormat.format(msg, realmName));
            }
            return;
        }

        // BEGIN IASRI 4856062
        // If a realm is available set its name in the Realm(Adapter)
        rlm.setRealmName(loginConfig.getRealmName(),
                         loginConfig.getAuthMethod());

        // END IASRI 4856062

        /*
         * First check to see if there is a custom mapping for the login
         * method. If so, use it. Otherwise, check if there is a mapping in
         * org/apache/catalina/startup/Authenticators.properties.
         */
        GlassFishValve authenticator = null;
        if (customAuthenticators != null) {
            authenticator = (GlassFishValve)
                customAuthenticators.get(loginConfig.getAuthMethod());
        }

        if (authenticator == null) {
            // Identify the class name of the Valve we should configure
            String authenticatorName = null;

            // BEGIN RIMOD 4808402
            // If login-config is given but auth-method is null, use NONE
            // so that NonLoginAuthenticator is picked
            String authMethod = loginConfig.getAuthMethod();
            if (authMethod == null) {
                authMethod = "NONE";
            }
            authenticatorName = authenticators.getProperty(authMethod);
            // END RIMOD 4808402
            /* RIMOD 4808402
            authenticatorName =
                    authenticators.getProperty(loginConfig.getAuthMethod());
            */

            if (authenticatorName == null) {
                String msg = rb.getString(
                    "webModuleContextConfig.authenticatorMissing");
                throw new LifecycleException(MessageFormat.format(msg,
                    loginConfig.getAuthMethod()));
            }

            // Instantiate and install an Authenticator of the requested class
            try {
                Class authenticatorClass = Class.forName(authenticatorName);
                authenticator = (GlassFishValve)
                    authenticatorClass.newInstance();
            } catch (Exception e) {
                String msg = rb.getString(
                    "webModuleContextConfig.authenticatorInstantiate");
                throw new LifecycleException(
                    MessageFormat.format(msg, authenticatorName),
                    e);
            }
        }

        if (authenticator != null && context instanceof ContainerBase) {
            Pipeline pipeline = ((ContainerBase) context).getPipeline();
            if (pipeline != null) {
                ((ContainerBase) context).addValve(authenticator);
                if (logger.isLoggable(Level.FINEST)) {
                    logger.log(Level.FINEST,
                        "webModuleContextConfig.authenticatorConfigured",
                        loginConfig.getAuthMethod());
                }
            }
        }
    }
    
    
    /**
     * Process the default configuration file, if it exists.
     * The default config must be read with the container loader - so
     * container servlets can be loaded
     */
    protected void defaultConfig() {
        ;
    }


    /**
     * Process a "stop" event for this Context.
     */
    protected synchronized void stop() {
        
        super.stop();

        try {
            ComponentEnvManager namingMgr = habitat.getComponent(
                com.sun.enterprise.container.common.spi.util.ComponentEnvManager.class);
            if (namingMgr!=null) {
                namingMgr.unbindFromComponentNamespace(webBundleDescriptor);
            }
        } catch (javax.naming.NamingException ex) {
            String msg = rb.getString(
                "webModuleContextConfig.unbindNamespaceError");
            msg = MessageFormat.format(msg, context.getName());
            logger.log(Level.WARNING, msg, ex);
        }        
    }


}
