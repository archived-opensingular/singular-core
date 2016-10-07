package br.net.mirante.singular.exemplos.notificacaosimplificada.form.dinamizado;

import br.net.mirante.singular.exemplos.notificacaosimplificada.form.vocabulario.STypeLinhaProducao;
import br.net.mirante.singular.form.SInfoType;
import br.net.mirante.singular.form.provider.STextQueryProvider;
import br.net.mirante.singular.form.provider.TextQueryProvider;

import static br.net.mirante.singular.exemplos.notificacaosimplificada.form.vocabulario.SPackageVocabularioControlado.dominioService;

@SInfoType(spackage = SPackageNotificacaoSimplificadaDinamizado.class)
public class STypeLinhaProducaoDinamizado extends STypeLinhaProducao {

    @Override
    protected STextQueryProvider getProvider() {
        return (STextQueryProvider) (builder, query) -> dominioService(builder.getCurrentInstance()).listarLinhasProducaoDinamizado(query).forEach(lp -> {
            builder.add().set(id, lp.getId()).set(descricao, lp.getDescricao());
        });
    }

}