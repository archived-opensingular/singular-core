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

package org.opensingular.singular.form.showcase.component.form.core.select;

import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SPackage;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.singular.form.showcase.component.CaseItem;
import org.opensingular.singular.form.showcase.component.Group;

/**
 * Pemite a seleção de valores compostos de varios tipos diferentes.
 */
@CaseItem(componentName = "Select", subCaseName = "Tipo Composto", group = Group.INPUT)
public class CaseInputCoreSelectCompositePackage extends SPackage {

    @Override
    protected void onLoadPackage(PackageBuilder pb) {

        final STypeComposite<SIComposite> tipoMyForm         = pb.createCompositeType("testForm");
        final STypeComposite<SIComposite> ingredienteQuimico = tipoMyForm.addFieldComposite("ingredienteQuimico");

        ingredienteQuimico.asAtr().label("Ingrediente Quimico");

        final STypeString formulaQuimica = ingredienteQuimico.addFieldString("formulaQuimica");
        final STypeString nome           = ingredienteQuimico.addFieldString("nome");

        ingredienteQuimico.selection()
                .id(formulaQuimica)
                .display("${nome} - ${formulaQuimica}")
                .simpleProvider(listaBuilder -> {
                    listaBuilder.add().set(formulaQuimica, "H20").set(nome, "Água");
                    listaBuilder.add().set(formulaQuimica, "H2O2").set(nome, "Água Oxigenada");
                    listaBuilder.add().set(formulaQuimica, "O2").set(nome, "Gás Oxigênio");
                    listaBuilder.add().set(formulaQuimica, "C12H22O11").set(nome, "Açúcar");
                });

    }

}
