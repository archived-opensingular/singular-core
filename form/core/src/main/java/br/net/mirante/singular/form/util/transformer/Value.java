/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.util.transformer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.SIList;
import br.net.mirante.singular.form.SISimple;
import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.SType;
import br.net.mirante.singular.form.STypeComposite;
import br.net.mirante.singular.form.STypeList;
import br.net.mirante.singular.form.STypeSimple;
import br.net.mirante.singular.form.SingularFormException;

/**
 * Essa classe utilitaria realiza uma serie de operacoes sobre os valores guardados pelos MTIpos
 */
public class Value {

    private SInstance instancia;

    public Value(SInstance instancia) {
        this.instancia = instancia;
    }

    private static SInstance getInstance(SInstance instancia, SType target) {
        return (SInstance) instancia.findNearest(target).orElse(null);
    }

    /**
     * @param current instancia a partir da qual será buscada a instancia mais
     *                proxima do tipo simples tipo
     * @param tipo    um tipo simples
     * @param <T>
     * @return false se o valor do tipo simples for nulo ou se o tipo não for
     * encontrado a partir da instancia current informada
     */
    public static <T extends Serializable> boolean notNull(SInstance current, STypeSimple<? extends SISimple<T>, T> tipo) {
        return Value.of(current, tipo) != null;
    }

    public static <T> boolean notNull(SInstance current, STypeComposite tipo) {
        if (current != null && tipo != null) {
            SIComposite targetInstance = (SIComposite) getInstance(current, tipo);
            return Value.notNull(targetInstance);
        }
        return false;
    }


    public static <T> boolean notNull(SInstance current, STypeList tipo) {
        if (current != null && tipo != null) {
            SIList instanciaLista = (SIList) getInstance(current, tipo);
            return Value.notNull(instanciaLista);
        }
        return false;
    }

    public static <T> boolean notNull(SIList instanciaLista) {
        return instanciaLista != null && !instanciaLista.isEmpty();
    }

    public static <T> boolean notNull(SIComposite instanciaComposta) {
        return instanciaComposta != null && !instanciaComposta.isEmptyOfData();
    }

    public static <T> boolean notNull(SISimple instanciaSimples) {
        return instanciaSimples != null && !instanciaSimples.isEmptyOfData();
    }

    /**
     * Retorna o valor de uma instancia simples
     *
     * @param instanciaSimples
     * @param <T>
     * @return
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

    public static <T> boolean notNull(SInstance instancia) {
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
     *
     * @param instanciaComposta
     * @param path
     * @return
     */
    public static <T extends Serializable> T of(SInstance instanciaComposta, String path) {
        if (instanciaComposta instanceof SIComposite) {
            SInstance campo = ((SIComposite) instanciaComposta).getField(path);
            if (campo instanceof SISimple) {
                return Value.of((SISimple<T>) campo);
            } else if (campo instanceof SIList) {
                return (T) ofList((SIList) campo);
            }
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
     * alcançada a partir do {@paramref instancia} fornecido
     *
     * @param instancia
     * @param tipo
     * @param <T>
     * @return
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
     * parametro recursivamente. Usualmente value é o retorno do metodo
     * dehydrate.
     *
     * @param instancia
     * @param content
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
        return Value.of(instancia, tipo);
    }

    public <T extends Serializable> boolean notNull(STypeSimple<? extends SISimple<T>, T> tipo) {
        return Value.notNull(instancia, tipo);
    }

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
            if (this == o) return true;

            if (o == null || getClass() != o.getClass()) return false;

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