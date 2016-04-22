package br.net.mirante.singular.server.commons.config;


import br.net.mirante.singular.server.commons.exception.SingularServerException;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.util.Properties;

/**
 * Responsável por carregar as propriedades de configuração do
 * pet-server  analise e peticionamento
 */
public class ConfigProperties {

    public static final String SINGULAR_WS_ENDERECO = "singular.ws.endereco";
    public static final String SINGULAR_SERVIDOR_ENDERECO = "singular.servidor.endereco";
    public static final String SINGULAR_MODULE_FORM_ENDERECO = "singular.module.form.endereco";
    private static Properties propertiesServer = new Properties();

    static {
        try {
            String server = System.getProperty("singular.server.props.server", "classpath:singular.properties");
            propertiesServer.load(ResourceUtils.getURL(server).openStream());
        } catch (IOException e) {
            throw new SingularServerException(
                    "É necessário que os arquivo  singular.properties esteja disponivel na raiz do classpath da aplicação.", e);
        }
    }

    private ConfigProperties() {
    }

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
        if (propertiesServer.containsKey(key)) {
            Object value = propertiesServer.get(key);
            return value == null ? null : value.toString();
        }
        return System.getProperty(key);
    }

}
