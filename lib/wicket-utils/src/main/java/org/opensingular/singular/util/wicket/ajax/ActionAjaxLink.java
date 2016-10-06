/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.util.wicket.ajax;

import static org.opensingular.singular.util.wicket.util.WicketUtils.$m;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.model.IModel;

import org.opensingular.singular.util.wicket.util.WicketEventUtils;

@SuppressWarnings({ "serial" })
public abstract class ActionAjaxLink<T> extends AjaxLink<T> {
    
    private final static Logger LOGGER = Logger.getLogger(ActionAjaxLink.class.getName());
    
    public ActionAjaxLink(String id, IModel<T> model) {
        super(id, model);
    }

    public ActionAjaxLink(String id) {
        super(id);
    }

    protected abstract void onAction(AjaxRequestTarget target);

    @Override
    public void onClick(AjaxRequestTarget target) {
        try {
            onAction(target);
        } catch (RuntimeException ex) {
            LOGGER.log(Level.INFO, "Ajax error", ex);
            WicketEventUtils.addErrorMessage(this, null, $m.ofValue(ex.getMessage())); // TODO substituir por esquema de exceção de negócio
            WicketEventUtils.sendAjaxErrorEvent(this, target);
        }
    }

    @Override
    protected void disableLink(ComponentTag tag) {
        String tagName = tag.getName();
        if ("a".equalsIgnoreCase(tagName)) {
            tag.remove("class");
            tag.remove("onclick");
            tag.put("href", "javascript:");
            tag.put("disabled", "disabled");
            tag.put("style", "cursor: not-allowed;color: #999;");
        } else {
            super.disableLink(tag);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public ActionAjaxLink<T> setBody(IModel<?> bodyModel) {
        return (ActionAjaxLink<T>) super.setBody(bodyModel);
    }
}
