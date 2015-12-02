package br.net.mirante.singular.form.mform.options;

import br.net.mirante.singular.form.mform.MILista;
import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.SDocument;

public class LookupOptionsProvider implements MOptionsProvider {
    private String providerName;
    private Class<? extends MOptionsProvider> providerClass;

    public LookupOptionsProvider(String providerName) {
        this.providerName = providerName;
    }

    public LookupOptionsProvider(Class<? extends MOptionsProvider> providerClass) {
        this.providerClass = providerClass;
    }

    @Override
    public String toDebug() {
        return this.getClass().getName();
    }

    @Override
    public MILista<? extends MInstancia> getOpcoes(MInstancia instance) {
        SDocument document = instance.getDocument();
        MOptionsProvider provider = null;
        if(providerName != null){
            provider = document.lookupLocalService(providerName, MOptionsProvider.class);
        }else if(providerClass != null){
            provider = document.lookupLocalService(providerClass);
        }
        
        return provider.getOpcoes(instance);
    }
}