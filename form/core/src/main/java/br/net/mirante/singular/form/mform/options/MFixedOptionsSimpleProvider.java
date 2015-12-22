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

    public MFixedOptionsSimpleProvider(MTipo<?> tipoOpcoes, 
                            Collection<? extends Object> lista) {
        if (lista.isEmpty()) {
            throwEmpryListError();
        }
        this.opcoes = tipoOpcoes.novaLista();
        lista.forEach(o -> opcoes.addValor(o));
    }

    public MFixedOptionsSimpleProvider(MTipo<?> tipoOpcoes, Object[] lista) {
        if (lista.length == 0) {
            throwEmpryListError();
        }
        this.opcoes = tipoOpcoes.novaLista();
        if(tipoOpcoes instanceof MTipoSimples){
            Arrays.stream(lista).forEach(o -> opcoes.addValor(o));
        }else if(tipoOpcoes instanceof MSelectionableType){
            Arrays.stream(lista).forEach(o -> opcoes.addElement(o)); //TODO: Fabs : also for collections
        }
    }

    private void throwEmpryListError() {
        throw new RuntimeException("Empty list is not valid as options.");
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
