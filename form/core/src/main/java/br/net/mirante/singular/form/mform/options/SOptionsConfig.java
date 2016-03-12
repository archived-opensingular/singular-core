package br.net.mirante.singular.form.mform.options;

import java.math.BigInteger;
import java.util.LinkedHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Throwables;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SIList;
import br.net.mirante.singular.form.mform.SingularFormException;

/**
 * Mapeia cada MInstancia fornecida pelo OptionsProvider para uma par de
 * descricao e chave (label e key) É reponsável também por devolver a
 * MIinstancia correspondente a cada chave As chaves geradas são efêmeras enão
 * devem ser utilizadas para persistir. O objetivo dessas chaves é mapear um
 * valor na tela para uma MInstancia e memória no lado do servidor.
 */
public class SOptionsConfig {

    private BigInteger keySeed = BigInteger.ZERO;
    private static final Logger LOGGER = LoggerFactory.getLogger(SOptionsConfig.class);
    private BiMap<String, SInstance> optionsKeyInstanceMap;
    private LinkedHashMap<String, String> optionsKeylabelMap;
    private SIList<? extends SInstance> options;
    private SSelectionableInstance instance;

    public SOptionsConfig(SSelectionableInstance instancia) {
        this.instance = instancia;
    }

    protected SOptionsProvider getOptionsProvider() {
        if (instance instanceof SIList) {
            return ((SIList) instance).getElementsType().getOptionsProvider();
        }
        return instance.getType().getOptionsProvider();
    }

    private BiMap<String, SInstance> getOptions() {
        init();
        return optionsKeyInstanceMap;
    }

    private void init() {
        if (optionsKeyInstanceMap == null) {
            reloadOptionsFromProvider();
        }
    }

    /**
     * Reexecuta o listAvailableOptions do provider do tipo.
     */
    private void reloadOptionsFromProvider() {
        SOptionsProvider provider = getOptionsProvider();
        if (provider != null) {
            SIList<? extends SInstance> newOptions;
            try {
                newOptions = provider.listAvailableOptions(instance);
            } catch (Exception e) {
                if (instance instanceof SInstance) {
                    throw new SingularFormException("Erro ao listar opções para instancia ", e, (SInstance) instance);
                }
                throw Throwables.propagate(e);
            }
            LOGGER.warn("Opções recarregadas para " + toString());
            if (newOptions != null && !newOptions.equals(options)) {
                options = newOptions;
                optionsKeyInstanceMap = HashBiMap.create(options.size());
                optionsKeylabelMap = new LinkedHashMap<>(options.size());
                for (br.net.mirante.singular.form.mform.SInstance instance : options) {
                    /* ignora silenciosamente valores duplicados */
                    if (!optionsKeyInstanceMap.inverse().containsKey(instance)) {
                        String key = newUniqueKey();
                        optionsKeyInstanceMap.put(key, instance);
                        optionsKeylabelMap.put(key, instance.getSelectLabel());
                    }
                }
            }
        } else {
            optionsKeyInstanceMap = HashBiMap.create();
            optionsKeylabelMap = new LinkedHashMap<>();
        }
    }

    private String newUniqueKey() {
        keySeed = keySeed.add(BigInteger.ONE);
        return String.valueOf(keySeed);
    }


    public String getLabelFromKey(String key) {
        if (key == null || optionsKeylabelMap == null) {
            return null;
        }
        return optionsKeylabelMap.get(key);
    }

    public String getKeyFromOption(SInstance option) {
        if (option == null) {
            return null;
        }
        return getOptions().inverse().get(option);
    }

    public String getLabelFromOption(SInstance option) {
        if (option == null) {
            return null;
        }
        return getLabelFromKey(getOptions().inverse().get(option));
    }

    /**
     * Obtém a chave efêmera mapeada para o valor
     *
     * @param key
     * @return
     */
    public SInstance getValueFromKey(String key) {
        if (key == null) {
            return null;
        }
        return getOptions().get(key);
    }

    /**
     * @return Um mapa de chave e label representando as Minstancias disponibilizadas pelo provider do tipo
     * da MInstancia.
     */
    public LinkedHashMap<String, String> listSelectOptions() {
        reloadOptionsFromProvider();
        return optionsKeylabelMap;
    }
}
