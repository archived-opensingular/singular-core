package br.net.mirante.singular.form.mform.options;

import java.util.Arrays;
import java.util.Collection;

import br.net.mirante.singular.form.mform.MILista;
import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.mform.MTipoSimples;

@SuppressWarnings("serial")
public class MFixedOptionsSimpleProvider implements MOptionsProvider {

    private final MILista<? extends MInstancia> opcoes;

    public MFixedOptionsSimpleProvider(MTipo<?> tipoOpcoes, Collection<? extends Object> lista) {
        this.opcoes = tipoOpcoes.novaLista();
        if (lista != null) {
            init(tipoOpcoes, lista.toArray(new Object[0]));
        }
    }

    public MFixedOptionsSimpleProvider(MTipo<?> tipoOpcoes, Object[] lista) {
        this.opcoes = tipoOpcoes.novaLista();
        if (lista != null) {
            init(tipoOpcoes, lista);
        }
    }

    private void init(MTipo<?> tipoOpcoes, Object[] lista){
        if (lista.length == 0) {
            throwEmpryListError();
        }
        if(tipoOpcoes instanceof MTipoSimples){
            Arrays.stream(lista).forEach(o -> {
                if (o instanceof MInstancia) {
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
        MInstancia e = opcoes.addNovo();
        e.setValor(o);
        return this;
    }

    /**
     * Add a new element to the Provider optionlist with the key values informed.
     * @param value to be set (MSelectionableInstance.hydratate) on the element
     * @param selectLabel
     * @return this
     */
    public MFixedOptionsSimpleProvider add(Object value, String selectLabel){
        MInstancia instancia = opcoes.addNovo();
        MSelectionableInstance e = (MSelectionableInstance)instancia;
        e.setSelectLabel(selectLabel);
        instancia.setValor(value);
        return this;
    }

    @Override
    public MILista<? extends MInstancia> listOptions(MInstancia optionsInstance) {
        return opcoes;
    }

    @Override
    public String toDebug() {
        return opcoes.toDebug();
    }

}
