package br.net.mirante.singular.form.mform.options;

import br.net.mirante.singular.form.mform.MILista;
import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.SDocument;

public class LookupOptionsProvider implements MOptionsProvider {
    private final String providerName;

    public LookupOptionsProvider(String providerName) {
        this.providerName = providerName;
    }

    @Override
    public String toDebug() {
        return this.getClass().getName();
    }

    @Override
    public MILista<? extends MInstancia> getOpcoes(MInstancia instance) {
        SDocument document = instance.getDocument();
        MOptionsProvider provider = document.lookupLocalService(
                                providerName, MOptionsProvider.class);
        
        return provider.getOpcoes(instance);
    }
}