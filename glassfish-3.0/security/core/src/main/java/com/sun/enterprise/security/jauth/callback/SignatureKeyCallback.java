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

package com.sun.enterprise.security.jauth.callback;

import java.security.KeyStore;
import java.security.PrivateKey;
import javax.crypto.SecretKey;
import java.security.cert.Certificate;
import javax.security.auth.callback.Callback;
import javax.security.auth.x500.X500Principal;

/**
 * Callback for Signing Key.
 *
 * @version 1.8, 03/03/04
 */
public class SignatureKeyCallback implements Callback {

    private PrivateKey key;
    private X500Principal authority;
    private Certificate[] chain;

    /**
     * Constructs this SignatureKeyCallback with an authority.
     *
     * <p> Both a PrivateKey and corresponding certificate chain
     * will be returned.  The <i>authority</i> input parameter
     * specifies the X500Principal name of the root CA
     * certificate returned in the chain.
     * An authority does not have to be specified.
     *
     * @param authority the X500Principal name of the root CA
     *			certificate returned in the requested chain,
     *			or null
     */
    public SignatureKeyCallback(X500Principal authority) {
	this.authority = authority;
    }

    /**
     * Get the authority.
     *
     * @return the authority, or null
     */
    public X500Principal getAuthority() {
	return authority;
    }

    /**
     * Set the requested signing key.
     *
     * @param key the signing key
     * @param chain the corresponding certificate chain
     */
    public void setKey(PrivateKey key, Certificate[] chain) {
	this.key = key;
	this.chain = chain;
    }

    /**
     * Get the requested signing key.
     *
     * @return the signing key
     */
    public PrivateKey getKey() {
	return key;
    }

    /**
     * Get the certificate chain.
     *
     * @return the certificate chain
     */
    public Certificate[] getChain() {
	return chain;
    }
}
