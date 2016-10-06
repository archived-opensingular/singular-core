package org.opensingular.singular.exemplos.notificacaosimplificada.form;

import org.opensingular.singular.exemplos.notificacaosimplificada.form.vocabulario.STypeEmbalagemPrimaria;
import org.opensingular.singular.exemplos.notificacaosimplificada.form.vocabulario.STypeEmbalagemSecundaria;
import org.opensingular.singular.exemplos.notificacaosimplificada.form.vocabulario.STypeUnidadeMedida;
import org.opensingular.singular.exemplos.notificacaosimplificada.service.DominioService;
import org.opensingular.singular.form.SIComposite;
import org.opensingular.singular.form.SInfoType;
import org.opensingular.singular.form.SInstance;
import org.opensingular.singular.form.SType;
import org.opensingular.singular.form.STypeAttachmentList;
import org.opensingular.singular.form.STypeComposite;
import org.opensingular.singular.form.STypeList;
import org.opensingular.singular.form.STypeSimple;
import org.opensingular.singular.form.TypeBuilder;
import org.opensingular.singular.form.type.core.STypeInteger;
import org.opensingular.singular.form.type.core.attachment.STypeAttachment;
import org.opensingular.singular.form.util.transformer.Value;
import org.opensingular.singular.form.view.SViewListByMasterDetail;

import java.util.Optional;

@SInfoType(spackage = SPackageNotificacaoSimplificada.class)
public class STypeAcondicionamento extends STypeComposite<SIComposite> {


    public STypeEmbalagemPrimaria                       embalagemPrimaria;
    public STypeEmbalagemSecundaria                     embalagemSecundaria;
    public STypeInteger                                 quantidade;
    public STypeAttachmentList                          estudosEstabilidade;
    public STypeAttachmentList                          layoutsRotulagem;
    public STypeList<STypeLocalFabricacao, SIComposite> locaisFabricacao;
    public STypeInteger                                 prazoValidade;
    public STypeUnidadeMedida                           unidadeMedida;
    public STypeAttachmentList                          laudosControle;

    static DominioService dominioService(SInstance ins) {
        return ins.getDocument().lookupService(DominioService.class);
    }

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);


        embalagemPrimaria = this.addField("embalagemPrimaria", STypeEmbalagemPrimaria.class);
        embalagemPrimaria.asAtr().displayString("${descricao}");
        embalagemSecundaria = this.addField("embalagemSecundaria", STypeEmbalagemSecundaria.class);

        quantidade = this.addFieldInteger("quantidade", true);
        quantidade
                .asAtrBootstrap()
                .colPreference(3)
                .asAtr()
                .label("Quantidade");

        unidadeMedida = this.addField("unidadeMedida", STypeUnidadeMedida.class);


        prazoValidade = this.addFieldInteger("prazoValidade", true);
        prazoValidade
                .asAtr().label("Prazo de validade (meses)")
                .asAtrBootstrap().colPreference(3);

        estudosEstabilidade = this.addFieldListOfAttachment("estudosEstabilidade", "estudoEstabilidade");

        estudosEstabilidade.asAtr()
                .label("Estudo de estabilidade")
                .displayString("<#list _inst as c>${c.name}<#sep>, </#sep></#list>");
        {

            STypeAttachment f = estudosEstabilidade.getElementsType();
            SType<?> nomeArquivo = (STypeSimple) f.getField(f.FIELD_NAME);
            nomeArquivo.asAtr().label("Nome do Arquivo");
        }

        laudosControle = this.addFieldListOfAttachment("laudosControle", "laudoControle");

        laudosControle.asAtr()
                .visible(false)
                .label("Laudo de controle dos insumos ativos e do produto acabado")
                .displayString("<#list _inst as c>${c.name}<#sep>, </#sep></#list>");
        {

            STypeAttachment f = laudosControle.getElementsType();
            SType<?> nomeArquivo = (STypeSimple) f.getField(f.FIELD_NAME);
            nomeArquivo.asAtr().label("Nome do Arquivo");
        }

        {
            layoutsRotulagem = this.addFieldListOfAttachment("layoutsRotulagem", "layoutRotulagem");
            layoutsRotulagem.asAtr().label("Layout da rotulagem");

            STypeAttachment f = layoutsRotulagem.getElementsType();
            SType<?> nomeArquivo = (STypeSimple) f.getField(f.FIELD_NAME);
            nomeArquivo.asAtr().label("Nome do Arquivo");
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
                .asAtr().label("Local de fabricação");


    }
}
