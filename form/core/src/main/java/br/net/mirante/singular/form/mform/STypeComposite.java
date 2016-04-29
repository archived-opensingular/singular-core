/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.mform;

import br.net.mirante.singular.form.mform.basic.view.SView;
import br.net.mirante.singular.form.mform.basic.view.SViewAttachmentList;
import br.net.mirante.singular.form.mform.basic.view.SViewAutoComplete;
import br.net.mirante.singular.form.mform.basic.view.SViewSelectionBySelect;
import br.net.mirante.singular.form.mform.builder.selection.SelectionBuilder;
import br.net.mirante.singular.form.mform.core.*;
import br.net.mirante.singular.form.mform.util.brasil.STypeCEP;
import br.net.mirante.singular.form.mform.util.brasil.STypeCNPJ;
import br.net.mirante.singular.form.mform.util.brasil.STypeCPF;
import br.net.mirante.singular.form.mform.util.comuns.STypeEMail;

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

    @Override
    public Collection<SType<?>> getContainedTypes() {
        return getFields();
    }

    private <I extends SInstance, T extends SType<I>> T addInternal(String localName, T type) {
        if (instanceCount > 0){
            throw new SingularFormException(
                    "O MTipo '" + type.getName() + "' já possui instancias associadas, não é seguro alterar sua definição. ");
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

    public <I extends SInstance, T extends SType<I>> T addField(String fieldSimpleName, Class<T> type, boolean required) {
        T field = addField(fieldSimpleName, type);
        field.withRequired(required);
        return field;
    }

    public <I extends SInstance, T extends SType<I>> T addField(String fieldSimpleName, Class<T> typeClass) {
        T novo = extendType(fieldSimpleName, typeClass);
        return addInternal(fieldSimpleName, novo);
    }

    //TODO: FABS : THIS IS UNTESTED
    public <I extends SInstance, T extends SType<I>> T addField(String fieldSimpleName, T parentType) {
        T field = extendType(fieldSimpleName, parentType);
        return addInternal(fieldSimpleName, field);
    }

    public <I extends SInstance, T extends SType<I>> STypeList<T, I> addFieldListOf(String nomeSimplesNovoTipo, Class<T> listTypeClass) {
        T type = resolveType(listTypeClass);
        STypeList<T, I> field = createTypeListOf(nomeSimplesNovoTipo, type);
        return addInternal(nomeSimplesNovoTipo, field);
    }

    public <I extends SInstance, T extends SType<I>> STypeList<T, I> addFieldListOf(String fieldSimpleName, T elementsType) {
        STypeList<T, I> novo = createTypeListOf(fieldSimpleName, elementsType);
        return addInternal(fieldSimpleName, novo);
    }

    public STypeAttachmentList addFieldListOfAttachment(String listName, String fieldName) {
        STypeAttachmentList novo = extendType(listName, STypeAttachmentList.class);
        novo.setView(SViewAttachmentList::new);
        novo.setElementsTypeFieldName(fieldName);
        return addInternal(listName, novo);
    }

    public <I extends SIComposite> STypeList<STypeComposite<I>, I> addFieldListOfComposite(String fieldSimpleName,
            String simpleNameNewCompositeType) {
        STypeList<STypeComposite<I>, I> novo = createListOfNewTypeComposite(fieldSimpleName, simpleNameNewCompositeType);
        return addInternal(fieldSimpleName, novo);
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
        return (fieldsLocal == null) ? Collections.emptyList() : fieldsLocal.values();

    }

    // --------------------------------------------------------------------------
    // Atalhos de conveniência
    // --------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public STypeComposite<SIComposite> addFieldComposite(String fieldSimpleName) {
        return addField(fieldSimpleName, STypeComposite.class);
    }

    public STypeString addFieldString(String fieldSimpleName) {
        return addField(fieldSimpleName, STypeString.class);
    }

    public STypeString addFieldString(String fieldSimpleName, boolean required) {
        return addField(fieldSimpleName, STypeString.class, required);
    }

    // Não deve estar aqui pois é específico do Brasil
    @Deprecated
    public STypeCPF addFieldCPF(String fieldSimpleName) {
        return addField(fieldSimpleName, STypeCPF.class);
    }

    // Não deve estar aqui pois é específico do Brasil
    @Deprecated
    public STypeCNPJ addFieldCNPJ(String fieldSimpleName) {
        return addField(fieldSimpleName, STypeCNPJ.class);
    }

    public STypeEMail addFieldEmail(String fieldSimpleName) {
        return addField(fieldSimpleName, STypeEMail.class);
    }

    // Não deve estar aqui pois é específico do Brasil
    @Deprecated
    public STypeCEP addFieldCEP(String fieldSimpleName) {
        return addField(fieldSimpleName, STypeCEP.class);
    }

    public STypeDate addFieldDate(String fieldSimpleName) {
        return addField(fieldSimpleName, STypeDate.class);
    }

    public STypeDate addFieldDate(String fieldSimpleName, boolean required) {
        return addField(fieldSimpleName, STypeDate.class, required);
    }

    public STypeDateTime addFieldDateTime(String fieldSimpleName) {
        return addField(fieldSimpleName, STypeDateTime.class);
    }

    public STypeDateTime addFieldDateTime(String fieldSimpleName, boolean required) {
        return addField(fieldSimpleName, STypeDateTime.class, required);
    }

    public STypeBoolean addFieldBoolean(String fieldSimpleName) {
        return addField(fieldSimpleName, STypeBoolean.class);
    }

    public STypeBoolean addFieldBoolean(String fieldSimpleName, boolean required) {
        return addField(fieldSimpleName, STypeBoolean.class, required);
    }

    public STypeInteger addFieldInteger(String fieldSimpleName) {
        return addField(fieldSimpleName, STypeInteger.class);
    }

    public STypeInteger addFieldInteger(String fieldSimpleName, boolean required) {
        return addField(fieldSimpleName, STypeInteger.class, required);
    }

    public STypeDecimal addFieldDecimal(String fieldSimpleName, boolean required) {
        return addField(fieldSimpleName, STypeDecimal.class, required);
    }

    public STypeDecimal addFieldDecimal(String fieldname) {
        return addFieldDecimal(fieldname, false);
    }

    public STypeMonetary addFieldMonetary(String fieldSimpleName) {
        return addField(fieldSimpleName, STypeMonetary.class);
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


}
