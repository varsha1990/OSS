/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 * 
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 * 
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 * 
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 * 
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 * 
 * Contributor(s):
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
package javax.faces.component.html;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.component.behavior.ClientBehaviorHolder;
import javax.el.MethodExpression;
import javax.el.ValueExpression;


/*
 * ******* GENERATED CODE - DO NOT EDIT *******
 */


/**
 * <p>Represents an HTML <code>form</code> element.  Child input components
 * will be submitted unless they have been disabled.</p>
 * <p>By default, the <code>rendererType</code> property must be set to "<code>javax.faces.Form</code>".
 * This value can be changed by calling the <code>setRendererType()</code> method.</p>
 */
public class HtmlForm extends javax.faces.component.UIForm implements ClientBehaviorHolder {



    private static final String OPTIMIZED_PACKAGE = "javax.faces.component.";

    public HtmlForm() {
        super();
        setRendererType("javax.faces.Form");
        handleAttribute("enctype", "application/x-www-form-urlencoded");
    }


    /**
     * <p>The standard component type for this component.</p>
     */
    public static final String COMPONENT_TYPE = "javax.faces.HtmlForm";


    protected enum PropertyKeys {
        accept,
        acceptcharset,
        dir,
        enctype,
        lang,
        onclick,
        ondblclick,
        onkeydown,
        onkeypress,
        onkeyup,
        onmousedown,
        onmousemove,
        onmouseout,
        onmouseover,
        onmouseup,
        onreset,
        onsubmit,
        role,
        style,
        styleClass,
        target,
        title,
;
        String toString;
        PropertyKeys(String toString) { this.toString = toString; }
        PropertyKeys() { }
        public String toString() {
            return ((toString != null) ? toString : super.toString());
        }
}

