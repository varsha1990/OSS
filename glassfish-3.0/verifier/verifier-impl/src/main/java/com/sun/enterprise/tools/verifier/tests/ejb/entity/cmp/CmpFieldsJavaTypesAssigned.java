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
package com.sun.enterprise.tools.verifier.tests.ejb.entity.cmp;

import com.sun.enterprise.tools.verifier.tests.ejb.EjbTest;
import java.lang.ClassLoader;
import com.sun.enterprise.tools.verifier.tests.*;
import java.lang.reflect.*;
import com.sun.enterprise.deployment.*;
import com.sun.enterprise.tools.verifier.*;
import java.util.*;
import com.sun.enterprise.tools.verifier.tests.ejb.EjbCheck;
import com.sun.enterprise.tools.verifier.tests.ejb.EjbUtils;
import com.sun.enterprise.tools.verifier.tests.ejb.RmiIIOPUtils;

/** 
 * The Bean Provider must ensure that the Java types assigned to the 
 * container-managed fields are restricted to the following: Java primitive 
 * types, Java serializable types, and references of enterprise beans' remote 
 * or home interfaces.
 */
public class CmpFieldsJavaTypesAssigned extends EjbTest implements EjbCheck { 


    /** 
     * The Bean Provider must ensure that the Java types assigned to the 
     * container-managed fields are restricted to the following: Java primitive 
     * types, Java serializable types, and references of enterprise beans' remote 
     * or home interfaces.
     *
     * @param descriptor the Enterprise Java Bean deployment descriptor
     *   
     * @return <code>Result</code> the results for this assertion
     */
    public Result check(EjbDescriptor descriptor) {

	Result result = getInitializedResult();
	ComponentNameConstructor compName = getVerifierContext().getComponentNameConstructor();

	if (descriptor instanceof EjbEntityDescriptor) {
	    String persistence =
		((EjbEntityDescriptor)descriptor).getPersistenceType();
	    if (EjbEntityDescriptor.CONTAINER_PERSISTENCE.equals(persistence)) {
 
                // this test apply only to 1.x cmp beans
                if (EjbCMPEntityDescriptor.CMP_1_1 != ((EjbCMPEntityDescriptor) descriptor).getCMPVersion()) {
		    result.addNaDetails(smh.getLocalString
					("tests.componentNameConstructor",
					 "For [ {0} ]",
					 new Object[] {compName.toString()}));
	            result.notApplicable(smh.getLocalString
				 ("com.sun.enterprise.tools.verifier.tests.ejb.entity.cmp2.CMPTest.notApplicable3",
				  "Test do not apply to this cmp-version of container managed persistence EJBs"));
        	    return result;                    
                }   
                
		boolean oneFailed = false;
		boolean badField = false;

		Iterator itr = ((EjbCMPEntityDescriptor)descriptor).getPersistenceDescriptor().getCMPFields().iterator();
		while (itr.hasNext()) {
 
		    FieldDescriptor nextPersistentField = (FieldDescriptor)itr.next();
		    badField = false;
		    boolean foundField = false;

		    // ensure that the Java types assigned to the container-managed 
		    // fields are restricted to the following: Java primitive types, 
		    // Java serializable types, and references of enterprise beans' 
		    // remote or home interfaces.
		    Class c1 = null;
		    try {
			Class c = Class.forName(((EjbEntityDescriptor)descriptor).getEjbClassName(), false, getVerifierContext().getClassLoader());
			// start do while loop here....
			do {
			    try {
				c1 = c;
				Field f = c.getDeclaredField(nextPersistentField.getName());
				foundField = true;
				Class fc = f.getType();
				if ((RmiIIOPUtils.isValidRmiIDLPrimitiveType(fc)) || 
				    (descriptor.getRemoteClassName().equals(fc.getName())) ||
				    (descriptor.getHomeClassName().equals(fc.getName())) ||
				    (EjbUtils.isValidSerializableType(fc))||
				    (descriptor.getLocalClassName().equals(fc.getName())) ||
				    (descriptor.getLocalHomeClassName().equals(fc.getName()))) {
				    continue;
				} else {
				    if (!oneFailed) {
					oneFailed = true;
				    }
				    badField = true;
				}
        
				if (badField) {
				    result.addErrorDetails(smh.getLocalString
							   ("tests.componentNameConstructor",
							    "For [ {0} ]",
							    new Object[] {compName.toString()}));
				    result.failed(smh.getLocalString
						  (getClass().getName() + ".failed",
						   "Error: Field [ {0} ] defined within entity bean class [ {1} ] was assigned an invalid type.  Container managed field must be assigned in the entity bean class with Java types restricted to the following: Java primitive types, Java serializable types, and references of enterprise beans' remote or home interfaces.",
						   new Object[] {nextPersistentField.getName(),((EjbEntityDescriptor)descriptor).getEjbClassName()}));
				}
			    } catch (NoSuchFieldException e) {
				foundField = false;
			    }  
                        } while (((c = c.getSuperclass()) != null) && (!foundField));

                        if (!foundField) {
                            if (!oneFailed) {
                                oneFailed = true;
                            }
			    result.addErrorDetails(smh.getLocalString
						   ("tests.componentNameConstructor",
						    "For [ {0} ]",
						    new Object[] {compName.toString()}));
			    result.failed(smh.getLocalString
					  (getClass().getName() + ".failedException1",
					   "Error: [ {0} ] field not found within class [ {1} ]",
					   new Object[] {nextPersistentField.getName(),((EjbEntityDescriptor)descriptor).getEjbClassName()}));
		        }
		    } catch (ClassNotFoundException e) {
			Verifier.debug(e);
			result.addErrorDetails(smh.getLocalString
					       ("tests.componentNameConstructor",
						"For [ {0} ]",
						new Object[] {compName.toString()}));
			result.failed(smh.getLocalString
				      (getClass().getName() + ".failedException",
				       "Error: [ {0} ] class not found.",
				       new Object[] {((EjbEntityDescriptor)descriptor).getEjbClassName()}));
		    }  

		    if (!oneFailed) {
			result.addGoodDetails(smh.getLocalString
					      ("tests.componentNameConstructor",
					       "For [ {0} ]",
					       new Object[] {compName.toString()}));
			result.passed(smh.getLocalString
				      (getClass().getName() + ".passed",
				       "This entity bean class [ {0} ] has assigned [ {1} ] container managed field with valid Java type.",
				       new Object[] {c1.getName(),nextPersistentField.getName()}));
		    }
                }
                if (oneFailed) {
                    result.setStatus(Result.FAILED);
                } else {
                    result.setStatus(Result.PASSED);
                }
		return result;
 
	    } else { // if (BEAN_PERSISTENCE.equals(persistence)) {
		result.addNaDetails(smh.getLocalString
				    ("tests.componentNameConstructor",
				     "For [ {0} ]",
				     new Object[] {compName.toString()}));
		result.notApplicable(smh.getLocalString
				     (getClass().getName() + ".notApplicable1",
				      "Expected [ {0} ] managed persistence, but [ {1} ] bean has [ {2} ] managed persistence.",
				      new Object[] {EjbEntityDescriptor.CONTAINER_PERSISTENCE,descriptor.getName(),persistence}));
		return result;
	    }
	} else {
	    result.addNaDetails(smh.getLocalString
				("tests.componentNameConstructor",
				 "For [ {0} ]",
				 new Object[] {compName.toString()}));
	    result.notApplicable(smh.getLocalString
				 (getClass().getName() + ".notApplicable",
				  "{0} expected {1} bean, but called with {2}.",
				  new Object[] {getClass(),"Entity","Session"}));
	    return result;
	}



    }
}
