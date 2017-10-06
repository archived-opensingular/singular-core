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
        this.crudShellContent = studioDefinition.makeStartContent(this);
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

    private class CallbackAjaxBehaviour extends AbstractDefaultAjaxBehavior {
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
        return studioDefinition.makeEditContent(this, previousContent, instance);
    }

    public CrudListContent makeListContent() {
        return studioDefinition.makeListContent(this);
    }

}