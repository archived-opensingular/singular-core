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

package org.opensingular.singular.form.showcase.component.form.validation;

import static org.opensingular.lib.wicket.util.util.WicketUtils.$b;

import org.apache.wicket.ajax.markup.html.form.AjaxButton;

import org.opensingular.singular.form.showcase.component.CaseCustomizer;
import org.opensingular.singular.form.showcase.component.CaseBase;

public class CaseValidationPartialCustomizer implements CaseCustomizer {

    @Override
    public void customize(CaseBase caseBase) {
        caseBase.getBotoes().add((id, currentInstance) -> {
            final AjaxButton aj = new PartialValidationButton(id, currentInstance);

            aj.add($b.attr("value", "Validação Parcial"));

            return aj;
        });
    }

}
