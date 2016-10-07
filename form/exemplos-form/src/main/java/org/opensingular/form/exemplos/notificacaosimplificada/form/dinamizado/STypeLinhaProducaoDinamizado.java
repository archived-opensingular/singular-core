package org.opensingular.form.exemplos.notificacaosimplificada.form.dinamizado;

import org.opensingular.form.exemplos.notificacaosimplificada.form.vocabulario.SPackageVocabularioControlado;
import org.opensingular.form.exemplos.notificacaosimplificada.form.vocabulario.STypeLinhaProducao;
import org.opensingular.form.SInfoType;
import org.opensingular.form.provider.STextQueryProvider;

@SInfoType(spackage = SPackageNotificacaoSimplificadaDinamizado.class)
public class STypeLinhaProducaoDinamizado extends STypeLinhaProducao {

    @Override
    protected STextQueryProvider getProvider() {
        return (STextQueryProvider) (builder, query) -> SPackageVocabularioControlado.dominioService(builder.getCurrentInstance()).listarLinhasProducaoDinamizado(query).forEach(lp -> {
            builder.add().set(id, lp.getId()).set(descricao, lp.getDescricao());
        });
    }

}