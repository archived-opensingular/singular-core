/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
    public static final String SINGULAR_EAGER_LOAD_FLOW_DEFINITIONS = "singular.flow.eager.load";

    public static final String SINGULAR_DEV_MODE                    = "singular.development";
    public static final String SINGULAR_SERVER_ADDR                 = "singular.server.address";

    // Limites globais são limites máximos, não configuráveis por arquivo.
    public static final String FILEUPLOAD_GLOBAL_MAX_REQUEST_SIZE   = "singular.fileupload.global_max_request_size";
    public static final String FILEUPLOAD_GLOBAL_MAX_FILE_SIZE      = "singular.fileupload.global_max_file_size";
    public static final String FILEUPLOAD_GLOBAL_MAX_FILE_COUNT     = "singular.fileupload.global_max_file_count";
    public static final String FILEUPLOAD_GLOBAL_MAX_FILE_AGE       = "singular.fileupload.global_max_file_age";

    // Limites default são limites configuráveis por arquivo. Não podem exceder os limites globais.
    public static final String FILEUPLOAD_DEFAULT_MAX_REQUEST_SIZE  = "singular.fileupload.default_max_request_size";
    public static final String FILEUPLOAD_DEFAULT_MAX_FILE_SIZE     = "singular.fileupload.default_max_file_size";

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