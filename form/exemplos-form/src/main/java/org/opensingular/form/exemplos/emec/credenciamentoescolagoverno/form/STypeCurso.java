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
package org.opensingular.form.exemplos.emec.credenciamentoescolagoverno.form;

import org.opensingular.form.SIComposite;
import org.opensingular.form.SInfoType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.STypeList;
import org.opensingular.form.TypeBuilder;
import org.opensingular.form.view.SViewListByMasterDetail;

@SInfoType(spackage = SPackageCredenciamentoEscolaGoverno.class)
public class STypeCurso extends STypeComposite<SIComposite>{

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);
        
        addFieldString("grau", true)
            .selectionOf("Sequencial", "Bacharelado", "Licenciatura", "Curso Superior de Tecnologia")
            .withRadioView()
            .asAtr().label("Grau")
            .asAtrBootstrap().maxColPreference();
        addFieldString("denominacao", true)
            .selectionOf("Ciência da Computação", "Física", "Matemática")
            .asAtr().label("Denominação do Curso")
            .asAtrBootstrap().colPreference(9);
        addFieldInteger("anoPretendido")
            .asAtr().required().label("Ano Pretendido")
            .asAtrBootstrap().colPreference(3);
        
        final STypeList<STypeComposite<SIComposite>, SIComposite> turnos = addFieldListOfComposite("turnosFuncionamento", "turnoFuncionamento");
        turnos.withView(SViewListByMasterDetail::new).asAtr().required().label("Turno de Funcionamento");
        
        final STypeComposite<SIComposite> turno = turnos.getElementsType();
        turno.addFieldInteger("vagasAno", true)
            .asAtr().label("Nº Total Vagas ao Ano")
            .asAtrBootstrap().colPreference(4);
        turno.addFieldInteger("cargaHorariaCurso", true)
            .asAtr().label("Carga Horária do Curso")
            .asAtrBootstrap().colPreference(4);
        turno.addFieldString("turno", true)
            .selectionOf("Matutino", "Vespertino", "Noturno")
            .withSelectView()
            .asAtr().label("Turno")
            .asAtrBootstrap().colPreference(4);
        turno.addFieldInteger("periodo", true)
            .asAtr().label("Período")
            .asAtrBootstrap().colPreference(4);
        turno.addFieldString("periodicidadeIntegralizacao", true)
            .selectionOf("Semestral", "Anual")
            .withSelectView()
            .asAtr().label("Periodicidade para Integralização")
            .asAtrBootstrap().colPreference(4);
    }
}
