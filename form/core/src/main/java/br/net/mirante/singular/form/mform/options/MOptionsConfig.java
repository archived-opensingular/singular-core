package br.net.mirante.singular.form.mform.options;

import br.net.mirante.singular.form.mform.MILista;
import br.net.mirante.singular.form.mform.MInstancia;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.UUID;

/**
 * Mapeia cada MInstancia fornecida pelo OptionsProvider
 * para uma par de descricao e chave (label e key)
 * É reponsável também por devolver a MIinstancia correspondente a cada
 * chave
 */
public class MOptionsConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(MOptionsConfig.class);
    private BiMap<String, MInstancia> optionsKeyInstanceMap;
    private BiMap<String, String> optionsKeylabelMap;
    private MILista<? extends MInstancia> options;
    private MSelectionableInstance instancia;

    public MOptionsConfig(MSelectionableInstance instancia) {
        this.instancia = instancia;
    }

    protected MOptionsProvider getOptionsProvider() {
        if (instancia instanceof MILista) {
            return ((MILista) instancia).getTipoElementos().getProviderOpcoes();
        }
        return instancia.getMTipo().getProviderOpcoes();
    }

    private BiMap<String, MInstancia> getOptions() {
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
        MOptionsProvider provider = getOptionsProvider();
        if (provider != null) {
            MILista<? extends MInstancia> newOptions = provider.listAvailableOptions(instancia);
            LOGGER.warn("Opções recarregadas para "+toString());
            if (newOptions != null && !newOptions.equals(options)) {
                options = newOptions;
                optionsKeyInstanceMap = HashBiMap.create(options.size());
                optionsKeylabelMap = HashBiMap.create(options.size());
                for (MInstancia mInstancia : options) {
                    String key = UUID.randomUUID().toString();
                    optionsKeyInstanceMap.put(key, mInstancia);
                    optionsKeylabelMap.put(key, mInstancia.getSelectLabel());
                }
            }
        } else {
            optionsKeyInstanceMap = HashBiMap.create();
            optionsKeylabelMap = HashBiMap.create(options.size());
            options = new MILista<>();
        }
    }

    private MILista<? extends MInstancia> getOptionsList() {
        if (options == null) {
            return new MILista<>();
        }
        return options;
    }

    public String getLabelFromOption(MInstancia selectedValue) {
        if (selectedValue == null) {
            return null;
        }
        String key = getOptions().inverse().get(selectedValue);
        return optionsKeylabelMap.get(key);
    }

    public String getLabelFromKey(String key) {
        if (key == null) {
            return null;
        }
        return optionsKeylabelMap.get(key);
    }

    public String getKeyFromOptions(MInstancia option) {
        if (option == null) {
            return null;
        }
        return getOptions().inverse().get(option);
    }

    public MInstancia getValueFromKey(String key) {
        if (key == null) {
            return null;
        }
        return getOptions().get(key);
    }

    /**
     * @return Um mapa de chave e valor representando as Minstancias disponibilizadas pelo provider do tipo
     * da instancia.
     */
    public Map<String, String> listSelectOptions() {
        reloadOptionsFromProvider();
        return optionsKeylabelMap;
    }
}
