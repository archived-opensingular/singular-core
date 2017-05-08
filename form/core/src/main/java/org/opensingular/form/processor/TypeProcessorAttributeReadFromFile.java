/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
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

package org.opensingular.form.processor;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.apache.commons.lang3.StringUtils;
import org.opensingular.form.InternalAccess;
import org.opensingular.form.SType;
import org.opensingular.form.SingularFormException;
import org.opensingular.lib.commons.util.PropertiesUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;


/**
 * Processador que faz a leitura de atributos de {@link SType} que forem definidos em classes próprias e que possuam
 * arquivo de configuração de attributos com o mesmo nome da classe.
 * <p> Verifica se o atributo ainda não teve o seu tipo registrado. Nesse caso, coloca o atributo lidos como sendo do
 * tipo String temporariamente até a carga da definição do atributo.</p>
 *
 * @author Daniel C. Bordin on 29/04/2017.
 */
public class TypeProcessorAttributeReadFromFile {

    private static final String SUFFIX_PROPERTIES = ".properties";

    /** Instância única do processador. */
    public final static TypeProcessorAttributeReadFromFile INSTANCE = new TypeProcessorAttributeReadFromFile();

    /** Objeto de acesso a metodos internos da API. */
    private static InternalAccess internalAccess;

    /**
     * Cache com informações sobre a presença ou não de arquivos de definição de atribuitos associados a um classe
     * específica.
     */
    private final LoadingCache<Class<?>, FileDefinitions> cache = CacheBuilder.newBuilder().softValues().build(
            new CacheLoader<Class<?>, FileDefinitions>() {
                public FileDefinitions load(Class<?> key) {
                    return readDefinitionsFor(key);
                }
            });

    TypeProcessorAttributeReadFromFile() { }

    /** Método chamado logo após o registro do tipo. Nesse caso verificará se precisa transferir algum atributo. */
    public <T extends SType<?>> void onRegisterTypeByClass(@Nonnull T type, @Nonnull Class<T> typeClass) {
        FileDefinitions definitions = cache.getUnchecked(typeClass);
        for (String[] entry : definitions.definitions) {
            try {
                SType<?> target = type;
                if (entry[0] != null) {
                    target = target.getLocalType(entry[0]);
                }
                getInternalAccess().setAttributeValueSavingForLatter(target, entry[1], entry[2]);
            } catch (Exception e) {
                String key = (entry[0] == null ? "" : entry[0]) + '@' + entry[1];
                throw new SingularFormException(
                        "Erro configurando atributo da chave '" + key + "' lidos de " + definitions.url, e);
            }
        }
    }

    /**
     * Recupera a lista de valores extras de atributos associados a classe informada.
     *
     * @return Nunca null, mas pode ser um lista com conteúdo zero.
     */
    @Nonnull
    private FileDefinitions readDefinitionsFor(@Nonnull Class<?> typeClass) {
        URL url = lookForFile(typeClass);
        if (url != null) {
            try {
                Properties props = PropertiesUtils.load(url);
                if (!props.isEmpty()) {
                    return new FileDefinitions(url, readDefinitionsFor(props));
                }
            } catch (Exception e) {
                throw new SingularFormException("Erro lendo propriedades para " + typeClass.getName() + " em " + url,
                        e);
            }
        }
        return FileDefinitions.EMPTY;
    }

    /** Lê as associações de atributos a partir de um arquivo de propriedades. */
    @Nonnull
    private List<String[]> readDefinitionsFor(@Nonnull Properties props) {
        List<String[]> vals = new ArrayList<>(props.size());
        for (Map.Entry<Object, Object> entry : props.entrySet()) {
            String key = (String) entry.getKey();
            int pos = key.indexOf('@');
            if (pos == -1 || pos == key.length() - 1) {
                throw new SingularFormException("Invalid attribute definition key='" + key + "'");
            }
            String[] defintion = new String[3];
            defintion[0] = pos == 0 ? null : StringUtils.trimToNull(key.substring(0, pos));
            defintion[1] = StringUtils.trimToNull(key.substring(pos + 1));
            defintion[2] = StringUtils.trimToNull((String) entry.getValue());
            if (defintion[1] == null) {
                throw new SingularFormException("Invalid attribute definition key='" + key + "'");
            }
            vals.add(defintion);
        }
        return vals;
    }

    /** Verifica se há um arquivos com valores de atributos associados a classe informada. */
    @Nullable
    private URL lookForFile(@Nonnull Class<?> typeClass) {
        String name = typeClass.getSimpleName();
        Class<?> context = typeClass;
        for (; context.isMemberClass(); context = context.getEnclosingClass()) {
            name = context.getEnclosingClass().getSimpleName() + '$' + name;
        }
        return context.getResource(name + SUFFIX_PROPERTIES);
    }

    /** Representa um lsita de valores de atributos obtidos de um arquivo específico. */
    private static class FileDefinitions {

        public static final FileDefinitions EMPTY = new FileDefinitions(null, Collections.emptyList());

        public final URL url;
        public final List<String[]> definitions;

        private FileDefinitions(URL url, List<String[]> definitions) {
            this.url = url;
            this.definitions = definitions;
        }
    }

    /** Recebe o objeto que viabiliza executar chamadas internas da API (chamadas a métodos não públicos). */
    public static final void setInternalAccess(@Nonnull InternalAccess internalAccess) {
        TypeProcessorAttributeReadFromFile.internalAccess = internalAccess;
    }

    /** Garante a carga do objeto a chamada internas da API. */
    @Nonnull
    private static final InternalAccess getInternalAccess() {
        if (internalAccess == null) {
            InternalAccess.load();
            return Objects.requireNonNull(internalAccess);
        }
        return internalAccess;
    }
}
