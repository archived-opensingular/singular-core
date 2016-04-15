/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.exemplos.canabidiol;

import org.apache.commons.lang3.BooleanUtils;

import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SInfoType;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.STypeList;
import br.net.mirante.singular.form.mform.TypeBuilder;
import br.net.mirante.singular.form.mform.core.STypeBoolean;
import br.net.mirante.singular.form.mform.core.attachment.STypeAttachment;
import br.net.mirante.singular.form.mform.util.transformer.Value;

@SInfoType(spackage = SPackagePeticaoCanabidiol.class)
public class STypePrescricao extends STypeComposite<SIComposite> {

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);

        this.addField("medico", STypeMedico.class)
                .asAtrBasic()
                .label("Médico Prescritor")
                .asAtrAnnotation().setAnnotated();

        this.addField("cid", STypeCID.class)
                .asAtrBasic()
                .label("CID-10")
                .subtitle("selecione CID-10 da doença de base, ou seja o motivo principal da solicitação do produto")
                .asAtrAnnotation().setAnnotated();

        STypeBoolean outrosCids = this.addFieldBoolean("outrosCids");
        outrosCids
                .asAtrBasic()
                .label("Outros CID-10 associados?");


        STypeList<STypeCID, SIComposite> listaCids = this.addFieldListOf("cids", STypeCID.class);
        listaCids
                .asAtrBasic()
                .label("Outros CIDs")
                .dependsOn(outrosCids)

                .visible(inst -> BooleanUtils.isTrue(Value.of(inst, outrosCids)));

        STypeAttachment receitaMedica = this
                .addField("receitaMedica", STypeAttachment.class);
        receitaMedica
                .asAtrBasic()
                .required()
                .asAtrBasic()
                .label("Receita Médica")
                .subtitle("Deve conter: nome do paciente, nome comercial do produto, posologia, quantitativo necessário, tempo de tratamento, data, assinatura e carimbo do prescritor (com nº do CRM).");

        STypeAttachment laudoMedico = this
                .addField("laudoMedico", STypeAttachment.class);
        laudoMedico
                .asAtrBasic()
                .required()
                .asAtrBasic()
                .label("Laudo Médico")
                .subtitle("Deve conter: CID, nome da doença, descrição do caso, tratamentos anteriores e justificativa para a utilização de produto não registrado no Brasil em comparação com as alternativas terapêuticas já existentes e registradas pela Anvisa.");

    }


}
