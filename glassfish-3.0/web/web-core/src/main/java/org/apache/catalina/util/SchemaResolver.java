/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2008 Sun Microsystems, Inc. All rights reserved.
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
 *
 * This file incorporates work covered by the following copyright and
 * permission notice:
 *
 * Copyright 2004 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.apache.catalina.util;


import java.util.HashMap;

import org.apache.tomcat.util.digester.Digester;
import org.xml.sax.InputSource;
import org.xml.sax.EntityResolver;
import org.xml.sax.SAXException;
// START PWC 6457880
import java.net.URI;
import java.net.URISyntaxException;
// END PWC 6457880

/**
 * This class implements a local SAX's <code>EntityResolver</code>. All
 * DTDs and schemas used to validate the web.xml file will re-directed
 * to a local file stored in the servlet-api.jar and jsp-api.jar.
 *
 * @author Jean-Francois Arcand
 */
public class SchemaResolver implements EntityResolver {

    /**
     * The disgester instance for which this class is the entity resolver.
     */
    protected Digester digester;


    /**
     * The URLs of dtds and schemas that have been registered, keyed by the
     * public identifier that corresponds.
     */
    protected HashMap entityValidator = new HashMap();


    /**
     * The public identifier of the DTD we are currently parsing under
     * (if any).
     */
    protected String publicId = null;


    /**
     * Extension to make the difference between DTD and Schema.
     */
    protected String schemaExtension = "xsd";

    // START PWC 6457880
    /**
     * Attribute value used to turn on/off network access of dtd/schema
     */
    private static boolean forceLocalSchema = false;
    // END PWC 6457880

    /**
     * Create a new <code>EntityResolver</code> that will redirect
     * all remote dtds and schema to a locat destination.
     * @param digester schemaLocation the XML Schema used to validate xml instance.
     */
    public SchemaResolver(Digester digester) {
        this.digester = digester;
    }


    /**
     * Register the specified DTD/Schema URL for the specified public
     * identifier. This must be called before the first call to
     * <code>parse()</code>.
     *
     * When adding a schema file (*.xsd), only the name of the file
     * will get added. If two schemas with the same name are added,
     * only the last one will be stored.
     *
     * @param publicId Public identifier of the DTD to be resolved
     * @param entityURL The URL to use for reading this DTD
     */
     public void register(String publicId, String entityURL) {
         String key = publicId;
         if (publicId.indexOf(schemaExtension) != -1)
             key = publicId.substring(publicId.lastIndexOf('/')+1);
         entityValidator.put(key, entityURL);
     }


    /**
     * Resolve the requested external entity.
     *
     * @param publicId The public identifier of the entity being referenced
     * @param systemId The system identifier of the entity being referenced
     *
     * @exception SAXException if a parsing exception occurs
     *
     */
    public InputSource resolveEntity(String publicId, String systemId)
        throws SAXException {

        if (publicId != null) {
            this.publicId = publicId;
            digester.setPublicId(publicId);
        }

        // Has this system identifier been registered?
        String entityURL = null;
        if (publicId != null) {
            entityURL = (String) entityValidator.get(publicId);
        }

        // Redirect the schema location to a local destination
        String key = null;
        if (entityURL == null && systemId != null) {
            key = systemId.substring(systemId.lastIndexOf('/')+1);
            entityURL = (String)entityValidator.get(key);
        }

/* PWC 6457880
        if (entityURL == null) {
           return (null);
        }

*/
        // START PWC 6457880
        if (entityURL == null) {
            if (forceLocalSchema) {
                URI u;
                try {
                    u  = new URI(systemId);
                } catch (URISyntaxException e) {
                    throw new SAXException(e);
                }
                String scheme = u.getScheme();
                // if the scheme is local, let the digester look it up
                // otherwise, throw an exception
                if (scheme != null && (scheme.equals("file") ||
                    scheme.equals("jar"))) {
                    return (null);
                }
                else {
                    throw new SAXException("Unable to find local schema for "+key);
                }
            }
            else {
                return (null);
            }
        }
        // END PWC 6457880
        try {
            return (new InputSource(entityURL));
        } catch (Exception e) {
            throw new SAXException(e);
        }

    }

    // START PWC 6457880
    public static void setForceLocalSchema(boolean flag) {
        forceLocalSchema = flag;
    }
    // END PWC 6457880
}