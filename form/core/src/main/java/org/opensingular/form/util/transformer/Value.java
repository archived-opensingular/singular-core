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

package org.opensingular.form.util.transformer;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SIList;
import org.opensingular.form.SISimple;
import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.STypeList;
import org.opensingular.form.STypeSimple;
import org.opensingular.form.SingularFormException;
import org.opensingular.form.document.SDocument;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Essa classe utilitaria realiza uma serie de operacoes sobre os valores guardados pelos MTIpos
 */
public class Value {

    private final SInstance instance;

    public Value(SInstance instance) {
        this.instance = instance;
    }

    private static SInstance getInstance(SInstance instance, SType target) {
        return (SInstance) instance.findNearest(target).orElse(null);
    }

    /**
     * @param current instancia a partir da qual será buscada a instancia mais
     *                proxima do tipo simples tipo
     * @param type    um tipo simples
     * @return false se o valor do tipo simples for nulo ou se o tipo não for
     * encontrado a partir da instancia current informada
     */
    public static <T extends Serializable> boolean notNull(SInstance current, STypeSimple<? extends SISimple<T>, T> type) {
        return Value.of(current, type) != null;
    }

    public static boolean notNull(SInstance current, STypeComposite type) {
        if (current != null && type != null) {
            SIComposite targetInstance = (SIComposite) getInstance(current, type);
            return Value.notNull(targetInstance);
        }
        return false;
    }


    public static boolean notNull(SInstance current, STypeList type) {
        if (current != null && type != null) {
            SIList listInstance = (SIList) getInstance(current, type);
            return Value.notNull(listInstance);
        }
        return false;
    }

    public static boolean notNull(SIList listInstance) {
        return listInstance != null && !listInstance.isEmpty();
    }

    public static boolean notNull(SIComposite compositeInstance) {
        return compositeInstance != null && !compositeInstance.isEmptyOfData();
    }

    public static boolean notNull(SISimple simpleInstance) {
        return simpleInstance != null && !simpleInstance.isEmptyOfData();
    }

    /**
     * Retorna o valor de uma instancia simples
     */
    public static <T> T of(SISimple<?> simpleInstance) {
        if (simpleInstance != null) {
            return (T) simpleInstance.getValue();
        }
        return null;
    }

    public static <T> List<T> ofList(SIList<?> list) {
        if (list != null) {
            return (List<T>) list.getValue();
        }
        return null;
    }

    public static boolean notNull(SInstance instance) {
        if (instance instanceof SIComposite) {
            return Value.notNull((SIComposite) instance);
        } else if (instance instanceof SISimple) {
            return Value.notNull((SISimple) instance);
        } else if (instance instanceof SIList) {
            return Value.notNull((SIList) instance);
        } else {
            throw new SingularFormException("Tipo de instancia não suportado", instance);
        }
    }

    /**
     * Passar a usar {@link SInstance#getValue(String)}.
     */
    @Deprecated
    public static <T extends Serializable> T of(SInstance compositeInstance, String...path) {
        if (compositeInstance instanceof SIComposite) {
            SInstance campo = ((SIComposite) compositeInstance).getField(Arrays.stream(path).collect(Collectors.joining(".")));
            if (campo instanceof SISimple) {
                return Value.of((SISimple<T>) campo);
            } else if (campo instanceof SIList) {
                return (T) ofList((SIList) campo);
            }
        }
        return null;
    }

    public static String stringValueOf(SInstance compositeInstance, String path) {
        Serializable s = of(compositeInstance, path);
        if (s != null) {
            return String.valueOf(s);
        }
        return null;
    }

    public static <T> List<T> ofList(SInstance compositeInstance, String path) {
        if (compositeInstance instanceof SIComposite) {
            SInstance campo = ((SIComposite) compositeInstance).getField(path);
            if (campo instanceof SIList) {
                return Value.ofList((SIList<?>) campo);
            }
        }
        return null;
    }

    /**
     * Retorna o valor de uma instancia de um tipo simples que pode ser
     * alcançada a partir do {@param instancia} fornecido
     */
    public static <T extends Serializable> T of(SInstance instance, STypeSimple<? extends SISimple<T>, T> type) {
        if (instance != null && type != null) {
            SISimple targetInstance = (SISimple) getInstance(instance, type);
            if (targetInstance != null) {
                return (T) Value.of(targetInstance);
            }
        }
        return null;
    }

