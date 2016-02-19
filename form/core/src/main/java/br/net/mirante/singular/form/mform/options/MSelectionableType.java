package br.net.mirante.singular.form.mform.options;

import br.net.mirante.singular.form.mform.SType;

@SuppressWarnings({ "rawtypes", "unchecked" })
public interface MSelectionableType<BASE extends SType>  extends MSelectionable {

    public MOptionsProvider getProviderOpcoes();

    /**
     * This method is API internal use only, do not call it.
     * Use withSelection* methods instead.
     * @param p
     *  MoptionsProvider instance or lambda.
     */
    public void setProviderOpcoes(MOptionsProvider p);

    default public boolean hasProviderOpcoes() {
        return getProviderOpcoes() != null;
    }


}
