package br.net.mirante.singular.showcase.component.input.core.select;

import br.net.mirante.singular.form.PackageBuilder;
import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.SPackage;
import br.net.mirante.singular.form.STypeComposite;

public class CaseInputCoreSelectCompositePojoPackage extends SPackage {

    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {

        final STypeComposite<?>           tipoMyForm         = pb.createCompositeType("testForm");
        final STypeComposite<SIComposite> ingredienteQuimico = tipoMyForm.addFieldComposite("ingredienteQuimico");

        ingredienteQuimico.asAtr().label("Ingrediente Quimico");

        ingredienteQuimico.addFieldString("formulaQuimica");
        ingredienteQuimico.addFieldString("nome");

        ingredienteQuimico.selectionOf(IngredienteQuimico.class)
                .id(IngredienteQuimico::getNome)
                .display("${nome} - ${formulaQuimica}")
                .autoConverterOf(IngredienteQuimico.class)
                .simpleProvider(new IngredienteQuimicoFilteredProvider());

    }

}
