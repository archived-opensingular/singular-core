package br.net.mirante.singular.server.commons.config;


import br.net.mirante.singular.commons.base.SingularProperties;

import java.util.Properties;

/**
 * Responsável por carregar as propriedades de configuração do
 * pet-server  analise e peticionamento
 */
public class ConfigProperties {

    public static final  String     SINGULAR_WS_ENDERECO          = "singular.ws.endereco";
    public static final  String     SINGULAR_SERVIDOR_ENDERECO    = "singular.servidor.endereco";
    public static final  String     SINGULAR_MODULE_FORM_ENDERECO = "singular.module.form.endereco";
    public static final  String     SINGULAR_DEV_MODE             = "singular.config.dev";
    private static final Properties PROPERTIES_SERVER             = SingularProperties.INSTANCE.getProperties();

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

    private static String lookupProperty(String key) {
        if (PROPERTIES_SERVER.containsKey(key)) {
            Object value = PROPERTIES_SERVER.get(key);
            return value == null ? null : value.toString();
        }
        return System.getProperty(key);
    }

}