package org.opensingular.singular.exemplos.notificacaosimplificada.form.dinamizado;

import org.opensingular.singular.exemplos.notificacaosimplificada.form.vocabulario.STypeLinhaProducao;
import org.opensingular.singular.form.SInfoType;
import org.opensingular.singular.form.provider.STextQueryProvider;

import static org.opensingular.singular.exemplos.notificacaosimplificada.form.vocabulario.SPackageVocabularioControlado.dominioService;

@SInfoType(spackage = SPackageNotificacaoSimplificadaDinamizado.class)
public class STypeLinhaProducaoDinamizado extends STypeLinhaProducao {

    @Override
    protected STextQueryProvider getProvider() {
        return (STextQueryProvider) (builder, query) -> dominioService(builder.getCurrentInstance()).listarLinhasProducaoDinamizado(query).forEach(lp -> {
            builder.add().set(id, lp.getId()).set(descricao, lp.getDescricao());
        });
    }

}