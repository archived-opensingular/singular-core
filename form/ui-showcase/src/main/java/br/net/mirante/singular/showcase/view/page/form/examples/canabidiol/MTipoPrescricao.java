package br.net.mirante.singular.showcase.view.page.form.examples.canabidiol;

import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MInfoTipo;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.MTipoLista;
import br.net.mirante.singular.form.mform.TipoBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.core.MTipoBoolean;
import br.net.mirante.singular.form.mform.core.attachment.MTipoAttachment;
import org.apache.commons.lang3.BooleanUtils;

@MInfoTipo(nome = "MTipoPrescricao", pacote = MPacotePeticaoCanabidiol.class)
public class MTipoPrescricao extends MTipoComposto<MIComposto> implements CanabidiolUtil {

    @Override
    protected void onCargaTipo(TipoBuilder tb) {
        super.onCargaTipo(tb);

        this.addCampo("medico", MTipoMedico.class)
                .as(AtrBasic::new)
                .label("Médico Prescritor");

        this.addCampo("cid", MTipoCID.class)
                .as(AtrBasic::new)
                .label("CID-10")
                .subtitle("selecione CID-10 da doença de base, ou seja o motivo principal da solicitação do produto");

        MTipoBoolean outrosCids = this.addCampoBoolean("outrosCids");
        outrosCids
                .as(AtrBasic::new)
                .label("Outros CID-10 associados?");


        MTipoLista<MTipoCID, MIComposto> listaCids = this.addCampoListaOf("cids", MTipoCID.class);
        listaCids
                .as(AtrBasic::new)
                .label("Outros CIDs")
                .dependsOn(outrosCids)
                .visivel(false)
                .visivel(inst -> BooleanUtils.isTrue(getValue(inst, outrosCids)));

        MTipoAttachment receitaMedica = this
                .addCampo("receitaMedica", MTipoAttachment.class);
        receitaMedica
                .as(AtrBasic::new)
                .label("Receita Médica")
                .subtitle("Deve conter: nome do paciente, nome comercial do produto, posologia, quantitativo necessário, tempo de tratamento, data, assinatura e carimbo do prescritor (com nº do CRM).");

        MTipoAttachment laudoMedico = this
                .addCampo("laudoMedico", MTipoAttachment.class);
        laudoMedico
                .as(AtrBasic::new)
                .label("Laudo Médico")
                .subtitle("Deve conter: CID, nome da doença, descrição do caso, tratamentos anteriores e justificativa para a utilização de produto não registrado no Brasil em comparação com as alternativas terapêuticas já existentes e registradas pela Anvisa.");

    }


}
