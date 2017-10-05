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
package com.sun.enterprise.jbi.serviceengine.bridge.transport;
import com.sun.enterprise.jbi.serviceengine.comm.MessageExchangeTransport;
import com.sun.enterprise.jbi.serviceengine.comm.MessageExchangeTransportFactory;
import com.sun.enterprise.jbi.serviceengine.core.ServiceEngineEndpoint;
import com.sun.logging.LogDomains;
import com.sun.xml.ws.api.message.Message;
import com.sun.xml.ws.api.server.WebServiceContextDelegate;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.server.WSEndpoint;
import java.security.Principal;
import java.util.logging.Logger;
import javax.jbi.messaging.MessageExchange;

/**
 *
 * @author Manisha Umbarje
 */
public class NMRServerConnection implements WebServiceContextDelegate {
    
    private MessageExchange me;
    private ServiceEngineEndpoint endpt;
    private MessageExchangeTransport meTransport;
    
    private static Logger logger =
            LogDomains.getLogger(NMRServerConnection.class, LogDomains.SERVER_LOGGER);
    
    /** Creates a new instance of NMRServerConnection */
    public NMRServerConnection(MessageExchange messageExchange,
            ServiceEngineEndpoint endpt) {
        this.me = messageExchange;
        this.endpt = endpt;
        meTransport = MessageExchangeTransportFactory.getHandler(me);
    }

    public Packet receiveRequest() {
        Packet packet = new Packet();
        Message message = meTransport.receive(endpt.getEndpointMetaData());
        packet.setMessage(message);
        if(packet.invocationProperties != null) {
            packet.invocationProperties.putAll(meTransport.getMessageProperties());
        }
        return packet;
    }
    
    public void sendResponse(Packet packet) throws Exception {
        meTransport.send(packet, endpt.getEndpointMetaData());
    }
    
    public void handleException(Exception e) {
        meTransport.sendError(e);
    }
    
    //TODO: Implement these methods
    public Principal getUserPrincipal(Packet request) {
        return null;
    }
    
    public boolean isUserInRole(Packet request, String role) {
        return false;
    }
    
    public String getEPRAddress(Packet request, WSEndpoint endpoint) {
        return null;
    }
    
    public String getWSDLAddress(Packet request, WSEndpoint endpoint) {
        return null;
    }
}
