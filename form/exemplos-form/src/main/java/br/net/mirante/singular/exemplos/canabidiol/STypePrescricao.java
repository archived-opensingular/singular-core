package br.net.mirante.singular.exemplos.canabidiol;

import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.MInfoTipo;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.STypeLista;
import br.net.mirante.singular.form.mform.TypeBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.core.AtrCore;
import br.net.mirante.singular.form.mform.core.STypeBoolean;
import br.net.mirante.singular.form.mform.core.annotation.AtrAnnotation;
import br.net.mirante.singular.form.mform.core.attachment.STypeAttachment;
import br.net.mirante.singular.form.mform.util.transformer.Value;
import org.apache.commons.lang3.BooleanUtils;

@MInfoTipo(nome = "MTipoPrescricao", pacote = SPackagePeticaoCanabidiol.class)
public class STypePrescricao extends STypeComposite<SIComposite> {

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);

        this.addCampo("medico", STypeMedico.class)
                .as(AtrBasic::new)
                .label("Médico Prescritor")
                .as(AtrAnnotation::new).setAnnotated();

        this.addCampo("cid", STypeCID.class)
                .as(AtrBasic::new)
                .label("CID-10")
                .subtitle("selecione CID-10 da doença de base, ou seja o motivo principal da solicitação do produto")
                .as(AtrAnnotation::new).setAnnotated();

        STypeBoolean outrosCids = this.addCampoBoolean("outrosCids");
        outrosCids
                .as(AtrBasic::new)
                .label("Outros CID-10 associados?");


        STypeLista<STypeCID, SIComposite> listaCids = this.addCampoListaOf("cids", STypeCID.class);
        listaCids
                .as(AtrBasic::new)
                .label("Outros CIDs")
                .dependsOn(outrosCids)

                .visivel(inst -> BooleanUtils.isTrue(Value.of(inst, outrosCids)));

        STypeAttachment receitaMedica = this
                .addCampo("receitaMedica", STypeAttachment.class);
        receitaMedica
                .as(AtrCore::new)
                .obrigatorio()
                .as(AtrBasic::new)
                .label("Receita Médica")
                .subtitle("Deve conter: nome do paciente, nome comercial do produto, posologia, quantitativo necessário, tempo de tratamento, data, assinatura e carimbo do prescritor (com nº do CRM).");

        STypeAttachment laudoMedico = this
                .addCampo("laudoMedico", STypeAttachment.class);
        laudoMedico
                .as(AtrCore::new)
                .obrigatorio()
                .as(AtrBasic::new)
                .label("Laudo Médico")
                .subtitle("Deve conter: CID, nome da doença, descrição do caso, tratamentos anteriores e justificativa para a utilização de produto não registrado no Brasil em comparação com as alternativas terapêuticas já existentes e registradas pela Anvisa.");

    }


}
