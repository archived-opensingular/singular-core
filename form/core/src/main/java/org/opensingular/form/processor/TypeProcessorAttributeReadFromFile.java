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

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.opensingular.form.InternalAccess;
import org.opensingular.form.SType;
import org.opensingular.form.SingularFormException;
import org.opensingular.internal.lib.commons.xml.MElement;
import org.opensingular.internal.lib.commons.xml.MParser;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;


/**
 * Processador que faz a leitura de atributos de {@link SType} que forem definidos em classes próprias e que possuam
 * arquivo de configuração de attributos com o mesmo nome da classe (arquivos XML).
 * <p> Verifica se o atributo ainda não teve o seu tipo registrado. Nesse caso, coloca o atributo lidos como sendo do
 * tipo String temporariamente até a carga da definição do atributo.</p>
 *
 * @author Daniel C. Bordin on 29/04/2017.
 * @author torquato.neto Refatoração para arquivos XML 31/05/2017.
 */
public class TypeProcessorAttributeReadFromFile {

    private static final String SUFFIX_XML = ".xml";

    /** Instância única do processador. */
    public final static TypeProcessorAttributeReadFromFile INSTANCE = new TypeProcessorAttributeReadFromFile();

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
        for (AttibuteEntry entry : definitions.definitions) {
            try {
                SType<?> target = type;
                if (entry.subFieldPath != null) {
                    target = target.getLocalType(entry.subFieldPath);
                }
                InternalAccess.INTERNAL.setAttributeValueSavingForLatter(target, entry.attributeName,
                        entry.attributeValue);
            } catch (Exception e) {
                throw new SingularFormException(String.format("Erro configurando atributo da chave (field: %s,  attributeName: %s) lidos de %s",
                        entry.subFieldPath, entry.attributeName, definitions.url), e);
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
                MElement xml = MParser.parse(url.openStream(), false, false);
                if ( xml != null){
                    return new FileDefinitions(url, readDefinitionsFor(xml));
                }
            } catch (Exception e) {
                throw new SingularFormException("Erro lendo propriedades para " + typeClass.getName() + " em " + url,
                        e);
            }
        }
        return FileDefinitions.EMPTY;
    }

    @Nonnull
    public static List<AttibuteEntry> readDefinitionsFor(@Nonnull MElement xml) {
        List<AttibuteEntry> vals = new ArrayList<AttibuteEntry>();
        NodeList attrs = xml.getElementsByTagName("attr");

        if(attrs.getLength() == 0){
            throw new SingularFormException("The tag <attr><attr/> is mandatory");
        }
        
        for (int i = 0; i < attrs.getLength(); i++) {
            
            AttibuteEntry definition = new AttibuteEntry();

            if (attrs.item(i).getNodeType() == Node.ELEMENT_NODE) {

                if (attrs.item(i).getAttributes() != null) {
                    if (attrs.item(i).getAttributes().getNamedItem("field") != null) {
                        definition.subFieldPath = attrs.item(i).getAttributes().getNamedItem("field").getTextContent();
                    }
                    if (attrs.item(i).getAttributes().getNamedItem("name") != null) {
                        definition.attributeName = attrs.item(i).getAttributes().getNamedItem("name").getTextContent();
                    }
                    definition.attributeValue = attrs.item(i).getTextContent();
                }
                if (StringUtils.isEmpty(definition.attributeName)) {
                    throw new SingularFormException("O nome do atributo é obrigatório");
                }
                //TODO verificar com daniel se que bloquear para atributos nao conhecidos
//              if (!definition.attributeName.startsWith(SDictionary.SINGULAR_PACKAGES_PREFIX)) {
//                  throw new SingularFormException(String.format("attribute name not supported, it should be started with %s", SDictionary.SINGULAR_PACKAGES_PREFIX));
//              }

                vals.add(definition);
            }
        }
        
        return vals;
    }

    
    /** Verifica se há um arquivos com valores de atributos associados a classe informada. */
    @Nullable
    private URL lookForFile(@Nonnull Class<?> typeClass) {
        String name = typeClass.getSimpleName();
        Class<?> context = typeClass;
        for (; context.isMemberClass(); context = context.getEnclosingClass()) {
            name = concatNames(context, name);
        }
        return context.getResource(name + SUFFIX_XML);
    }

    @Nonnull
    private String concatNames(@Nonnull Class<?> context, @Nonnull String name) {
        return context.getEnclosingClass().getSimpleName() + '$' + name;
    }

    /** Representa um lsita de valores de atributos obtidos de um arquivo específico. */
    private static class FileDefinitions {

        public static final FileDefinitions EMPTY = new FileDefinitions(null, Collections.emptyList());

        public final URL url;
        public final List<AttibuteEntry> definitions;

        private FileDefinitions(URL url, List<AttibuteEntry> definitions) {
            this.url = url;
            this.definitions = definitions;
        }
    }

    static class AttibuteEntry {

        String subFieldPath;
        String attributeName;
        String attributeValue;
    }
}
