package br.net.mirante.singular.exemplos.notificacaosimplificada.form.dinamizado;

import br.net.mirante.singular.exemplos.notificacaosimplificada.form.vocabulario.STypeLinhaProducao;
import br.net.mirante.singular.form.mform.SInfoType;
import br.net.mirante.singular.form.mform.provider.FilteredProvider;

import static br.net.mirante.singular.exemplos.notificacaosimplificada.form.vocabulario.SPackageVocabularioControlado.dominioService;

@SInfoType(spackage = SPackageNotificacaoSimplificadaDinamizado.class)
public class STypeLinhaProducaoDinamizado extends STypeLinhaProducao {

    @Override
    protected FilteredProvider getProvider() {
        return (ins, filter) -> dominioService(ins).listarLinhasProducaoDinamizado(filter);
    }
}
