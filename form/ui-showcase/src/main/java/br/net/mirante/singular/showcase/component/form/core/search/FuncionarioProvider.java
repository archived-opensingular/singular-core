package br.net.mirante.singular.showcase.component.form.core.search;


import java.util.List;

import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.provider.Config;
import br.net.mirante.singular.form.provider.FilteredProvider;
import br.net.mirante.singular.form.provider.ProviderContext;

public class FuncionarioProvider implements FilteredProvider<Funcionario> {

    private static final FuncionarioRepository repository = new FuncionarioRepository();

    @Override
    public void configureProvider(Config cfg) {

        cfg.getFilter().addFieldString("nome").asAtr().label("Nome").asAtrBootstrap().colPreference(6);
        cfg.getFilter().addFieldString("funcao").asAtr().label("Função").asAtrBootstrap().colPreference(6);
        cfg.getFilter().addFieldInteger("idade").asAtr().label("Idade").asAtrBootstrap().colPreference(2);

        cfg.result()
                .addColumn("nome", "Nome")
                .addColumn("funcao", "Função")
                .addColumn("idade", "Idade");
    }

    @Override
    public List<Funcionario> load(ProviderContext<SInstance> context) {
        //@destacar
        return repository.get(context.getFilterInstance());
    }

}