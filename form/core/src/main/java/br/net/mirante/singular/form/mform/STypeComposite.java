package br.net.mirante.singular.form.mform;

import br.net.mirante.singular.form.mform.basic.view.MSelecaoPorRadioView;
import br.net.mirante.singular.form.mform.basic.view.MSelecaoPorSelectView;
import br.net.mirante.singular.form.mform.core.SPackageCore;
import br.net.mirante.singular.form.mform.core.STypeBoolean;
import br.net.mirante.singular.form.mform.core.STypeData;
import br.net.mirante.singular.form.mform.core.STypeDecimal;
import br.net.mirante.singular.form.mform.core.STypeInteger;
import br.net.mirante.singular.form.mform.core.STypeMonetario;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.mform.options.MOptionsProvider;
import br.net.mirante.singular.form.mform.options.MSelectionableCompositeType;
import br.net.mirante.singular.form.mform.util.comuns.STypeCEP;
import br.net.mirante.singular.form.mform.util.comuns.STypeCNPJ;
import br.net.mirante.singular.form.mform.util.comuns.STypeCPF;
import br.net.mirante.singular.form.mform.util.comuns.STypeEMail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@MInfoTipo(nome = "MTipoComposto", pacote = SPackageCore.class)
public class STypeComposite<TIPO_INSTANCIA extends SIComposite>
        extends SType<TIPO_INSTANCIA>
        implements ICompositeType, MSelectionableCompositeType {

    private Map<String, SType<?>> fieldsLocal;

    private transient FieldMapOfRecordType fieldsConsolidated;

    private MOptionsProvider optionsProvider;

    private String selectLabel;

    @SuppressWarnings("unchecked")
    public STypeComposite() {
        super((Class<? extends TIPO_INSTANCIA>) SIComposite.class);
    }

    protected STypeComposite(Class<TIPO_INSTANCIA> classeInstancia) {
        super(classeInstancia);
    }

    @Override
    public Collection<SType<?>> getContainedTypes() {
        return getFields();
    }

    private <I extends SInstance, T extends SType<I>> T addInterno(String localName, T type) {
        if (instanceCount > 0){
            throw new SingularFormException("O MTipo '"+type.getName()+"' já possui instancias associadas, não é seguro alterar sua definição. ");
        }
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
                if (getSuperType() != null && getSuperType() instanceof STypeComposite) {
                    // Busca reaproveitar, pois muitas extensões são locais e
                    // não acrescentam campso
                    fieldsConsolidated = ((STypeComposite<?>) getSuperType()).getFieldsConsolidated();
                } else {
                    fieldsConsolidated = new FieldMapOfRecordType();
                }
            } else {
                fieldsConsolidated = new FieldMapOfRecordType();
                if (getSuperType() != null && getSuperType() instanceof STypeComposite) {
                    fieldsConsolidated.addAll(((STypeComposite<?>) getSuperType()).getFieldsConsolidated());
                }
                fieldsConsolidated.addAll(fieldsLocal);
            }
        }
        return fieldsConsolidated;
    }

    /**
     * Remover essa chamada pois ela é confusa.
     * Adicionar um campo apenas pelo tipo sem passar um nome impede que a mesma chamada para o mesmo tipo
     * seja feita novamente pois nesse caso definiria um tipo com o nome repetido.
     *
     * @param classeTipo
     * @param <I>
     * @param <T>
     * @return
     */
    @Deprecated
    public <I extends SInstance, T extends SType<I>> T addCampo(Class<T> classeTipo) {
        T tipo = resolverTipo(classeTipo);
        return addCampo(tipo.getSimpleName(), classeTipo);
    }

    public <I extends SInstance, T extends SType<I>> T addCampo(String nomeCampo, Class<T> tipo, boolean obrigatorio) {
        T novo = addCampo(nomeCampo, tipo);
        novo.withObrigatorio(obrigatorio);
        return novo;
    }

    public <I extends SInstance, T extends SType<I>> T addCampo(String nomeCampo, Class<T> classeTipo) {
        T novo = extenderType(nomeCampo, classeTipo);
        return addInterno(nomeCampo, novo);
    }

    public <I extends SInstance, T extends SType<I>> STypeLista<T, I> addCampoListaOf(String nomeSimplesNovoTipo, Class<T> classeTipoLista) {
        T tipo = resolverTipo(classeTipoLista);
        STypeLista<T, I> novo = createTipoListaOf(nomeSimplesNovoTipo, tipo);
        return addInterno(nomeSimplesNovoTipo, novo);
    }

    public <I extends SInstance, T extends SType<I>> STypeLista<T, I> addCampoListaOf(String nomeCampo, T tipoElementos) {
        STypeLista<T, I> novo = createTipoListaOf(nomeCampo, tipoElementos);
        return addInterno(nomeCampo, novo);
    }

    public <I extends SIComposite> STypeLista<STypeComposite<I>, I> addCampoListaOfComposto(String nomeCampo, String nomeNovoTipoComposto) {
        STypeLista<STypeComposite<I>, I> novo = createTipoListaOfNovoTipoComposto(nomeCampo, nomeNovoTipoComposto);
        return addInterno(nomeCampo, novo);
    }

    public SType<?> getCampo(String nomeCampo) {
        return getFieldsConsolidated().get(nomeCampo);
    }

    /**
     * @return todos os campos deste tipo específico, incluindo os campos do tipo pai.
     */
    public Collection<SType<?>> getFields() {
        return getFieldsConsolidated().getFields();
    }

    /**
     * @return campos declarados neste tipo específico, não incluindo os campos do tipo pai.
     */
    public Collection<SType<?>> getFieldsLocal() {
        return (fieldsLocal == null) ? Collections.emptyList() : fieldsLocal.values();

    }

    // --------------------------------------------------------------------------
    // Atalhos de conveniência
    // --------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public STypeComposite<SIComposite> addCampoComposto(String nomeCampo) {
        return addCampo(nomeCampo, STypeComposite.class);
    }

    public STypeString addCampoString(String nomeCampo) {
        return addCampo(nomeCampo, STypeString.class);
    }

    public STypeCPF addCampoCPF(String nomeCampo) {
        return addCampo(nomeCampo, STypeCPF.class);
    }

    public STypeCNPJ addCampoCNPJ(String nomeCampo) {
        return addCampo(nomeCampo, STypeCNPJ.class);
    }

    public STypeEMail addCampoEmail(String nomeCampo) {
        return addCampo(nomeCampo, STypeEMail.class);
    }

    public STypeCEP addCampoCEP(String nomeCampo) {
        return addCampo(nomeCampo, STypeCEP.class);
    }

    public STypeString addCampoString(String nomeCampo, boolean obrigatorio) {
        return addCampo(nomeCampo, STypeString.class, obrigatorio);
    }

    public STypeData addCampoData(String nomeCampo) {
        return addCampo(nomeCampo, STypeData.class);
    }

    public STypeData addCampoData(String nomeCampo, boolean obrigatorio) {
        return addCampo(nomeCampo, STypeData.class, obrigatorio);
    }

    public STypeBoolean addCampoBoolean(String nomeCampo) {
        return addCampo(nomeCampo, STypeBoolean.class);
    }

    public STypeBoolean addCampoBoolean(String nomeCampo, boolean obrigatorio) {
        return addCampo(nomeCampo, STypeBoolean.class, obrigatorio);
    }

    public STypeInteger addCampoInteger(String nomeCampo) {
        return addCampo(nomeCampo, STypeInteger.class);
    }

    public STypeInteger addCampoInteger(String nomeCampo, boolean obrigatorio) {
        return addCampo(nomeCampo, STypeInteger.class, obrigatorio);
    }

    public STypeDecimal addCampoDecimal(String nomeCampo, boolean obrigatorio) {
        return addCampo(nomeCampo, STypeDecimal.class, obrigatorio);
    }

    public STypeDecimal addCampoDecimal(String fieldname) {
        return addCampoDecimal(fieldname, false);
    }

    public STypeMonetario addCampoMonetario(String nomeCampo) {
        return addCampo(nomeCampo, STypeMonetario.class);
    }

    /**
     * Configura o tipo para utilizar a view {@link MSelecaoPorSelectView}
     */
    public STypeComposite<TIPO_INSTANCIA> withSelectView() {
        return (STypeComposite<TIPO_INSTANCIA>) super.withView(MSelecaoPorSelectView::new);
    }

    /**
     * Configura o tipo para utilizar a view {@link MSelecaoPorRadioView}
     */
    public STypeComposite<TIPO_INSTANCIA> withRadioView() {
        return (STypeComposite<TIPO_INSTANCIA>) super.withView(MSelecaoPorRadioView::new);
    }

    @Override
    public MOptionsProvider getProviderOpcoes() {
        return optionsProvider;
    }

    @Override
    public void setProviderOpcoes(MOptionsProvider p) {
        optionsProvider = p;
    }


    @Override
    public String getSelectLabel() {
        return this.selectLabel;
    }

    @Override
    public void setSelectLabel(String selectLabel) {
        this.selectLabel = selectLabel;
    }

    public SType<?> getCampo(STypeString siglaUF) {
        return getCampo(siglaUF.getSimpleName());
    }

    /**
     * Mapa de alto nível que funciona tanto por nome quanto por índice do campo
     * ordenado. Otimiza a performance e mantem a ordem original da criação dos
     * campos.
     */
    final static class FieldMapOfRecordType {

        private LinkedHashMap<String, FieldRef> fields;

        private List<SType<?>> fieldsList;

        public int size() {
            return (fields == null) ? 0 : fields.size();
        }

        public boolean isEmpty() {
            return (fields == null) || fields.isEmpty();
        }

        public List<SType<?>> getFields() {
            return (fields == null) ? Collections.emptyList() : garantirLista();
        }

        public SType<?> get(String fieldName) {
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

        public void addAll(Map<String, SType<?>> toBeAdded) {
            toBeAdded.values().forEach(f -> addInterno(f));
        }

        private void addInterno(SType<?> field) {
            if (fields == null) {
                fields = new LinkedHashMap<>();
            }
            fields.put(field.getSimpleName(), new FieldRef(field));
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

        public SType<?> getByIndex(int fieldIndex) {
            if (fields != null) {
                return garantirLista().get(fieldIndex);
            }
            throw new SingularFormException("Indice do campo incorreto: " + fieldIndex);
        }

        private List<SType<?>> garantirLista() {
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
        private final SType<?> field;
        private int index = -1;

        public FieldRef(SType<?> field) {
            this.field = field;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public SType<?> getField() {
            return field;
        }
    }

}
