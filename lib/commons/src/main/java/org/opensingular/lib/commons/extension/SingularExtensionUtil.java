package org.opensingular.lib.commons.extension;

import org.opensingular.lib.commons.context.SingularContext;
import org.opensingular.lib.commons.context.SingularSingletonStrategy;
import org.opensingular.lib.commons.scan.SingularClassPathScanner;
import org.opensingular.lib.commons.util.Loggable;

import java.util.ArrayList;
import java.util.List;

public class SingularExtensionUtil implements Loggable {
    public static SingularExtensionUtil get() {
        return ((SingularSingletonStrategy) SingularContext.get())
                .singletonize(SingularExtensionUtil.class, SingularExtensionUtil::new);
    }

    public <T> List<T> findExtensionByClass(Class<T> extensionClass) {
        List<T> list = new ArrayList<>();
        for (Class<?> extension : SingularClassPathScanner.get().findClassesAnnotatedWith(SingularExtension.class)) {
            if (extensionClass.isAssignableFrom(extension)) {
                try {
                    list.add((T) extension.newInstance());
                } catch (InstantiationException | IllegalAccessException e) {
                    getLogger().error("Não foi possivel criar uma nova instancia de {}", extension);
                }
            }
        }
        return list;
    }
}