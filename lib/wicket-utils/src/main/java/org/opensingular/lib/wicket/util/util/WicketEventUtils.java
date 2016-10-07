/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
