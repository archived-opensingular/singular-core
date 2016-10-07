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

package org.opensingular.singular.form.showcase.component.form.layout;

import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SPackage;
import org.opensingular.form.STypeComposite;
import org.opensingular.singular.form.showcase.component.CaseItem;
import org.opensingular.singular.form.showcase.component.Group;


/**
 * Configura automaticamente o tamanho das colunas do bootstrap para telas menores,
 * multiplicando pelo fator de 2, 3 e 4 para colunas md (médium), sm (small) e xs (extra small),
 * mantendo o máximo de 12.
 * Por exemplo, ao configurar o tamanho para 3, o tamanho md será 6, sm 12 e xs 12.
 */
@CaseItem(componentName = "Grid", subCaseName = "Simple", group = Group.LAYOUT)
public class CaseSimpleGridPackage extends SPackage {

    @Override
    protected void onLoadPackage(PackageBuilder pb) {
        STypeComposite<?> testForm = pb.createCompositeType("testForm");

        testForm.addFieldString("nome")
                .asAtr().label("Nome")
                .asAtrBootstrap().colPreference(6);
        testForm.addFieldInteger("idade")
                .asAtr().label("Idade")
                .asAtrBootstrap().colPreference(2);
        testForm.addFieldEmail("email")
                .asAtr().label("E-mail")
                .asAtrBootstrap().colPreference(8);

    }
}
