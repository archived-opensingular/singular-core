package org.opensingular.form.wicket.util;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.opensingular.form.SInstance;

public class SingularFormProcessingPayload {
    private Set<String> typesName;

    public SingularFormProcessingPayload(Collection<SInstance> instancesToUpdateComponents) {
        this.typesName = instancesToUpdateComponents
                .stream().map(i -> i.getType().getNameSimple())
                .collect(Collectors.toSet());
    }

    public boolean hasUpdatedType(Set<String> typeNames) {
        return CollectionUtils.containsAny(this.typesName, typeNames);
    }
}
