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


    static {
        propertiesServer.put(SINGULAR_WS_ENDERECO, "http://localhost:8080/notificacaosimplificada/SingularWS?wsdl");
        propertiesServer.put(SINGULAR_SERVIDOR_ENDERECO, "http://localhost:8080/singular/peticionamento");
        propertiesServer.put(SINGULAR_MODULE_FORM_ENDERECO, "/peticionamento");
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
        return propertiesServer.getProperty(key);
    }

    public static String get(IServerContext context, String key) {
        return propertiesServer.getProperty(context.getPropertiesBaseKey() + "." + key);
    }

    public static class SelectedProperties {

        private Properties selected;

        protected SelectedProperties(Properties selected) {
            this.selected = selected;
        }

        public String get(String key) {
            return selected.getProperty(key);
        }
    }


}
