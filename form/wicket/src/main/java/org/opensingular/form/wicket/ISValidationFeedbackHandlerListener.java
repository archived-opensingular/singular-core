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

package org.opensingular.form.wicket;

import java.io.Serializable;
import java.util.Collection;
import java.util.Optional;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;

import org.opensingular.lib.commons.lambda.IConsumer;
import org.opensingular.form.SInstance;
import org.opensingular.form.validation.IValidationError;

public interface ISValidationFeedbackHandlerListener extends Serializable {

    void onFeedbackChanged(SValidationFeedbackHandler handler,
                           Optional<AjaxRequestTarget> target,
                           Component container,
                           Collection<SInstance> baseInstances,
                           Collection<IValidationError> oldErrors, Collection<IValidationError> newErrors);

    static ISValidationFeedbackHandlerListener refresh(Component... components) {
        return withTarget(t -> t.add(components));
    }
    static ISValidationFeedbackHandlerListener withTarget(IConsumer<AjaxRequestTarget> withTarget) {
        return new ISValidationFeedbackHandlerListener() {
            @Override
            public void onFeedbackChanged(SValidationFeedbackHandler handler, Optional<AjaxRequestTarget> target, Component container, Collection<SInstance> baseInstances, Collection<IValidationError> oldErrors, Collection<IValidationError> newErrors) {
                if (target.isPresent())
                    withTarget.accept(target.get());
            }
        };
    }
}