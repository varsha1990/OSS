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
 *
 */

package org.glassfish.tests.embedded.web.servlet;

import org.junit.*;
import org.junit.Assert;
import org.glassfish.api.embedded.*;
import org.glassfish.api.deployment.*;

import javax.naming.*;
import java.io.*;
import java.util.*;

import com.gargoylesoftware.htmlunit.*;

/**
 * Created by IntelliJ IDEA.
 * User: dochez
 * Date: Nov 4, 2009
 * Time: 1:44:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class ServletMain {

    public static void main(String[] args) {
        ServletMain test = new ServletMain();
        System.setProperty("basedir", System.getProperty("user.dir"));
        try {
            test.test();
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
    
    @Test
    public void test() throws Exception {

        Server server = new Server.Builder("web").build();
        try {
            File f = new File(System.getProperty("basedir"));
            f = new File(f, "target");
            f = new File(f, "classes");
            ScatteredArchive.Builder builder = new ScatteredArchive.Builder("hello", f);
            builder.addClassPath(f.toURI().toURL());
            builder.resources(f);
            ScatteredArchive war = builder.buildWar();
            System.out.println("War content");
            Enumeration<String> contents = war.entries();
            while(contents.hasMoreElements()) {
                System.out.println(contents.nextElement());
            }
            Port port = server.createPort(8882);

            server.addContainer(server.createConfig(ContainerBuilder.Type.web));
            DeployCommandParameters dp = new DeployCommandParameters(f);
            String appName = server.getDeployer().deploy(war, dp);
            WebClient webClient = new WebClient();
            try {
                Page page =  webClient.getPage("http://localhost:8882/classes/hello");
                System.out.println("Got response " + page.getWebResponse().getContentAsString());
                Assert.assertTrue("Servlet returne wrong content", page.getWebResponse().getContentAsString().startsWith("Hello World"));
                String hostName = System.getProperty("com.sun.aas.hostName");
                assert hostName!=null;
                page =  webClient.getPage("http://"+hostName+":8882/classes/hello");
                System.out.println("Got response " + page.getWebResponse().getContentAsString());
                Assert.assertTrue("Servlet returned wrong content", page.getWebResponse().getContentAsString().startsWith("Hello World"));
            } finally {
                System.out.println("Undeploying");
                server.getDeployer().undeploy(appName, null);
                port.close();
            }

        } finally {
            System.out.println("Stopping the server !");
            try {
                server.stop();
            } catch (LifecycleException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }
}
