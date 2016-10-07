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
import org.opensingular.singular.form.showcase.component.CaseItem;
import org.opensingular.singular.form.showcase.component.Group;
import org.opensingular.singular.form.showcase.component.Resource;

/**
 * É possivel utilizar objetos serializaveis para realizar a seleção, porem neste caso, é necessario informar o conversor.
 */
@CaseItem(componentName = "Select", subCaseName = "Tipo composto com objetos serializaveis.", group = Group.INPUT,
        resources = {@Resource(IngredienteQuimico.class), @Resource(IngredienteQuimicoFilteredProvider.class)})
public class CaseInputCoreSelectCompositePojoPackage extends SPackage {

    @Override
    protected void onLoadPackage(PackageBuilder pb) {

        final STypeComposite<?>           tipoMyForm         = pb.createCompositeType("testForm");
        final STypeComposite<SIComposite> ingredienteQuimico = tipoMyForm.addFieldComposite("ingredienteQuimico");

        ingredienteQuimico.asAtr().label("Ingrediente Quimico");

        ingredienteQuimico.addFieldString("formulaQuimica");
        ingredienteQuimico.addFieldString("nome");

        ingredienteQuimico.selectionOf(IngredienteQuimico.class)
                .id(IngredienteQuimico::getNome)
                .display("${nome} - ${formulaQuimica}")
                .autoConverterOf(IngredienteQuimico.class)
                .simpleProvider(new IngredienteQuimicoFilteredProvider());

    }

}
