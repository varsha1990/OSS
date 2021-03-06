/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2009 Sun Microsystems, Inc. All rights reserved.
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
package com.sun.ejb.monitoring.stats;

import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.glassfish.external.probe.provider.StatsProviderManager;
import org.glassfish.external.probe.provider.PluginPoint;
import com.sun.ejb.containers.EjbContainerUtilImpl;

import org.glassfish.external.statistics.*;
import org.glassfish.external.statistics.impl.*;

/**
 * Utility class for Ejb monitoring.
 *
 * @author Marina Vatkina
 */
public class EjbMonitoringUtils {

    private static final Logger _logger =
            EjbContainerUtilImpl.getInstance().getLogger();

    static final String NODE = "/";
    static final String SEP = "-";
    static final String APPLICATION_NODE = "applications" + NODE;
    static final String EJB_MONITORING_NODE = "ejb-container";
    static final String METHOD_NODE = NODE + "bean-methods" + NODE;


    static String registerComponent(String appName, String moduleName, 
                String beanName, Object listener, String invokerId) {
        String beanSubTreeNode = getBeanNode(appName, moduleName, beanName);
        try {
            StatsProviderManager.register(EJB_MONITORING_NODE, 
                    PluginPoint.APPLICATIONS, beanSubTreeNode, listener, null, invokerId);
        } catch (Exception ex) {
            _logger.log(Level.SEVERE, "[**EjbMonitoringUtils**] Could not register listener for " 
                    + "appName: " + appName + "; modName: " + moduleName + "; beanName: " + beanName, ex);

            return null;
        }

        return beanSubTreeNode;
    }

    static String registerSubComponent(String appName, String moduleName,
            String beanName, String subNode, Object listener, String invokerId) {
        String subTreeNode = getBeanNode(appName, moduleName, beanName) + NODE + subNode;
        if (_logger.isLoggable(Level.FINE)) {
            _logger.fine("SUB-NODE NAME: " + subTreeNode);
        }
        try {
             StatsProviderManager.register(EJB_MONITORING_NODE, 
                    PluginPoint.APPLICATIONS, subTreeNode, listener, null, invokerId);
        } catch (Exception ex) {
            _logger.log(Level.SEVERE, "[**EjbMonitoringUtils**] Could not register subnode ["
                    + subNode + "] listener for appName: " + appName 
                    + "; modName: " + moduleName + "; beanName: " + beanName, ex);

            return null;
        }

        return subTreeNode;
    }

    static String registerMethod(String parentNode, String mname, Object listener, String invokerId) {
        String subTreeNode = parentNode + METHOD_NODE + mname;
        if (_logger.isLoggable(Level.FINE)) {
            _logger.fine("METHOD NODE NAME: " + subTreeNode);
        }
        try {
            StatsProviderManager.register(EJB_MONITORING_NODE,
                    PluginPoint.APPLICATIONS, subTreeNode, listener, null, invokerId);
        } catch (Exception ex) {
            _logger.log(Level.SEVERE, "[**EjbMonitoringUtils**] Could not register method "
                    + "listener for " + subTreeNode, ex);
            return null;
        }

        return subTreeNode;
    }



    static String stringify(Method m) {
        if (_logger.isLoggable(Level.FINE)) {
            _logger.fine("==> Converting method to String: " + m);
        }
        StringBuffer sb = new StringBuffer();
        sb.append(m.getName());
        Class[] args = m.getParameterTypes();
        for (Class c : args) {
            sb.append(SEP).append(c.getName());
        }
        return sb.toString().replaceAll("\\.", "\\\\.").replaceAll("_", "\\\\.");
    }

    static String getBeanNode(String appName, String moduleName, String beanName) {
        StringBuffer sb = new StringBuffer();
        /** sb.append(APPLICATION_NODE); **/

        if (appName != null) {
            sb.append(appName).append(NODE);
        }
        sb.append(moduleName).append(NODE).append(beanName);

        String beanSubTreeNode = sb.toString().replaceAll("\\.", "\\\\.").
               replaceAll("_jar", "\\\\.jar").replaceAll("_war", "\\\\.war");

        if (_logger.isLoggable(Level.FINE)) {
            _logger.fine("BEAN NODE NAME: " + beanSubTreeNode);
        }
        return beanSubTreeNode;
    }

    public static String getInvokerId(String appName, String modName, String beanName) {
        return "_" + appName + "_" + modName + "_" + beanName;
    }

}
