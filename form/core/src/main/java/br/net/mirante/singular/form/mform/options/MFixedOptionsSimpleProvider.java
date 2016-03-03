package br.net.mirante.singular.form.mform.options;

import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SList;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.STypeSimple;

import java.util.Arrays;
import java.util.Collection;

@SuppressWarnings("serial")
public class MFixedOptionsSimpleProvider implements MOptionsProvider {

    private final SList<? extends SInstance> opcoes;

    public MFixedOptionsSimpleProvider(SType<?> tipoOpcoes, Collection<?> lista) {
        this.opcoes = tipoOpcoes.novaLista();
        if (lista != null) {
            init(tipoOpcoes, lista.toArray(new Object[lista.size()]));
        }
    }

    public MFixedOptionsSimpleProvider(SType<?> tipoOpcoes, Object[] lista) {
        this.opcoes = tipoOpcoes.novaLista();
        if (lista != null) {
            init(tipoOpcoes, lista);
        }
    }

    private void init(SType<?> tipoOpcoes, Object[] lista) {
        if (lista.length == 0) {
            throwEmpryListError();
        }
        if (tipoOpcoes instanceof STypeSimple) {
            Arrays.stream(lista).forEach(o -> {
                if (o instanceof SInstance) {
                    opcoes.addElement(o);
                } else {
                    opcoes.addValor(o);
                }
            });
        } else if (tipoOpcoes instanceof MSelectionableType) {
            Arrays.stream(lista).forEach(opcoes::addElement);//TODO: Fabs : also for collections
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
    public MFixedOptionsSimpleProvider add(Object o) {
        SInstance e = opcoes.addNovo();
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
    public MFixedOptionsSimpleProvider add(Object value, String selectLabel) {
        SInstance instancia = opcoes.addNovo();
        MSelectionableInstance e = (MSelectionableInstance) instancia;
        e.setSelectLabel(selectLabel);
        instancia.setValue(value);
        return this;
    }

    @Override
    public SList<? extends SInstance> listOptions(SInstance optionsInstance) {
        return opcoes;
    }

    @Override
    public String toDebug() {
        return opcoes.toDebug();
    }

}
