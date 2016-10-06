package org.opensingular.singular.server.commons.flow;


import org.opensingular.flow.core.ProcessDefinition;
import org.opensingular.form.SIComposite;
import org.opensingular.singular.server.commons.wicket.view.form.FormPageConfig;

import java.io.Serializable;
import java.util.Optional;

@FunctionalInterface
public interface LazyFlowDefinitionResolver extends Serializable {

    Optional<Class<? extends ProcessDefinition>> resolve(FormPageConfig cfg, SIComposite iRoot);

}