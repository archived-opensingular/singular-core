package org.opensingular.studio.app.spring;

import org.opensingular.lib.commons.context.SingularContext;
import org.opensingular.lib.commons.context.SingularSingletonStrategy;
import org.opensingular.lib.commons.scan.SingularClassPathScanner;
import org.opensingular.studio.app.StudioAppConfig;

import java.lang.reflect.Modifier;
import java.util.List;
import java.util.stream.Collectors;


public class StudioAppConfigProvider {

    private StudioAppConfig config;

    private StudioAppConfigProvider() {
    }

    public static StudioAppConfigProvider get() {
        return ((SingularSingletonStrategy) SingularContext.get()).singletonize(StudioAppConfigProvider.class, StudioAppConfigProvider::new);
    }

    public StudioAppConfig retrieve() {
        if (config == null) {
            List<Class<? extends StudioAppConfig>> configs = findAllInstantiableConfigs();
            if (configs.isEmpty()) {
                throw new StudioAppConfigProviderException("É obrigatorio implementar a classe " + StudioAppConfig.class);
            }
            if (configs.size() > 1) {
                throw new StudioAppConfigProviderException("Não é permitido possuir mais de uma implementação de " + StudioAppConfig.class);
            }
            Class<? extends StudioAppConfig> configClass = configs.get(0);
            try {
                config = configClass.newInstance();
            } catch (InstantiationException | IllegalAccessException ex) {
                throw new StudioAppConfigProviderException("Não foi possivel criar uma nova instancia de " + configClass.getName(), ex);
            }
        }
        return config;
    }

    private List<Class<? extends StudioAppConfig>> findAllInstantiableConfigs() {
        return SingularClassPathScanner.get()
                        .findSubclassesOf(StudioAppConfig.class)
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