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
import org.opensingular.form.SIComposite;
import org.opensingular.form.SPackage;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.STypeList;
import org.opensingular.form.type.core.STypeInteger;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.type.util.STypeYearMonth;
import org.opensingular.form.view.SViewListByMasterDetail;
import org.opensingular.singular.form.showcase.component.CaseItem;
import org.opensingular.singular.form.showcase.component.Group;

/**
 * List by Master Detail
 */
@CaseItem(componentName = "List by Master Detail", subCaseName = "Aninhado", group = Group.LAYOUT)
public class CaseListByMasterDetailNestedPackage extends SPackage {

    @Override
    protected void onLoadPackage(PackageBuilder pb) {

        STypeComposite<?> testForm = pb.createCompositeType("testForm");

        STypeList<STypeComposite<SIComposite>, SIComposite> experiencias        = testForm.addFieldListOfComposite("experienciasProfissionais", "experiencia");
        STypeComposite<?>                                   experiencia         = experiencias.getElementsType();
        STypeYearMonth                                      dtInicioExperiencia = experiencia.addField("inicio", STypeYearMonth.class, true);
        STypeYearMonth                                      dtFimExperiencia    = experiencia.addField("fim", STypeYearMonth.class);
        STypeString                                         empresa             = experiencia.addFieldString("empresa", true);
        STypeString                                         atividades          = experiencia.addFieldString("atividades");

        STypeList<STypeComposite<SIComposite>, SIComposite> cargos = experiencia.addFieldListOfComposite("cargos", "cargo");
        STypeComposite<?> cargo = cargos.getElementsType();
        STypeString nome = cargo.addFieldString("nome", true);
        STypeYearMonth dtInicioCargo = cargo.addField("inicio", STypeYearMonth.class, true);
        STypeYearMonth dtFimCargo = cargo.addField("fim", STypeYearMonth.class);


        STypeList<STypeComposite<SIComposite>, SIComposite> pets = cargo.addFieldListOfComposite("pets", "pet");
        STypeComposite<?> pet = pets.getElementsType();
        STypeString nomeDoPet = pet.addFieldString("nome", true);
        STypeString tipoDoPet = pet.addFieldString("tipo", true);
        tipoDoPet.selectionOf("Gatinho", "Cachorrinho", "Papagaio");
        STypeInteger idadePet = pet.addFieldInteger("idade");

        {
            //@destacar:bloco
            experiencias
                    .withView(SViewListByMasterDetail::new)
                    .asAtr().label("Experiências profissionais");
            //@destacar:fim
            dtInicioExperiencia
                    .asAtr().label("Data inicial")
                    .asAtrBootstrap().colPreference(2);
            dtFimExperiencia
                    .asAtr().label("Data final")
                    .asAtrBootstrap().colPreference(2);
            empresa
                    .asAtr().label("Empresa")
                    .asAtrBootstrap().colPreference(8);
            //@destacar:bloco
            cargos
                    .withView(SViewListByMasterDetail::new)
                    .asAtr().label("Cargos na empresa");
            dtInicioCargo
                    .asAtr().label("Data inicial")
                    .asAtrBootstrap().colPreference(4);
            dtFimCargo
                    .asAtr().label("Data final")
                    .asAtrBootstrap().colPreference(4);
            nome
                    .asAtr().label("Nome")
                    .asAtrBootstrap().colPreference(4);
            pets
                    .withView(new SViewListByMasterDetail()
                            .col(nomeDoPet)
                            .col(tipoDoPet))
                    .asAtr()
                    .label("Animais de estimação no trabalho");
            nomeDoPet
                    .asAtr().label("Nome")
                    .asAtrBootstrap().colPreference(4);
            tipoDoPet
                    .withSelectView()
                    .asAtr().label("Tipo")
                    .asAtrBootstrap().colPreference(4);
            idadePet
                    .asAtr().label("Idade")
                    .asAtrBootstrap().colPreference(4);
            //@destacar:fim
            atividades
                    .withTextAreaView()
                    .asAtr().label("Atividades Desenvolvidas");
        }

    }
}