    /**
     * <p>Return the value of the <code>accept</code> property.</p>
     * <p>Contents: List of content types that a server processing this form
     * will handle correctly
     */
    public java.lang.String getAccept() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.accept);

    }

    /**
     * <p>Set the value of the <code>accept</code> property.</p>
     */
    public void setAccept(java.lang.String accept) {
        getStateHelper().put(PropertyKeys.accept, accept);
        handleAttribute("accept", accept);
    }


    /**
     * <p>Return the value of the <code>acceptcharset</code> property.</p>
     * <p>Contents: List of character encodings for input data
     * that are accepted by the server processing
     * this form.
     */
    public java.lang.String getAcceptcharset() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.acceptcharset);

    }

    /**
     * <p>Set the value of the <code>acceptcharset</code> property.</p>
     */
    public void setAcceptcharset(java.lang.String acceptcharset) {
        getStateHelper().put(PropertyKeys.acceptcharset, acceptcharset);
    }


    /**
     * <p>Return the value of the <code>dir</code> property.</p>
     * <p>Contents: Direction indication for text that does not inherit directionality.
     * Valid values are "LTR" (left-to-right) and "RTL" (right-to-left).
     * These attributes are case sensitive when rendering to XHTML, so
     * care must be taken to have the correct case.
     */
    public java.lang.String getDir() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.dir);

    }

    /**
     * <p>Set the value of the <code>dir</code> property.</p>
     */
    public void setDir(java.lang.String dir) {
        getStateHelper().put(PropertyKeys.dir, dir);
        handleAttribute("dir", dir);
    }


    /**
     * <p>Return the value of the <code>enctype</code> property.</p>
     * <p>Contents: Content type used to submit the form to the server.  If not
     * specified, the default value is
     * "application/x-www-form-urlencoded".
     */
    public java.lang.String getEnctype() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.enctype, "application/x-www-form-urlencoded");

    }

    /**
     * <p>Set the value of the <code>enctype</code> property.</p>
     */
    public void setEnctype(java.lang.String enctype) {
        getStateHelper().put(PropertyKeys.enctype, enctype);
    }


    /**
     * <p>Return the value of the <code>lang</code> property.</p>
     * <p>Contents: Code describing the language used in the generated markup
     * for this component.
     */
    public java.lang.String getLang() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.lang);

    }

    /**
     * <p>Set the value of the <code>lang</code> property.</p>
     */
    public void setLang(java.lang.String lang) {
        getStateHelper().put(PropertyKeys.lang, lang);
        handleAttribute("lang", lang);
    }


    /**
     * <p>Return the value of the <code>onclick</code> property.</p>
     * <p>Contents: Javascript code executed when a pointer button is
     * clicked over this element.
     */
    public java.lang.String getOnclick() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.onclick);

    }

    /**
     * <p>Set the value of the <code>onclick</code> property.</p>
     */
    public void setOnclick(java.lang.String onclick) {
        getStateHelper().put(PropertyKeys.onclick, onclick);
        handleAttribute("onclick", onclick);
    }


    /**
     * <p>Return the value of the <code>ondblclick</code> property.</p>
     * <p>Contents: Javascript code executed when a pointer button is
     * double clicked over this element.
     */
    public java.lang.String getOndblclick() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.ondblclick);

    }

    /**
     * <p>Set the value of the <code>ondblclick</code> property.</p>
     */
    public void setOndblclick(java.lang.String ondblclick) {
        getStateHelper().put(PropertyKeys.ondblclick, ondblclick);
        handleAttribute("ondblclick", ondblclick);
    }


    /**
     * <p>Return the value of the <code>onkeydown</code> property.</p>
     * <p>Contents: Javascript code executed when a key is
     * pressed down over this element.
     */
    public java.lang.String getOnkeydown() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.onkeydown);

    }

    /**
     * <p>Set the value of the <code>onkeydown</code> property.</p>
     */
    public void setOnkeydown(java.lang.String onkeydown) {
        getStateHelper().put(PropertyKeys.onkeydown, onkeydown);
        handleAttribute("onkeydown", onkeydown);
    }


    /**
     * <p>Return the value of the <code>onkeypress</code> property.</p>
     * <p>Contents: Javascript code executed when a key is
     * pressed and released over this element.
     */
    public java.lang.String getOnkeypress() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.onkeypress);

    }

    /**
     * <p>Set the value of the <code>onkeypress</code> property.</p>
     */
    public void setOnkeypress(java.lang.String onkeypress) {
        getStateHelper().put(PropertyKeys.onkeypress, onkeypress);
        handleAttribute("onkeypress", onkeypress);
    }


    /**
     * <p>Return the value of the <code>onkeyup</code> property.</p>
     * <p>Contents: Javascript code executed when a key is
     * released over this element.
     */
    public java.lang.String getOnkeyup() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.onkeyup);

    }

    /**
     * <p>Set the value of the <code>onkeyup</code> property.</p>
     */
    public void setOnkeyup(java.lang.String onkeyup) {
        getStateHelper().put(PropertyKeys.onkeyup, onkeyup);
        handleAttribute("onkeyup", onkeyup);
    }


    /**
     * <p>Return the value of the <code>onmousedown</code> property.</p>
     * <p>Contents: Javascript code executed when a pointer button is
     * pressed down over this element.
     */
    public java.lang.String getOnmousedown() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.onmousedown);

    }

    /**
     * <p>Set the value of the <code>onmousedown</code> property.</p>
     */
    public void setOnmousedown(java.lang.String onmousedown) {
        getStateHelper().put(PropertyKeys.onmousedown, onmousedown);
        handleAttribute("onmousedown", onmousedown);
    }


    /**
     * <p>Return the value of the <code>onmousemove</code> property.</p>
     * <p>Contents: Javascript code executed when a pointer button is
     * moved within this element.
     */
    public java.lang.String getOnmousemove() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.onmousemove);

    }

    /**
     * <p>Set the value of the <code>onmousemove</code> property.</p>
     */
    public void setOnmousemove(java.lang.String onmousemove) {
        getStateHelper().put(PropertyKeys.onmousemove, onmousemove);
        handleAttribute("onmousemove", onmousemove);
    }


    /**
     * <p>Return the value of the <code>onmouseout</code> property.</p>
     * <p>Contents: Javascript code executed when a pointer button is
     * moved away from this element.
     */
    public java.lang.String getOnmouseout() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.onmouseout);

    }

    /**
     * <p>Set the value of the <code>onmouseout</code> property.</p>
     */
    public void setOnmouseout(java.lang.String onmouseout) {
        getStateHelper().put(PropertyKeys.onmouseout, onmouseout);
        handleAttribute("onmouseout", onmouseout);
    }


    /**
     * <p>Return the value of the <code>onmouseover</code> property.</p>
     * <p>Contents: Javascript code executed when a pointer button is
     * moved onto this element.
     */
    public java.lang.String getOnmouseover() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.onmouseover);

    }

    /**
     * <p>Set the value of the <code>onmouseover</code> property.</p>
     */
    public void setOnmouseover(java.lang.String onmouseover) {
        getStateHelper().put(PropertyKeys.onmouseover, onmouseover);
        handleAttribute("onmouseover", onmouseover);
    }


    /**
     * <p>Return the value of the <code>onmouseup</code> property.</p>
     * <p>Contents: Javascript code executed when a pointer button is
     * released over this element.
     */
    public java.lang.String getOnmouseup() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.onmouseup);

    }

    /**
     * <p>Set the value of the <code>onmouseup</code> property.</p>
     */
    public void setOnmouseup(java.lang.String onmouseup) {
        getStateHelper().put(PropertyKeys.onmouseup, onmouseup);
        handleAttribute("onmouseup", onmouseup);
    }


    /**
     * <p>Return the value of the <code>onreset</code> property.</p>
     * <p>Contents: Javascript code executed when this form is reset.
     */
    public java.lang.String getOnreset() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.onreset);

    }

    /**
     * <p>Set the value of the <code>onreset</code> property.</p>
     */
    public void setOnreset(java.lang.String onreset) {
        getStateHelper().put(PropertyKeys.onreset, onreset);
        handleAttribute("onreset", onreset);
    }


    /**
     * <p>Return the value of the <code>onsubmit</code> property.</p>
     * <p>Contents: Javascript code executed when this form is submitted.
     */
    public java.lang.String getOnsubmit() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.onsubmit);

    }

    /**
     * <p>Set the value of the <code>onsubmit</code> property.</p>
     */
    public void setOnsubmit(java.lang.String onsubmit) {
        getStateHelper().put(PropertyKeys.onsubmit, onsubmit);
        handleAttribute("onsubmit", onsubmit);
    }


    /**
     * <p>Return the value of the <code>role</code> property.</p>
     * <p>Contents: <p class="changed_added_2_2">Per the WAI-ARIA spec and its
     * relationship to HTML5 (Section title ARIA Role Attriubute),
     * every HTML element may have a "role" attribute whose value
     * must be passed through unmodified on the element on which it
     * is declared in the final rendered markup.  The attribute, if
     * specified, must have a value that is a string literal that is,
     * or an EL Expression that evaluates to, a set of
     * space-separated tokens representing the various WAI-ARIA roles
     * that the element belongs to.
     * </p>
     * 
     * <p class="changed_added_2_2">It is the page author's
     * responsibility to ensure that the user agent is capable of
     * correctly interpreting the value of this attribute.</p>
     */
    public java.lang.String getRole() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.role);

    }

    /**
     * <p>Set the value of the <code>role</code> property.</p>
     */
    public void setRole(java.lang.String role) {
        getStateHelper().put(PropertyKeys.role, role);
        handleAttribute("role", role);
    }


    /**
     * <p>Return the value of the <code>style</code> property.</p>
     * <p>Contents: CSS style(s) to be applied when this component is rendered.
     */
    public java.lang.String getStyle() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.style);

    }

    /**
     * <p>Set the value of the <code>style</code> property.</p>
     */
    public void setStyle(java.lang.String style) {
        getStateHelper().put(PropertyKeys.style, style);
        handleAttribute("style", style);
    }


    /**
     * <p>Return the value of the <code>styleClass</code> property.</p>
     * <p>Contents: Space-separated list of CSS style class(es) to be applied when
     * this element is rendered.  This value must be passed through
     * as the "class" attribute on generated markup.
     */
    public java.lang.String getStyleClass() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.styleClass);

    }

    /**
     * <p>Set the value of the <code>styleClass</code> property.</p>
     */
    public void setStyleClass(java.lang.String styleClass) {
        getStateHelper().put(PropertyKeys.styleClass, styleClass);
    }


    /**
     * <p>Return the value of the <code>target</code> property.</p>
     * <p>Contents: Name of a frame where the response
     * retrieved after this form submit is to
     * be displayed.
     */
    public java.lang.String getTarget() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.target);

    }

    /**
     * <p>Set the value of the <code>target</code> property.</p>
     */
    public void setTarget(java.lang.String target) {
        getStateHelper().put(PropertyKeys.target, target);
        handleAttribute("target", target);
    }


    /**
     * <p>Return the value of the <code>title</code> property.</p>
     * <p>Contents: Advisory title information about markup elements generated
     * for this component.
     */
    public java.lang.String getTitle() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.title);

    }

    /**
     * <p>Set the value of the <code>title</code> property.</p>
     */
    public void setTitle(java.lang.String title) {
        getStateHelper().put(PropertyKeys.title, title);
        handleAttribute("title", title);
    }


    private static final Collection<String> EVENT_NAMES = Collections.unmodifiableCollection(Arrays.asList("click","dblclick","keydown","keypress","keyup","mousedown","mousemove","mouseout","mouseover","mouseup"));

    public Collection<String> getEventNames() {
        return EVENT_NAMES;    }


    public String getDefaultEventName() {
        return null;    }


    private void handleAttribute(String name, Object value) {
        List<String> setAttributes = (List<String>) this.getAttributes().get("javax.faces.component.UIComponentBase.attributesThatAreSet");
        if (setAttributes == null) {
            String cname = this.getClass().getName();
            if (cname != null && cname.startsWith(OPTIMIZED_PACKAGE)) {
                setAttributes = new ArrayList<String>(6);
                this.getAttributes().put("javax.faces.component.UIComponentBase.attributesThatAreSet", setAttributes);
            }
        }
        if (setAttributes != null) {
            if (value == null) {
                ValueExpression ve = getValueExpression(name);
                if (ve == null) {
                    setAttributes.remove(name);
                }
            } else if (!setAttributes.contains(name)) {
                setAttributes.add(name);
            }
        }
    }

}
