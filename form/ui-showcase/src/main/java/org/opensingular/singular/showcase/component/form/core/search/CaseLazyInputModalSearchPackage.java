package org.opensingular.singular.showcase.component.form.core.search;

import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SPackage;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.converter.ValueToSICompositeConverter;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.view.SViewSearchModal;
import org.opensingular.singular.showcase.component.CaseItem;
import org.opensingular.singular.showcase.component.Group;
import org.opensingular.singular.showcase.component.Resource;

/**
 * Permite a seleção a partir de uma busca filtrada, sendo necessario fazer o controle de paginação manualmente
 */
@CaseItem(componentName = "Search Select", subCaseName = "Lazy Pagination", group = Group.INPUT,
resources = {@Resource(Funcionario.class), @Resource(LazyFuncionarioProvider.class), @Resource(FuncionarioRepository.class)})
public class CaseLazyInputModalSearchPackage extends SPackage {

    @Override
    protected void onLoadPackage(PackageBuilder pb) {

        final STypeComposite<?> tipoMyForm = pb.createCompositeType("testForm");

        final STypeComposite funcionario = tipoMyForm.addFieldComposite("funcionario");
        funcionario.asAtr().label("Funcionario").displayString("${nome} - ${funcao}");

        final STypeString nome   = funcionario.addFieldString("nome");
        final STypeString funcao = funcionario.addFieldString("funcao");

        funcionario.withView(new SViewSearchModal().title("Buscar Profissionais"))
                .asAtrProvider()
                //@destacar
                .filteredProvider(new LazyFuncionarioProvider())
                .converter((ValueToSICompositeConverter<Funcionario>) (newFunc, func) -> {
                    newFunc.setValue(nome, func.getNome());
                    newFunc.setValue(funcao, func.getFuncao());
                });

    }

}