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

package org.opensingular.singular.form.showcase.component.form.core;

import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SPackage;
import org.opensingular.form.STypeComposite;
import org.opensingular.singular.form.showcase.component.CaseItem;
import org.opensingular.singular.form.showcase.component.Group;

/**
 * Campo Text Area
 */
@CaseItem(componentName = "String", subCaseName = "Text Area", group = Group.INPUT)
public class CaseInputCoreTextAreaPackage extends SPackage {

    @Override
    //@formatter:off
    protected void onLoadPackage(PackageBuilder pb) {

        STypeComposite<?> tipoMyForm = pb.createCompositeType("testForm");

        tipoMyForm.addFieldString("observacao1")
                .withTextAreaView()
                .asAtr().label("Observação (default)");

        tipoMyForm.addFieldString("observacao2")
            .withTextAreaView(view->view.setLines(2))
            .asAtr()
            .label("Observação (2 linhas e 500 de limite)")
            .maxLength(500);

        tipoMyForm.addFieldString("observacao3")
                .withTextAreaView(view->view.setLines(10))
                .asAtr()
                .label("Observação (10 linhas e 5000 de limite)")
                .maxLength(5000);
    }
}