    /**
     * Configura os valores contidos em value na MInstancia passara como
     * parametro recursivamente. Usualmente content é o retorno do metodo
     * dehydrate.
     */
    public static void hydrate(SInstance instance, Content content) {
        if (instance != null) {
            if (instance instanceof SIComposite) {
                fromMap((Map<String, Content>) content.getRawContent(), (SIComposite) instance);
            } else if (instance instanceof SISimple) {
                if (content.getRawContent() == null) {
                    instance.clearInstance();
                } else {
                    instance.setValue(content.getRawContent());
                }
            } else if (instance instanceof SIList) {
                fromList((List<Content>) content.getRawContent(), (SIList) instance);
            } else {
                throw new SingularFormException("Tipo de instancia não suportado: " + instance.getClass().getName(), instance);
            }
        }
    }

    private static void fromMap(Map<String, Content> map, SIComposite instance) {
        for (Map.Entry<String, Content> entry : map.entrySet()) {
            hydrate(instance.getField(entry.getKey()), entry.getValue());
        }
    }

    private static void fromList(List<Content> list, SIList sList) {
        for (Content o : list) {
            SInstance newInstance = sList.addNew();
            hydrate(newInstance, o);
        }
    }

    /**
     * Extrai para objetos serializáveis todos os dados de uma MIinstancia
     * recursivamente
     *
     * @param value MIinstancia a partir da qual se deseja extrair os dados
     * @return Objetos serializáveis representando os dados da MInstancia
     */
    public static Content dehydrate(SInstance value) {
        if (value != null) {
            if (value instanceof SIComposite) {
                LinkedHashMap<String, Content> map = new LinkedHashMap<>();
                toMap(map, value);
                return new Content(map, value.getType().getName());
            } else if (value instanceof SISimple) {
                return new Content((Serializable) value.getValue(), value.getType().getName());
            } else if (value instanceof SIList) {
                List<Content> list = new ArrayList<>();
                toList(list, value);
                return new Content((Serializable) list, value.getType().getName());
            } else {
                throw new SingularFormException("Tipo de instancia não suportado", value);
            }
        }
        return null;
    }

    private static void toMap(Map<String, Content> value, SInstance instance) {
        if (instance instanceof SIComposite) {
            SIComposite item = (SIComposite) instance;
            for (SInstance i : item.getAllChildren()) {
                value.put(i.getName(), dehydrate(i));
            }
        }
    }

    private static void toList(List<Content> value, SInstance instance) {
        if (instance instanceof SIList<?>) {
            for (SInstance i : ((SIList<?>) instance).getValues()) {
                value.add(dehydrate(i));
            }
        }
    }

    public <T extends Serializable> T of(STypeSimple<? extends SISimple<T>, T> type) {
        return Value.of(instance, type);
    }

    public <T extends Serializable> boolean notNull(STypeSimple<? extends SISimple<T>, T> type) {
        return Value.notNull(instance, type);
    }

    /** Copia os valores de um formulário para outro. Presupõem que os formulários são do mesmo tipo. */
    public static void copyValues(SDocument origin, SDocument destiny) {
        copyValues(origin.getRoot(), destiny.getRoot());
    }

    /** Copia os valores de uma instância para outra. Presupõem que as instâncias são do mesmo tipo. */
    public static void copyValues(SInstance origin, SInstance target) {
        target.clearInstance();
        hydrate(target, dehydrate(origin));
    }

    public static class Content implements Serializable {

        private final Serializable rawContent;
        private final String       typeName;

        public Content(Serializable rawContent, String typeName) {
            this.rawContent = rawContent;
            this.typeName = typeName;
        }

        public Serializable getRawContent() {
            return rawContent;
        }

        public String getTypeName() {
            return typeName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            } else if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Content content = (Content) o;

            return new EqualsBuilder()
                    .append(rawContent, content.rawContent)
                    .append(typeName, content.typeName)
                    .isEquals();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder(17, 37)
                    .append(rawContent)
                    .append(typeName)
                    .toHashCode();
        }

        @Override
        public String toString() {
            return String.format("Tipo: %s, Objeto: %s ", typeName, ObjectUtils.defaultIfNull(rawContent, "").toString());
        }
    }

}