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

    String SYSTEM_PROPERTY_SINGULAR_SERVER_HOME = "singular.server.home";
    String HIBERNATE_GENERATOR                  = "flow.persistence.hibernate.generator";
    String HIBERNATE_SEQUENCE_PROPERTY_PATTERN  = "flow.persistence.%s.sequence";
    String SINGULAR_EAGER_LOAD_FLOW_DEFINITIONS = "singular.flow.eager.load";
    String SINGULAR_SEND_EMAIL                  = "singular.send.email";

    String SINGULAR_DEV_MODE    = "singular.development";
    String SINGULAR_SERVER_ADDR = "singular.server.address";

    // Limites globais são limites máximos, não configuráveis por arquivo.
    String FILEUPLOAD_GLOBAL_MAX_REQUEST_SIZE = "singular.fileupload.global_max_request_size";
    String FILEUPLOAD_GLOBAL_MAX_FILE_SIZE    = "singular.fileupload.global_max_file_size";
    String FILEUPLOAD_GLOBAL_MAX_FILE_COUNT   = "singular.fileupload.global_max_file_count";
    String FILEUPLOAD_GLOBAL_MAX_FILE_AGE     = "singular.fileupload.global_max_file_age";

    // Limites default são limites configuráveis por arquivo. Não podem exceder os limites globais.
    String FILEUPLOAD_DEFAULT_MAX_REQUEST_SIZE = "singular.fileupload.default_max_request_size";
    String FILEUPLOAD_DEFAULT_MAX_FILE_SIZE    = "singular.fileupload.default_max_file_size";

    // Identifica se o singular deve usar o banco em memória, ou se conectar a um banco externo.
    String USE_EMBEDDED_DATABASE = "singular.database.embbeded";
    String JNDI_DATASOURCE       = "singular.jndi.name.datasource";

    // Identifica o nome do schema que deve ser utilizado
    String CUSTOM_SCHEMA_NAME = "singular.custom.schema.name";

    String DEFAULT_CAS_ENABLED   = "singular.cas.default.enabled";
    String DISABLE_AUTHORIZATION = "singular.auth.disable";

    String REST_ALLOWED_COMMON_NAME = "singular.rest.allowed.common.name";

//    ParmDef<String> ADDRESS = new ParmDef<>("Asasd.ad.asda " , String.class, "dadad")

    static SingularProperties get() {
        return SingularPropertiesImpl.get();
    }

    /**
     * Verifica se a propriedade de nome informado existe.
     */
    boolean containsKey(String key);

    /**
     * Retorna o valor da propriedade solicitada. Pode retornar null.
     */
    String getProperty(String key);


    /**
     * Retorna o valor da propriedade solicitada. Retornar {@param defaultValue} se
     * a propriedade retornar null;
     */
    default String getProperty(String key, String defaultValue) {
        String value = getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }


    boolean isTrue(String key);

    boolean isFalse(String key);

    default String getSingularServerHome() {
        return System.getProperty(SYSTEM_PROPERTY_SINGULAR_SERVER_HOME);
    }

}