package org.opensingular.server.commons.config;


import org.opensingular.lib.commons.base.SingularProperties;

import java.util.Optional;

/**
 * Responsável por carregar as propriedades de configuração do
 * pet-server  analise e peticionamento
 */
public class ConfigProperties {

    public static final String SINGULAR_SERVIDOR_ENDERECO           = "singular.servidor.endereco";
    public static final String SINGULAR_DEV_MODE                    = "singular.config.dev";
    public static final String SINGULAR_EAGER_LOAD_FLOW_DEFINITIONS = "singular.flow.eager.load";

    /**
     * Permite acesso as propriedades configuradas para servidor de peticionamento como um todo:
     * server.properties
     *
     * @return
     */
    public static String get(String key) {
        return lookupProperty(key);
    }

    public static String get(IServerContext context, String key) {
        return lookupProperty(context.getPropertiesBaseKey() + "." + key);
    }

    public static String get(String key, String defaultValue) {
        return Optional.ofNullable(lookupProperty(key)).orElse(defaultValue);
    }

    private static String lookupProperty(String key) {
        if (SingularProperties.get().containsKey(key)) {
            return SingularProperties.get().getProperty(key);
        }
        return System.getProperty(key);
    }

    public static boolean isDevelopmentMode() {
        return "true".equals(ConfigProperties.get(ConfigProperties.SINGULAR_DEV_MODE));
    }

}