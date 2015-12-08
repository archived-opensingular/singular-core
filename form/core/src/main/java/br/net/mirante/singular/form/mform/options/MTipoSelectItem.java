package br.net.mirante.singular.form.mform.options;

import java.util.Collection;

import br.net.mirante.singular.form.mform.AtrRef;
import br.net.mirante.singular.form.mform.MInfoTipo;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.TipoBuilder;
import br.net.mirante.singular.form.mform.core.MIString;
import br.net.mirante.singular.form.mform.core.MPacoteCore;
import br.net.mirante.singular.form.mform.core.MTipoString;

@MInfoTipo(nome = "SelectItem", pacote = MPacoteCore.class)
public class MTipoSelectItem extends MTipoComposto<MISelectItem> {
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

    public MOptionsProvider selectionOf(Collection<MISelectItem> opcoes) {
        optionsProvider = new MFixedOptionsSimpleProvider(this, opcoes);
        return optionsProvider;
    }

    protected MOptionsProvider selectionOf(MISelectItem ... opcoes) {
        optionsProvider = new MFixedOptionsSimpleProvider(this, opcoes);
        return optionsProvider;
    }

    /**
     * Register a collection of options to be selected for this field.
     * Laso restricts the range of values available for the field.
     * 
     * @param options Collection of values to be used.
     * @return <code>this</code>
     */
    public MTipoSelectItem withSelectionOf(Collection<MISelectItem> options) {
        optionsProvider = new MFixedOptionsSimpleProvider(this, options);
        return this;
    }
    
    /**
     * Register a collection of options to be selected for this field.
     * Laso restricts the range of values available for the field.
     * 
     * @param options Collection of values to be used.
     * @return <code>this</code>
     */
    public MTipoSelectItem withSelectionOf(MISelectItem ... opcoes) {
        optionsProvider = new MFixedOptionsSimpleProvider(this, opcoes);
        return this;
    }
    
    /**
     * Registers the name of the provider used to load options for this type.
     * This provider will be loaded from the SDocument attached to the Minstance
     * enclosing this type.
     * 
     * @param providerName : Name of the {@link MOptionsProvider} to be used.
     * @return <code>this</code>
     */
    public MTipoSelectItem withSelectionFromProvider(final String providerName) {
        optionsProvider = new LookupOptionsProvider(providerName);
        return this;
    }
    
    /**
     * Registers the class of the provider used to load options for this type.
     * This provider will be loaded from the SDocument attached to the Minstance
     * enclosing this type.
     * @param providerClass : Class of the {@link MOptionsProvider} to be used.
     * @return <code>this</code>
     */
    public MTipoSelectItem withSelectionFromProvider(Class<? extends MOptionsProvider> providerClass) {
        optionsProvider = new LookupOptionsProvider(providerClass);
        return this;
    }

    // SELECTION OF END
}
