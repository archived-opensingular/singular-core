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
import org.opensingular.form.*;
import org.opensingular.form.document.SDocument;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Essa classe utilitaria realiza uma serie de operacoes sobre os valores guardados pelos MTIpos
 */
public class Value {

    private final SInstance instance;

    public Value(SInstance instance) {
        this.instance = instance;
    }

    private static SInstance getInstance(SInstance instancia, SType target) {
        return (SInstance) instancia.findNearest(target).orElse(null);
    }

    /**
     * @param current instancia a partir da qual será buscada a instancia mais
     *                proxima do tipo simples tipo
     * @param tipo    um tipo simples
     * @return false se o valor do tipo simples for nulo ou se o tipo não for
     * encontrado a partir da instancia current informada
     */
    public static <T extends Serializable> boolean notNull(SInstance current, STypeSimple<? extends SISimple<T>, T> tipo) {
        return Value.of(current, tipo) != null;
    }

    public static boolean notNull(SInstance current, STypeComposite tipo) {
        if (current != null && tipo != null) {
            SIComposite targetInstance = (SIComposite) getInstance(current, tipo);
            return Value.notNull(targetInstance);
        }
        return false;
    }


    public static boolean notNull(SInstance current, STypeList tipo) {
        if (current != null && tipo != null) {
            SIList instanciaLista = (SIList) getInstance(current, tipo);
            return Value.notNull(instanciaLista);
        }
        return false;
    }

    public static boolean notNull(SIList instanciaLista) {
        return instanciaLista != null && !instanciaLista.isEmpty();
    }

    public static boolean notNull(SIComposite instanciaComposta) {
        return instanciaComposta != null && !instanciaComposta.isEmptyOfData();
    }

    public static boolean notNull(SISimple instanciaSimples) {
        return instanciaSimples != null && !instanciaSimples.isEmptyOfData();
    }

    /**
     * Retorna o valor de uma instancia simples
     */
    public static <T> T of(SISimple<?> instanciaSimples) {
        if (instanciaSimples != null) {
            return (T) instanciaSimples.getValue();
        }
        return null;
    }

    public static <T> List<T> ofList(SIList<?> lista) {
        if (lista != null) {
            return (List<T>) lista.getValue();
        }
        return null;
    }

    public static boolean notNull(SInstance instancia) {
        if (instancia instanceof SIComposite) {
            return Value.notNull((SIComposite) instancia);
        } else if (instancia instanceof SISimple) {
            return Value.notNull((SISimple) instancia);
        } else if (instancia instanceof SIList) {
            return Value.notNull((SIList) instancia);
        } else {
            throw new SingularFormException("Tipo de instancia não suportado", instancia);
        }
    }

    /**
     * Retorna o valor de uma instancia filha simples a partir da instancia
     * composta informada
     */
    public static <T extends Serializable> T of(SInstance instanciaComposta, String...path) {
        if (instanciaComposta instanceof SIComposite) {
            SInstance campo = ((SIComposite) instanciaComposta).getField(Arrays.stream(path).collect(Collectors.joining(".")));
            if (campo instanceof SISimple) {
                return Value.of((SISimple<T>) campo);
            } else if (campo instanceof SIList) {
                return (T) ofList((SIList) campo);
            }
        }
        return null;
    }

    public static <T extends Serializable> Optional<T> ofOpt(SInstance instanciaComposta, String...path) {
        if (instanciaComposta instanceof SIComposite) {
            Optional<SInstance> campoOpt = ((SIComposite) instanciaComposta).getFieldOpt(Arrays.stream(path).collect(Collectors.joining(".")));
            if (campoOpt.isPresent()) {
                SInstance campo = campoOpt.get();
                if (campo instanceof SISimple) {
                    return Optional.ofNullable(Value.of((SISimple<T>) campo));
                } else if (campo instanceof SIList) {
                    return Optional.ofNullable((T) ofList((SIList) campo));
                }
            }
        }
        return Optional.empty();
    }

    public static String stringValueOf(SInstance instanciaComposta, String path) {
        Serializable s = of(instanciaComposta, path);
        if (s != null) {
            return String.valueOf(s);
        }
        return null;
    }

    public static <T> List<T> ofList(SInstance instanciaComposta, String path) {
        if (instanciaComposta instanceof SIComposite) {
            SInstance campo = ((SIComposite) instanciaComposta).getField(path);
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
    public static <T extends Serializable> T of(SInstance instancia, STypeSimple<? extends SISimple<T>, T> tipo) {
        if (instancia != null && tipo != null) {
            SISimple targetInstance = (SISimple) getInstance(instancia, tipo);
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
    public static void hydrate(SInstance instancia, Content content) {
        if (instancia != null) {
            if (instancia instanceof SIComposite) {
                fromMap((Map<String, Content>) content.getRawContent(), (SIComposite) instancia);
            } else if (instancia instanceof SISimple) {
                if (content.getRawContent() == null) {
                    instancia.clearInstance();
                } else {
                    instancia.setValue(content.getRawContent());
                }
            } else if (instancia instanceof SIList) {
                fromList((List<Content>) content.getRawContent(), (SIList) instancia);
            } else {
                throw new SingularFormException("Tipo de instancia não suportado: " + instancia.getClass().getName());
            }
        }
    }

    private static void fromMap(Map<String, Content> map, SIComposite instancia) {
        for (Map.Entry<String, Content> entry : map.entrySet()) {
            hydrate(instancia.getField(entry.getKey()), entry.getValue());
        }
    }

    private static void fromList(List<Content> list, SIList sList) {
        for (Content o : list) {
            SInstance novo = sList.addNew();
            hydrate(novo, o);
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

    private static void toMap(Map<String, Content> value, SInstance instancia) {
        if (instancia instanceof SIComposite) {
            SIComposite item = (SIComposite) instancia;
            for (SInstance i : item.getAllChildren()) {
                value.put(i.getName(), dehydrate(i));
            }
        }
    }

    private static void toList(List<Content> value, SInstance instancia) {
        if (instancia instanceof SIList<?>) {
            for (SInstance i : ((SIList<?>) instancia).getValues()) {
                value.add(dehydrate(i));
            }
        }
    }

    public <T extends Serializable> T of(STypeSimple<? extends SISimple<T>, T> tipo) {
        return Value.of(instance, tipo);
    }

    public <T extends Serializable> boolean notNull(STypeSimple<? extends SISimple<T>, T> tipo) {
        return Value.notNull(instance, tipo);
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