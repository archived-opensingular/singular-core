/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.mform.options;

import br.net.mirante.singular.commons.util.Loggable;
import br.net.mirante.singular.form.mform.SIList;
import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SingularFormException;
import com.google.common.base.Throwables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Mapeia cada MInstancia fornecida pelo OptionsProvider para uma par de
 * descricao e chave (label e key) É reponsável também por devolver a
 * MIinstancia correspondente a cada chave As chaves geradas são efêmeras enão
 * devem ser utilizadas para persistir. O objetivo dessas chaves é mapear um
 * valor na tela para uma MInstancia e memória no lado do servidor.
 */
public class SOptionsConfig implements Loggable {

        private SIList<? extends SInstance> options;
    private SelectOptionIndex index = null;
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

    private SelectOptionIndex getOptions() {
        init();
        return index;
    }

    private void init() {
        if (index == null) {
            index = new SelectOptionIndex();
            reloadOptionsFromProvider(null);
        }
    }

    /**
     * Reexecuta o listAvailableOptions do provider do tipo.
     *
     * @param filter
     */
    private void reloadOptionsFromProvider(String filter) {
        SOptionsProvider provider = getOptionsProvider();
        if (provider != null) {
            SIList<? extends SInstance> newOptions;
            try {
                newOptions = provider.listAvailableOptions(instance, filter);
            } catch (Exception e) {
                if (instance instanceof SInstance) {
                    throw new SingularFormException("Erro ao listar opções para instancia ", e, (SInstance) instance);
                }
                throw Throwables.propagate(e);
            }
            getLogger().warn("Opções recarregadas para " + toString());
            if (newOptions != null && !newOptions.equals(options)) {
                remapValues(newOptions);
            }
        } else {
            index = new SelectOptionIndex();
        }
    }

    private void remapValues(SIList<? extends SInstance> newOptions) {
        options = newOptions;
        if (index == null) {
            index = new SelectOptionIndex();
        }
        index.reindex(newOptions);
    }


    public String getLabelFromKey(Object key) {
        if (key == null || index == null) {
            return null;
        }
        return index.getLabel(key);
    }

    public String getKeyFromLabel(String label) {
        if (label == null || index == null) {
            return null;
        }
        return String.valueOf(index.getKey(label));
    }

    public String getKeyFromOption(SInstance option) {
        if (option == null || index == null) {
            return null;
        }
        return String.valueOf(index.getKey(option));
    }

    public String getLabelFromOption(SInstance option) {
        if (option == null || index == null) {
            return null;
        }
        return index.getLabel(option);
    }

    /**
     * Obtém a chave efêmera mapeada para o valor
     *
     * @param key
     * @return
     */
    public SInstance getValueFromKey(Object key) {
        if (key == null) {
            return null;
        }
        return index.getInstance(key);
    }

    /**
     * @return Um mapa de chave e label representando as Minstancias disponibilizadas pelo provider do tipo
     * da MInstancia.
     */
    public Map<String, String> listSelectOptions() {
        return listSelectOptions(null);
    }

    public Map<String, String> listSelectOptions(String filter) {
        reloadOptionsFromProvider(filter);
        return index.listKeyLabel();
    }
}
