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
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.mform.options.SOptionsProvider;


@SInfoType(spackage = SPackagePeticaoCanabidiol.class)
public class STypeMedico extends STypeComposite<SIComposite> {

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);

        this
                .addFieldString("nome")
                .asAtrBasic()
                .required()
                .asAtrBasic()
                .label("Nome do Médico")
                .asAtrBootstrap()
                .colPreference(6);

        this
                .addFieldString("CRM")
                .asAtrBasic()
                .required()
                .asAtrBasic()
                .label("Número do CRM")
                .asAtrBootstrap()
                .colPreference(3);

        STypeComposite<?> estado = addFieldComposite("UFCRM");
        estado
                .asAtrBasic()
                .required()
                .asAtrBasic()
                .label("UF do CRM")
                .asAtrBootstrap()
                .colPreference(3);
        estado.addFieldString("sigla");
        STypeString nomeUF = estado.addFieldString("nome");
        estado
                .withSelectionFromProvider(nomeUF, (SOptionsProvider) (inst, f) -> SelectBuilder.buildEstados(estado));

        addFieldCPF("cpf")
                .asAtrBasic()
                .label("CPF")
                .asAtrBootstrap()
                .colPreference(3);


        this.addField("endereco", STypeEndereco.class)
                .asAtrBasic()
                .label("Endereço")
                .asAtrAnnotation().setAnnotated();

        STypeContato tipoTelefone = this.addField("contato", STypeContato.class);
        tipoTelefone
                .telefoneFixo
                .asAtrBasic()
                .required()
                .asAtrAnnotation().setAnnotated();

    }
}
