/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form;

import br.net.mirante.singular.form.builder.selection.SSelectionBuilder;
import br.net.mirante.singular.form.builder.selection.SelectionBuilder;
import br.net.mirante.singular.form.type.core.*;
import br.net.mirante.singular.form.type.country.brazil.STypeCEP;
import br.net.mirante.singular.form.type.country.brazil.STypeCNPJ;
import br.net.mirante.singular.form.type.country.brazil.STypeCPF;
import br.net.mirante.singular.form.type.util.STypeEMail;
import br.net.mirante.singular.form.view.SView;
import br.net.mirante.singular.form.view.SViewAttachmentList;
import br.net.mirante.singular.form.view.SViewAutoComplete;
import br.net.mirante.singular.form.view.SViewSelectionBySelect;

import java.io.Serializable;
import java.util.*;

@SInfoType(name = "STypeComposite", spackage = SPackageCore.class)
public class STypeComposite<INSTANCE_TYPE extends SIComposite> extends SType<INSTANCE_TYPE> implements ICompositeType {

    private Map<String, SType<?>> fieldsLocal;

    private transient FieldMapOfRecordType fieldsConsolidated;

    @SuppressWarnings("unchecked")
    public STypeComposite() {
        super((Class<? extends INSTANCE_TYPE>) SIComposite.class);
    }

    protected STypeComposite(Class<INSTANCE_TYPE> instanceClass) {
        super(instanceClass);
    }

    protected void extendSubReference() {
        super.extendSubReference();
        if (getSuperType() instanceof STypeComposite) {
            Map<String, SType<?>> fieldsSuper = ((STypeComposite<?>) getSuperType()).fieldsLocal;
            if (fieldsSuper != null) {
                for (Map.Entry<String,SType<?>> entry: fieldsSuper.entrySet()) {
                    addField(entry.getKey(), entry.getValue());
                }
            }
            fieldsConsolidated = null;
        }
    }

    @Override
    public Collection<SType<?>> getContainedTypes() {
        return getFields();
    }

    private void checkNameNewField(String localName, SType<?> type) {
        if(fieldsLocal != null) {
            if (localName == null) {
                localName = type.getNameSimple();
            }
            if(fieldsLocal.containsKey(localName)) {
                throw new SingularFormException("Já existe um campo criado com o nome '" + localName + "' em " + this);
            }
        }
    }

    private <I extends SInstance, T extends SType<I>> T addInternal(String localName, T type) {
        if (instanceCount > 0){
            throw new SingularFormException("O MTipo '" + type.getName() +
                    "' já possui instancias associadas, não é seguro alterar sua definição. ", this);
        }
        if (fieldsLocal == null) {
            fieldsLocal = new LinkedHashMap<>();
        }
        fieldsConsolidated = null;

        fieldsLocal.put(localName, type);
        return type;
    }

