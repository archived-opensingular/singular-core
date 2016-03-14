package br.net.mirante.singular.pet.commons.config;


import br.net.mirante.singular.pet.commons.exception.SingularServerException;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.util.Properties;

/**
 * Responsável por carregar as propriedades de configuração do
 * pet-server  analise e peticionamento
 */
public class ConfigProperties {

    public static final String ANALISE_CONTEXT_KEY = "singular.analise.context";
    public static final String PETICIONAMENTO_CONTEXT_KEY = "singular.peticionamento.context";
    private static Properties propertiesServer = new Properties();
    private static Properties propertiesPet = new Properties();
    private static Properties propertiesAnl = new Properties();


    static {
        try {
            String server = System.getProperty("singular.server.props.server", "classpath:server.properties");
            String peticionamento = System.getProperty("singular.server.props.peticionamento", "classpath:peticionamento.properties");
            String analise = System.getProperty("singular.server.props.analise", "classpath:analise.properties");
            propertiesServer.load(ResourceUtils.getURL(server).openStream());
            propertiesPet.load(ResourceUtils.getURL(server).openStream());
            propertiesPet.load(ResourceUtils.getURL(peticionamento).openStream());
            propertiesAnl.load(ResourceUtils.getURL(server).openStream());
            propertiesAnl.load(ResourceUtils.getURL(analise).openStream());
        } catch (IOException e) {
            throw new SingularServerException(
                    "É necessário que os arquivos server.properties, analise.properties e peticionamento.properties estejam disponíveis na raiz do classpath da aplicação." +
                            " É possivel alterar o caminho dos arquivos utilizando as respectivas propriedades de sistema:  singular.server.props.server, singular.server.props.analise, singular.server.props.peticionamento : ", e);
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
