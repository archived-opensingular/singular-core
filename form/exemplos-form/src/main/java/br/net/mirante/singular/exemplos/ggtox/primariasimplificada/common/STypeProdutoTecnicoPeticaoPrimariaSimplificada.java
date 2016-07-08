package br.net.mirante.singular.exemplos.ggtox.primariasimplificada.common;


import br.net.mirante.singular.exemplos.ggtox.primariasimplificada.form.SPackagePPSCommon;
import br.net.mirante.singular.form.*;
import br.net.mirante.singular.form.type.core.STypeBoolean;
import br.net.mirante.singular.form.view.SViewListByForm;
import br.net.mirante.singular.form.view.SViewListByMasterDetail;


@SInfoType(spackage = SPackagePPSCommon.class)
public class STypeProdutoTecnicoPeticaoPrimariaSimplificada extends STypeComposite<SIComposite> {

    public STypeList<STypeProdutoTecnico, SIComposite> produtosTecnicos;

    public STypeBoolean produtoTecnicoNaoSeAplica;

    @Override
    protected void onLoadType(TypeBuilder builder) {
        super.onLoadType(builder);

        this
                .asAtrAnnotation()
                .setAnnotated();

        asAtr().label("Produto Técnico");

        produtoTecnicoNaoSeAplica = addFieldBoolean("produtoTecnicoNaoSeAplica");

        produtoTecnicoNaoSeAplica
                .asAtr()
                .label("Produto técnico não se aplica");

        produtosTecnicos = addFieldListOf("produtosTecnicos", STypeProdutoTecnico.class);

        produtosTecnicos
                .asAtr()
                .label("Produtos técnicos");

        produtosTecnicos
                .withView(new SViewListByMasterDetail()
                        .col(produtosTecnicos.getElementsType().numeroProcessoProdutoTecnico)
                        .col(produtosTecnicos.getElementsType().nomeProdutoTecnico));


    }
}
