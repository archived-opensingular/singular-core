package br.net.mirante.singular.commons.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Properties;

public enum SingularProperties {

    INSTANCE;

    public static final String HIBERNATE_GENERATOR = "flow.persistence.hibernate.generator";
    public static final String HIBERNATE_SEQUENCE_PROPERTY_PATTERN = "flow.persistence.%s.sequence";
    private static final Logger logger = LoggerFactory.getLogger(SingularProperties.class);
    private Properties p = new Properties();

    SingularProperties() {
        InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("singular.properties");
        if (is == null) {
            is = SingularProperties.class.getClassLoader().getResourceAsStream("singular.properties");
        }
        if (is != null) {
            load(is);
        }
    }

    private void load(InputStream is) {
        try {
            this.p.clear();
            p.load(is);
        } catch (Exception e) {
            logger.warn("Arquivo singular.properties não foi encontrado");
        }
    }

    /**
     * Copia as propriedades do @param props para as properties internas.
     * As propriedades previamente existentes serão removidas
     * Esse método é utilizado para testes unitários com difererentes contextos.
     *
     * @param propertiesStream o arquivo
     */
    public void loadFrom(InputStream propertiesStream) {
        load(propertiesStream);
    }

    public String getProperty(String key) {
        return p.getProperty(key);
    }
}
