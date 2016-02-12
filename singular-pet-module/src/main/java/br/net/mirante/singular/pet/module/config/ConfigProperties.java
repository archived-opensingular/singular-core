package br.net.mirante.singular.pet.module.config;


import br.net.mirante.singular.pet.module.exception.SingularServerException;

import java.io.IOException;
import java.util.Properties;

/**
 * Responsável por carregar as propriedades de configuração do
 * pet-server  analise e peticionamento
 */
public class ConfigProperties {

    private static Properties propertiesServer = new Properties();
    private static Properties propertiesPet = new Properties();
    private static Properties propertiesAnl = new Properties();


    static {
        try {
            propertiesServer.load(ConfigProperties.class.getResource("/server.properties").openStream());
            propertiesPet.load(ConfigProperties.class.getResource("/server.properties").openStream());
            propertiesPet.load(ConfigProperties.class.getResource("/peticionamento.properties").openStream());
            propertiesAnl.load(ConfigProperties.class.getResource("/server.properties").openStream());
            propertiesAnl.load(ConfigProperties.class.getResource("/analise.properties").openStream());
        } catch (IOException e) {
            throw new SingularServerException(e.getMessage(), e);
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
        return propertiesServer.getProperty(key);
    }

    /**
     * Permite acesso as propriedades configuradas para peticionamento:
     * server.properties e peticionamento.properties
     *
     * @return
     */
    public static SelectedProperties pet() {
        return new SelectedProperties(propertiesPet);
    }

    /**
     * Permite acesso as propriedades configuradas para analise:
     * server.properties e analise.properties
     *
     * @return
     */
    public static SelectedProperties anl() {
        return new SelectedProperties(propertiesAnl);
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
