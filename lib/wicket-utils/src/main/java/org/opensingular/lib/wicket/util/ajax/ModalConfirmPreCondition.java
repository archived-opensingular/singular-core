package org.opensingular.lib.wicket.util.ajax;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.attributes.AjaxCallListener;
import org.apache.wicket.util.template.PackageTextTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * USAGE:
 * new AjaxLink<Void>("myAjaxStuff") {
 *      @Override
 *      protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
 *          super.updateAjaxAttributes(attributes);
 *          attributes.getAjaxCallListeners().add(new ModalConfirmPreCondition());
 *      }
 * };*
 */
public class ModalConfirmPreCondition extends AjaxCallListener {
    private String title          = "Tem certeza?";
    private String message          = "Tem certeza?";
    private String confirmLabel     = "Sim";
    private String cancelLabel      = "Não";
    private String confirmClassName = "btn-sucess";
    private String cancelClassName  = "btn-default";

    @Override
    public CharSequence getPrecondition(Component component) {
        return new PackageTextTemplate(getClass(), "BootBoxModalConfirmPreCondition.js")
                .interpolate(createModel()).getString();
    }

    private Map<String, String> createModel() {
        final Map<String, String> model = new HashMap<>();
        model.put("title", title);
        model.put("message", message);
        model.put("confirmLabel", confirmLabel);
        model.put("cancelLabel", cancelLabel);
        model.put("confirmClassName", confirmClassName);
        model.put("cancelClassName", cancelClassName);
        return model;
    }

    public ModalConfirmPreCondition setMessage(String message) {
        this.message = message;
        return this;
    }

    public ModalConfirmPreCondition setConfirmLabel(String confirmLabel) {
        this.confirmLabel = confirmLabel;
        return this;
    }

    public ModalConfirmPreCondition setCancelLabel(String cancelLabel) {
        this.cancelLabel = cancelLabel;
        return this;
    }

    public ModalConfirmPreCondition setConfirmClassName(String confirmClassName) {
        this.confirmClassName = confirmClassName;
        return this;
    }

    public ModalConfirmPreCondition setCancelClassName(String cancelClassName) {
        this.cancelClassName = cancelClassName;
        return this;
    }

    public ModalConfirmPreCondition setTitle(String title) {
        this.title = title;
        return this;
    }
}