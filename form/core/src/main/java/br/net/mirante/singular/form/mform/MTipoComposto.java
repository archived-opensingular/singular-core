package br.net.mirante.singular.form.mform;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import br.net.mirante.singular.form.mform.core.*;
import br.net.mirante.singular.form.mform.options.MOptionsProvider;
import br.net.mirante.singular.form.mform.options.MSelectionableType;
import br.net.mirante.singular.form.mform.util.comuns.MTipoCEP;
import br.net.mirante.singular.form.mform.util.comuns.MTipoCNPJ;
import br.net.mirante.singular.form.mform.util.comuns.MTipoCPF;
import br.net.mirante.singular.form.mform.util.comuns.MTipoEMail;

@MInfoTipo(nome = "MTipoComposto", pacote = MPacoteCore.class)
public class MTipoComposto<TIPO_INSTANCIA extends MIComposto>
        extends MTipo<TIPO_INSTANCIA>
        implements ICompositeType, MSelectionableType {

    private Map<String, MTipo<?>> fieldsLocal;

    private transient FieldMapOfRecordType fieldsConsolidated;


    //TODO: Fabs : Check why this is not working
    // SELECTION ATRIBUTES
//    static final public AtrRef<MTipoString, MIString, String>
//            ID_FIELD = new AtrRef<>(MPacoteCore.class, "ID_FIELD",
//            MTipoString.class, MIString.class, String.class),
//            VALUE_FIELD = new AtrRef<>(MPacoteCore.class, "VALUE_FIELD",
//                    MTipoString.class, MIString.class, String.class);
    private MOptionsProvider optionsProvider;

    @SuppressWarnings("unchecked")
    public MTipoComposto() {
        super((Class<? extends TIPO_INSTANCIA>) MIComposto.class);
    }

    protected MTipoComposto(Class<TIPO_INSTANCIA> classeInstancia) {
        super(classeInstancia);
    }

    @Override
    protected void onCargaTipo(TipoBuilder tb) {
        super.onCargaTipo(tb);

//        if(!ID_FIELD.isBinded()) tb.createTipoAtributo(ID_FIELD);
//        if(!VALUE_FIELD.isBinded()) tb.createTipoAtributo(VALUE_FIELD);
    }

    @Override
    public Collection<MTipo<?>> getContainedTypes() {
        return getFields();
    }
    
    private <I extends MInstancia, T extends MTipo<I>> T addInterno(String localName, T type) {
        if (fieldsLocal == null) {
            fieldsLocal = new LinkedHashMap<>();
        }
        fieldsConsolidated = null;

        fieldsLocal.put(localName, type);
        return type;
    }

    final FieldMapOfRecordType getFieldsConsolidated() {
        if (fieldsConsolidated == null) {
            if (fieldsLocal == null) {
                if (getSuperTipo() != null && getSuperTipo() instanceof MTipoComposto) {
                    // Busca reaproveitar, pois muitas extensões são locais e
                    // não acrescentam campso
                    fieldsConsolidated = ((MTipoComposto<?>) getSuperTipo()).getFieldsConsolidated();
                } else {
                    fieldsConsolidated = new FieldMapOfRecordType();
                }
            } else {
                fieldsConsolidated = new FieldMapOfRecordType();
                if (getSuperTipo() != null && getSuperTipo() instanceof MTipoComposto) {
                    fieldsConsolidated.addAll(((MTipoComposto<?>) getSuperTipo()).getFieldsConsolidated());
                }
                fieldsConsolidated.addAll(fieldsLocal);
            }
        }
        return fieldsConsolidated;
    }

    public <I extends MInstancia, T extends MTipo<I>> T addCampo(Class<T> classeTipo) {
        T tipo = resolverTipo(classeTipo);
        return extenderTipo(tipo.getNomeSimples(), tipo);
    }

    public <I extends MInstancia, T extends MTipo<I>> T addCampo(String nomeCampo, Class<T> tipo, boolean obrigatorio) {
        T novo = addCampo(nomeCampo, tipo);
        novo.withObrigatorio(obrigatorio);
        return novo;
    }

    public <I extends MInstancia, T extends MTipo<I>> T addCampo(String nomeCampo, Class<T> classeTipo) {
        T novo = extenderTipo(nomeCampo, classeTipo);
        return addInterno(nomeCampo, novo);
    }

    public <I extends MInstancia, T extends MTipo<I>> MTipoLista<T, I> addCampoListaOf(String nomeSimplesNovoTipo, Class<T> classeTipoLista) {
        T tipo = resolverTipo(classeTipoLista);
        MTipoLista<T, I> novo = createTipoListaOf(nomeSimplesNovoTipo, tipo);
        return addInterno(nomeSimplesNovoTipo, novo);
    }

    public <I extends MInstancia, T extends MTipo<I>> MTipoLista<T, I> addCampoListaOf(String nomeCampo, T tipoElementos) {
        MTipoLista<T, I> novo = createTipoListaOf(nomeCampo, tipoElementos);
        return addInterno(nomeCampo, novo);
    }

    public <I extends MIComposto> MTipoLista<MTipoComposto<I>, I> addCampoListaOfComposto(String nomeCampo, String nomeNovoTipoComposto) {
        MTipoLista<MTipoComposto<I>, I> novo = createTipoListaOfNovoTipoComposto(nomeCampo, nomeNovoTipoComposto);
        return addInterno(nomeCampo, novo);
    }

    public MTipo<?> getCampo(String nomeCampo) {
        return getFieldsConsolidated().get(nomeCampo);
    }

    /**
     * @return todos os campos deste tipo específico, incluindo os campos do tipo pai.
     */
    public Collection<MTipo<?>> getFields() {
        return getFieldsConsolidated().getFields();
    }

    /**
     * @return campos declarados neste tipo específico, não incluindo os campos do tipo pai.
     */
    public Collection<MTipo<?>> getFieldsLocal() {
        return (fieldsLocal == null) ? Collections.emptyList() : fieldsLocal.values();

    }

    @Deprecated
    public Set<String> getCampos() {
        return getFields().stream().map(f -> f.getNome()).collect(Collectors.toSet());
    }

    // --------------------------------------------------------------------------
    // Atalhos de conveniência
    // --------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public MTipoComposto<MIComposto> addCampoComposto(String nomeCampo) {
        return addCampo(nomeCampo, MTipoComposto.class);
    }

    public MTipoString addCampoString(String nomeCampo) {
        return addCampo(nomeCampo, MTipoString.class);
    }

    public MTipoCPF addCampoCPF(String nomeCampo) {
        return addCampo(nomeCampo, MTipoCPF.class);
    }

    public MTipoCNPJ addCampoCNPJ(String nomeCampo) {
        return addCampo(nomeCampo, MTipoCNPJ.class);
    }

    public MTipoEMail addCampoEmail(String nomeCampo) {
        return addCampo(nomeCampo, MTipoEMail.class);
    }

    public MTipoCEP addCampoCEP(String nomeCampo) {
        return addCampo(nomeCampo, MTipoCEP.class);
    }

    public MTipoString addCampoString(String nomeCampo, boolean obrigatorio) {
        return addCampo(nomeCampo, MTipoString.class, obrigatorio);
    }

    public MTipoData addCampoData(String nomeCampo) {
        return addCampo(nomeCampo, MTipoData.class);
    }

    public MTipoData addCampoData(String nomeCampo, boolean obrigatorio) {
        return addCampo(nomeCampo, MTipoData.class, obrigatorio);
    }

    public MTipoBoolean addCampoBoolean(String nomeCampo) {
        return addCampo(nomeCampo, MTipoBoolean.class);
    }

    public MTipoBoolean addCampoBoolean(String nomeCampo, boolean obrigatorio) {
        return addCampo(nomeCampo, MTipoBoolean.class, obrigatorio);
    }

    public MTipoInteger addCampoInteger(String nomeCampo) {
        return addCampo(nomeCampo, MTipoInteger.class);
    }

    public MTipoInteger addCampoInteger(String nomeCampo, boolean obrigatorio) {
        return addCampo(nomeCampo, MTipoInteger.class, obrigatorio);
    }

    public MTipoDecimal addCampoDecimal(String nomeCampo) {
        return addCampo(nomeCampo, MTipoDecimal.class);
    }

    public MTipoMonetario addCampoMonetario(String nomeCampo) {
        return addCampo(nomeCampo, MTipoMonetario.class);
    }

    @Override
    public void setProviderOpcoes(MOptionsProvider p) {
        optionsProvider = p;
    }

    @Override
    public MOptionsProvider getProviderOpcoes() {
        return optionsProvider;
    }

    /**
     * Configures default key, value fields with names "key" and "value".
     * You can override this method if you want to define your own fields for
     * your instance.
     *
     * @return <code>this</code>
     */
    public MTipoComposto configureKeyValueFields(){
        return withKeyValueField("id", "value");
    }

    /**
     * Configures key, value fields with names informed.
     * If you are specializing a {@link MTipoComposto} you can use this
     * method to define your own fields.
     *
     * @return <code>this</code>
     */
    public MTipoComposto withKeyValueField(String key, String value){
        return withIdField(key).withValueField(value);
    }

    protected String id_sel, val_sel;

    private MTipoComposto withIdField(String fieldName){
//        setValorAtributo(ID_FIELD, fieldName);
        id_sel = fieldName;
        addCampoString(fieldName);
        return this;
    }

    private MTipoComposto withValueField(String fieldName){
//        setValorAtributo(VALUE_FIELD, fieldName);
        val_sel = fieldName;
        addCampoString(fieldName);
        return this;
    }

    public MIComposto create(Object key, Object value){
        MIComposto instance = this.novaInstancia();
        instance.setFieldId(key);
        instance.setFieldValue(value);
        return instance;
    }

    /**
     * Mapa de alto nível que funciona tanto por nome quanto por índice do campo
     * ordenado. Otimiza a performance e mantem a ordem original da criação dos
     * campos.
     */
    final static class FieldMapOfRecordType {

        private LinkedHashMap<String, FieldRef> fields;

        private List<MTipo<?>> fieldsList;

        public int size() {
            return (fields == null) ? 0 : fields.size();
        }

        public boolean isEmpty() {
            return (fields == null) || fields.isEmpty();
        }

        public List<MTipo<?>> getFields() {
            return (fields == null) ? Collections.emptyList() : garantirLista();
        }

        public MTipo<?> get(String fieldName) {
            if (fields != null) {
                FieldRef fr = fields.get(fieldName);
                if (fr != null) {
                    return fr.getField();
                }
            }
            return null;
        }

        public void addAll(FieldMapOfRecordType toBeAdded) {
            if (!toBeAdded.isEmpty()) {
                toBeAdded.fields.values().forEach(fr -> addInterno(fr.getField()));
            }
        }

        public void addAll(Map<String, MTipo<?>> toBeAdded) {
            toBeAdded.values().forEach(f -> addInterno(f));
        }

        private void addInterno(MTipo<?> field) {
            if (fields == null) {
                fields = new LinkedHashMap<>();
            }
            fields.put(field.getNomeSimples(), new FieldRef(field));
        }

        public int findIndex(String fieldName) {
            if (fields != null) {
                garantirLista();
                FieldRef fr = fields.get(fieldName);
                if (fr != null) {
                    return fr.getIndex();
                }
            }
            return -1;
        }

        public MTipo<?> getByIndex(int fieldIndex) {
            if (fields != null) {
                return garantirLista().get(fieldIndex);
            }
            throw new SingularFormException("Indice do campo incorreto: " + fieldIndex);
        }

        private List<MTipo<?>> garantirLista() {
            if (fieldsList == null) {
                int index = 0;
                fieldsList = new ArrayList<>(fields.size());
                for (FieldRef ref : fields.values()) {
                    ref.setIndex(index);
                    fieldsList.add(ref.getField());
                    index++;
                }
            }
            return fieldsList;
        }
    }

    private static final class FieldRef {
        private final MTipo<?> field;
        private int            index = -1;

        public FieldRef(MTipo<?> field) {
            this.field = field;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public MTipo<?> getField() {
            return field;
        }
    }
}