    final FieldMapOfRecordType getFieldsConsolidated() {
        if(isRecursiveReference()) {
            return ((STypeComposite<?>) getSuperType()).getFieldsConsolidated();
        }
        if (fieldsConsolidated == null) {
            if (fieldsLocal == null) {
                if (getSuperType() instanceof STypeComposite) {
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
     * Cria um novo campo com o nome informado como sendo do tipo informado e já marcado como obrigatório.
     */
    public <I extends SInstance, T extends SType<I>> T addField(String fieldSimpleName, Class<T> type, boolean required) {
        T field = addField(fieldSimpleName, type);
        field.withRequired(required);
        return field;
    }

    /**
     * Cria um novo campo com o nome informado como sendo do tipo informado.
     */
    public <I extends SInstance, T extends SType<I>> T addField(String fieldSimpleName, Class<T> typeClass) {
        return addField(fieldSimpleName, resolveType(typeClass));
    }

    //TODO: FABS : THIS IS UNTESTED
    /**
     * Cria um novo campo com o nome informado como sendo do tipo informado.
     */
    public <I extends SInstance, T extends SType<I>> T addField(String fieldSimpleName, T parentType) {
        checkNameNewField(fieldSimpleName, parentType);
        T field = extendType(fieldSimpleName, parentType);
        return addInternal(fieldSimpleName, field);
    }

    /**
     * Cria um novo campo lista com o nome informado e sendo o tipo de seus elementos o tipo da classe informada.
     */
    public <I extends SInstance, T extends SType<I>> STypeList<T, I> addFieldListOf(String fieldSimpleName, Class<T> listTypeClass) {
        return addFieldListOf(fieldSimpleName, resolveType(listTypeClass));
    }

    /**
     * Cria um novo campo lista com o nome informado e sendo o tipo de seus elementos o tipo informado.
     */
    public <I extends SInstance, T extends SType<I>> STypeList<T, I> addFieldListOf(String fieldSimpleName, T elementsType) {
        checkNameNewField(fieldSimpleName, null);
        STypeList<T, I> novo = createTypeListOf(fieldSimpleName, elementsType);
        return addInternal(fieldSimpleName, novo);
    }

    /**
     * Cria um campo lista com sendo do tipo composite ({@link STypeComposite}) com o
     * nome infomado. O novo tipo composite é criado sem campos, devendo ser estruturado
     * na sequencia.
     */
    public <I extends SIComposite> STypeList<STypeComposite<I>, I> addFieldListOfComposite(String fieldSimpleName,
            String simpleNameNewCompositeType) {
        checkNameNewField(fieldSimpleName, null);
        STypeList<STypeComposite<I>, I> novo = createListOfNewTypeComposite(fieldSimpleName, simpleNameNewCompositeType);
        return addInternal(fieldSimpleName, novo);
    }

    public STypeAttachmentList addFieldListOfAttachment(String listName, String fieldName) {
        checkNameNewField(listName,null);
        STypeAttachmentList novo = extendType(listName, STypeAttachmentList.class);
        novo.setView(SViewAttachmentList::new);
        novo.setElementsTypeFieldName(fieldName);
        return addInternal(listName, novo);
    }

    public SType<?> getField(String fieldSimpleName) {
        return getFieldsConsolidated().get(fieldSimpleName);
    }

    /**
     * @return todos os campos deste tipo específico, incluindo os campos do
     *         tipo pai.
     */
    public Collection<SType<?>> getFields() {
        return getFieldsConsolidated().getFields();
    }

    /**
     * @return campos declarados neste tipo específico, não incluindo os campos
     *         do tipo pai.
     */
    public Collection<SType<?>> getFieldsLocal() {
        return isRecursiveReference() ? ((STypeComposite<?>) getSuperType()).getFieldsLocal() :
                (fieldsLocal == null) ? Collections.emptyList() : fieldsLocal.values();

    }

    // --------------------------------------------------------------------------
    // Atalhos de conveniência
    // --------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public STypeComposite<SIComposite> addFieldComposite(String fieldSimpleName) {
        return addField(fieldSimpleName, STypeComposite.class);
    }

    /**
     * Cria um novo campo do tipo {@link STypeString} com o nome informado.
     */
    public STypeString addFieldString(String fieldSimpleName) {
        return addField(fieldSimpleName, STypeString.class);
    }

    /**
     * Cria um novo campo do tipo {@link STypeString} com o nome informado.
     */
    public STypeString addFieldString(String fieldSimpleName, boolean required) {
        return addField(fieldSimpleName, STypeString.class, required);
    }

    public STypeEMail addFieldEmail(String fieldSimpleName) {
        return addField(fieldSimpleName, STypeEMail.class);
    }

    /**
     * Cria um novo campo do tipo {@link STypeDate} com o nome informado.
     */
    public STypeDate addFieldDate(String fieldSimpleName) {
        return addField(fieldSimpleName, STypeDate.class);
    }

    /**
     * Cria um novo campo do tipo {@link STypeDate} com o nome informado.
     */
    public STypeDate addFieldDate(String fieldSimpleName, boolean required) {
        return addField(fieldSimpleName, STypeDate.class, required);
    }

    /**
     * Cria um novo campo do tipo {@link STypeDateTime} com o nome informado.
     */
    public STypeDateTime addFieldDateTime(String fieldSimpleName) {
        return addField(fieldSimpleName, STypeDateTime.class);
    }

    /**
     * Cria um novo campo do tipo {@link STypeDateTime} com o nome informado.
     */
    public STypeDateTime addFieldDateTime(String fieldSimpleName, boolean required) {
        return addField(fieldSimpleName, STypeDateTime.class, required);
    }

    /**
     * Cria um novo campo do tipo {@link STypeBoolean} com o nome informado.
     */
    public STypeBoolean addFieldBoolean(String fieldSimpleName) {
        return addField(fieldSimpleName, STypeBoolean.class);
    }

    /**
     * Cria um novo campo do tipo {@link STypeBoolean} com o nome informado.
     */
    public STypeBoolean addFieldBoolean(String fieldSimpleName, boolean required) {
        return addField(fieldSimpleName, STypeBoolean.class, required);
    }

    /**
     * Cria um novo campo do tipo {@link STypeInteger} com o nome informado.
     */
    public STypeInteger addFieldInteger(String fieldSimpleName) {
        return addField(fieldSimpleName, STypeInteger.class);
    }

    /**
     * Cria um novo campo do tipo {@link STypeInteger} com o nome informado.
     */
    public STypeInteger addFieldInteger(String fieldSimpleName, boolean required) {
        return addField(fieldSimpleName, STypeInteger.class, required);
    }

    /**
     * Cria um novo campo do tipo {@link STypeDecimal} com o nome informado.
     */
    public STypeDecimal addFieldDecimal(String fieldSimpleName, boolean required) {
        return addField(fieldSimpleName, STypeDecimal.class, required);
    }

    /**
     * Cria um novo campo do tipo {@link STypeDecimal} com o nome informado.
     */
    public STypeDecimal addFieldDecimal(String fieldname) {
        return addFieldDecimal(fieldname, false);
    }

    /**
     * Cria um novo campo do tipo {@link STypeMonetary} com o nome informado.
     */
    public STypeMonetary addFieldMonetary(String fieldSimpleName) {
        return addField(fieldSimpleName, STypeMonetary.class);
    }

    public SSelectionBuilder selection() {
        this.setView(SViewSelectionBySelect::new);
        return new SSelectionBuilder(this);
    }

    public SSelectionBuilder autocomplete() {
        this.setView(SViewAutoComplete::new);
        return new SSelectionBuilder(this);
    }

    public <T extends Serializable> SelectionBuilder<T, INSTANCE_TYPE, INSTANCE_TYPE> selectionOf(Class<T> clazz, SView view) {
        this.setView(() -> view);
        return new SelectionBuilder<>(this);
    }

    public <T extends Serializable> SelectionBuilder<T, INSTANCE_TYPE, INSTANCE_TYPE> selectionOf(Class<T> clazz) {
        return selectionOf(clazz, new SViewSelectionBySelect());
    }

    public <T extends Serializable> SelectionBuilder<T, INSTANCE_TYPE, INSTANCE_TYPE> autocompleteOf(Class<T> clazz) {
        return selectionOf(clazz, new SViewAutoComplete());
    }

    public <T extends Serializable> SelectionBuilder<T, INSTANCE_TYPE, INSTANCE_TYPE> lazyAutocompleteOf(Class<T> clasz) {
        this.setView(() -> new SViewAutoComplete(SViewAutoComplete.Mode.DYNAMIC));
        return new SelectionBuilder<>(this);
    }

    // TODO (from Daniel) MArquei como deprecated pois está estranho esse
    // método. Verificar se há uma solução melhor e refatorar
    @Deprecated
    public SType<?> getField(SType<?> field) {
        return getField(field.getNameSimple());
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
            fields.put(field.getNameSimple(), new FieldRef(field));
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

    @Override
    public void init(INSTANCE_TYPE newInstance) {
        super.init(newInstance);
        initFields(newInstance);
    }

    private void initFields(INSTANCE_TYPE newInstance) {
        Collection<SType> fields = (Collection) getFields();
        fields.stream().forEach((t)->{
            t.init(newInstance.getField(t.getNameSimple()));
        });
    }
}