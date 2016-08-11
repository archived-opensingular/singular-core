package br.net.mirante.singular.server.commons.flow;


import br.net.mirante.singular.flow.core.ProcessDefinition;
import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.server.commons.wicket.view.form.FormPageConfig;

import java.io.Serializable;
import java.util.Optional;

@FunctionalInterface
public interface LazyFlowDefinitionResolver extends Serializable {

    Optional<Class<? extends ProcessDefinition>> resolve(FormPageConfig cfg, SIComposite iRoot);

}