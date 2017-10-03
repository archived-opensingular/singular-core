package org.opensingular.studio.core.config;

import org.opensingular.lib.commons.context.SingularContext;
import org.opensingular.lib.commons.context.SingularSingletonStrategy;
import org.opensingular.lib.commons.scan.SingularClassPathScanner;

import java.lang.reflect.Modifier;
import java.util.List;
import java.util.stream.Collectors;


public class StudioConfigProvider {

    private StudioConfig config;

    private StudioConfigProvider() {
    }

    public static StudioConfigProvider get() {
        return ((SingularSingletonStrategy) SingularContext.get()).singletonize(StudioConfigProvider.class, StudioConfigProvider::new);
    }

    public StudioConfig retrieve() {
        if (config == null) {
            List<Class<? extends StudioConfig>> configs = findAllInstantiableConfigs();
            if (configs.isEmpty()) {
                throw new StudioAppConfigProviderException("É obrigatorio implementar a classe " + StudioConfig.class);
            }
            if (configs.size() > 1) {
                throw new StudioAppConfigProviderException("Não é permitido possuir mais de uma implementação de " + StudioConfig.class);
            }
            Class<? extends StudioConfig> configClass = configs.get(0);
            try {
                config = configClass.newInstance();
            } catch (InstantiationException | IllegalAccessException ex) {
                throw new StudioAppConfigProviderException("Não foi possivel criar uma nova instancia de " + configClass.getName(), ex);
            }
        }
        return config;
    }

    private List<Class<? extends StudioConfig>> findAllInstantiableConfigs() {
        return SingularClassPathScanner.get()
                        .findSubclassesOf(StudioConfig.class)
                        .stream()
                        .filter(config -> !(Modifier.isAbstract(config.getModifiers()) || config.isInterface() || config.isAnonymousClass()))
                        .collect(Collectors.toList());
    }

    private static class StudioAppConfigProviderException extends RuntimeException {
        public StudioAppConfigProviderException(String s) {
            super(s);
        }

        public StudioAppConfigProviderException(String s, Throwable throwable) {
            super(s, throwable);
        }
    }

}