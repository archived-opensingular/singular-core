/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.exemplos.canabidiol;

import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SInfoType;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.TypeBuilder;
import br.net.mirante.singular.form.mform.util.transformer.Value;

@SInfoType(spackage = SPackagePeticaoCanabidiol.class)
public class STypePessoa extends STypeComposite<SIComposite> {

    public static final String LABEL_TIPO_DOCUMENTO = "Documento de Identificação Oficial";
    public STypeDocumentoSelect tipoDocumento;

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);

        this
                .addFieldString("nome")
                .asAtrCore()
                .required()
                .asAtrBasic()
                .label("Nome")
                .asAtrBootstrap().colPreference(6);

        this
                .addFieldDate("dataNascimento")
                .asAtrCore()
                .required()
                .asAtrBasic()
                .label("Data de Nascimento")
                .asAtrBootstrap().colPreference(3);

        //Criacção de tipos withselction fica muito truncada caso seja necessário manter a referencia ao objeto para interactions e para adicionar atributos.

        //ruim: Para  adicionar atributos não é possivel adicionar selection
        //ruim: Para manter a referencia não pode acionar atributos:
        tipoDocumento = this.addField("tipoDocumento", STypeDocumentoSelect.class);
        tipoDocumento
                .asAtrCore()
                .required()
                .asAtrBasic()
                .label(LABEL_TIPO_DOCUMENTO)
                .asAtrBootstrap()
                .colPreference(6);

        this
                .addFieldString("nomeNoDocumento")
                .asAtrCore()
                .required(ins -> "55358729".equals(Value.of(ins, tipoDocumento)))
                .asAtrBasic()
                .label("Nome")
                .visivel(ins -> "55358729".equals(Value.of(ins, tipoDocumento)))
                .dependsOn(tipoDocumento)
                .asAtrBootstrap()
                .colPreference(3);

        this
                .addFieldString("numeroDocumento")
                .asAtrCore()
                .required()
                .asAtrBasic()
                .label("Número")
                .visivel(ins -> ins.findNearestValue(tipoDocumento).orElse(null) != null)
                .dependsOn(tipoDocumento)
                .asAtrBootstrap().colPreference(2);

        addFieldCPF("cpf")
                .asAtrCore()
                .required()
                .asAtrBasic()
                .label("CPF")
                .asAtrBootstrap()
                .colPreference(3);


        this.addFieldString("passaporte")
                .asAtrBasic()
                .label("Número do Passaporte")
                .asAtrBootstrap()
                .colPreference(3);

        this.addField("endereco", STypeEndereco.class)
                .asAtrBasic()
                .label("Endereço")
                .asAtrAnnotation().setAnnotated();

        this.addField("contato", STypeContato.class)
                .asAtrAnnotation().setAnnotated();
    }

}
