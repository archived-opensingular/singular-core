package br.net.mirante.singular.exemplos.notificacaosimplificada.baixocusto;

import br.net.mirante.singular.exemplos.notificacaosimplificada.domain.UnidadeMedida;
import br.net.mirante.singular.exemplos.notificacaosimplificada.service.DominioService;
import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SIList;
import br.net.mirante.singular.form.mform.SInfoType;
import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.STypeAttachmentList;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.STypeList;
import br.net.mirante.singular.form.mform.STypeSimple;
import br.net.mirante.singular.form.mform.TypeBuilder;
import br.net.mirante.singular.form.mform.basic.view.SViewAutoComplete;
import br.net.mirante.singular.form.mform.basic.view.SViewListByMasterDetail;
import br.net.mirante.singular.form.mform.core.STypeInteger;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.mform.core.attachment.STypeAttachment;
import br.net.mirante.singular.form.mform.util.transformer.Value;

import java.util.Optional;

@SInfoType(spackage = SPackageNotificacaoSimplificada.class)
public class STypeAcondicionamento extends STypeComposite<SIComposite> {


    STypeEmbalagemPrimaria embalagemPrimaria;
    STypeEmbalagemSecundaria embalagemSecundaria;
    STypeInteger quantidade;
    STypeComposite<SIComposite> unidadeMedida;
    STypeString idUnidadeMedida;
    STypeString descricaoUnidadeMedida;
    STypeAttachmentList estudosEstabilidade;
    STypeAttachmentList layoutsRotulagem;
    STypeList<STypeLocalFabricacao, SIComposite> locaisFabricacao;
    STypeInteger prazoValidade;

    static DominioService dominioService(SInstance ins) {
        return ins.getDocument().lookupService(DominioService.class);
    }

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);


        embalagemPrimaria = (STypeEmbalagemPrimaria) this.addField("embalagemPrimaria", STypeEmbalagemPrimaria.class);
        embalagemSecundaria = (STypeEmbalagemSecundaria) this.addField("embalagemSecundaria", STypeEmbalagemSecundaria.class);

        quantidade = this.addFieldInteger("quantidade", true);
        quantidade
                .asAtrBootstrap()
                .colPreference(3)
                .asAtrBasic()
                .label("Quantidade");

        unidadeMedida = this.addFieldComposite("unidadeMedida");
        idUnidadeMedida = unidadeMedida.addFieldString("id");
        descricaoUnidadeMedida = unidadeMedida.addFieldString("descricao");
        unidadeMedida
                .asAtrBootstrap()
                .colPreference(3)
                .asAtrBasic()
                .label("Unidade de medida")
                .getTipo().setView(SViewAutoComplete::new);
        unidadeMedida.withSelectionFromProvider(descricaoUnidadeMedida, (ins, filter) -> {
            final SIList<?> list = ins.getType().newList();
            for (UnidadeMedida um : dominioService(ins).unidadesMedida(filter)) {
                final SIComposite c = (SIComposite) list.addNew();
                c.setValue(idUnidadeMedida, um.getId());
                c.setValue(descricaoUnidadeMedida, um.getDescricao());
            }
            return list;
        });

        estudosEstabilidade = this.addFieldListOfAttachment("estudosEstabilidade", "estudoEstabilidade");

        estudosEstabilidade.asAtrBasic()
                .label("Estudo de estabilidade")
                .displayString("<#list _inst as c>${c.name}<#sep>, </#sep></#list>");
        {

            STypeAttachment f = estudosEstabilidade.getElementsType();
            SType<?> nomeArquivo = (STypeSimple) f.getField(f.FIELD_NAME);
            nomeArquivo.asAtrBasic().label("Nome do Arquivo");
        }

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


        prazoValidade = this.addFieldInteger("prazoValidade", true);
        prazoValidade.asAtrBasic().label("Prazo de validade (meses)");

    }
}
