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

import net.vidageek.mirror.dsl.Mirror;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.IFormSubmitter;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.visit.ClassVisitFilter;
import org.apache.wicket.util.visit.IVisitor;
import org.apache.wicket.util.visit.Visits;

public class SingularFormWicket<T> extends Form<T> {
    //TODO (by Daniel Bordin) Entender por que esse método copia vários método da classe Form do Wicket. Isso pode
    // dar problema com novas versões do wicket. Entender o racional e verificar senão há uma solução melhor.

    public SingularFormWicket(String id, IModel<T> model) {
        super(id, model);
    }

    public SingularFormWicket(String id) {
        super(id);
    }

    protected boolean isIgnoreValidation() {
        return true;
    }

    protected boolean isIgnoreErrors() {
        return true;
    }

    //@formatter:off
    @Override
    protected void callOnError(IFormSubmitter submitter) {
        if (!isIgnoreErrors())
            super.callOnError(submitter);
    }

    @Override
    public void process(IFormSubmitter submittingComponent) {
        if (!isEnabledInHierarchy() || !isVisibleInHierarchy()) {
            // since process() can be called outside of the default form workflow, an additional
            // check is needed

            // FIXME throw listener exception
            return;
        }

        final boolean ignoreValidation = isIgnoreValidation();
        final boolean ignoreErrors     = isIgnoreErrors();

        // run validation
        if (!ignoreValidation) {
            validate();
        } else {
            convertWithoutValidateNestedForms();
            convertWithoutValidateComponents();
            onValidate();
        }

        // If a validation error occurred
        if (!ignoreErrors && hasError()) {
            // mark all children as invalid
            markFormComponentsInvalid();

            // let subclass handle error
            callOnError(submittingComponent);
        } else {
            // mark all children as valid
            markFormComponentsValid();

            // before updating, call the interception method for clients
            beforeUpdateFormComponentModels();

            // Update model using form data
            updateFormComponentModels();

            // validate model objects after input values have been bound
            singularInternalOnValidateModelObjects();
            if (!ignoreErrors && hasError()) {
                callOnError(submittingComponent);
                return;
            }

            // Form has no error
            delegateSubmit(submittingComponent);
        }
    }

    private void convertWithoutValidateNestedForms() {
        Visits.visitPostOrder(this, (IVisitor<SingularFormWicket<?>, Void>) (form, visit) -> {
            if (SingularFormWicket.this.equals(form)) {
                // skip self, only process children
                visit.stop();
            } else if (form.isSubmitted()) {
                form.convertWithoutValidateComponents();
                form.onValidate();
            }
        }, new ClassVisitFilter(SingularFormWicket.class));
    }

    protected void convertWithoutValidateComponents() {
        if (isEnabledInHierarchy() && isVisibleInHierarchy()) {
            visitFormComponentsPostOrder(new ValidationVisitor() {
                @Override
                public void validate(final FormComponent<?> formComponent) {
                    final Form<?> form = formComponent.getForm();
                    if ((!(form instanceof SingularFormWicket<?>) || (SingularFormWicket.this.equals(form)))
                            && form.isEnabledInHierarchy() && form.isVisibleInHierarchy()) {
                        formComponent.convertInput();
                    }
                }
            });
        }
    }

    /**
     * Calls {@linkplain #onValidateModelObjects()} on this form and all nested forms that are
     * visible and enabled
     */
    private void singularInternalOnValidateModelObjects() {   //Esse método é a mesma implementação no super. O nome foi alterado para o sonar não reclamar
        onValidateModelObjects();
        visitChildren(Form.class, (IVisitor<Form<?>, Void>) (form, visit) -> {
            if (form.isSubmitted()) {
                invokeOnValidateModelObjects(form);
            } else {
                visit.dontGoDeeper();
            }
        });
    }
    //@formatter:on

    private void invokeOnValidateModelObjects(Form<?> form) {
        if (form instanceof SingularFormWicket<?>) {
            ((SingularFormWicket<?>) form).onValidateModelObjects();
        } else {
            try {
                new Mirror().on(form).invoke().method("onValidateModelObjects").withoutArgs();
            } catch (Exception e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
        }
    }
}
