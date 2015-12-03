package br.net.mirante.singular.form.mform.options;

import br.net.mirante.singular.form.mform.MILista;
import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.MTipoSimples;

/**
 *  This interface represents the providers which will load options that
 *  populate the choices for a specific field. The provider is specified 
 *  during the declaration of a Type or Field by using the 
 *  {@link MTipoSimples#withSelectionFromProvider(String)} method.
 *
 */
public interface MOptionsProvider {

    public abstract String toDebug();

    /**
     * Returns the list of options for this selection.
     * 
     * @param optionsInstance : Current isntance used to select the options.
     * @return list of options from the expected {@link MInstancia} type.
     */
    public MILista<? extends MInstancia> getOpcoes(MInstancia optionsInstance);
}
