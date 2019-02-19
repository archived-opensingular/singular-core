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

package org.opensingular.form.wicket.component;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.visit.IVisitor;
import org.opensingular.form.SInstance;
import org.opensingular.form.event.ISInstanceListener;
import org.opensingular.form.event.SInstanceEvent;
import org.opensingular.form.event.SInstanceEventType;
import org.opensingular.form.wicket.model.ISInstanceAwareModel;
import org.opensingular.form.wicket.util.WicketFormProcessing;

/**
 * Esse botão deve ser utilizado quando for necessário alterar o conteudo de uma SInstance.
 * <p>
 * Note: Esse botão tem como função alertar o Wicket que o valor foi alterado, para que ele seja atualizado na tela.
 */
public abstract class SingularUpdateAjaxLink<T> extends AjaxLink<T> {

    private Form form;
    private final IModel<? extends SInstance> currentInstance;

    public SingularUpdateAjaxLink(String id, Form form, IModel<? extends SInstance> currentInstance) {
        super(id);
        this.form = form;
        this.currentInstance = currentInstance;
    }

    /**
     * This method must not be overriding.
     *
     * @param target
     */
    @Override
    public void onClick(AjaxRequestTarget target) {
        ISInstanceListener changeModelListener = createModelUpdateListener(target);
        onBeforeClick(changeModelListener);
        onClickEvent(target);
        onAfterClick(changeModelListener);
    }

    public abstract void onClickEvent(AjaxRequestTarget target);

    /**
     * Method responsible for add a listener to call Wicket model changed.
     *
     * @param changeModelListener
     */
    private void onBeforeClick(ISInstanceListener changeModelListener) {
        currentInstance.getObject().getDocument().getInstanceListeners().add(SInstanceEventType.VALUE_CHANGED, changeModelListener);

    }

    /**
     * Method responsible for remove a listener to call Wicket model changed.
     *
     * @param changeModelListener
     */
    private void onAfterClick(ISInstanceListener changeModelListener) {
        currentInstance.getObject().getDocument().getInstanceListeners().remove(SInstanceEventType.VALUE_CHANGED, changeModelListener);
    }

    /**
     * Create a Listener to force model changed when SIntance value has changed.
     *
     * @param target
     * @return
     */
    private ISInstanceListener createModelUpdateListener(AjaxRequestTarget target) {
        return evt -> form.visitFormComponents(visitorUpdateModelChanged(evt, target));
    }

    private IVisitor<FormComponent<?>, Object> visitorUpdateModelChanged(SInstanceEvent evt, AjaxRequestTarget target) {
        return (object, visit) -> {
            IModel<?> model = object.getDefaultModel();
            if (model instanceof ISInstanceAwareModel) {
                SInstance compSInstance = ((ISInstanceAwareModel) model).getSInstance();
                if (compSInstance != null && evt.getSource().isSameOrDescendantOf(compSInstance)) {
                    WicketFormProcessing.refreshComponentOrCellContainer(target, object);
                    object.modelChanged();
                }
            }
        };
    }

}

