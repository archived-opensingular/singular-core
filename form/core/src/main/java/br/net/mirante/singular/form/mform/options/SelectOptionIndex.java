package br.net.mirante.singular.form.mform.options;

import br.net.mirante.singular.commons.util.Loggable;
import br.net.mirante.singular.form.mform.SIList;
import br.net.mirante.singular.form.mform.SInstance;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

/**
 * Indexa as opções retornadas por um provider.
 * Retorna na lista de opções a última lista de valores retornada pelo provider, mas
 * resolver chave e valor de qualquer instancia já retornada anteriormente pelo provider.
 * Mantém as chaves retornadas anteriormente em um cache de opções antigas.
 * Eventualmente valores contidos no cache de opções antigas pode ser promovido a opção ativa e mapeado de volta para
 * a lista de valores.
 */
public class SelectOptionIndex implements Loggable {

    private BiMap<String, SInstance> optionsKeyInstanceMap;
    private BiMap<String, String> optionsKeylabelMap;

    private BiMap<String, SInstance> old_optionsKeyInstanceMap;
    private BiMap<String, String> old_optionsKeylabelMap;

    private BigInteger keySeed = BigInteger.ZERO;

    public SelectOptionIndex() {
        optionsKeyInstanceMap = HashBiMap.create();
        optionsKeylabelMap = HashBiMap.create();

        old_optionsKeyInstanceMap = HashBiMap.create();
        old_optionsKeylabelMap = HashBiMap.create();
    }

    private String newUniqueKey() {
        keySeed = keySeed.add(BigInteger.ONE);
        return String.valueOf(keySeed);
    }

    public boolean containsKey(Object key) {
        return optionsKeyInstanceMap.containsKey(key)
                || old_optionsKeyInstanceMap.containsKey(key);
    }

    public boolean containsInstance(SInstance instance) {
        return optionsKeyInstanceMap.inverse().containsKey(instance)
                || old_optionsKeyInstanceMap.inverse().containsKey(instance);

    }

    public boolean containsLabel(String label) {
        return optionsKeylabelMap.inverse().containsKey(label)
                || old_optionsKeylabelMap.inverse().containsKey(label);
    }


    public SInstance getInstance(String label) {
        String key = optionsKeylabelMap.inverse().get(label);
        SInstance instance = null;
        if (key != null) {
            instance = optionsKeyInstanceMap.get(key);
        } else {
            key = old_optionsKeylabelMap.inverse().get(label);
            instance = old_optionsKeyInstanceMap.get(key);
        }
        return instance;
    }

    public SInstance getInstance(Object key) {
        SInstance instance = optionsKeyInstanceMap.get(key);
        if (instance == null) {
            instance = old_optionsKeyInstanceMap.get(key);
        }
        return instance;
    }

    public Object getKey(SInstance instance) {
        Object key = optionsKeyInstanceMap.inverse().get(instance);
        if (key == null) {
            key = old_optionsKeyInstanceMap.inverse().get(instance);
        }
        return key;
    }

    public Object getKey(String label) {
        Object key = optionsKeylabelMap.inverse().get(label);
        if (key == null) {
            key = old_optionsKeylabelMap.inverse().get(label);
        }
        return key;
    }

    public String getLabel(Object key) {
        String label = optionsKeylabelMap.get(key);
        if (label == null) {
            label = old_optionsKeylabelMap.get(key);
        }
        return label;
    }

    public String getLabel(SInstance instance) {
        String key = optionsKeyInstanceMap.inverse().get(instance);
        String label = null;
        if (key != null) {
            label = optionsKeylabelMap.get(key);
        } else {
            key = old_optionsKeyInstanceMap.inverse().get(instance);
            label = old_optionsKeylabelMap.get(key);
        }
        return label;
    }


    public void put(String label, SInstance instance) {
        String key = newUniqueKey();
        optionsKeyInstanceMap.put(key, instance);
        optionsKeylabelMap.put(key, instance.getSelectLabel());
    }

    private void putBack(String key, String label, SInstance instance) {
        optionsKeyInstanceMap.put(key, instance);
        optionsKeylabelMap.put(key, instance.getSelectLabel());
    }

    private void keepOld(String key, String label, SInstance instance) {
        old_optionsKeyInstanceMap.put(key, instance);
        old_optionsKeylabelMap.put(key, instance.getSelectLabel());
    }

    public Map<String, String> listKeyLabel() {
        return optionsKeylabelMap;
    }

    public Map<String, String> listKeyLabelOld() {
        return old_optionsKeylabelMap;
    }

    public Map<String, String> listAllKeyLabel() {
        Map<String, String> all = HashBiMap.create();
        all.putAll(optionsKeylabelMap);
        all.putAll(old_optionsKeylabelMap);
        return all;
    }


    /**
     * Atualiza a lista de valores válidos promovendo valores do cache e mapeando novos valores.
     * @param options
     */
    public void reindex(SIList<? extends SInstance> options) {
        removeValuesNotPresent(options);
        restoreOldValues(options);
        mapNewValues(options);
    }

    /**
     * Mapeia novos valores para a nova lista de valores válidos
     * @param options
     */
    private void mapNewValues(SIList<? extends SInstance> options) {
        for (br.net.mirante.singular.form.mform.SInstance instance : options) {
                    /* ignora silenciosamente valores duplicados */
            if (!containsInstance(instance) &&
                    !containsLabel(Optional
                            .ofNullable(instance)
                            .map(SInstance::getSelectLabel)
                            .orElse(null))) {

                put(instance.getSelectLabel(), instance);
            }
        }
    }

    /**
     * Compara a lista anterior de valores válidos com a nova lista de valores válidos e move
     * os valores que não são comuns às duas listas para o cahce de valores antigos.
     * @param options
     */
    private void removeValuesNotPresent(SIList<? extends SInstance> options) {
        Iterator<SInstance> it = optionsKeyInstanceMap.values().iterator();
        while (it.hasNext()) {
            SInstance instance = it.next();
            if (!options.getValues().contains(instance)) {
                String key = optionsKeyInstanceMap.inverse().get(instance);
                keepOld(key, optionsKeylabelMap.get(key), instance);

                it.remove();
                optionsKeylabelMap.remove(key);
            }
        }


    }

    /**
     * Compara a lista anterior de valores válidos com o cache de valores antigos e move
     * os valores que são comuns às duas listas para a nova lista de valores válidos mantendo o mapeamento
     * de chaves já realizado anteriormente.
     * @param options
     */
    private void restoreOldValues(SIList<? extends SInstance> options) {
        Iterator<SInstance> it = old_optionsKeyInstanceMap.values().iterator();
        while (it.hasNext()) {
            SInstance instance = it.next();
            if (options.getValues().contains(instance)) {
                String key = old_optionsKeyInstanceMap.inverse().get(instance);
                putBack(key, old_optionsKeylabelMap.get(key), instance);
                it.remove();
                old_optionsKeylabelMap.remove(key);
            }
        }
    }
}
