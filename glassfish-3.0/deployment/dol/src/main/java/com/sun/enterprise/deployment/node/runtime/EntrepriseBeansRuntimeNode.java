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

package com.sun.enterprise.deployment.node.runtime;

import com.sun.enterprise.deployment.EjbBundleDescriptor;
import com.sun.enterprise.deployment.EjbDescriptor;
import com.sun.enterprise.deployment.ResourceReferenceDescriptor;
import com.sun.enterprise.deployment.node.XMLElement;
import com.sun.enterprise.deployment.util.DOLUtils;
import com.sun.enterprise.deployment.xml.RuntimeTagNames;
import com.sun.enterprise.deployment.xml.WebServicesTagNames;
import org.w3c.dom.Node;

import java.util.Iterator;

/**
 * This node handles runtime deployment descriptors for ejb bundle
 * 
 * @author  Jerome Dochez
 * @version 
 */
public class EntrepriseBeansRuntimeNode extends RuntimeDescriptorNode {
    
    /**
     * Initializes the child handler;
     */
    public EntrepriseBeansRuntimeNode() {
        // we do not care about our standard DDS handles
        handlers = null;
                         
        registerElementHandler(new XMLElement(RuntimeTagNames.EJB), 
                               EjbNode.class);                    
        registerElementHandler(new XMLElement(RuntimeTagNames.PM_DESCRIPTORS),
                               PMDescriptorsNode.class);                    
        registerElementHandler(new XMLElement(RuntimeTagNames.CMP_RESOURCE), 
                               CmpResourceNode.class);                    
        registerElementHandler
            (new XMLElement(RuntimeTagNames.MESSAGE_DESTINATION), 
             MessageDestinationRuntimeNode.class);

        registerElementHandler
            (new XMLElement(WebServicesTagNames.WEB_SERVICE),
             WebServiceRuntimeNode.class);
    }
    
   /**
    * @return the descriptor instance to associate with this XMLNode
    */    
    public Object getDescriptor() {
        return getParentNode().getDescriptor();
    }                
    
    
    protected XMLElement getXMLRootTag() {
        return new XMLElement(RuntimeTagNames.EJBS);
    }                 

    /**
     * receives notiification of the value for a particular tag
     * 
     * @param element the xml element
     * @param value it's associated value
     */
    public void setElementValue(XMLElement element, String value) {
	
        if (RuntimeTagNames.NAME.equals(element.getQName())) {
            DOLUtils.getDefaultLogger().finer("Ignoring runtime bundle name " + value);
            return;
        }

        if (RuntimeTagNames.UNIQUE_ID.equals(element.getQName())) {
            DOLUtils.getDefaultLogger().finer("Ignoring unique id");
            return;
        }
	    super.setElementValue(element, value);
    }
    
    /**
     * write the descriptor class to a DOM tree and return it
     *
     * @param parent node for the DOM tree
     * @param the descriptor to write
     * @return the DOM tree top node
     */    
    public Node writeDescriptor(Node parent, String nodeName, EjbBundleDescriptor bundleDescriptor) {    

        Node ejbs = super.writeDescriptor(parent, nodeName, bundleDescriptor);
	
        // NOTE : unique-id is no longer written out to sun-ejb-jar.xml.  It is persisted via
        // domain.xml deployment context properties instead.
        
        // ejb*
        EjbNode ejbNode = new EjbNode();
        for (Iterator ejbIterator = bundleDescriptor.getEjbs().iterator();ejbIterator.hasNext();) {
            EjbDescriptor ejbDescriptor = (EjbDescriptor) ejbIterator.next();
            ejbNode.writeDescriptor(ejbs, RuntimeTagNames.EJB, ejbDescriptor);
        }
        
        // pm-descriptors?
	PMDescriptorsNode pmsNode = new PMDescriptorsNode();
	pmsNode.writeDescriptor(ejbs, RuntimeTagNames.PM_DESCRIPTORS, bundleDescriptor);
        
        // cmpresource?
        ResourceReferenceDescriptor rrd = bundleDescriptor.getCMPResourceReference();
        if ( rrd != null ) {
            CmpResourceNode crn = new CmpResourceNode();
            crn.writeDescriptor(ejbs, RuntimeTagNames.CMP_RESOURCE, rrd);
        }
        
		// message-destination*
        writeMessageDestinationInfo(ejbs, bundleDescriptor);

		// webservice-description*
        WebServiceRuntimeNode webServiceNode = new WebServiceRuntimeNode();
        webServiceNode.writeWebServiceRuntimeInfo(ejbs, bundleDescriptor);

        return ejbs;
    }
        
}
