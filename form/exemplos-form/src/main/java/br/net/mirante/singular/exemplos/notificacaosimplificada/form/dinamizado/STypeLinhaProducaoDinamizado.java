package br.net.mirante.singular.exemplos.notificacaosimplificada.form.dinamizado;

import br.net.mirante.singular.exemplos.notificacaosimplificada.domain.LinhaCbpf;
import br.net.mirante.singular.exemplos.notificacaosimplificada.form.vocabulario.STypeLinhaProducao;
import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SIList;
import br.net.mirante.singular.form.mform.SInfoType;
import br.net.mirante.singular.form.mform.options.SOptionsProvider;

import static br.net.mirante.singular.exemplos.notificacaosimplificada.form.vocabulario.SPackageVocabularioControlado.dominioService;

@SInfoType(spackage = SPackageNotificacaoSimplificadaDinamizado.class)
public class STypeLinhaProducaoDinamizado extends STypeLinhaProducao {

    @Override
    protected SOptionsProvider getProvider() {
        return (ins, filter) -> {
            final SIList<?> list = ins.getType().newList();
            for (LinhaCbpf lc : dominioService(ins).listarLinhasProducaoDinamizado(filter)) {
                final SIComposite c = (SIComposite) list.addNew();
                c.setValue(id, lc.getId());
                c.setValue(descricao, lc.getDescricao());
            }
            return list;
        };
    }
}
