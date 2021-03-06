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

package org.glassfish.connectors.admin.cli;

import org.jvnet.hk2.annotations.Service;
import org.jvnet.hk2.annotations.Scoped;
import org.jvnet.hk2.annotations.Inject;
import org.jvnet.hk2.component.PerLookup;
import org.glassfish.api.I18n;
import org.glassfish.api.Param;
import org.glassfish.api.ActionReport;
import org.glassfish.api.admin.AdminCommand;
import org.glassfish.api.admin.AdminCommandContext;
import com.sun.enterprise.util.LocalStringManagerImpl;
import com.sun.enterprise.config.serverbeans.Resources;
import com.sun.enterprise.config.serverbeans.Resource;
import com.sun.enterprise.config.serverbeans.ResourcePool;
import com.sun.appserv.connectors.internal.api.ConnectorRuntime;
import com.sun.appserv.connectors.internal.api.ConnectorRuntimeException;

@Service(name = "flush-connection-pool")
@Scoped(PerLookup.class)
@I18n("flush.connection.pool")
public class FlushConnectionPool implements AdminCommand {
    final private static LocalStringManagerImpl localStrings =
            new LocalStringManagerImpl(FlushConnectionPool.class);

    @Param(name = "pool_name", primary = true)
    String poolName;

    @Inject
    Resources resources;

    @Inject
    ConnectorRuntime _runtime;

    public void execute(AdminCommandContext context) {
        final ActionReport report = context.getActionReport();

        if (!isPoolExist()) {
            report.setMessage(localStrings.getLocalString(
                    "flush.connection.pool.notexist",
                    "Resource pool {0} does not exist.", poolName));
            report.setActionExitCode(ActionReport.ExitCode.FAILURE);
            return;

        }

        try {
            _runtime.flushConnectionPool(poolName);
            report.setActionExitCode(ActionReport.ExitCode.SUCCESS);
        } catch (ConnectorRuntimeException e) {
            report.setMessage(localStrings.getLocalString("flush.connection.pool.fail",
                    "Failed to flush connection pool {0} due to {1}.", poolName, e.getMessage()));
            report.setActionExitCode(ActionReport.ExitCode.FAILURE);
            report.setFailureCause(e);
        }
    }

    private boolean isPoolExist() {
        for (Resource resource : resources.getResources()) {
            if (resource instanceof ResourcePool) {
                if (((ResourcePool) resource).getName().equals(poolName)) {
                    return true;
                }
            }
        }
        return false;
    }
}
