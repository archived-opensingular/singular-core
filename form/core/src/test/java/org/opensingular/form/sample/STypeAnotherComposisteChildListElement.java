/*
 *
 *  * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.opensingular.form.sample;

import org.opensingular.form.SIComposite;
import org.opensingular.form.SIList;
import org.opensingular.form.SInfoType;
import org.opensingular.form.SInstance;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.STypeList;
import org.opensingular.form.TypeBuilder;
import org.opensingular.form.view.SViewListByMasterDetail;

import javax.annotation.Nonnull;
import java.util.Optional;

@SInfoType(spackage = FormTestPackage.class, newable = false, name = "STypeAnotherComposisteChildListElement")
public class STypeAnotherComposisteChildListElement extends STypeComposite<SIComposite> {


    public CrossReferenceComposite                                 crossReferenceComposite;
    public STypeList<STypeAnotherComposisteNestedListElement, SIComposite> horariosTarifas;

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onLoadType(@Nonnull TypeBuilder tb) {

        crossReferenceComposite = this.addField("crossReferenceComposite", CrossReferenceComposite.class);
        crossReferenceComposite.selection()
                .id(crossReferenceComposite.identificador)
                .display(crossReferenceComposite.nome)
                .simpleProvider(builder -> {
                    findEmbarcacoesList(builder.getCurrentInstance()).forEach(embarcacao -> {
                        builder.add()
                                .set(crossReferenceComposite.identificador, embarcacao.findField(STypeFirstListElement.class, e -> e.identificador).map(SInstance::getValue).orElse(null))
                                .set(crossReferenceComposite.nome, embarcacao.findField(STypeFirstListElement.class, e -> e.nome).map(SInstance::getValue).orElse(null));
                    });
                });
        crossReferenceComposite
                .asAtr()
                .dependsOn(STypeCompositeWithListField.class, sTypeEmbarcacoes -> sTypeEmbarcacoes.theList);


        horariosTarifas = this.addFieldListOf("horariosTarifas", STypeAnotherComposisteNestedListElement.class);
        horariosTarifas.withView(
                new SViewListByMasterDetail()
                        .col("Partida", "${_inst.partida.pais!''} - ${_inst.partida.uf!''} - ${_inst.partida.municipio!''} - ${_inst.partida.localAtracacao!''} - ${_inst.partida.diaSemana!''} - ${_inst.partida.horario!''}")
                        .col("Chegada", "${_inst.chegada.pais!''} - ${_inst.chegada.uf!''} - ${_inst.chegada.municipio!''} - ${_inst.chegada.localAtracacao!''} - ${_inst.chegada.diaSemana!''} - ${_inst.chegada.horario!''}")
        );

        this
                .asAtr()
                .displayString("Esquema para Embarcação: ${(crossReferenceComposite.nome)!}")
                .dependsOn(crossReferenceComposite)
                .asAtrAnnotation()
                .setAnnotated();

    }

    @SuppressWarnings("unchecked")
    private SIList<SIComposite> findEmbarcacoesList(SInstance instance) {
        Optional<SIComposite> embarcacoes = instance.findNearest(STypeCompositeWithListField.class);
        if (embarcacoes.isPresent()) {
            return (SIList<SIComposite>) embarcacoes.get().getField(STypeCompositeWithListField.EMBARCACOES_FIELD_NAME);
        }
        return null;
    }

}
