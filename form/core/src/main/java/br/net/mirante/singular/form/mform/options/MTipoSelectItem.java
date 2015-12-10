package br.net.mirante.singular.form.mform.options;

import br.net.mirante.singular.form.mform.AtrRef;
import br.net.mirante.singular.form.mform.MInfoTipo;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.TipoBuilder;
import br.net.mirante.singular.form.mform.core.MIString;
import br.net.mirante.singular.form.mform.core.MPacoteCore;
import br.net.mirante.singular.form.mform.core.MTipoString;

@MInfoTipo(nome = "SelectItem", pacote = MPacoteCore.class)
public class MTipoSelectItem extends MTipoComposto<MISelectItem>
    implements MSelectionableType{
    private MOptionsProvider optionsProvider;
    
    protected static final String FIELD_ID = "id", FIELD_VALUE = "value";
    
    static final protected AtrRef<MTipoString, MIString, String> 
        ID_FIELD = new AtrRef<>(MPacoteCore.class, "ID_FIELD",
                            MTipoString.class, MIString.class, String.class),
        VALUE_FIELD = new AtrRef<>(MPacoteCore.class, "VALUE_FIELD",
                            MTipoString.class, MIString.class, String.class);
    
    public MTipoSelectItem() {
        super(MISelectItem.class);
    }
    
    @Override
    protected void onCargaTipo(TipoBuilder tb) {
        super.onCargaTipo(tb);
        tb.createTipoAtributo(ID_FIELD);//.withDefaultValueIfNull(FIELD_ID);
        tb.createTipoAtributo(VALUE_FIELD);//.withDefaultValueIfNull(FIELD_VALUE);
//        withIdField(FIELD_ID);
//        withValueField(FIELD_VALUE);
        addCampoString(FIELD_ID);
        addCampoString(FIELD_VALUE);
    }
    
    public MTipoSelectItem withIdField(String fieldName){
        setValorAtributo(ID_FIELD, fieldName);
        addCampoString(fieldName);
        return this;
    }
    
    public MTipoSelectItem withValueField(String fieldName){
        setValorAtributo(FIELD_VALUE, fieldName);
        addCampoString(fieldName);
        return this;
    }
    
    // SELECTION OF BEGIN

    public MOptionsProvider getProviderOpcoes() {
        return optionsProvider;
    }
    
    public void setProviderOpcoes(MOptionsProvider p){
        optionsProvider = p;
    }
}
