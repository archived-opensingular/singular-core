package br.net.mirante.singular.commons.base;

import br.net.mirante.singular.commons.lambda.IConsumerEx;
import br.net.mirante.singular.commons.util.PropertiesUtils;
import org.apache.commons.io.input.NullInputStream;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Supplier;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

public enum SingularPropertiesImpl implements SingularProperties {
    INSTANCE;

    private static final Logger LOGGER                      = LoggerFactory.getLogger(SingularProperties.class);
    private static final String DEFAULT_PROPERTIES_FILENAME = "singular-defaults.properties";
    private static final String[] PROPERTIES_FILES_NAME = {"singular-form-service.properties", "singular.properties"};
    private volatile Properties properties;
    private Supplier<Properties> singularDefaultPropertiesSupplier = this::getSingularDefaultProperties;

    public static SingularPropertiesImpl get() {
        return INSTANCE;
    }

    private static File findConfDir() {
        String path = System.getProperty(SYSTEM_PROPERTY_SINGULAR_SERVER_HOME);
        if (path != null) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("   Encontrado a propriedade singular.server.home={}", path);
            }
            File confDir = new File(path, "conf");
            if (confDir.exists()) {
                if (!confDir.isDirectory() && LOGGER.isWarnEnabled()) {
                    LOGGER.warn("   Era esperado que \"[singular.server.home]\\conf\" fosse um diretório");
                }
                return confDir;
            } else if (LOGGER.isWarnEnabled()) {
                LOGGER.warn("      Não exite o diretório {}", confDir);
            }
        }
        return null;
    }

    /**
     * Limpa as propriedades da memoria e força recarga a partir da memória e classPath.
     */
    public synchronized void reload() {
        LOGGER.info("Carregando configurações do Singular");
        Properties newProperties = readClasspathDefaults();
        readPropertiesFilesOverrides(newProperties);
        properties = newProperties;
    }

    public void setSingularServerHome(String serverHome) {
        if (serverHome != null)
            System.setProperty(SYSTEM_PROPERTY_SINGULAR_SERVER_HOME, serverHome);
        else
            System.clearProperty(SYSTEM_PROPERTY_SINGULAR_SERVER_HOME);
    }

    /**
     * Verifica se a propriedade de nome informado existe.
     */
    @Override
    public boolean containsKey(String key) {
        return getProperties().containsKey(key) || System.getProperties().containsKey(key);
    }

    /**
     * Retorna o valor da propriedade solicitada. Pode retornar null.
     */
    @Override
    public String getProperty(String key) {
        //se contém a chave ainda que esta seja com valor nulo
        if (getProperties().containsKey(key)) {
            return getProperties().getProperty(key);
        } else {
            return System.getProperties().getProperty(key);
        }
    }

    @Override
    public boolean isTrue(String key) {
        return "true".equals(Optional.ofNullable(getProperty(key)).map(String::toLowerCase).orElse(null));
    }

    @Override
    public boolean isFalse(String key) {
        return "false".equals(Optional.ofNullable(getProperty(key)).map(String::toLowerCase).orElse(null));
    }

    public String setProperty(String key, String value) {
        return (String) getProperties().setProperty(key, value);
    }

    private synchronized Properties getProperties() {
        //Faz leitura lazy das propriedades, pois no construtor da enum, as variáveis estáticas não estão disponíveis
        if (properties == null) {
            reload();
        }
        return properties;
    }

    private void readPropertiesFilesOverrides(Properties newProperties) {
        File confDir = findConfDir();
        if (confDir != null) {
            for (String name : PROPERTIES_FILES_NAME) {
                File arq = new File(confDir, name);
                loadOverriding(newProperties, arq);
            }
        }
    }

    private Properties readClasspathDefaults() {
        Properties newProperties = null;
        for (String name : PROPERTIES_FILES_NAME) {
            URL url = findProperties(name);
            if (url == null) {
                LOGGER.warn("   Não foi encontrado o arquivo no classpath: {}", name);
            } else {
                LOGGER.info("   Lendo arquivo de propriedades '{}' em {}", name, url);
                newProperties = loadNotOverriding(newProperties, name, url);
            }
        }
        Properties resolvedProperties = newProperties == null ? new Properties() : newProperties;

        appendDefaultProperties(resolvedProperties);

        return resolvedProperties;
    }

    /**
     * Adiciona as propriedades default que já não foram carregadas dos arquivos em PROPERTIES_FILES_NAME.
     *
     * @param resolvedProperties
     */
    private void appendDefaultProperties(Properties resolvedProperties) {
        Properties defaults = singularDefaultPropertiesSupplier.get();
        for (String key : defaults.stringPropertyNames()) {
            if (!resolvedProperties.containsKey(key)) {
                resolvedProperties.setProperty(key, StringUtils.defaultString(defaults.getProperty(key)));
            }
        }
    }

    public Properties getSingularDefaultProperties() {
        Properties defaults = new Properties();
        try (
                InputStream input = defaultIfNull(SingularProperties.class.getResourceAsStream(DEFAULT_PROPERTIES_FILENAME), new NullInputStream(0));
                Reader reader = new InputStreamReader(input, "utf-8")) {
            defaults.load(reader);
        } catch (IOException ex) {
            throw new IllegalStateException("");
        }
        return defaults;
    }

    private Properties loadNotOverriding(Properties newProperties, String propertiesName, URL propertiesUrl) {
        Properties props;
        try (InputStream input = propertiesUrl.openStream()) {
            props = PropertiesUtils.load(propertiesUrl, "utf-8");
        } catch (IOException e) {
            throw new SingularException("Erro lendo arquivo de propriedade", e).add("url", propertiesUrl);
        }
        if (newProperties == null) {
            return props;
        }
        for (Map.Entry<Object, Object> entry : props.entrySet()) {
            if (newProperties.containsKey(entry.getKey())) {
                throw new SingularException("O arquivo de propriedade '" + propertiesName +
                        "' no classpath define novamente a propriedade '" + entry.getKey() +
                        "' definida anteriormente em outro arquivo de propriedade no class path.").add("url",
                        propertiesUrl);
            } else {
                newProperties.setProperty((String) entry.getKey(), (String) entry.getValue());
            }
        }
        return newProperties;
    }

    private URL findProperties(String name) {
        try {
            return SingularPropertiesImpl.class.getClassLoader().getResource(name);
        } catch (Exception e) {
            throw new SingularException("Erro procurando arquivo de properties '" + name + "' no class path", e);
        }
    }

    private URL add(URL current, String name, Enumeration<URL> resources) throws URISyntaxException {
        URL selected = current;
        while (resources.hasMoreElements()) {
            URL u = resources.nextElement();
            if (selected == null) {
                selected = u;
            } else if (!selected.toURI().equals(u.toURI())) {
                throw new SingularException(
                        "Foram encontrados dois arquivos com mesmo nome '" + name + "' no class path").add("arquivo 1",
                        selected).add("arquivo 2", u);
            }
        }
        return selected;
    }

    private void loadOverriding(Properties newProperties, File arq) {
        if (arq.exists()) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("   Lendo arquivo de propriedades {}", arq);
            }
            try (FileInputStream in = new FileInputStream(arq)) {
                loadOverriding(newProperties, in);
            } catch (Exception e) {
                throw new SingularException("Erro lendo arquivo de propriedades", e).add("arquivo", arq);
            }
        }
    }

    private void loadOverriding(Properties newProperties, URL resoruce) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("   Lendo arquivo de propriedades {}", resoruce);
        }
        try {
            loadOverriding(newProperties, resoruce.openStream());
        } catch (Exception e) {
            throw new SingularException("Erro lendo arquivo de propriedades", e).add("url", resoruce);
        }
    }

    private void loadOverriding(Properties newProperties, InputStream in) throws IOException {
        Properties p = new Properties();
        p.load(in);
        for (Map.Entry<Object, Object> entry : p.entrySet()) {
            newProperties.setProperty((String) entry.getKey(), (String) entry.getValue());
        }
    }

    /**
     * Copia as propriedades do arquivo para as properties internas. As propriedades previamente existentes serão
     * sobrepostas. Esse método é utilizado para testes unitários com difererentes contextos.
     */
    public synchronized void reloadAndOverrideWith(URL propertiesURL) {
        reload();
        loadOverriding(getProperties(), propertiesURL);
    }

    public static class Tester {
        private final Properties props;

        public Tester(Properties props) {
            this.props = props;
        }

        public static <EX extends Exception> void runInSandbox(IConsumerEx<SingularPropertiesImpl, EX> callable) throws EX {
            Object state = saveState();
            try {
                callable.accept(SingularPropertiesImpl.get());
            } finally {
                restoreState(state);
            }
        }

        protected static void restoreState(Object stateObject) {
            State  state      = (State) stateObject;
            String serverHome = state.systemBackup.get(SYSTEM_PROPERTY_SINGULAR_SERVER_HOME);
            SingularPropertiesImpl.get().setSingularServerHome(serverHome);
            SingularPropertiesImpl.INSTANCE.properties = state.propertiesBackup;
        }

        public static Object saveState() {
            State state = new State();
            PropertiesUtils.copyTo(SingularPropertiesImpl.INSTANCE.properties, state.propertiesBackup);
            state.systemBackup.put(SYSTEM_PROPERTY_SINGULAR_SERVER_HOME, System.getProperty(SYSTEM_PROPERTY_SINGULAR_SERVER_HOME));
            return state;
        }

        public String getProperty(String key) {
            return props.getProperty(key);
        }

        public String setProperty(String key, String value) {
            return (String) props.setProperty(key, value);
        }

        private static class State implements Serializable {
            final Properties          propertiesBackup = new Properties();
            final Map<String, String> systemBackup     = new HashMap<>();
        }
    }
}