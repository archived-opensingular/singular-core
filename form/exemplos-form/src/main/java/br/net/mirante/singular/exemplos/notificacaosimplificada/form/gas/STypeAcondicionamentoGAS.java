package br.net.mirante.singular.exemplos.notificacaosimplificada.form.gas;

import br.net.mirante.singular.exemplos.notificacaosimplificada.form.SPackageNotificacaoSimplificada;
import br.net.mirante.singular.exemplos.notificacaosimplificada.form.STypeLocalFabricacao;
import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SInfoType;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.STypeAttachmentList;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.STypeList;
import br.net.mirante.singular.form.mform.STypeSimple;
import br.net.mirante.singular.form.mform.TypeBuilder;
import br.net.mirante.singular.form.mform.basic.view.SViewListByMasterDetail;
import br.net.mirante.singular.form.mform.core.attachment.STypeAttachment;
import br.net.mirante.singular.form.mform.util.transformer.Value;

import java.util.Optional;

@SInfoType(spackage = SPackageNotificacaoSimplificada.class)
public class STypeAcondicionamentoGAS extends STypeComposite<SIComposite> {

    public SType embalagemPrimaria;
    public STypeAttachmentList layoutsRotulagem;
    public STypeList<STypeLocalFabricacao, SIComposite> locaisFabricacao;


    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);


        embalagemPrimaria = this.addFieldString("embalagemPrimaria");
        ((STypeSimple) embalagemPrimaria)
                .withSelectView()
                .withSelection()
                .add("Cilindro")
                .add("Tanque")
                .add("Caminhão Tanque");
        embalagemPrimaria
                .asAtrBasic()
                .displayString("${descricao}")
                .label("Emabalagem Primária")
                .asAtrBootstrap()
                .colPreference(4);


        {
            layoutsRotulagem = this.addFieldListOfAttachment("layoutsRotulagem", "layoutRotulagem");
            layoutsRotulagem.asAtrBasic().label("Layout da rotulagem");

            STypeAttachment f = layoutsRotulagem.getElementsType();
            SType<?> nomeArquivo = (STypeSimple) f.getField(f.FIELD_NAME);
            nomeArquivo.asAtrBasic().label("Nome do Arquivo");
        }


        locaisFabricacao = this.addFieldListOf("locaisFabricacao", STypeLocalFabricacao.class);
        locaisFabricacao
                .withView(new SViewListByMasterDetail()
                        .col(locaisFabricacao.getElementsType().tipoLocalFabricacao)
                        .col(locaisFabricacao.getElementsType(), i -> {
                            String label = String.valueOf(Optional.ofNullable(Value.of(i, "outroLocalFabricacao.razaoSocial")).orElse(""));
                            label += String.valueOf(Optional.ofNullable(Value.of(i, "empresaTerceirizada.empresa.razaoSocial")).orElse(""));
                            label += String.valueOf(Optional.ofNullable(Value.of(i, "empresaInternacional.razaoSocial")).orElse(""));
                            label += String.valueOf(Optional.ofNullable(Value.of(i, "empresaPropria.razaoSocial")).orElse(""));
                            return label;
                        }).col(locaisFabricacao.getElementsType().empresaTerceirizada.etapasFabricacao()))
                .asAtrBasic().label("Local de fabricação");


    }

}
