/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.lib.wicket.util.util;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;

import org.opensingular.lib.wicket.util.ajax.AjaxErrorEventPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WicketEventUtils {

    private static Logger logger = LoggerFactory.getLogger(WicketEventUtils.class);

    private WicketEventUtils() {}

    public static void addErrorMessage(Component component, String messageKey, IModel<?> messageModel) {
        if (messageKey != null && component != null) {
            component.error(new StringResourceModel(messageKey, messageModel).getString());
        }
        logger.error("Obrigatório informar component e messageKey em addErrorMessage, ignorando chamada...");
    }

    public static void sendAjaxErrorEvent(Component component, AjaxRequestTarget target) {
        component.send(component, Broadcast.BUBBLE, new AjaxErrorEventPayload(target));
    }

    public static void onAjaxErrorEventRerender(IEvent<?> event, boolean stop, Component... components) {
        Object payload = event.getPayload();
        if (payload instanceof AjaxErrorEventPayload) {
            AjaxErrorEventPayload error = (AjaxErrorEventPayload) payload;
            error.getTarget().add(components);
            if (stop) {
                event.stop();
            }
        }
    }
}
