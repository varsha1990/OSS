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
package com.sun.enterprise.tools.verifier.tests.ejb.entity.ejbfindermethod;

import com.sun.enterprise.tools.verifier.tests.ejb.EjbTest;
import java.lang.reflect.*;

import com.sun.enterprise.deployment.EjbEntityDescriptor;
import com.sun.enterprise.deployment.EjbDescriptor;
import com.sun.enterprise.tools.verifier.tests.ejb.EjbCheck;
import com.sun.enterprise.tools.verifier.*;
import java.lang.ClassLoader;
import com.sun.enterprise.tools.verifier.tests.*;

/** 
 * ejbFind<METHOD>(...) methods not defined for entity bean with container 
 *  managed persistence test.  
 *
 *   EJB class contains no ejbFind<METHOD>(...) methods declared in the bean 
 *   class when the bean is container managed.  
 *
 *     A finder method name must start with the prefix ``ejbFind'' 
 *     (e.g. ejbFindByPrimaryKey, ejbFindLargeAccounts, ejbFindLateShipments). 
 * 
 */
public class EjbFinderMethodNameCmp extends EjbTest implements EjbCheck { 


    /**
     * ejbFind<METHOD>(...) methods not defined for entity bean with container 
     *  managed persistence test.  
     *
     *   EJB class contains no ejbFind<METHOD>(...) methods declared in the bean 
     *   class when the bean is container managed.  
     *
     *     A finder method name must start with the prefix ``ejbFind'' 
     *     (e.g. ejbFindByPrimaryKey, ejbFindLargeAccounts, ejbFindLateShipments). 
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

		boolean oneFailed = false;
		int foundAtLeastOne = 0;
		try {
    
		    // retrieve the home interface methods
		    VerifierTestContext context = getVerifierContext();
		    ClassLoader jcl = context.getClassLoader();
    	  
		    // retrieve the EJB Class Methods
		    Class EJBClass = Class.forName(descriptor.getEjbClassName(), false, getVerifierContext().getClassLoader());
                    // start do while loop here....
                    do {
			Method [] ejbFinderMethods = EJBClass.getDeclaredMethods();
    	  
			for (int j = 0; j < ejbFinderMethods.length; ++j) {
			    if (ejbFinderMethods[j].getName().startsWith("ejbFind")) {
				foundAtLeastOne++;
				if (!oneFailed) {
				    oneFailed = true;
				}
				result.addErrorDetails(smh.getLocalString
				       ("tests.componentNameConstructor",
					"For [ {0} ]",
					new Object[] {compName.toString()}));
				result.addErrorDetails(smh.getLocalString
						       (getClass().getName() + ".debug1",
							"For EJB Class [ {0} ] method [ {1} ]",
							new Object[] {EJBClass.getName(),ejbFinderMethods[j].getName()}));
				result.addErrorDetails(smh.getLocalString
						       (getClass().getName() + ".failed",
							"Error: An improperly named [ {0} ] method was found.",
							new Object[] {ejbFinderMethods[j].getName()}));
			    }
			}
                    } while (((EJBClass = EJBClass.getSuperclass()) != null));
           
		    if (foundAtLeastOne == 0){
			result.addGoodDetails(smh.getLocalString
					      ("tests.componentNameConstructor",
					       "For [ {0} ]",
					       new Object[] {compName.toString()}));
			result.passed(smh.getLocalString
				      (getClass().getName() + ".passed",
				       "[ {0} ] does not declare any ejbFind<METHOD>(...) methods.",
				       new Object[] {descriptor.getEjbClassName()}));
		    }
  
		} catch (ClassNotFoundException e) {
		    Verifier.debug(e);
		    result.addErrorDetails(smh.getLocalString
				       ("tests.componentNameConstructor",
					"For [ {0} ]",
					new Object[] {compName.toString()}));
		    result.failed(smh.getLocalString
				  (getClass().getName() + ".failedException",
				   "Error: EJB Class [ {1} ] does not exist or is not loadable.",
				   new Object[] {descriptor.getEjbClassName()}));
		    oneFailed = true;
		}
    
		if (oneFailed) {
		    result.setStatus(result.FAILED);
		} else {
		    result.setStatus(result.PASSED);
		}
    
		return result;

	    } else { // if (BEAN_PERSISTENCE.equals(persistence))
		result.addNaDetails(smh.getLocalString
				       ("tests.componentNameConstructor",
					"For [ {0} ]",
					new Object[] {compName.toString()}));
		result.notApplicable(smh.getLocalString
				     (getClass().getName() + ".notApplicable1",
				      "Expected [ {0} ] managed persistence, but [ {1} ] bean has [ {2} ] managed persistence.",
				      new Object[] {EjbEntityDescriptor.BEAN_PERSISTENCE,descriptor.getName(),persistence}));
		return result;
	    }

	} else {
	    result.addNaDetails(smh.getLocalString
				       ("tests.componentNameConstructor",
					"For [ {0} ]",
					new Object[] {compName.toString()}));
	    result.notApplicable(smh.getLocalString
				 (getClass().getName() + ".notApplicable",
				  "[ {0} ] expected {1} bean, but called with {2} bean.",
				  new Object[] {getClass(),"Entity","Session"}));
	    return result;
	}
    }
}
