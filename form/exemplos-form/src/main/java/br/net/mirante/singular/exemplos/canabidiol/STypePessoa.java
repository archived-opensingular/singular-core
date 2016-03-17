/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.exemplos.canabidiol;

import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SInfoType;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.TypeBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.ui.AtrBootstrap;
import br.net.mirante.singular.form.mform.core.AtrCore;
import br.net.mirante.singular.form.mform.core.annotation.AtrAnnotation;
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
                .as(AtrCore::new)
                .obrigatorio()
                .as(AtrBasic::new)
                .label("Nome")
                .as(AtrBootstrap::new).colPreference(6);

        this
                .addFieldDate("dataNascimento")
                .as(AtrCore::new)
                .obrigatorio()
                .as(AtrBasic::new)
                .label("Data de Nascimento")
                .as(AtrBootstrap::new).colPreference(3);

        //Criacção de tipos withselction fica muito truncada caso seja necessário manter a referencia ao objeto para interactions e para adicionar atributos.

        //ruim: Para  adicionar atributos não é possivel adicionar selection
        //ruim: Para manter a referencia não pode acionar atributos:
        tipoDocumento = this.addField("tipoDocumento", STypeDocumentoSelect.class);
        tipoDocumento
                .as(AtrCore::new)
                .obrigatorio()
                .as(AtrBasic::new)
                .label(LABEL_TIPO_DOCUMENTO)
                .as(AtrBootstrap::new)
                .colPreference(6);

        this
                .addFieldString("nomeNoDocumento")
                .as(AtrCore::new)
                .obrigatorio(ins -> "55358729".equals(Value.of(ins, tipoDocumento)))
                .as(AtrBasic::new)
                .label("Nome")
                .visivel(ins -> "55358729".equals(Value.of(ins, tipoDocumento)))
                .dependsOn(tipoDocumento)
                .as(AtrBootstrap::new)
                .colPreference(3);

        this
                .addFieldString("numeroDocumento")
                .as(AtrCore::new)
                .obrigatorio()
                .as(AtrBasic::new)
                .label("Número")
                .visivel(ins -> ins.findNearestValue(tipoDocumento).orElse(null) != null)
                .dependsOn(tipoDocumento)
                .as(AtrBootstrap::new).colPreference(2);

        this.addFieldCPF("cpf")
                .as(AtrCore::new)
                .obrigatorio()
                .as(AtrBasic::new)
                .label("CPF")
                .as(AtrBootstrap::new)
                .colPreference(3);


        this.addFieldString("passaporte")
                .as(AtrBasic::new)
                .label("Número do Passaporte")
                .as(AtrBootstrap::new)
                .colPreference(3);

        this.addField("endereco", STypeEndereco.class)
                .as(AtrBasic::new)
                .label("Endereço")
                .as(AtrAnnotation::new).setAnnotated();

        this.addField("contato", STypeContato.class)
                .as(AtrAnnotation::new).setAnnotated();
    }

}
