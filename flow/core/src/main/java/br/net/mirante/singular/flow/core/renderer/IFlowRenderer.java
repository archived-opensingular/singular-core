package br.net.mirante.singular.flow.core.renderer;

import br.net.mirante.singular.flow.core.ProcessDefinition;
import br.net.mirante.singular.flow.core.property.MetaDataRef;

public interface IFlowRenderer {
    
    public static final MetaDataRef<Boolean> SEND_EMAIL = new MetaDataRef<>("SEND_EMAIL", Boolean.class);
    
    byte[] generateImage(ProcessDefinition<?> definicao);
}
