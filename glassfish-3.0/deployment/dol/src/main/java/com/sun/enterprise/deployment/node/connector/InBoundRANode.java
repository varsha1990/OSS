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

package com.sun.enterprise.deployment.node.connector;

import com.sun.enterprise.deployment.ConnectorDescriptor;
import com.sun.enterprise.deployment.Descriptor;
import com.sun.enterprise.deployment.InboundResourceAdapter;
import com.sun.enterprise.deployment.MessageListener;
import com.sun.enterprise.deployment.node.DeploymentDescriptorNode;
import com.sun.enterprise.deployment.node.DescriptorFactory;
import com.sun.enterprise.deployment.node.XMLElement;
import com.sun.enterprise.deployment.xml.ConnectorTagNames;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;

import java.util.Map;


/**
 * This node is responsible for handling the Connector DTD related inbound-resourceadapter XML tag
 *
 * @author  Sheetal Vartak
 * @version 
 */
public class InBoundRANode extends DeploymentDescriptorNode {
    
    /**
     * all sub-implementation of this class can use a dispatch table to map xml element to
     * method name on the descriptor class for setting the element value. 
     *  
     * @return the map with the element name as a key, the setter method as a value
     */    

    private InboundResourceAdapter descriptor = null;

    //default constructor
    public InBoundRANode() {
	registerElementHandler(new XMLElement(ConnectorTagNames.MSG_LISTENER),
			       MessageListenerNode.class); 
    }

    /**
     * SAX Parser API implementation, we don't really care for now.
     */
     public void startElement(XMLElement element, Attributes attributes) {
     }

    /**
     * all sub-implementation of this class can use a dispatch table to map xml element to
     * method name on the descriptor class for setting the element value. 
     *  
     * @return the map with the element name as a key, the setter method as a value
     */ 
    protected Map getDispatchTable() {    
        Map table = super.getDispatchTable();
	return table;
    } 

    /**
    * @return the descriptor instance to associate with this XMLNode
    */    
    public Object getDescriptor() {
        if (descriptor==null) {
	    // the descriptor associated with the InBoundRANode is a InboundResourceAdapter 
	    // This descriptor is available with the parent node of the InBoundRANode
	    descriptor = (InboundResourceAdapter)DescriptorFactory.getDescriptor(getXMLPath());
	    ((ConnectorDescriptor)(getParentNode().getDescriptor())).setInboundResourceAdapter(descriptor);
		   
        } 
        return descriptor;
    }

     /**
     * Adds  a new DOL descriptor instance to the descriptor instance associated with 
     * this XMLNode
     *
     * @param descriptor the new descriptor
     */
    public void addDescriptor(Object obj) {
	if (obj instanceof MessageListener) {
	    descriptor.addMessageListener((MessageListener)obj);
	}
    }

    /**
     * write the descriptor class to a DOM tree and return it
     *
     * @param parent node for the DOM tree
     * @param the descriptor to write
     * @return the DOM tree top node
     */    
    public Node writeDescriptor(Node connectorNode, Descriptor descriptor) {  
	Node inBoundNode = appendChild(connectorNode, ConnectorTagNames.INBOUND_RESOURCE_ADAPTER);
	appendInBoundNode(inBoundNode, (InboundResourceAdapter)((ConnectorDescriptor)descriptor).getInboundResourceAdapter());
	return connectorNode;
    }

    /**
     * method to add the child nodes of INBOUND_RESOURCE_ADAPTER
     */
    private void appendInBoundNode (Node inBoundNode, InboundResourceAdapter conDesc) {

	Node msgAdapter = appendChild(inBoundNode, ConnectorTagNames.MSG_ADAPTER);

	MessageListenerNode msgListener = new MessageListenerNode();
	msgAdapter = msgListener.writeDescriptor(msgAdapter, conDesc);
    }    

}
