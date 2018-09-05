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

package org.opensingular.lib.wicket.util.modal;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Optional;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.event.IEvent;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSContainer;
import org.opensingular.lib.wicket.util.jquery.JQuery;
import org.opensingular.lib.wicket.util.scripts.Scripts;

/**
 * Listener para eventos <code>IOpenModalEvent</code> e <code>ICloseModalEvent</code>,
 * que gerencia abertura e fechamento de modais.
 */
public class BSModalEventListenerBehavior extends Behavior {

    private final BSContainer<?> modalItemsContainer;

    /**
     * @param modalItemsContainer instância de BSContainer<?> que irá conter os componentes de modal.
     */
    public BSModalEventListenerBehavior(BSContainer<?> modalItemsContainer) {
        this.modalItemsContainer = modalItemsContainer;
    }

    @Override
    public void onEvent(Component component, IEvent<?> event) {
        Object payload = event.getPayload();

        if (payload instanceof IOpenModalEvent) {
            handleOpenModalEvent(event, (IOpenModalEvent) payload);

        } else if (payload instanceof ICloseModalEvent) {
            handleCloseModalEvent(event, (ICloseModalEvent) payload);
        }
    }

    protected BSModalWindow newModalWindow(String modalId, IOpenModalEvent openModalEvent) {
        return new BSModalWindow(modalId);
    }

    protected void handleOpenModalEvent(IEvent<?> event, IOpenModalEvent payload) {
        event.stop();

        final String modalId = modalItemsContainer.newChildId();
        final BSModalWindow modal = newModalWindow(modalId, payload);
        modalItemsContainer.newTag("div", modal.setOutputMarkupId(true).setOutputMarkupId(true));

        final Component content = payload.getBodyContent(modal.getId() + "_body");
        modal.setBody(content);

        payload.configureModal(modal.getModalBorder(), content);

        final Optional<AjaxRequestTarget> target = payload.getTarget();

        // adiciona uma div para a renderização da modal
        target.ifPresent(t -> {
            t.prependJavaScript(JQuery.$(modalItemsContainer) + ""
                + ".append('<div id=\"" + modal.getMarkupId() + "\"></div>');");
            t.add(modal);
            t.appendJavaScript(Scripts.multipleModalBackDrop());
        });

        modal.show(target.orElse(null));
        modal.setOnHideCallBack(t -> {
            Component removedComponent = modalItemsContainer.removeItem(modal);
            if ((t != null) && (removedComponent != null))
                t.appendJavaScript(JQuery.$(removedComponent, modal) + ".remove();");
        });
    }

    protected void handleCloseModalEvent(IEvent<?> event, ICloseModalEvent payload) {
        Deque<Component> stack = new LinkedList<>();
        MarkupContainer container = modalItemsContainer;
        pushChildren(stack, container);
        while (!stack.isEmpty()) {
            Component child = stack.pop();
            if (child instanceof BSModalWindow) {
                BSModalWindow modal = (BSModalWindow) child;
                if (payload.matchesBodyContent(modal.getBody())) {
                    event.stop();
                    modal.hide(payload.getTarget());
                    break;
                }
            } else if (child instanceof MarkupContainer) {
                pushChildren(stack, (MarkupContainer) child);
            }
        }
    }

    private static void pushChildren(Deque<Component> stack, MarkupContainer container) {
        for (Component child : container)
            stack.push(child);
    }

}