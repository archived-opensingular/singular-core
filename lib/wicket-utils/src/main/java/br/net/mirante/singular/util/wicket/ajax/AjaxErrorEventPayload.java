package br.net.mirante.singular.util.wicket.ajax;

import org.apache.wicket.ajax.AjaxRequestTarget;

public class AjaxErrorEventPayload {
    private final AjaxRequestTarget target;
    public AjaxErrorEventPayload(AjaxRequestTarget target) {
        this.target = target;
    }
    public AjaxRequestTarget getTarget() {
        return target;
    }
}
