/*
 *
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

package org.glassfish.connectors.admin.cli;


import com.sun.enterprise.config.serverbeans.MailResource;
import com.sun.enterprise.config.serverbeans.Resource;
import com.sun.enterprise.config.serverbeans.Resources;
import com.sun.enterprise.v3.common.PropsFileActionReporter;
import com.sun.logging.LogDomains;
import org.glassfish.api.ActionReport;
import org.glassfish.api.ActionReport.MessagePart;
import org.glassfish.api.admin.AdminCommandContext;
import org.glassfish.api.admin.CommandRunner;
import org.glassfish.api.admin.ParameterMap;
import org.glassfish.tests.utils.ConfigApiTest;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.jvnet.hk2.component.Habitat;
import org.jvnet.hk2.config.DomDocument;

import java.util.ArrayList;
import java.util.List;

public class ListJavaMailResourcesTest extends ConfigApiTest {

    private Habitat habitat;
    private int origNum = 0;
    private ParameterMap parameters;
    private AdminCommandContext context;
    private CommandRunner cr;

    public DomDocument getDocument(Habitat habitat) {
        return new TestDocument(habitat);
    }

    public String getFileName() {
        return "DomainTest";
    }

    @Before
    public void setUp() {
        habitat = getHabitat();
        parameters = new ParameterMap();
        cr = habitat.getComponent(CommandRunner.class);
        assertTrue(cr != null);
        Resources resources = habitat.getComponent(Resources.class);
        context = new AdminCommandContext(
                LogDomains.getLogger(ListJavaMailResourcesTest.class, LogDomains.ADMIN_LOGGER),
                new PropsFileActionReporter());
        for (Resource resource : resources.getResources()) {
            if (resource instanceof MailResource) {
                origNum = origNum + 1;
            }
        }
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of execute method, of class ListJavaMailResources.
     * list-javamail-resources
     */
    @Test
    public void testExecuteSuccessListOriginal() {
        ListJavaMailResources listCommand = habitat.getComponent(ListJavaMailResources.class);
        cr.getCommandInvocation("list-javamail-resources", context.getActionReport()).parameters(parameters).execute(listCommand);
        List<MessagePart> list = context.getActionReport().getTopMessagePart().getChildren();
        if (origNum == 0) {
            //Nothing to list
        } else {
            assertEquals(origNum, list.size());
        }
        assertEquals(ActionReport.ExitCode.SUCCESS, context.getActionReport().getActionExitCode());
    }


    /**
     * Test of execute method, of class ListJavaMailResource.
     * create-javamail-resource --mailuser=test --mailhost=localhost
     * --fromaddress=test@sun.com mailresource
     * list-javamail-resources
     */
    @Test
    public void testExecuteSuccessListMailResource() {
        parameters.set("mailhost", "localhost");
        parameters.set("mailuser", "test");
        parameters.set("fromaddress", "test@sun.com");
        parameters.set("jndi_name", "mailresource");
        CreateJavaMailResource createCommand = habitat.getComponent(CreateJavaMailResource.class);
        assertTrue(createCommand != null);
        cr.getCommandInvocation("create-javamail-resource", context.getActionReport()).parameters(parameters).execute(createCommand);
        assertEquals(ActionReport.ExitCode.SUCCESS, context.getActionReport().getActionExitCode());

        parameters = new ParameterMap();
        ListJavaMailResources listCommand = habitat.getComponent(ListJavaMailResources.class);
        assertTrue(listCommand != null);
        cr.getCommandInvocation("list-javamail-resources", context.getActionReport()).parameters(parameters).execute(listCommand);
        List<MessagePart> list = context.getActionReport().getTopMessagePart().getChildren();
        assertEquals(origNum + 1, list.size());
        List<String> listStr = new ArrayList<String>();
        for (MessagePart mp : list) {
            listStr.add(mp.getMessage());
        }
        assertTrue(listStr.contains("mailresource"));
        assertEquals(ActionReport.ExitCode.SUCCESS, context.getActionReport().getActionExitCode());
    }


    /**
     * Test of execute method, of class ListJdbcResource.
     * delete-javamail-resource mailresource
     * list-javamail-resources
     */
    @Test
    public void testExecuteSuccessListNoMailResource() {
        parameters.set("jndi_name", "mailresource");
        DeleteJavaMailResource deleteCommand = habitat.getComponent(DeleteJavaMailResource.class);
        assertTrue(deleteCommand != null);
        cr.getCommandInvocation("delete-javamail-resource", context.getActionReport()).parameters(parameters).execute(deleteCommand);
        assertEquals(ActionReport.ExitCode.SUCCESS, context.getActionReport().getActionExitCode());

        parameters = new ParameterMap();
        ListJavaMailResources listCommand = habitat.getComponent(ListJavaMailResources.class);
        cr.getCommandInvocation("list-javamail-resources", context.getActionReport()).parameters(parameters).execute(listCommand);
        List<MessagePart> list = context.getActionReport().getTopMessagePart().getChildren();
        assertEquals(origNum - 1, list.size());
        List<String> listStr = new ArrayList<String>();
        for (MessagePart mp : list) {
            listStr.add(mp.getMessage());
        }
        assertFalse(listStr.contains("mailresource"));
        assertEquals(ActionReport.ExitCode.SUCCESS, context.getActionReport().getActionExitCode());
    }


}
