package br.net.mirante.singular.showcase.component.form.core.select;

import br.net.mirante.singular.form.PackageBuilder;
import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.SPackage;
import br.net.mirante.singular.form.STypeComposite;
import br.net.mirante.singular.showcase.component.CaseItem;
import br.net.mirante.singular.showcase.component.Group;

/**
 * É possivel utilizar objetos serializaveis para realizar a seleção, porem neste caso, é necessario informar o conversor.
 */
@CaseItem(componentName = "Select", subCaseName = "Tipo composto com objetos serializaveis.", group = Group.INPUT,
        resources = {IngredienteQuimico.class, IngredienteQuimicoFilteredProvider.class})
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
