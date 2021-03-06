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




package org.apache.catalina.authenticator;


import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.catalina.Container;
import org.apache.catalina.HttpRequest;
import org.apache.catalina.HttpResponse;
/** CR 6411114 (Lifecycle implementation moved to ValveBase)
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
*/
import org.apache.catalina.LifecycleException;
/** CR 6411114 (Lifecycle implementation moved to ValveBase)
import org.apache.catalina.LifecycleListener;
*/
import org.apache.catalina.Logger;
import org.apache.catalina.Request;
import org.apache.catalina.Response;
import org.apache.catalina.Session;
import org.apache.catalina.SessionEvent;
import org.apache.catalina.SessionListener;
import org.apache.catalina.valves.ValveBase;
/** CR 6411114 (Lifecycle implementation moved to ValveBase)
import org.apache.catalina.util.LifecycleSupport;
*/
import org.apache.catalina.util.StringManager;


/**
 * A <strong>Valve</strong> that supports a "single sign on" user experience,
 * where the security identity of a user who successfully authenticates to one
 * web application is propogated to other web applications in the same
 * security domain.  For successful use, the following requirements must
 * be met:
 * <ul>
 * <li>This Valve must be configured on the Container that represents a
 *     virtual host (typically an implementation of <code>Host</code>).</li>
 * <li>The <code>Realm</code> that contains the shared user and role
 *     information must be configured on the same Container (or a higher
 *     one), and not overridden at the web application level.</li>
 * <li>The web applications themselves must use one of the standard
 *     Authenticators found in the
 *     <code>org.apache.catalina.authenticator</code> package.</li>
 * </ul>
 *
 * @author Craig R. McClanahan
 * @version $Revision: 1.7 $ $Date: 2007/05/05 05:31:53 $
 */

