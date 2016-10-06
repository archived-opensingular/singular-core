package org.opensingular.singular.exemplos.notificacaosimplificada.form.gas;

import org.opensingular.singular.exemplos.notificacaosimplificada.form.SPackageNotificacaoSimplificada;
import org.opensingular.singular.exemplos.notificacaosimplificada.form.STypeLocalFabricacao;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SInfoType;
import org.opensingular.form.SType;
import org.opensingular.form.STypeAttachmentList;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.STypeList;
import org.opensingular.form.STypeSimple;
import org.opensingular.form.TypeBuilder;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.type.core.attachment.STypeAttachment;
import org.opensingular.form.util.transformer.Value;
import org.opensingular.form.view.SViewListByMasterDetail;

import java.util.Optional;

@SInfoType(spackage = SPackageNotificacaoSimplificada.class)
public class STypeAcondicionamentoGAS extends STypeComposite<SIComposite> {

    public STypeString                                  embalagemPrimaria;
    public STypeAttachmentList                          layoutsRotulagem;
    public STypeList<STypeLocalFabricacao, SIComposite> locaisFabricacao;


    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);

        embalagemPrimaria = this.addFieldString("embalagemPrimaria");
        embalagemPrimaria.selectionOf("Cilindro", "Tanque", "Caminhão Tanque");
        embalagemPrimaria
                .asAtr()
                .label("Emabalagem Primária")
                .asAtrBootstrap()
                .colPreference(4);


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
                            label += String.valueOf(Optional.ofNullable(Value.of(i, "envasadora.razaoSocial")).orElse(""));
                            return label;
                        }).col(locaisFabricacao.getElementsType().empresaTerceirizada.etapasFabricacao()))
                .asAtr().label("Local de fabricação");


    }

}
