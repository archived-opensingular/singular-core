package br.net.mirante.singular.form.mform.options;

import java.util.Arrays;
import java.util.Collection;

import br.net.mirante.singular.form.mform.SList;
import br.net.mirante.singular.form.mform.SInstance2;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.STypeSimples;

@SuppressWarnings("serial")
public class MFixedOptionsSimpleProvider implements MOptionsProvider {

    private final SList<? extends SInstance2> opcoes;

    public MFixedOptionsSimpleProvider(SType<?> tipoOpcoes, Collection<? extends Object> lista) {
        this.opcoes = tipoOpcoes.novaLista();
        if (lista != null) {
            init(tipoOpcoes, lista.toArray(new Object[0]));
        }
    }

    public MFixedOptionsSimpleProvider(SType<?> tipoOpcoes, Object[] lista) {
        this.opcoes = tipoOpcoes.novaLista();
        if (lista != null) {
            init(tipoOpcoes, lista);
        }
    }

    private void init(SType<?> tipoOpcoes, Object[] lista){
        if (lista.length == 0) {
            throwEmpryListError();
        }
        if(tipoOpcoes instanceof STypeSimples){
            Arrays.stream(lista).forEach(o -> {
                if (o instanceof SInstance2) {
                    opcoes.addElement(o);
                } else {
                    opcoes.addValor(o);
                }
            });
        } else if(tipoOpcoes instanceof MSelectionableType){
            Arrays.stream(lista).forEach(o -> opcoes.addElement(o)); //TODO: Fabs : also for collections
        }
    }

    private void throwEmpryListError() {
        throw new RuntimeException("Empty list is not valid as options.");
    }

    /**
     * Add a new element to the Provider optionlist with value o.
     * @param o Value to be set (MSelectionableInstance.setValor) on the element
     * @return this
     */
    public MFixedOptionsSimpleProvider add(Object o){
        SInstance2 e = opcoes.addNovo();
        e.setValor(o);
        return this;
    }

    /**
     * Add a new element to the Provider optionlist with the key values informed.
     * @param value to be set (MSelectionableInstance.hydrate) on the element
     * @param selectLabel
     * @return this
     */
    public MFixedOptionsSimpleProvider add(Object value, String selectLabel){
        SInstance2 instancia = opcoes.addNovo();
        MSelectionableInstance e = (MSelectionableInstance)instancia;
        e.setSelectLabel(selectLabel);
        instancia.setValor(value);
        return this;
    }

    @Override
    public SList<? extends SInstance2> listOptions(SInstance2 optionsInstance) {
        return opcoes;
    }

    @Override
    public String toDebug() {
        return opcoes.toDebug();
    }

}
