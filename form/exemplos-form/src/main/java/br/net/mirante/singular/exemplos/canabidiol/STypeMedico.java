/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.exemplos.canabidiol;

import br.net.mirante.singular.exemplos.SelectBuilder;
import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SInfoType;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.TypeBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.ui.AtrBootstrap;
import br.net.mirante.singular.form.mform.core.AtrCore;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.mform.core.annotation.AtrAnnotation;
import br.net.mirante.singular.form.mform.options.SOptionsProvider;


@SInfoType(spackage = SPackagePeticaoCanabidiol.class)
public class STypeMedico extends STypeComposite<SIComposite> {

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);

        this
                .addFieldString("nome")
                .as(AtrCore::new)
                .obrigatorio()
                .as(AtrBasic::new)
                .label("Nome do Médico")
                .as(AtrBootstrap::new)
                .colPreference(6);

        this
                .addFieldString("CRM")
                .as(AtrCore::new)
                .obrigatorio()
                .as(AtrBasic::new)
                .label("Número do CRM")
                .as(AtrBootstrap::new)
                .colPreference(3);

        STypeComposite<?> estado = this.addFieldComposite("UFCRM");
        estado
                .as(AtrCore::new)
                .obrigatorio()
                .as(AtrBasic::new)
                .label("UF do CRM")
                .as(AtrBootstrap::new)
                .colPreference(3);
        estado.addFieldString("sigla");
        STypeString nomeUF = estado.addFieldString("nome");
        estado
                .withSelectionFromProvider(nomeUF, (SOptionsProvider) inst -> SelectBuilder.buildEstados(estado));

        this.addFieldCPF("cpf")
                .as(AtrBasic::new)
                .label("CPF")
                .as(AtrBootstrap::new)
                .colPreference(3);


        this.addField("endereco", STypeEndereco.class)
                .as(AtrBasic::new)
                .label("Endereço")
                .as(AtrAnnotation::new).setAnnotated();

        STypeContato tipoTelefone = this.addField("contato", STypeContato.class);
        tipoTelefone
                .telefoneFixo
                .as(AtrCore::new)
                .obrigatorio()
                .as(AtrAnnotation::new).setAnnotated();

    }
}
