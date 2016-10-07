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

package org.opensingular.singular.form.showcase.component.form.core.search;

import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SPackage;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.converter.ValueToSICompositeConverter;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.view.SViewSearchModal;
import org.opensingular.singular.form.showcase.component.CaseItem;
import org.opensingular.singular.form.showcase.component.Group;
import org.opensingular.singular.form.showcase.component.Resource;

/**
 * Permite a seleção a partir de uma busca filtrada, fazendo o controle de paginação de forma automatica.
 */
@CaseItem(componentName = "Search Select", subCaseName = "In Memory Pagination", group = Group.INPUT,
resources = {@Resource(Funcionario.class), @Resource(FuncionarioProvider.class), @Resource(FuncionarioRepository.class)})
public class CaseInputModalSearchPackage extends SPackage {

    @Override
    protected void onLoadPackage(PackageBuilder pb) {

        final STypeComposite<?> tipoMyForm = pb.createCompositeType("testForm");

        final STypeComposite funcionario = tipoMyForm.addFieldComposite("funcionario");
        funcionario.asAtr().label("Funcionario").displayString("${nome} - ${funcao}");

        final STypeString nome  = funcionario.addFieldString("nome");
        final STypeString funcao = funcionario.addFieldString("funcao");

        funcionario.withView(new SViewSearchModal().title("Buscar Profissionais"))
                .asAtrProvider()
                //@destacar
                .filteredProvider(new FuncionarioProvider())
                .converter((ValueToSICompositeConverter<Funcionario>) (newFunc, func) -> {
                    newFunc.setValue(nome, func.getNome());
                    newFunc.setValue(funcao, func.getFuncao());
                });

    }

}
