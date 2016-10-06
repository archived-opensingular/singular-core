/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.lib.commons.base;

/**
 * Carrega os arquivos de propriedades do singular e dá fácil acesso ao mesmos, mediante um singleton {@link
 * SingularProperties#get()}. <p>Primeiro lê do arquivos de propriedades e depois tentar ler do diretório de
 * configuração se o mesmo existir, ou seja, as variáveis no diretório de configuração têm precedência.</p>
 *
 * @author Daniel C. Bordin
 * @author Vinicius Nunes
 */
public interface SingularProperties {
    public static final String SYSTEM_PROPERTY_SINGULAR_SERVER_HOME = "singular.server.home";
    public static final String HIBERNATE_GENERATOR                  = "flow.persistence.hibernate.generator";
    public static final String HIBERNATE_SEQUENCE_PROPERTY_PATTERN  = "flow.persistence.%s.sequence";
    public static final String FILEUPLOAD_GLOBAL_MAX_REQUEST_SIZE   = "singular.fileupload.global_max_request_size";
    public static final String FILEUPLOAD_GLOBAL_MAX_FILE_SIZE      = "singular.fileupload.global_max_file_size";
    public static final String SINGULAR_DEV_MODE      = "singular.development";



    public static SingularProperties get() {
        return SingularPropertiesImpl.get();
    }

    /**
     * Verifica se a propriedade de nome informado existe.
     */
    public boolean containsKey(String key);

    /**
     * Retorna o valor da propriedade solicitada. Pode retornar null.
     */
    public String getProperty(String key);

    public boolean isTrue(String key);

    public boolean isFalse(String key);

    default String getSingularServerHome() {
        return System.getProperty(SYSTEM_PROPERTY_SINGULAR_SERVER_HOME);
    }
}