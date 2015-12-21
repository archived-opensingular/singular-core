package br.net.mirante.singular.showcase.input.core.select;

import br.net.mirante.singular.showcase.CaseBase;
import br.net.mirante.singular.showcase.ResourceRef;
import br.net.mirante.singular.view.page.form.crud.services.MFileIdsOptionsProvider;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;

public class CaseInputCoreSelectProvider extends CaseBase implements Serializable {
    public CaseInputCoreSelectProvider() {
        super("Select", "Provedor Dinâmico");
        setDescriptionHtml("É permitido alterar o provedor de dados de forma que estes sejam carregados de forma dinâmica ou de outras fontes de informação.");
    }

    @Override
    public List<ResourceRef> getAditionalSources() {
        Optional<ResourceRef> rr = ResourceRef.forSource(MFileIdsOptionsProvider.class);
        if(rr.isPresent()) return newArrayList(rr.get());
        return Collections.emptyList();

    }
}
