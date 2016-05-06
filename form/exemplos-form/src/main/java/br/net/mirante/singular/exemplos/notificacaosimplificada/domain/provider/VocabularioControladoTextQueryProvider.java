package br.net.mirante.singular.exemplos.notificacaosimplificada.domain.provider;

import br.net.mirante.singular.exemplos.notificacaosimplificada.domain.dto.VocabularioControladoDTO;
import br.net.mirante.singular.exemplos.notificacaosimplificada.domain.generic.VocabularioControlado;
import br.net.mirante.singular.exemplos.notificacaosimplificada.service.DominioService;
import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.provider.TextQueryProvider;

import java.util.List;

public class VocabularioControladoTextQueryProvider<T extends VocabularioControlado> implements TextQueryProvider<VocabularioControladoDTO, SIComposite> {

    private static final long serialVersionUID = -7403539812215320485L;

    private final Class<T> vocabularioClass;

    public VocabularioControladoTextQueryProvider(Class<T> vocabularioClass) {
        this.vocabularioClass = vocabularioClass;
    }

    @Override
    public List<VocabularioControladoDTO> load(SIComposite ins, String query) {
        return ins.getDocument().lookupService(DominioService.class).buscarVocabulario(vocabularioClass, query);
    }
}
