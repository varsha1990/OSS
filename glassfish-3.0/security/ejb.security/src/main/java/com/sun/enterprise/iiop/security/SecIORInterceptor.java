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

package com.sun.enterprise.iiop.security;

import com.sun.enterprise.config.serverbeans.IiopListener;
import java.util.logging.*;

import org.omg.IOP.Codec;
import org.omg.IOP.TaggedComponent;
import org.omg.PortableInterceptor.IORInfo;


import com.sun.logging.*;
import com.sun.enterprise.deployment.EjbDescriptor;
//import com.sun.enterprise.util.ORBManager;
//import org.glassfish.enterprise.iiop.api.GlassFishORBHelper;
//import com.sun.enterprise.iiop.ASORBUtilities;
import org.glassfish.enterprise.iiop.util.IIOPUtils;
import org.jvnet.hk2.component.Habitat;
import org.omg.CORBA.ORB;


public class SecIORInterceptor extends org.omg.CORBA.LocalObject
                    implements org.omg.PortableInterceptor.IORInterceptor {
    

    private static Logger _logger=null;

    static {
	_logger=LogDomains.getLogger(SecIORInterceptor.class,LogDomains.SECURITY_LOGGER);
    }

    private Codec codec;
    private Habitat habitat;
    //private GlassFishORBHelper helper = null;
    private ORB orb;
    
    public SecIORInterceptor(Codec c, Habitat habitat, ORB orb) {
        codec = c;
        this.habitat = habitat;
        //helper = habitat.getComponent(GlassFishORBHelper.class);
        this.orb = orb;
    }
    
    public void destroy() {
    }
    
    public String name() {
        return "SecIORInterceptor";
    }
    
    // Note: this is called for all remote refs created from this ORB,
    // including EJBs and COSNaming objects.
    public void establish_components(IORInfo iorInfo) {
        try {
	    _logger.log(Level.FINE, 
			"SecIORInterceptor.establish_components->:");
	    //EjbDescriptor desc = CSIV2TaggedComponentInfo.getEjbDescriptor( iorInfo ) ;
	    addCSIv2Components(iorInfo);
        } catch (Exception e) {
            _logger.log(Level.WARNING,"Exception in establish_components", e);
        } finally {
	    _logger.log(Level.FINE, 
			"SecIORInterceptor.establish_components<-:");
	}
    }

   
    private void addCSIv2Components(IORInfo iorInfo)
    {
      EjbDescriptor desc = null;
      try {
	if(_logger.isLoggable(Level.FINE)) {
	    _logger.log(Level.FINE, 
			".addCSIv2Components->: "
			+ " " + iorInfo );
	}

        /*TODO:V3:Cluster uncomment later
        if (ASORBUtilities.isGMSAvailableAndClusterHeartbeatEnabled()) {
	    // If this app server instance is part of a dynamic cluster (that is,
	    // one that supports RMI-IIOP failover and load balancing, DO NOT
	    // create the CSIv2 components here.  Instead, handle this in the
	    // ORB's ServerGroupManager, in conjunctions with the 
	    // CSIv2SSLTaggedComponentHandler.
            return;
        }*/

	if(_logger.isLoggable(Level.FINE)) {
	    _logger.log(Level.FINE, 
			".addCSIv2Components " );
	}

        
        
	//ORB orb = helper.getORB();
	int sslMutualAuthPort = getServerPort("SSL_MUTUALAUTH");
//	try {
//	    sslMutualAuthPort = ((com.sun.corba.ee.spi.legacy.interceptor.IORInfoExt)iorInfo).
//								getServerPort("SSL_MUTUALAUTH");
//	} catch (com.sun.corba.ee.spi.legacy.interceptor.UnknownType ute) {
//            _logger.log(Level.FINE,"UnknownType exception", ute);
//	}
       
	if(_logger.isLoggable(Level.FINE)) {
	    _logger.log(Level.FINE, 
			".addCSIv2Components: sslMutualAuthPort: " 
			+ sslMutualAuthPort);
	}

	CSIV2TaggedComponentInfo ctc = new CSIV2TaggedComponentInfo( orb,
	    sslMutualAuthPort, habitat );
        desc = ctc.getEjbDescriptor(iorInfo);

	// Create CSIv2 tagged component
	int sslport = getServerPort("SSL");
//	try {
//	    sslport = ((com.sun.corba.ee.spi.legacy.interceptor.IORInfoExt)iorInfo).
//								getServerPort("SSL");
//	} catch (com.sun.corba.ee.spi.legacy.interceptor.UnknownType ute) {
//            _logger.log(Level.FINE,"UnknownType exception", ute);
//	}
       if(_logger.isLoggable(Level.FINE)) {
	    _logger.log(Level.FINE, 
			".addCSIv2Components: sslport: " 
			+ sslport);
	}

	TaggedComponent csiv2Comp = null;
	if ( desc != null ) {
	    csiv2Comp = ctc.createSecurityTaggedComponent(sslport, desc);
	} else { 
	    // this is not an EJB object, must be a non-EJB CORBA object
	    csiv2Comp = ctc.createSecurityTaggedComponent(sslport);
	}

	iorInfo.add_ior_component(csiv2Comp);

      } finally {
	  if(_logger.isLoggable(Level.FINE)) {
	      _logger.log(Level.FINE,
			  ".addCSIv2Components<-: "
			  + " " + iorInfo + " " + desc );
	  }
      }
    }

    private int getServerPort(String mech) {
        IiopListener[] iiopListenerBeans = (IiopListener[]) IIOPUtils.getInstance().
                getIiopService().getIiopListener().toArray(new IiopListener[0]);
        for (IiopListener ilisten : iiopListenerBeans) {
            if (mech.equalsIgnoreCase("SSL")) {
                if (ilisten.getSecurityEnabled().equalsIgnoreCase("true") &&
                        ilisten.getSsl() != null &&
                        !ilisten.getSsl().getClientAuthEnabled().equalsIgnoreCase("true")) {
                    return Integer.parseInt(ilisten.getPort());
                }
            } else if (mech.equalsIgnoreCase("SSL_MUTUALAUTH")) {
                if (ilisten.getSecurityEnabled().equalsIgnoreCase("true") &&
                        ilisten.getSsl() != null &&
                        ilisten.getSsl().getClientAuthEnabled().equalsIgnoreCase("true")) {
                    return Integer.parseInt(ilisten.getPort());
                }
            } else if (!ilisten.getSecurityEnabled().equalsIgnoreCase("true")) {
                return Integer.parseInt(ilisten.getPort());
            }
        }
        return -1;
    }
}

// End of file.