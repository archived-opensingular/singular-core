package br.net.mirante.singular.form.mform.options;

import br.net.mirante.singular.form.mform.MILista;
import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.document.SDocument;

/**
 * Class responsible for looking up for the desired providers from the current
 * instance in order to populate the options.
 * 
 * @author Fabricio Buzeto
 *
 */
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
        MOptionsProvider provider = whichProvider(document);
        return provider.getOpcoes(instance);
    }

    private MOptionsProvider whichProvider(SDocument document) {
        if(providerName != null){
            return document.lookupService(providerName, MOptionsProvider.class);
        }else if(providerClass != null){
            return document.lookupService(providerClass);
        }
        return null;
    }
}