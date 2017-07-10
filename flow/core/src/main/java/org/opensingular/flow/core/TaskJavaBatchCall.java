package org.opensingular.flow.core;

import java.util.Collection;

@FunctionalInterface
public interface TaskJavaBatchCall<K extends ProcessInstance> {

    Object call(Collection<K> instanciasProcesso);

}
