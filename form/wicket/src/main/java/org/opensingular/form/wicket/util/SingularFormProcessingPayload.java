package org.opensingular.form.wicket.util;

import org.apache.commons.collections4.CollectionUtils;
import org.opensingular.form.SInstance;

import java.util.Set;
import java.util.stream.Collectors;

public class SingularFormProcessingPayload {
    private Set<String> typesName;

    public SingularFormProcessingPayload(Set<SInstance> instancesToUpdateComponents) {
        this.typesName = instancesToUpdateComponents
                .stream().map(i -> i.getType().getNameSimple())
                .collect(Collectors.toSet());
    }

    public boolean hasUpdatedType(Set<String> typeNames) {
        return CollectionUtils.containsAny(this.typesName, typeNames);
    }
}
