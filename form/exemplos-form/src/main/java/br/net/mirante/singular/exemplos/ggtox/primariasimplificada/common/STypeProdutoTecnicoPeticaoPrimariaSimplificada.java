package br.net.mirante.singular.exemplos.ggtox.primariasimplificada.common;


import br.net.mirante.singular.exemplos.ggtox.primariasimplificada.form.SPackagePPSCommon;
import br.net.mirante.singular.form.*;
import br.net.mirante.singular.form.type.core.STypeBoolean;
import br.net.mirante.singular.form.util.transformer.Value;


@SInfoType(spackage = SPackagePPSCommon.class)
public class STypeProdutoTecnicoPeticaoPrimariaSimplificada extends STypeComposite<SIComposite> {

    public STypeProdutoTecnico produtoTecnico;

    public STypeList<STypeProdutoTecnico, SIComposite> produtosTecnicos;

    public STypeBoolean produtoTecnicoNaoSeAplica;

    @Override
    protected void onLoadType(TypeBuilder builder) {
        super.onLoadType(builder);

        asAtr().label("Produto Técnico");

        produtoTecnicoNaoSeAplica = addFieldBoolean("produtoTecnicoNaoSeAplica");

        produtoTecnicoNaoSeAplica
                .asAtr()
                .label("Produto técnico não se aplica");

        produtoTecnico = addField("produtoTecnico", STypeProdutoTecnico.class);

        produtoTecnico
                .asAtr()
                .dependsOn(produtoTecnicoNaoSeAplica)
                .visible(si -> {
                    return !Value.notNull(si, produtoTecnicoNaoSeAplica) || !Value.of(si, produtoTecnicoNaoSeAplica);
                });

        produtosTecnicos = addFieldListOf("produtosTecnicos", STypeProdutoTecnico.class);

        produtosTecnicos
                .asAtr()
                .label("Produtos técnicos");





    }
}
