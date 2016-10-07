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

import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SPackage;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.type.core.STypeInteger;
import org.opensingular.singular.form.showcase.component.CaseItem;
import org.opensingular.singular.form.showcase.component.Group;

/**
 * Validação customizada, no exemplo verifica se o campo é menor que 1000
 */
@CaseItem(componentName = "Custom", group = Group.VALIDATION)
public class CaseValidationCustomPackage extends SPackage {

    @Override
    protected void onLoadPackage(PackageBuilder pb) {

        STypeComposite<?> tipoMyForm = pb.createCompositeType("testForm");
        STypeInteger mTipoInteger = tipoMyForm.addFieldInteger("qtd");
        mTipoInteger.asAtr().label("Quantidade");
        mTipoInteger.asAtr().required();
        mTipoInteger.addInstanceValidator(validatable -> {
            if(validatable.getInstance().getInteger() > 1000){
                validatable.error("O Campo deve ser menor que 1000");
            }
        });

    }
}
