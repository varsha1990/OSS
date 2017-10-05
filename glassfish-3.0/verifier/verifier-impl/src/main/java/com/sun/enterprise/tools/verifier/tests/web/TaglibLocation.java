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
package com.sun.enterprise.tools.verifier.tests.web;

import java.util.*;
import javax.servlet.descriptor.*;
import com.sun.enterprise.deployment.*;
import com.sun.enterprise.tools.verifier.*;
import com.sun.enterprise.tools.verifier.tests.*;


/**
 *  Must check for the existence of the tld file.
 *  @author     Arun Jain
 */
public class TaglibLocation extends Taglib implements WebCheck {
    

     /** 
     * .tld file exists test.
     * 
     * @param descriptor the Web deployment descriptor
     *   
     * @return <code>Result</code> the results for this assertion
     */
    public Result check(WebBundleDescriptor descriptor) {

	Result result = getInitializedResult();
	ComponentNameConstructor compName = getVerifierContext().getComponentNameConstructor();
        boolean passed = true;
        String taglibLocation;  
        Iterable<TaglibDescriptor> taglibConfig = null;
        if (descriptor.getJspConfigDescriptor() != null) {
            taglibConfig = descriptor.getJspConfigDescriptor().getTaglibs();
        }

        if (taglibConfig != null) {
            for (TaglibDescriptor taglibDescriptor : taglibConfig) {
                // test all the Tag lib descriptors.
                taglibLocation = taglibDescriptor.getTaglibLocation();
                if(passed == false)
                    check(descriptor, taglibLocation, result);
                else
                    passed = check(descriptor, taglibLocation, result);         
            }
        } else {
	    result.addNaDetails(smh.getLocalString
				       ("tests.componentNameConstructor",
					"For [ {0} ]",
					new Object[] {compName.toString()}));
            result.notApplicable(smh.getLocalString
                                 (getClass().getName() + ".notApplicable",
                                  "There are no TagLibConfigurationDescriptors within the web archive [ {0} ]",
                                  new Object[] {descriptor.getName()}));
            result.setStatus(Result.NOT_APPLICABLE);       
            return result;
            
        } 
        
        if ( passed) {
            result.setStatus(Result.PASSED);       
        }  else {
            result.setStatus(Result.FAILED);
         }
        
        return result;
    }
}
