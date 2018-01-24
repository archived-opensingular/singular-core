/*
 *
 *  * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.opensingular.studio.core.panel;

import de.alpharogroup.wicket.js.addon.toastr.ToastrType;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.opensingular.form.SInstance;
import org.opensingular.lib.commons.lambda.IConsumer;
import org.opensingular.lib.wicket.util.toastr.ToastrHelper;
import org.opensingular.studio.core.definition.StudioDefinition;

import java.io.Serializable;

public class CrudShellManager implements Serializable {

    private StudioDefinition studioDefinition;
    private CrudShellContent crudShellContent;
    private CrudShell crudShell;

    public CrudShellManager(StudioDefinition studioDefinition, CrudShell crudShell) {
        this.studioDefinition = studioDefinition;
        this.crudShell = crudShell;
        this.crudShellContent = studioDefinition.buildStartContent(this);
    }

    public void replaceContent(AjaxRequestTarget ajaxRequestTarget, CrudShellContent newContent) {
        crudShellContent = (CrudShellContent) crudShellContent.replaceWith(newContent);
        ajaxRequestTarget.add(crudShell);
    }

    public StudioDefinition getStudioDefinition() {
        return studioDefinition;
    }

    public CrudShellContent getCrudShellContent() {
        return crudShellContent;
    }

    public void addToastrMessage(ToastrType type, String message) {
        new ToastrHelper(crudShellContent).addToastrMessage(type, message);
    }

    public void update(AjaxRequestTarget ajaxRequestTarget) {
        ajaxRequestTarget.add(crudShell);
    }

    public void addConfirm(String message, AjaxRequestTarget ajaxRequestTarget, IConsumer<AjaxRequestTarget> onConfirm) {
        CallbackAjaxBehaviour callbackAjaxBehaviour = new CallbackAjaxBehaviour(onConfirm);
        crudShell.add(callbackAjaxBehaviour);
        ajaxRequestTarget.appendJavaScript("bootbox.confirm('" + message + "', " +
                "function(ok){if(ok){Wicket.Ajax.get({u:'" + callbackAjaxBehaviour.getCallbackUrl() + "'});}})");
    }

    private static class CallbackAjaxBehaviour extends AbstractDefaultAjaxBehavior {
        private final IConsumer<AjaxRequestTarget> callback;

        private CallbackAjaxBehaviour(IConsumer<AjaxRequestTarget> callback) {
            this.callback = callback;
        }

        @Override
        protected void respond(AjaxRequestTarget ajaxRequestTarget) {
            callback.accept(ajaxRequestTarget);
        }
    }

    public CrudEditContent makeEditContent(CrudShellContent previousContent, IModel<SInstance> instance) {
        return studioDefinition.buildEditContent(this, previousContent, instance);
    }

    public CrudListContent makeListContent() {
        return studioDefinition.buildListContent(this);
    }

}