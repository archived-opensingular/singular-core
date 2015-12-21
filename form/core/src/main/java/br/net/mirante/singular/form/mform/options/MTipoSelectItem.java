package br.net.mirante.singular.form.mform.options;

import br.net.mirante.singular.form.mform.AtrRef;
import br.net.mirante.singular.form.mform.MInfoTipo;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.TipoBuilder;
import br.net.mirante.singular.form.mform.core.MIString;
import br.net.mirante.singular.form.mform.core.MPacoteCore;
import br.net.mirante.singular.form.mform.core.MTipoString;

/**
 * This Type defines a <key,value> pair that can be used on selection lists
 * on forms.
 * 
 * @author Fabricio Buzeto
 *
 */
@MInfoTipo(nome = "SelectItem", pacote = MPacoteCore.class)
public class MTipoSelectItem extends MTipoComposto<MISelectItem>
    implements MSelectionableType<MTipoSelectItem> {

    private MOptionsProvider optionsProvider;

    static final protected AtrRef<MTipoString, MIString, String> ID_FIELD    = new AtrRef<>(MPacoteCore.class, "ID_FIELD", MTipoString.class, MIString.class, String.class);
    static final protected AtrRef<MTipoString, MIString, String> VALUE_FIELD = new AtrRef<>(MPacoteCore.class, "VALUE_FIELD", MTipoString.class, MIString.class, String.class);

    public MTipoSelectItem() {
        super(MISelectItem.class);
    }

    @Override
    protected void onCargaTipo(TipoBuilder tb) {
        super.onCargaTipo(tb);
        tb.createTipoAtributo(ID_FIELD);
        tb.createTipoAtributo(VALUE_FIELD);
    }

    /**
     * Configures default key, value fields with names "key" and "value".
     * You can override this method if you want to define your own fields for 
     * your instance.
     * 
     * @return <code>this</code>
     */
    public MTipoSelectItem configureKeyValueFields() {
        return withKeyValueField("id", "value");
    }

    /**
     * Configures key, value fields with names informed.
     * If you are specializing a {@link MTipoSelectItem} you can use this 
     * method to define your own fields.
     * 
     * @return <code>this</code>
     */
    public MTipoSelectItem withKeyValueField(String key, String value) {
        return withIdField(key).withValueField(value);
    }

    private MTipoSelectItem withIdField(String fieldName) {
        setValorAtributo(ID_FIELD, fieldName);
        addCampoString(fieldName);
        return this;
    }

    private MTipoSelectItem withValueField(String fieldName) {
        setValorAtributo(VALUE_FIELD, fieldName);
        addCampoString(fieldName);
        return this;
    }

    public MISelectItem create(Object key, Object value) {
        MISelectItem instance = this.novaInstancia();
        instance.setFieldId(key);
        instance.setFieldValue(value);
        return instance;
    }

    @Override
    public MOptionsProvider getProviderOpcoes() {
        return optionsProvider;
    }
    @Override
    public void setProviderOpcoes(MOptionsProvider p) {
        optionsProvider = p;
    }
}
