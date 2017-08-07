package org.opensingular.lib.commons.extension;

import org.opensingular.lib.commons.context.SingularContext;
import org.opensingular.lib.commons.context.SingularSingletonStrategy;
import org.opensingular.lib.commons.util.Loggable;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

public class SingularExtensionUtil implements Loggable {
    public static SingularExtensionUtil get() {
        return ((SingularSingletonStrategy) SingularContext.get())
                .singletonize(SingularExtensionUtil.class, SingularExtensionUtil::new);
    }

    public <T> List<T> findExtensionsByClass(Class<T> extensionClass) {
        List<T> list = new ArrayList<>();
        for (T extension : ServiceLoader.load(extensionClass)) {
            list.add(extension);
        }
        return list;
    }

    public <T> T findExtensionByClass(Class<T> extensionClass) {
        return ServiceLoader.load(extensionClass).iterator().next();
    }

}