public class SingleSignOn
    extends ValveBase
    /** CR 6411114 (Lifecycle implementation moved to ValveBase)
    implements Lifecycle, SessionListener {
    */
    // START CR 6411114
    implements SessionListener {
    // END CR 6411114


    // ----------------------------------------------------- Static Variables

    private static final java.util.logging.Logger log =
        java.util.logging.Logger.getLogger(
            SingleSignOn.class.getName());

    /**
     * The string manager for this package.
     */
    protected final static StringManager sm =
        StringManager.getManager(Constants.Package);

    /**
     * Descriptive information about this Valve implementation.
     */
    protected static final String info =
        "org.apache.catalina.authenticator.SingleSignOn";


    // ----------------------------------------------------- Instance Variables

    /**
     * The cache of SingleSignOnEntry instances for authenticated Principals,
     * keyed by the cookie value that is used to select them.
     */
    protected HashMap cache = new HashMap();

    /**
     * The lifecycle event support for this component.
     */
    /** CR 6411114 (Lifecycle implementation moved to ValveBase)
    protected LifecycleSupport lifecycle = new LifecycleSupport(this);
    */

    /**
     * The cache of single sign on identifiers, keyed by the Session that is
     * associated with them.
     */
    protected HashMap reverse = new HashMap();

    /**
     * Component started flag.
     */
    /** CR 6411114 (Lifecycle implementation moved to ValveBase)
    protected boolean started = false;
    */


    // ------------------------------------------------------------- Properties

    /**
     * Return the debugging detail level.
     */
    public int getDebug() {
        return (this.debug);
    }

    /**
     * Set the debugging detail level.
     *
     * @param debug The new debugging detail level
     */
    public void setDebug(int debug) {
        this.debug = debug;
    }


    // ------------------------------------------------------ Lifecycle Methods

    /**
     * Add a lifecycle event listener to this component.
     *
     * @param listener The listener to add
     */
    /** CR 6411114 (Lifecycle implementation moved to ValveBase)
    public void addLifecycleListener(LifecycleListener listener) {

        lifecycle.addLifecycleListener(listener);

    }
    */


    /**
     * Get the lifecycle listeners associated with this lifecycle. If this 
     * Lifecycle has no listeners registered, a zero-length array is returned.
     */
    /** CR 6411114 (Lifecycle implementation moved to ValveBase)
    public LifecycleListener[] findLifecycleListeners() {

        return lifecycle.findLifecycleListeners();

    }
    */


    /**
     * Remove a lifecycle event listener from this component.
     *
     * @param listener The listener to remove
     */
    /** CR 6411114 (Lifecycle implementation moved to ValveBase)
    public void removeLifecycleListener(LifecycleListener listener) {

        lifecycle.removeLifecycleListener(listener);

    }
    */


    /**
     * Prepare for the beginning of active use of the public methods of this
     * component.  This method should be called after <code>configure()</code>,
     * and before any of the public methods of the component are utilized.
     *
     * @exception LifecycleException if this component detects a fatal error
     *  that prevents this component from being used
     */
    public void start() throws LifecycleException {

        // Validate and update our current component state
        /** CR 6411114 (Lifecycle implementation moved to ValveBase)
        if (started)
            throw new LifecycleException
                (sm.getString("authenticator.alreadyStarted"));
        lifecycle.fireLifecycleEvent(START_EVENT, null);
        started = true;
        */
        // START CR 6411114
        if (started)            // Ignore multiple starts
            return;
        super.start();
        // END CR 6411114

        if (debug >= 1)
            log("Started");

    }


    /**
     * Gracefully terminate the active use of the public methods of this
     * component.  This method should be the last one called on a given
     * instance of this component.
     *
     * @exception LifecycleException if this component detects a fatal error
     *  that needs to be reported
     */
    public void stop() throws LifecycleException {

        // Validate and update our current component state
        /** CR 6411114 (Lifecycle implementation moved to ValveBase)
        if (!started)
            throw new LifecycleException
                (sm.getString("authenticator.notStarted"));
        lifecycle.fireLifecycleEvent(STOP_EVENT, null);
        started = false;
        */
        // START CR 6411114
        if (!started)       // Ignore stop if not started
            return;
        // END CR 6411114

        if (debug >= 1)
            log("Stopped");
        // START CR 6411114
        super.stop();
        // END CR 6411114

    }


    // ------------------------------------------------ SessionListener Methods


    /**
     * Acknowledge the occurrence of the specified event.
     *
     * @param event SessionEvent that has occurred
     */
    public void sessionEvent(SessionEvent event) {

        // We only care about session destroyed events
        if (!Session.SESSION_DESTROYED_EVENT.equals(event.getType()))
            return;

        // Look up the single session id associated with this session (if any)
        Session session = event.getSession();
        if (debug >= 1)
            log("Process session destroyed on " + session);

        String ssoId = null;
        synchronized (reverse) {
            ssoId = (String) reverse.get(session);
        }
        if (ssoId == null) {
            return;
        }

        deregister(ssoId, session);
    }


    // ---------------------------------------------------------- Valve Methods


    /**
     * Return descriptive information about this Valve implementation.
     */
    public String getInfo() {

        return (info);

    }


    /**
     * Perform single-sign-on support processing for this request.
     *
     * @param request The servlet request we are processing
     * @param response The servlet response we are creating
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet error occurs
     */
    public int invoke(Request request, Response response)
        throws IOException, ServletException {

        // If this is not an HTTP request and response, just pass them on
        /* GlassFish 6386229
        if (!(request instanceof HttpRequest) ||
            !(response instanceof HttpResponse)) {
            return INVOKE_NEXT;
        }
        */
        HttpServletRequest hreq =
            (HttpServletRequest) request.getRequest();
        HttpServletResponse hres =
            (HttpServletResponse) response.getResponse();
        request.removeNote(Constants.REQ_SSOID_NOTE);

        // Has a valid user already been authenticated?
        if (debug >= 1)
            log("Process request for '" + hreq.getRequestURI() + "'");
        if (hreq.getUserPrincipal() != null) {
            if (debug >= 1)
                log(" Principal '" + hreq.getUserPrincipal().getName() +
                    "' has already been authenticated");
            return END_PIPELINE;
        }

        // Check for the single sign on cookie
        if (debug >= 1)
            log(" Checking for SSO cookie");
        Cookie cookie = null;
        Cookie cookies[] = hreq.getCookies();
        if (cookies == null)
            cookies = new Cookie[0];
        for (int i = 0; i < cookies.length; i++) {
            if (Constants.SINGLE_SIGN_ON_COOKIE.equals(cookies[i].getName())) {
                cookie = cookies[i];
                break;
            }
        }
        if (cookie == null) {
            if (debug >= 1)
                log(" SSO cookie is not present");
            return INVOKE_NEXT;
        }

        // Look up the cached Principal associated with this cookie value
        if (debug >= 1)
            log(" Checking for cached principal for " + cookie.getValue());
        SingleSignOnEntry entry = lookup(cookie.getValue());
        if (entry != null) {
            if (debug >= 1)
                log(" Found cached principal '" +
                    entry.principal.getName() + "' with auth type '" +
                    entry.authType + "'");
            request.setNote(Constants.REQ_SSOID_NOTE, cookie.getValue());
            ((HttpRequest) request).setAuthType(entry.authType);
            ((HttpRequest) request).setUserPrincipal(entry.principal);
        } else {
            if (debug >= 1)
                log(" No cached principal found, erasing SSO cookie");
            cookie.setMaxAge(0);
            hres.addCookie(cookie);
        }

        // Invoke the next Valve in our pipeline
        return INVOKE_NEXT;
    }


    // --------------------------------------------------------- Public Methods


    /**
     * Return a String rendering of this object.
     */
    public String toString() {

        StringBuffer sb = new StringBuffer("SingleSignOn[");
        if (container == null )
            sb.append("Container is null");
        else
            sb.append(container.getName());
        sb.append("]");
        return (sb.toString());

    }


    // -------------------------------------------------------- Package Methods


    /**
     * Associate the specified single sign on identifier with the
     * specified Session.
     *
     * @param ssoId Single sign on identifier
     * @param session Session to be associated
     */
    public void associate(String ssoId, Session session) {

        if (!started) {
            return;
        }

        if (debug >= 1)
            log("Associate sso id " + ssoId + " with session " + session);

        SingleSignOnEntry sso = lookup(ssoId);
        if (sso != null)
            sso.addSession(this, session);
        synchronized (reverse) {
            reverse.put(session, ssoId);
        }

    }

    /**
     * Deregister the specified session.  If it is the last session,
     * then also get rid of the single sign on identifier
     *
     * @param ssoId Single sign on identifier
     * @param session Session to be deregistered
     */
    protected void deregister(String ssoId, Session session) {

        synchronized (reverse) {
            reverse.remove(session);
        }

        SingleSignOnEntry sso = lookup(ssoId);
        if ( sso == null )
            return;

        sso.removeSession( session );

        // see if we are the last session, if so blow away ssoId
        if (sso.isEmpty()) {
            synchronized (cache) {
                sso = (SingleSignOnEntry) cache.remove(ssoId);
            }
        }
    }


    /**
     * Register the specified Principal as being associated with the specified
     * value for the single sign on identifier.
     *
     * @param ssoId Single sign on identifier to register
     * @param principal Associated user principal that is identified
     * @param authType Authentication type used to authenticate this
     *  user principal
     * @param username Username used to authenticate this user
     * @param password Password used to authenticate this user
     */
    protected void register(String ssoId, Principal principal, String authType,
                  String username, String password, String realmName) {

        if (debug >= 1)
            log("Registering sso id '" + ssoId + "' for user '" +
                principal.getName() + "' with auth type '" + authType + "'");

        synchronized (cache) {
            cache.put(ssoId, new SingleSignOnEntry(ssoId, principal, authType,
                                                   username, password,
                                                   realmName));
        }

    }


    // ------------------------------------------------------ Protected Methods


    /**
     * Log a message on the Logger associated with our Container (if any).
     *
     * @param message Message to be logged
     */
    protected void log(String message) {
        Logger logger = container.getLogger();
        if (logger != null) {
            logger.log(this.toString() + ": " + message);
        } else {
            log.info(this.toString() + ": " + message);
        }
    }


    /**
     * Log a message on the Logger associated with our Container (if any).
     *
     * @param message Message to be logged
     * @param t Associated exception
     */
    protected void log(String message, Throwable t) {
        Logger logger = container.getLogger();
        if (logger != null) {
            logger.log(this.toString() + ": " + message, t,
                Logger.WARNING);
        } else {
            log.log(java.util.logging.Level.WARNING,
                this.toString() + ": " + message, t);
        }
    }


    /**
     * Look up and return the cached SingleSignOn entry associated with this
     * sso id value, if there is one; otherwise return <code>null</code>.
     *
     * @param ssoId Single sign on identifier to look up
     */
    protected SingleSignOnEntry lookup(String ssoId) {

        synchronized (cache) {
            return ((SingleSignOnEntry) cache.get(ssoId));
        }

    }


}
