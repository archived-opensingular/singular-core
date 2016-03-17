/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.mform.util.transformer;

import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SISimple;
import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SIList;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.STypeList;
import br.net.mirante.singular.form.mform.STypeSimple;
import br.net.mirante.singular.form.mform.SingularFormException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
    public static <T> boolean notNull(SInstance current, STypeSimple<? extends SISimple<T>, T> tipo) {
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
    public static <T> T of(SInstance instanciaComposta, String path) {
        if (instanciaComposta instanceof SIComposite) {
            SInstance campo = ((SIComposite) instanciaComposta).getField(path);
            if (campo instanceof SISimple) {
                return Value.of((SISimple<T>) campo);
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
    public static <T> T of(SInstance instancia, STypeSimple<? extends SISimple<T>, T> tipo) {
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
     * @param value
     */
    public static void hydrate(SInstance instancia, Object value) {
        if (instancia != null) {
            if (instancia instanceof SIComposite) {
                fromMap((Map<String, Object>) value, (SIComposite) instancia);
            } else if (instancia instanceof SISimple) {
                ((SISimple) instancia).setValue(value);
            } else if (instancia instanceof SIList) {
                fromList((List<Object>) value, (SIList) instancia);
            } else {
                throw new SingularFormException("Tipo de instancia não suportado: " + instancia.getClass().getName());
            }
        }
    }

    private static void fromMap(Map<String, Object> map, SIComposite instancia) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            hydrate(instancia.getField(entry.getKey()), entry.getValue());
        }
    }

    private static void fromList(List<Object> list, SIList sList) {
        for (Object o : list) {
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
    public static Object dehydrate(SInstance value) {
        if (value != null) {
            if (value instanceof SIComposite) {
                Map<String, Object> map = new LinkedHashMap<>();
                toMap(map, (SInstance) value);
                return map;
            } else if (value instanceof SISimple) {
                return ((SISimple) value).getValue();
            } else if (value instanceof SIList) {
                List<Object> list = new ArrayList<>();
                toList(list, (SInstance) value);
                return list;
            } else {
                throw new SingularFormException("Tipo de instancia não suportado", value);
            }
        }
        return null;
    }

    private static void toMap(Map<String, Object> value, SInstance instancia) {
        if (instancia instanceof SIComposite) {
            SIComposite item = (SIComposite) instancia;
            for (SInstance i : item.getAllChildren()) {
                value.put(i.getName(), dehydrate(i));
            }
        }
    }

    private static void toList(List<Object> value, SInstance instancia) {
        if (instancia instanceof SIList<?>) {
            for (SInstance i : ((SIList<?>) instancia).getValues()) {
                value.add(dehydrate(i));
            }
        }
    }

    public <T> T of(STypeSimple<? extends SISimple<T>, T> tipo) {
        return Value.of(instancia, tipo);
    }

    public <T> boolean notNull(STypeSimple<? extends SISimple<T>, T> tipo) {
        return Value.notNull(instancia, tipo);
    }

}
