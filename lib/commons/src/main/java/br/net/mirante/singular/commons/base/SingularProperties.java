/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.commons.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

public enum SingularProperties {

    INSTANCE;

    public static final  String     HIBERNATE_GENERATOR                 = "flow.persistence.hibernate.generator";
    public static final  String     HIBERNATE_SEQUENCE_PROPERTY_PATTERN = "flow.persistence.%s.sequence";
    private static final Logger     logger                              = LoggerFactory.getLogger(SingularProperties.class);
    private              Properties properties                          = new Properties();

    SingularProperties() {
        reload();
    }

    public void reload() {
        final String path = System.getProperty("singular.server.props.server", "singular.properties");
        InputStream  in   = getFromFileSystem(path);
        if (in == null) {
            in = getFromClassLoader(path);
        }
        if (in != null) {
            load(in);
        }
    }

    private InputStream getFromFileSystem(String path) {
        try {
            return new FileInputStream(path);
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    private InputStream getFromClassLoader(String path) {
        InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream(path);
        if (is == null) {
            is = SingularProperties.class.getClassLoader().getResourceAsStream(path);
        }
        return is;
    }

    private void load(InputStream is) {
        try {
            this.properties.clear();
            properties.load(is);
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
        return properties.getProperty(key);
    }

    public Properties getProperties() {
        return properties;
    }

}