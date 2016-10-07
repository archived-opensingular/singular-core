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
 * Permite a configuração do inicio de uma nova linha, possibilitando melhor controle do layout.
 */
@CaseItem(componentName = "Grid", subCaseName = "Row Control", group = Group.LAYOUT)
public class CaseRowControlGridPackage extends SPackage {

    @Override
    protected void onLoadPackage(PackageBuilder pb) {
        super.onLoadPackage(pb);

        STypeComposite<?> testForm = pb.createCompositeType("testForm");

        testForm.addFieldString("nome")
                .asAtr().label("Nome")
                .asAtrBootstrap().colPreference(4);
        testForm.addFieldInteger("idade")
                .asAtr().label("Idade")
                .asAtrBootstrap().colPreference(1);
        testForm.addFieldEmail("email")
                .asAtr().label("E-mail")
                .asAtrBootstrap().newRow()
                .asAtrBootstrap().colPreference(5);
    }

}
