/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.commons.base;

/**
 * Carrega os arquivos de propriedades do singular e dá fácil acesso ao mesmos, mediante um singleton {@link
 * SingularProperties#INSTANCE}. <p>Primeiro lê do arquivos de propriedades e depois tentar ler do diretório de
 * configuração se o mesmo existir, ou seja, as variáveis no diretório de configuração têm precedência.</p>
 *
 * @author Daniel C. Bordin
 * @author Vinicius Nunes
 */
public interface SingularProperties {
    public static final String SYSTEM_PROPERTY_SINGULAR_SERVER_HOME = "singular.server.home";
    public static final String HIBERNATE_GENERATOR                  = "flow.persistence.hibernate.generator";
    public static final String HIBERNATE_SEQUENCE_PROPERTY_PATTERN  = "flow.persistence.%s.sequence";

    public static SingularProperties get() {
        return SingularPropertiesImpl.INSTANCE;
    }

    /**
     * Limpa as propriedades da memoria e força recarga a partir da memória e classPath.
     */
    public void reload();

    /**
     * Verifica se a propriedade de nome informado existe.
     */
    public boolean containsKey(String key);

    /**
     * Retorna o valor da propriedade solicitada. Pode retornar null.
     */
    public String getProperty(String key);

}