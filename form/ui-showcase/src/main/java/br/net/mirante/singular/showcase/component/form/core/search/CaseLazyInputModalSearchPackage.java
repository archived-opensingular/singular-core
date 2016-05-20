package br.net.mirante.singular.showcase.component.form.core.search;

import br.net.mirante.singular.form.PackageBuilder;
import br.net.mirante.singular.form.SPackage;
import br.net.mirante.singular.form.STypeComposite;
import br.net.mirante.singular.form.converter.ValueToSICompositeConverter;
import br.net.mirante.singular.form.type.core.STypeString;
import br.net.mirante.singular.form.view.SViewSearchModal;
import br.net.mirante.singular.showcase.component.CaseItem;
import br.net.mirante.singular.showcase.component.Group;

/**
 * Permite a seleção a partir de uma busca filtrada, sendo necessario fazer o controle de paginação manualmente
 */
@CaseItem(componentName = "Search Select", subCaseName = "Lazy Pagination", group = Group.INPUT,
resources = {Funcionario.class, LazyFuncionarioProvider.class, FuncionarioRepository.class})
public class CaseLazyInputModalSearchPackage extends SPackage {

    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {

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