package br.net.mirante.singular.exemplos.ggtox.primariasimplificada.common;


import br.net.mirante.singular.exemplos.ggtox.primariasimplificada.form.SPackagePPSCommon;
import br.net.mirante.singular.form.*;
import br.net.mirante.singular.form.persistence.STypePersistentComposite;
import br.net.mirante.singular.form.type.core.STypeInteger;
import br.net.mirante.singular.form.type.core.STypeString;
import br.net.mirante.singular.form.view.SViewListByMasterDetail;

import static br.net.mirante.singular.exemplos.ggtox.primariasimplificada.form.SPackagePPSCommon.ppsService;

@SInfoType(spackage = SPackagePPSCommon.class)
public class STypeProdutoFormuladoPeticaoPrimariaSimplificada extends STypePersistentComposite {


    public STypeFormuladorConformeMatriz           formulador;
    public STypeList<STypeFormulador, SIComposite> formuladores;
    public STypeComposite<SIComposite>             tipoFormulacao;


    @Override
    protected void onLoadType(TypeBuilder builder) {
        super.onLoadType(builder);

        this
                .asAtrAnnotation()
                .setAnnotated();

        this
                .asAtr()
                .label("Produto Formulado");

        tipoFormulacao = addFieldComposite("tipoFormulacao");
        STypeInteger idTipoFormulacao        = tipoFormulacao.addFieldInteger("idTipoFormulacao");
        STypeString  siglaTipoFormulacao     = tipoFormulacao.addFieldString("siglaTipoFormulacao");
        STypeString  nomeTipoFormulacao      = tipoFormulacao.addFieldString("nomeTipoFormulacao");
        STypeString  descricaoTipoFormulacao = tipoFormulacao.addFieldString("descricaoTipoFormulacao");

        tipoFormulacao
                .asAtr()
                .label("Tipo de Formulação")
                .asAtrBootstrap()
                .colPreference(4);

        tipoFormulacao
                .selection()
                .id(idTipoFormulacao)
                .display("${siglaTipoFormulacao} - ${nomeTipoFormulacao}")
                .simpleProvider(sb -> {
                    ppsService(sb.getCurrentInstance()).buscarTipoDeFormulacao()
                            .forEach(tipoFormulacaoEntity -> sb.add()
                                    .set(idTipoFormulacao, tipoFormulacaoEntity.getCod())
                                    .set(siglaTipoFormulacao, tipoFormulacaoEntity.getSigla())
                                    .set(nomeTipoFormulacao, tipoFormulacaoEntity.getNome())
                                    .set(descricaoTipoFormulacao, tipoFormulacaoEntity.getDescricao()));
                });

        formuladores = addFieldListOf("formuladores", STypeFormulador.class);

        formuladores
                .asAtr()
                .label("Formuladores");
        formuladores
                .withView(new SViewListByMasterDetail()
                        .col(formuladores.getElementsType().cnpj)
                        .col(formuladores.getElementsType().nome)
                        .col(formuladores.getElementsType().cidade)
                        .col(formuladores.getElementsType().estado)
                );
        formuladores
                .withMiniumSizeOf(1);


        formulador = addField("formulador", STypeFormuladorConformeMatriz.class);
        formulador
                .asAtrBootstrap()
                .colPreference(12)
                .newRow();

    }
}
