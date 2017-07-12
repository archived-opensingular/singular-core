package org.opensingular.flow.core;

import java.util.Collection;

@FunctionalInterface
public interface TaskJavaBatchCall<K extends FlowInstance> {

    Object call(Collection<K> instanciasProcesso);

}
