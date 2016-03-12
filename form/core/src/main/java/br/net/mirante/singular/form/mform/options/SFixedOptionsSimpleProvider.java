package br.net.mirante.singular.form.mform.options;

import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SIList;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.STypeSimple;

import java.util.Arrays;
import java.util.Collection;

@SuppressWarnings("serial")
public class SFixedOptionsSimpleProvider implements SOptionsProvider {

    private final SIList<? extends SInstance> options;

    public SFixedOptionsSimpleProvider(SType<?> optionsType, Collection<?> list) {
        this.options = optionsType.newList();
        if (list != null) {
            init(optionsType, list.toArray(new Object[list.size()]));
        }
    }

    public SFixedOptionsSimpleProvider(SType<?> optionsType, Object[] list) {
        this.options = optionsType.newList();
        if (list != null) {
            init(optionsType, list);
        }
    }

    private void init(SType<?> optionsType, Object[] list) {
        if (list.length == 0) {
            throwEmpryListError();
        }
        if (optionsType instanceof STypeSimple) {
            Arrays.stream(list).forEach(o -> {
                if (o instanceof SInstance) {
                    options.addElement(o);
                } else {
                    options.addValue(o);
                }
            });
        } else if (optionsType instanceof SSelectionableType) {
            Arrays.stream(list).forEach(options::addElement);//TODO: Fabs : also for collections
        }
    }

    private void throwEmpryListError() {
        throw new RuntimeException("Empty list is not valid as options.");
    }

    /**
     * Add a new element to the Provider optionlist with value o.
     *
     * @param o Value to be set (MSelectionableInstance.setValor) on the element
     * @return this
     */
    public SFixedOptionsSimpleProvider add(Object o) {
        SInstance e = options.addNew();
        e.setValue(o);
        return this;
    }

    /**
     * Add a new element to the Provider optionlist with the key values informed.
     *
     * @param value       to be set (MSelectionableInstance.hydrate) on the element
     * @param selectLabel
     * @return this
     */
    public SFixedOptionsSimpleProvider add(Object value, String selectLabel) {
        SInstance instancia = options.addNew();
        SSelectionableInstance e = (SSelectionableInstance) instancia;
        e.setSelectLabel(selectLabel);
        instancia.setValue(value);
        return this;
    }

    @Override
    public SIList<? extends SInstance> listOptions(SInstance optionsInstance) {
        return options;
    }

    @Override
    public String toDebug() {
        return options.toDebug();
    }

}
