/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.opensingular.bam.wicket.view;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Objects;

import javax.servlet.ServletContext;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.protocol.http.WebApplication;
import org.springframework.web.util.JavaScriptUtils;

import org.opensingular.lib.wicket.util.model.FallbackReadOnlyModel;
import org.opensingular.lib.wicket.util.util.WicketUtils;

public interface SingularWicketContainer<CONTAINER extends MarkupContainer, T> {

    default String getContextPath() {
        String rootContext = null;
        WebApplication webApplication = WebApplication.get();
        if (webApplication != null) {
            ServletContext servletContext = webApplication.getServletContext();
            if (servletContext != null) {
                rootContext = servletContext.getContextPath();
            }
        }
        return rootContext;
    }

    /**
     * Adiciona um model simples à página.
     */
    @SuppressWarnings("unchecked")
    default CONTAINER setModel(IModel<T> model) {
        CONTAINER self = (CONTAINER) this;
        self.setDefaultModel(model);
        return self;
    }
    @SuppressWarnings("unchecked")
    default IModel<T> getModel() {
        return (IModel<T>) ((CONTAINER) this).getDefaultModel();
    }
    @SuppressWarnings("unchecked")
    default CONTAINER setModelObject(T object) {
        CONTAINER self = (CONTAINER) this;
        self.setDefaultModelObject(object);
        return self;
    }
    @SuppressWarnings("unchecked")
    default T getModelObject() {
        return (T) ((CONTAINER) this).getDefaultModelObject();
    }

    @SuppressWarnings("unchecked")
    default StringResourceModel getMessage(String prop) {
        return new StringResourceModel(prop.trim(), ((CONTAINER) this), null);
    }

    @SuppressWarnings("unchecked")
    default FallbackReadOnlyModel<String> getFallbackMessage(String... props) {
        final IModel<String>[] models = new IModel[props.length];
        for (int i = 0; i < props.length; i++) {
            models[i] = getMessage(props[i]);
        }
        return new FallbackReadOnlyModel<>(models);
    }

    default StringResourceModel getMessage(String prop, IModel<?> model) {
        return new StringResourceModel(prop, model);
    }

    default StringResourceModel getMessage(String prop, Object... positionalPrameters) {
        HashMap<Integer, Object> params = new HashMap<>();
        for (int i = 0; i < positionalPrameters.length; i++)
            params.put(i, positionalPrameters[i]);
        return new StringResourceModel(prop, null, WicketUtils.$m.ofValue(params));
    }

    /**
     * Retornar um label utilizando o wicket-id como chave para encontrar o
     * texto no message bundle
     * 
     * @param wicketId chave do message bundle e wicket:id na página
     */
    default Label newMessageLabel(String wicketId) {
        return newMessageLabel(wicketId, wicketId);
    }

    /**
     * Adiciona um label à pagina utilizando o wicket-id como chave para
     * encontrar o texto no message bundle
     * 
     * @param wicketId chave do message bundle e wicket:id na página
     */
    default Label newMessageLabel(String wicketId, String messagesId) {
        return new Label(wicketId, getMessage(messagesId));
    }

    //    @SuppressWarnings("unchecked")
    //    default BaseAuthenticatedSession getSigaSession() {
    //        return (BaseAuthenticatedSession) ((CONTAINER) this).getSession();
    //    }

    @SuppressWarnings("unchecked")
    default void alert(AjaxRequestTarget target, Serializable message) {
        CONTAINER self = (CONTAINER) this;
        Page page = self.getPage();
        if (!self.equals(page) && page instanceof SingularWicketContainer) {
            ((SingularWicketContainer<?, ?>) page).alert(target, message);
        } else {
            String msgString;
            if (message instanceof IModel<?>) {
                msgString = Objects.toString(((IModel<?>) message).getObject());
            } else {
                msgString = Objects.toString(message);
            }
            target.appendJavaScript("alert('" + JavaScriptUtils.javaScriptEscape(msgString) + "');");
        }
    }
}
