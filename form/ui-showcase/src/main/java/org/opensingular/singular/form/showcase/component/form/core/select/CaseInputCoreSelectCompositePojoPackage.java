package org.opensingular.singular.form.showcase.component.form.core.select;

import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SPackage;
import org.opensingular.form.STypeComposite;
import org.opensingular.singular.form.showcase.component.CaseItem;
import org.opensingular.singular.form.showcase.component.Group;
import org.opensingular.singular.form.showcase.component.Resource;

/**
 * É possivel utilizar objetos serializaveis para realizar a seleção, porem neste caso, é necessario informar o conversor.
 */
@CaseItem(componentName = "Select", subCaseName = "Tipo composto com objetos serializaveis.", group = Group.INPUT,
        resources = {@Resource(IngredienteQuimico.class), @Resource(IngredienteQuimicoFilteredProvider.class)})
public class CaseInputCoreSelectCompositePojoPackage extends SPackage {

    @Override
    protected void onLoadPackage(PackageBuilder pb) {

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
