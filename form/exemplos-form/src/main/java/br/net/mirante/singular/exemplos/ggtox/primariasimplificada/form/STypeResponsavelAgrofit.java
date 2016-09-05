package br.net.mirante.singular.exemplos.ggtox.primariasimplificada.form;

import br.net.mirante.singular.form.SInfoType;
import br.net.mirante.singular.form.TypeBuilder;
import br.net.mirante.singular.form.persistence.STypePersistentComposite;
import br.net.mirante.singular.form.type.core.STypeBoolean;
import br.net.mirante.singular.form.view.SViewByBlock;

@SInfoType(name = "STypeResponsavelAgrofit", spackage = SPackagePeticaoPrimariaSimplificada.class)
public class STypeResponsavelAgrofit extends STypePersistentComposite {

    private final String DECLARACAO_ANEXO_AGROFIT = "declaracaoAnexoAgrofit";

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);

        final STypeBoolean declaracaoAnexoAgrofit = addField(DECLARACAO_ANEXO_AGROFIT, STypeBoolean.class);

        declaracaoAnexoAgrofit.addInstanceValidator(validatable -> {
            if (validatable.getInstance().getValue() == null || !validatable.getInstance().getValue()) {
                validatable.error("É obrigatório declarar que os anexos foram inseridos ao AGROFIT.");
            }
        });

        declaracaoAnexoAgrofit.asAtr().label("Declaro que os arquivos de parecer e oficio foram anexados ao Agrofit.");

        withView(new SViewByBlock(), block -> {
            block.newBlock("Responsável Agrofit").add(declaracaoAnexoAgrofit);
        });
    }

}
