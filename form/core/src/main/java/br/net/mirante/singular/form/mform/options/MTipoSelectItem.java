package br.net.mirante.singular.form.mform.options;

import br.net.mirante.singular.form.mform.*;
import br.net.mirante.singular.form.mform.core.MIString;
import br.net.mirante.singular.form.mform.core.MPacoteCore;
import br.net.mirante.singular.form.mform.core.MTipoString;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * This Type defines a <key,value> pair that can be used on selection lists
 * on forms.
 * 
 * @author Fabricio Buzeto
 *
 */
@MInfoTipo(nome = "SelectItem", pacote = MPacoteCore.class)
public class MTipoSelectItem extends MTipoComposto<MISelectItem>
    implements MSelectionableType<MTipoSelectItem>{
    private MOptionsProvider optionsProvider;
    
    static final protected AtrRef<MTipoString, MIString, String> 
        ID_FIELD = new AtrRef<>(MPacoteCore.class, "ID_FIELD",
                            MTipoString.class, MIString.class, String.class),
        VALUE_FIELD = new AtrRef<>(MPacoteCore.class, "VALUE_FIELD",
                            MTipoString.class, MIString.class, String.class);

    private List<String> searchFields = newArrayList(); //TODO [FABS] Change to use attribute later

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
    public MTipoSelectItem configureKeyValueFields(){
        return withKeyValueField("id", "value");
    }
    
    /**
     * Configures key, value fields with names informed.
     * If you are specializing a {@link MTipoSelectItem} you can use this 
     * method to define your own fields.
     * 
     * @return <code>this</code>
     */
    public MTipoSelectItem withKeyValueField(String key, String value){
        return withIdField(key).withValueField(value);
    }
    
    private MTipoSelectItem withIdField(String fieldName){
        setValorAtributo(ID_FIELD, fieldName);
        addCampoString(fieldName);
        return this;
    }
    
    private MTipoSelectItem withValueField(String fieldName){
        setValorAtributo(VALUE_FIELD, fieldName);
        addCampoString(fieldName);
        return this;
    }
    
    public MISelectItem create(Object key, Object value){
        MISelectItem instance = this.novaInstancia();
        instance.setFieldId(key);
        instance.setFieldValue(value);
        return instance;
    }

    public MOptionsProvider getProviderOpcoes() {
        return optionsProvider;
    }
    
    public void setProviderOpcoes(MOptionsProvider p){
        optionsProvider = p;
    }

    public void showFieldsOnSearch(String ... fields) {
        for(String f : fields){
            searchFields.add(f);
        }
    }

    public List<String> searchFields(){
        return newArrayList(searchFields);
    }
}
