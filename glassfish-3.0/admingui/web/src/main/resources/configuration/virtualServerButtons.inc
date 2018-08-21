<!--
 DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 
 Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 
 The contents of this file are subject to the terms of either the GNU
 General Public License Version 2 only ("GPL") or the Common Development
 and Distribution License("CDDL") (collectively, the "License").  You
 may not use this file except in compliance with the License. You can obtain
 a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
 language governing permissions and limitations under the License.
 
 When distributing the software, include this License Header Notice in each
 file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 Sun designates this particular file as subject to the "Classpath" exception
 as provided by Sun in the GPL Version 2 section of the License file that
 accompanied this code.  If applicable, add the following below the License
 Header, with the fields enclosed by brackets [] replaced by your own
 identifying information: "Portions Copyrighted [year]
 [name of copyright owner]"
 
 Contributor(s):
 
 If you wish your version of this file to be governed by only the CDDL or
 only the GPL Version 2, indicate your decision by adding "[Contributor]
 elects to include this software in this distribution under the [CDDL or GPL
 Version 2] license."  If you don't indicate a single choice of license, a
 recipient has the option to distribute your version of this file under
 either the CDDL, the GPL Version 2 or to extend the choice of license to
 its licensees as provided above.  However, if you add GPL Version 2 code
 and therefore, elected the GPL Version 2 license, then the option applies
 only if the new code is made subject to such option by the copyright
 holder.
-->

<!-- configuration/virtualServerButtons.inc -->

<!facet pageButtonsTop>
        <sun:panelGroup id="topButtons">
            <sun:button id="saveButton" rendered="#{edit}" text="$resource{i18n.button.Save}"
                onClick="if (guiValidate('#{reqMsg}','#{reqInt}','#{reqPort}')) {submitAndDisable(this, '$resource{i18n.button.Processing}');}; return false;" >
                <!command
                    convertArrayToCommaString(array="#{pageSession.selectedNetworkListeners}"  commaString="#{requestScope.tmp}");
                    mapPut(map="#{pageSession.valueMap}" key="NetworkListeners" value="$attribute{tmp}");
                    getRequestValue(key="stateGp" value="#{requestScope.stateGp}");
                    getRequestValue(key="ssoGroup" value="#{requestScope.ssoGroup}");
                    getRequestValue(key="accessLoggingFlagGp" value="#{requestScope.accessLoggingFlagGp}");

                    mapPut(map="#{pageSession.valueMap}" key="State" value="#{requestScope.stateGp}");
                    mapPut(map="#{pageSession.valueMap}" key="SsoEnabled" value="#{requestScope.ssoGroup}");
                    mapPut(map="#{pageSession.valueMap}" key="AccessLoggingEnabled" value="#{requestScope.accessLoggingFlagGp}");

                    saveBeanAttributes(objectNameStr="#{pageSession.objectNameStr}"  attrs="#{pageSession.valueMap}"
                        skipAttrs="#{pageSession.skipAttrsList}"
                        convertToFalse="#{pageSession.convertToFalseList}"
                    );
                    EnsureDefaultWebModule(vsObjStr="#{pageSession.objectNameStr}");
                    getUIComponent(clientId="$pageSession{propertyTableRowGroupId}", component=>$attribute{tableRowGroup});
                    getAllSingleMapRows(TableRowGroup="$attribute{tableRowGroup}",  Rows=>$attribute{newList});
                    setProxyProperties(objectNameStr="#{pageSession.objectNameStr}", propertyList="$attribute{newList}", systemProp="#{pageSession.systemProp}" );
                    prepareSuccessfulMsg();
                    gf.redirect(page="#{selfPage}&alertType=${alertType}&alertSummary=${alertSummary}&alertDetail=${alertDetail}");
                />
            </sun:button>
            <sun:button id="newButton" rendered="#{!edit}" text="$resource{i18n.button.OK}"
                onClick="if (guiValidate('#{reqMsg}','#{reqInt}','#{reqPort}')) {submitAndDisable(this, '$resource{i18n.button.Processing}');}; return false;" >
                <!command
                    convertArrayToCommaString(array="#{pageSession.selectedNetworkListeners}"  commaString="#{requestScope.tmp}");
                    mapPut(map="#{pageSession.valueMap}" key="NetworkListeners" value="$attribute{tmp}");
                    mapPut(map="#{pageSession.valueMap}" key="State" value="#{requestScope.stateGp}");
                    mapPut(map="#{pageSession.valueMap}" key="SsoEnabled" value="#{requestScope.ssoGroup}");
                    mapPut(map="#{pageSession.valueMap}" key="AccessLoggingEnabled" value="#{requestScope.accessLoggingFlagGp}");
                    createProxy(parentObjectNameStr="#{pageSession.parentObjectNameStr}" childType="#{pageSession.childType}"
                        attrs="#{pageSession.valueMap}"
                        convertToFalse="#{pageSession.convertToFalseList}"
                        result => $attribute{objStr});
                    EnsureDefaultWebModule(vsObjStr="#{requestScope.objStr}");
                    getUIComponent(clientId="$pageSession{propertyTableRowGroupId}", component=>$attribute{tableRowGroup});
                    getAllSingleMapRows(TableRowGroup="$attribute{tableRowGroup}",  Rows=>$attribute{newList});
                    setProxyProperties(objectNameStr="$attribute{objStr}", propertyList="$attribute{newList}", systemProp="#{pageSession.systemProp}" );
                    gf.redirect(page="#{parentPage}?configName=#{configName}")
                />
            </sun:button>
            <sun:button id="cancelButton" immediate="#{true}" primary="#{false}" rendered="#{pageSession.showCancelButton}" text="$resource{i18n.button.Cancel}" >
                <!command
                    gf.redirect(page="#{parentPage}?configName=#{configName}");
                />
            </sun:button>
        </sun:panelGroup>
    </facet>