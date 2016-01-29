package br.net.mirante.singular.form.mform.options;

import br.net.mirante.singular.form.mform.SList;
import br.net.mirante.singular.form.mform.SInstance2;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.LinkedHashMap;

/**
 * Mapeia cada MInstancia fornecida pelo OptionsProvider
 * para uma par de descricao e chave (label e key)
 * É reponsável também por devolver a MIinstancia correspondente a cada
 * chave
 * As chaves geradas são efêmeras enão devem ser utilizadas para persistir.
 * O objetivo dessas chaves é mapear um valor na tela para uma MInstancia e memória
 * no lado do servidor.
 */
public class MOptionsConfig {

    private BigInteger keySeed = BigInteger.ZERO;
    private static final Logger LOGGER = LoggerFactory.getLogger(MOptionsConfig.class);
    private BiMap<String, SInstance2> optionsKeyInstanceMap;
    private LinkedHashMap<String, String> optionsKeylabelMap;
    private SList<? extends SInstance2> options;
    private MSelectionableInstance instancia;

    public MOptionsConfig(MSelectionableInstance instancia) {
        this.instancia = instancia;
    }

    protected MOptionsProvider getOptionsProvider() {
        if (instancia instanceof SList) {
            return ((SList) instancia).getTipoElementos().getProviderOpcoes();
        }
        return instancia.getMTipo().getProviderOpcoes();
    }

    private BiMap<String, SInstance2> getOptions() {
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
            SList<? extends SInstance2> newOptions = provider.listAvailableOptions(instancia);
            LOGGER.warn("Opções recarregadas para " + toString());
            if (newOptions != null && !newOptions.equals(options)) {
                options = newOptions;
                optionsKeyInstanceMap = HashBiMap.create(options.size());
                optionsKeylabelMap = new LinkedHashMap<>(options.size());
                for (SInstance2 SInstance : options) {
                    /* ignora silenciosamente valores duplicados */
                    if (!optionsKeyInstanceMap.inverse().containsKey(SInstance)) {
                        String key = newUniqueKey();
                        optionsKeyInstanceMap.put(key, SInstance);
                        optionsKeylabelMap.put(key, SInstance.getSelectLabel());
                    }
                }
            }
        } else {
            optionsKeyInstanceMap = HashBiMap.create();
            optionsKeylabelMap = new LinkedHashMap<>();
        }
    }

    private String newUniqueKey(){
        keySeed = keySeed.add(BigInteger.ONE);
        return String.valueOf(keySeed);
    }


    public String getLabelFromKey(String key) {
        if (key == null) {
            return null;
        }
        return optionsKeylabelMap.get(key);
    }

    public String getKeyFromOptions(SInstance2 option) {
        if (option == null) {
            return null;
        }
        return getOptions().inverse().get(option);
    }

    /**
     * Obtém a chave efêmera mapeada para o valor
     *
     * @param key
     * @return
     */
    public SInstance2 getValueFromKey(String key) {
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
