/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.commons.base;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Carrega os arquivos de propriedades do singular e dá fácil acesso ao mesmos, mediante um singleton {@link
 * SingularProperties#INSTANCE}. <p>Primeiro lê do arquivos de propriedades e depois tentar ler do diretório de
 * configuração se o mesmo existir, ou seja, as variáveis no diretório de configuração têm precedência.</p>
 *
 * @author Daniel C. Bordin
 * @author Vinicius Nunes
 */
public enum SingularProperties {

    /**
     * Singleton de propriedades.
     */
    INSTANCE;

    public static final String SYSTEM_PROPERTY_SINGULAR_SERVER_HOME = "singular.server.home";
    public static final String HIBERNATE_GENERATOR = "flow.persistence.hibernate.generator";
    public static final String HIBERNATE_SEQUENCE_PROPERTY_PATTERN = "flow.persistence.%s.sequence";

    private static final Logger LOGGER = LoggerFactory.getLogger(SingularProperties.class);
    private static final String DEFAULT_PROPERTIES_FILENAME = "singular-defaults.properties";

    private Properties properties;

    private static final String[] PROPERTIES_FILES_NAME = { "singular-form-service.properties", "singular.properties" };

    public static SingularProperties get() {
        return INSTANCE;
    }
    
    private Properties getProperties() {
        //Faz leitura lazy das propriedades, pois no construtor da enum, as variáveis estáticas não estão disponíveis
        if (properties == null) {
            synchronized (this) {
                if (properties == null) {
                    reload();
                }
            }
        }
        return properties;
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
                if (LOGGER.isWarnEnabled()) {
                    LOGGER.warn("   Não foi encontrado o arquivo no classpath: {}", name);
                }
            } else {
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info("   Lendo arquivo de propriedades '{}' em {}", name, url);
                }
                newProperties = loadNotOverriding(newProperties, name, url);
            }
        }
        Properties resolvedProperties = newProperties == null ? new Properties() : newProperties;

        appendDefaultProperties(resolvedProperties, DEFAULT_PROPERTIES_FILENAME);

        return resolvedProperties;
    }

    /**
     * Adiciona as propriedades default que já não foram carregadas dos arquivos em PROPERTIES_FILES_NAME.
     * @param resolvedProperties
     */
    protected void appendDefaultProperties(Properties resolvedProperties, String defaultPropertiesFilename) {
        try (InputStream input = SingularProperties.class.getResourceAsStream(defaultPropertiesFilename);
            Reader reader = new InputStreamReader(input, "utf-8")) {
            Properties defaults = new Properties();
            defaults.load(reader);
            for (String key : defaults.stringPropertyNames()) {
                if (!resolvedProperties.containsKey(key)) {
                    resolvedProperties.setProperty(key, StringUtils.defaultString(defaults.getProperty(key)));
                }
            }
        } catch (IOException ex) {
            throw new IllegalStateException("");
        }
    }

    private Properties loadNotOverriding(Properties newProperties, String propertiesName, URL propertiesUrl) {
        Properties p = new Properties();
        try {
            p.load(propertiesUrl.openStream());
        } catch (IOException e) {
            throw new SingularException("Erro lendo arquivo de propriedade", e).add("url", propertiesUrl);
        }
        if (newProperties == null) {
            return p;
        }
        for (Map.Entry<Object, Object> entry : p.entrySet()) {
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

    private URL findProperties(String name) {
        try {
            URL url = add(null, name, ClassLoader.getSystemClassLoader().getResources(name));
            return add(url, name, SingularProperties.class.getClassLoader().getResources(name));
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
    public void reloadAndOverrideWith(URL propertiesURL) {
        reload();
        loadOverriding(getProperties(), propertiesURL);
    }

    /**
     * Verifica se a propriedade de nome informado existe.
     */
    public boolean containsKey(String key) {
        return getProperties().containsKey(key);
    }

    /**
     * Retorna o valor da propriedade solicitada. Pode retornar null.
     */
    public String getProperty(String key) {
        return getProperties().getProperty(key);
    }

}
