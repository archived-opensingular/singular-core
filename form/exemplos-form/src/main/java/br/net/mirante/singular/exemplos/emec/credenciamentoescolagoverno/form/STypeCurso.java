/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package br.net.mirante.singular.exemplos.emec.credenciamentoescolagoverno.form;

import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.SInfoType;
import br.net.mirante.singular.form.STypeComposite;
import br.net.mirante.singular.form.STypeList;
import br.net.mirante.singular.form.TypeBuilder;
import br.net.mirante.singular.form.view.SViewListByMasterDetail;

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
