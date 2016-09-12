package br.net.mirante.singular.exemplos.ggtox.primariasimplificada.form;

import br.net.mirante.singular.form.SInfoType;
import br.net.mirante.singular.form.TypeBuilder;
import br.net.mirante.singular.form.persistence.STypePersistentComposite;
import br.net.mirante.singular.form.type.core.STypeBoolean;
import br.net.mirante.singular.form.type.core.STypeString;
import br.net.mirante.singular.form.util.SingularPredicates;
import br.net.mirante.singular.form.view.SViewByBlock;

@SInfoType(name = "STypeResponsavelAgrofit", spackage = SPackagePeticaoPrimariaSimplificada.class)
public class STypeResponsavelAgrofit extends STypePersistentComposite {

    private final String DECLARACAO_ANEXO_AGROFIT = "declaracaoAnexoAgrofit";

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);

        final STypeString  produtoEncontrado      = addField("produtoEncontrado", STypeString.class);
        final STypeBoolean declaracaoAnexoAgrofit = addField(DECLARACAO_ANEXO_AGROFIT, STypeBoolean.class);

        produtoEncontrado.selectionOf("Sim", "Não");

        produtoEncontrado
                .asAtr()
                .label("Produto existente no AGROFIT")
                .required();

        produtoEncontrado
                .asAtrBootstrap()
                .colPreference(12);

        declaracaoAnexoAgrofit
                .asAtr()
                .dependsOn(produtoEncontrado)
                .visible(SingularPredicates.typeValueIsEqualsTo(produtoEncontrado, "Sim"));

        declaracaoAnexoAgrofit
                .asAtrBootstrap()
                .colPreference(12);

        declaracaoAnexoAgrofit.addInstanceValidator(validatable -> {
            if (validatable.getInstance().getValue() == null || !validatable.getInstance().getValue()) {
                validatable.error("É obrigatório declarar que os anexos foram inseridos ao AGROFIT.");
            }
        });

        declaracaoAnexoAgrofit.asAtr().label("Declaro que os arquivos de parecer e ofício foram anexados ao Agrofit.");

        withView(new SViewByBlock(), block -> {
            block.newBlock("Responsável Agrofit")
                    .add(produtoEncontrado)
                    .add(declaracaoAnexoAgrofit);
        });
    }

}
