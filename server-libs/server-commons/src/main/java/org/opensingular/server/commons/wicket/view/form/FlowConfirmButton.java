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

package org.opensingular.server.commons.wicket.view.form;

import org.opensingular.lib.commons.util.Loggable;
import org.opensingular.form.SInstance;
import org.opensingular.form.wicket.component.SingularSaveButton;
import org.opensingular.server.commons.exception.PetitionConcurrentModificationException;
import org.opensingular.server.commons.exception.SingularServerFormValidationError;
import org.opensingular.server.commons.persistence.entity.form.PetitionEntity;
import org.opensingular.lib.wicket.util.modal.BSModalBorder;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.springframework.orm.hibernate4.HibernateOptimisticLockingFailureException;

public class FlowConfirmButton<T extends PetitionEntity> extends SingularSaveButton implements Loggable {

    private final AbstractFormPage<T> formPage;
    private final String              transitionName;
    private final BSModalBorder       modal;

    public FlowConfirmButton(final String transitionName,
                             final String id,
                             final IModel<? extends SInstance> model,
                             final boolean validate,
                             final AbstractFormPage<T> formPage,
                             final BSModalBorder modal) {
        super(id, model, validate);
        this.formPage = formPage;
        this.transitionName = transitionName;
        this.modal = modal;
    }

    @Override
    protected void onValidationSuccess(AjaxRequestTarget ajaxRequestTarget, Form<?> form, IModel<? extends SInstance> model) {
        try {
            formPage.executeTransition(ajaxRequestTarget, form, transitionName, model);
        } catch (HibernateOptimisticLockingFailureException
                | PetitionConcurrentModificationException e) {
            getLogger().error("Erro ao salvar o XML", e);
            formPage.addToastrErrorMessage("message.save.concurrent_error");
        } catch (SingularServerFormValidationError ex){
            //Faz hide para executar o script que limpa o backdrop
            modal.hide(ajaxRequestTarget);
            formPage.addToastrErrorMessage("message.send.error");
            modal.show(ajaxRequestTarget);
        }
    }

    @Override
    protected void onValidationError(final AjaxRequestTarget ajaxRequestTarget,
                                     final Form<?> form,
                                     final IModel<? extends SInstance> instanceModel) {
        modal.hide(ajaxRequestTarget);
        formPage.addToastrErrorMessage("Não é possivel " + transitionName.toLowerCase() + " enquanto houver correções a serem feitas.");
        ajaxRequestTarget.add(form);
    }

}