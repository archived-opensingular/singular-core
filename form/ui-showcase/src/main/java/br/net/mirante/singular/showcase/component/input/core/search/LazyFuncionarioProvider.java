package br.net.mirante.singular.showcase.component.input.core.search;

import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.provider.FilteredPagedProvider;
import br.net.mirante.singular.form.mform.provider.ProviderContext;
import br.net.mirante.singular.form.mform.provider.filter.FilterConfigBuilder;

import java.util.List;

public class LazyFuncionarioProvider implements FilteredPagedProvider<Funcionario> {

    private static final FuncionarioRepository repository = new FuncionarioRepository();

    @Override
    public void configureFilter(FilterConfigBuilder fcb) {
        fcb
                .configureType(f -> {
                    f.addFieldString("nome").asAtr().label("Nome").asAtrBootstrap().colPreference(6);
                    f.addFieldString("funcao").asAtr().label("Função").asAtrBootstrap().colPreference(6);
                    f.addFieldInteger("idade").asAtr().label("Idade").asAtrBootstrap().colPreference(2);
                })
                .addColumn("nome", "Nome")
                .addColumn("funcao", "Função")
                .addColumn("idade", "Idade")
                //@destacar
                .lazy(true);
    }

    @Override
    public long getSize(ProviderContext<SInstance> context) {
        //@destacar
        return repository.get(context.getFilterInstance()).size();
    }

    @Override
    public List<Funcionario> load(ProviderContext<SInstance> context) {
        //@destacar
        return repository.get(context.getFilterInstance()).subList((int) context.getFirst(), (int) (context.getFirst() + context.getCount()));
    }

}