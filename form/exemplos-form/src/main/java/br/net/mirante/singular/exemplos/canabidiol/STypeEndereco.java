/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.exemplos.canabidiol;

import br.net.mirante.singular.exemplos.SelectBuilder;
import br.net.mirante.singular.exemplos.SelectBuilder.CidadeDTO;
import br.net.mirante.singular.exemplos.SelectBuilder.EstadoDTO;
import br.net.mirante.singular.form.mform.*;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.mform.util.transformer.Value;


@SInfoType(spackage = SPackagePeticaoCanabidiol.class)
public class STypeEndereco extends STypeComposite<SIComposite> {


    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);


        this.addFieldString("logradouro")
                .asAtrBasic()
                .required()
                .asAtrBasic()
                .label("Logradouro")
                .asAtrBootstrap()
                .colPreference(5);

        this.addFieldString("complemento")
                .asAtrBasic()
                .label("Complemento")
                .asAtrBootstrap()
                .colPreference(5);

        this.addFieldString("numero")
                .asAtrBasic()
                .label("Número")
                .asAtrBootstrap()
                .colPreference(2);

        this.addFieldString("bairro")
                .asAtrBasic()
                .label("Bairro")
                .asAtrBootstrap()
                .colPreference(4);

        addFieldCEP("CEP")
                .asAtrBasic()
                .label("CEP")
                .asAtrBootstrap()
                .colPreference(2);

        STypeComposite<?> estado = addFieldComposite("estado");
        estado
                .asAtrBasic()
                .required()
                .asAtrBasic()
                .label("Estado")
                .asAtrBootstrap()
                .colPreference(3);
        STypeString siglaUF = estado.addFieldString("sigla");
        STypeString nomeUF = estado.addFieldString("nome");

        estado.selectionOf(EstadoDTO.class)
                .id(EstadoDTO::getSigla)
                .display("${nome} - ${sigla}")
                .autoConverter(EstadoDTO.class)
                .simpleProvider(ins ->  SelectBuilder.buildEstados());

        STypeComposite<?> cidade = addFieldComposite("cidade");
        cidade
                .asAtrBasic()
                .required(inst -> Value.notNull(inst, (STypeSimple) estado.getField(siglaUF)))
                .asAtrBasic()
                .label("Cidade")
                .visible(inst -> Value.notNull(inst, (STypeSimple) estado.getField(siglaUF)))
                .dependsOn(estado)
                .asAtrBootstrap()
                .colPreference(3);
        cidade.addFieldInteger("id");
        STypeString nomeCidade = cidade.addFieldString("nome");
        cidade.addFieldString("UF");

        cidade.selectionOf(CidadeDTO.class)
                .id(CidadeDTO::getId)
                .display(CidadeDTO::getNome)
                .autoConverter(CidadeDTO.class)
                .simpleProvider(i -> SelectBuilder.buildMunicipiosFiltrado((String) Value.of(i, (STypeSimple) estado.getField(siglaUF.getNameSimple()))));

    }
}
