/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.form;

import org.opensingular.form.builder.selection.SSelectionBuilder;
import org.opensingular.form.builder.selection.SelectionBuilder;
import org.opensingular.form.type.core.SPackageCore;
import org.opensingular.form.type.core.STypeBoolean;
import org.opensingular.form.type.core.STypeDate;
import org.opensingular.form.type.core.STypeDateTime;
import org.opensingular.form.type.core.STypeDecimal;
import org.opensingular.form.type.core.STypeFieldRef;
import org.opensingular.form.type.core.STypeInteger;
import org.opensingular.form.type.core.STypeLong;
import org.opensingular.form.type.core.STypeMonetary;
import org.opensingular.form.type.core.STypePassword;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.type.core.STypeTime;
import org.opensingular.form.type.core.attachment.STypeAttachment;
import org.opensingular.form.type.util.STypeEMail;
import org.opensingular.form.view.SView;
import org.opensingular.form.view.SViewAttachmentList;
import org.opensingular.form.view.SViewAutoComplete;
import org.opensingular.form.view.SViewSelectionBySelect;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

@SInfoType(name = "STypeComposite", spackage = SPackageCore.class)
public class STypeComposite<INSTANCE_TYPE extends SIComposite> extends SType<INSTANCE_TYPE> implements ICompositeType {

    private Map<String, SType<?>>          fieldsLocal;

    private transient FieldMapOfRecordType fieldsConsolidated;

    @SuppressWarnings("unchecked")
    public STypeComposite() {
        super((Class<? extends INSTANCE_TYPE>) SIComposite.class);
    }

    protected STypeComposite(Class<INSTANCE_TYPE> instanceClass) {
        super(instanceClass);
    }

    @Override
    protected void extendSubReference() {
        if (!getSuperType().isComposite()) {
            return;
        }
        Map<String, SType<?>> fieldsSuper = ((STypeComposite<?>) getSuperType()).fieldsLocal;
        Map<String, SType<?>> complementaryFields = getComplementarySuperType().map(
                t -> ((STypeComposite<?>) t).fieldsLocal).orElse(null);
        if (fieldsSuper != null) {
            for (Map.Entry<String, SType<?>> entry : fieldsSuper.entrySet()) {
                SType<?> complementary = complementaryFields == null ? null : complementaryFields.get(entry.getKey());
                addFieldInternal(entry.getKey(), entry.getValue(), complementary);
            }
        }
        fieldsConsolidated = null;
    }

    /** Return the super type (parent type) of the current type. */
    @Override
    @Nonnull
    public final SType<INSTANCE_TYPE> getSuperType() {
        return Objects.requireNonNull(super.getSuperType());
    }

    @Override
    public Collection<SType<?>> getContainedTypes() {
        return getFields();
    }

    private void checkNameNewField(@Nonnull String fieldName) {
        Objects.requireNonNull(fieldName);
        if (fieldsLocal != null && fieldsLocal.containsKey(fieldName)) {
            String msg = "Tentativa de criar um segundo campo com o nome '" + fieldName + "' em " + this;
            SingularFormException e = new SingularFormException(msg, this);
            String probableWrongCall = detectIfProbableOnLoadTypeSuperCall(e);
            if (probableWrongCall != null) {
                e = new SingularFormException(msg +
                    ". Verifique se não ocorreu uma chamada indevida de super.onLoadType() (nao dever haver " +
                    "essa chamada) na linha\n   " + probableWrongCall, this);
            }
            throw e;
        }
    }

    private String detectIfProbableOnLoadTypeSuperCall(SingularFormException e) {
        boolean foundFirst = false;
        for (StackTraceElement element : e.getStackTrace()) {
            if ("onLoadType".equals(element.getMethodName())) {
                if (foundFirst) {
                    return element.toString();
                }
                foundFirst = true;
            } else if (foundFirst) {
                return null;
            }
        }
        return null;
    }

    private <T extends SType<?>> T addInternal(String localName, T type) {
        if (instanceCount > 0) {
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

    /** Retorna a quantidade de campos do tipo, incluindo os campos herdados do tipo pai. */
    public int size() {
        return getFieldsConsolidated().size();
    }

    /**
     * Retorna o indice do campo com o nome informado dentro do tipo.
     *
     * @return -1 senão encontrar
     */
    public int findIndexOf(String fieldSimpleName) {
        return getFieldsConsolidated().findIndex(fieldSimpleName);
    }

    private FieldMapOfRecordType getFieldsConsolidated() {
        if (isRecursiveReference()) {
            return ((STypeComposite<?>) getSuperType()).getFieldsConsolidated();
        }
        if (fieldsConsolidated == null) {
            if (fieldsLocal == null) {
                if (getSuperType().isComposite()) {
                    // Busca reaproveitar, pois muitas extensões são locais e
                    // não acrescentam campos
                    fieldsConsolidated = ((STypeComposite<?>) getSuperType()).getFieldsConsolidated();
                } else {
                    fieldsConsolidated = new FieldMapOfRecordType();
                }
            } else {
                fieldsConsolidated = new FieldMapOfRecordType();
                if (getSuperType().isComposite()) {
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
    public <T extends SType<?>> T addField(String fieldSimpleName, Class<T> type, boolean required) {
        T field = addField(fieldSimpleName, type);
        field.asAtr().required(required);
        return field;
    }

    /**
     * Cria um novo campo com o nome informado como sendo do tipo informado.
     */
    public <T extends SType<?>> T addField(String fieldSimpleName, Class<T> typeClass) {
        return addField(fieldSimpleName, resolveType(typeClass));
    }

    /**
     * Cria um novo campo com o nome informado como sendo do tipo informado.
     */
    @Nonnull
    public <T extends SType<?>> T addField(@Nullable String fieldSimpleName, @Nonnull T parentType) {
        return addFieldInternal(fieldSimpleName, parentType, null);
    }

    @Nonnull
    private <T extends SType<?>> T addFieldInternal(@Nullable String fieldSimpleName, @Nonnull T parentType,
            @Nullable SType<?> complementarySuperType) {
        SimpleName name = SFormUtil.resolveName(SimpleName.ofNullable(fieldSimpleName), parentType);
        checkNameNewField(name.get());
        if (isRecursiveReference()) {
            throw new SingularFormException(
                    "Can't add field '" + name + "' to " + this + " because it's a recursive reference.", this);
        }
        T field;
        if (complementarySuperType == null) {
            field = extendType(name, parentType);
        } else {
            field = extendMultipleTypes(name, parentType, complementarySuperType);
        }
        return addInternal(name.get(), field);
    }

    /**
     * Cria um novo campo lista com o nome informado e sendo o tipo de seus elementos o tipo da classe informada.
     */
    @Nonnull
    public <I extends SInstance, T extends SType<I>> STypeList<T, I> addFieldListOf(@Nonnull String fieldSimpleName,
        @Nonnull Class<T> listTypeClass) {
        return addFieldListOf(fieldSimpleName, resolveType(listTypeClass));
    }

    /**
     * Cria um novo campo lista com o nome informado e sendo o tipo de seus elementos o tipo informado.
     */
    @Nonnull
    public <I extends SInstance, T extends SType<I>> STypeList<T, I> addFieldListOf(@Nonnull String fieldSimpleName, @Nonnull T elementsType) {
        checkNameNewField(fieldSimpleName);
        STypeList<T, I> newList = createTypeListOf(fieldSimpleName, null, elementsType);
        return addInternal(fieldSimpleName, newList);
    }

    /**
     * Cria um novo campo lista com o nome informado e sendo o tipo de seus elementos o tipo informado e utilizando o nome de elemento informado
     */
    @Nonnull
    public <I extends SInstance, T extends SType<I>> STypeList<T, I> addFieldListOf(@Nonnull String fieldSimpleName, @Nonnull String elementSimpleName, @Nonnull T elementsType) {
        checkNameNewField(fieldSimpleName);
        STypeList<T, I> newList = createTypeListOf(fieldSimpleName, elementSimpleName, elementsType);
        return addInternal(fieldSimpleName, newList);
    }

    /**
     * Cria um campo lista com sendo do tipo composite ({@link STypeComposite}) com o
     * nome infomado. O novo tipo composite é criado sem campos, devendo ser estruturado
     * na sequencia.
     */
    @Nonnull
    public <I extends SIComposite> STypeList<STypeComposite<I>, I> addFieldListOfComposite(@Nonnull String fieldSimpleName,
        @Nonnull String simpleNameNewCompositeType) {
        checkNameNewField(fieldSimpleName);
        STypeList<STypeComposite<I>, I> newList = createListOfNewTypeComposite(fieldSimpleName, simpleNameNewCompositeType);
        return addInternal(fieldSimpleName, newList);
    }

    /**
     * Cria um novo campo do tipo {@link STypeAttachment} com o nome informado.
     * @param fieldSimpleName - nome do campo
     * @param required - se o campo é obrigatório
     */
    public STypeAttachment addFieldAttachment(String fieldSimpleName, boolean required) {
        return addField(fieldSimpleName, STypeAttachment.class, required);
    }
    /**
     * Cria um novo campo do tipo {@link STypeAttachment} com o nome informado.
     * @param fieldSimpleName - nome do campo
     */
    public STypeAttachment addFieldAttachment(String fieldSimpleName) {
        return addField(fieldSimpleName, STypeAttachment.class);
    }

    @Nonnull
    public STypeAttachmentList addFieldListOfAttachment(@Nonnull String listName, @Nonnull String fieldName) {
        checkNameNewField(listName);
        STypeAttachmentList newList = extendType(listName, STypeAttachmentList.class);
        newList.withView(SViewAttachmentList::new);
        newList.setElementsTypeFieldName(fieldName);
        return addInternal(listName, newList);
    }

    /**
     * Cria um novo campo do tipo {@link STypeFieldRef} com o nome informado.
     * @param fieldName - nome do campo
     */
    @SuppressWarnings("unchecked")
    public <T extends SType<I>, I extends SInstance> STypeFieldRef<I> addFieldRef(String fieldName, Class<T> sourceType) {
        return addField(fieldName, STypeFieldRef.class);
    }

    @Nullable
    public SType<?> getField(String fieldSimpleName) {
        return getFieldsConsolidated().get(fieldSimpleName);
    }

    /**
     * Retorna o campo da posição solicitada.
     */
    public SType<?> getField(int index) {
        return getFieldsConsolidated().getByIndex(index);
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
    @Nonnull
    public Collection<SType<?>> getFieldsLocal() {
        return isRecursiveReference() ? ((STypeComposite<?>) getSuperType()).getFieldsLocal() : (fieldsLocal == null) ? Collections.emptyList() : fieldsLocal.values();
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
     * Cria um novo campo do tipo {@link STypeTime} com o nome informado.
     */
    public STypeTime addFieldTime(String fieldSimpleName) {
        return addField(fieldSimpleName, STypeTime.class);
    }

    /**
     * Cria um novo campo do tipo {@link STypeTime} com o nome informado.
     */
    public STypeTime addFieldTime(String fieldSimpleName, boolean required) {
        return addField(fieldSimpleName, STypeTime.class, required);
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
     * Cria um novo campo do tipo {@link STypeLong} com o nome informado.
     */
    public STypeLong addFieldLong(String fieldSimpleName) {
        return addField(fieldSimpleName, STypeLong.class);
    }

    /**
     * Cria um novo campo do tipo {@link STypeLong} com o nome informado.
     */
    public STypeLong addFieldLong(String fieldSimpleName, boolean required) {
        return addField(fieldSimpleName, STypeLong.class, required);
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

    /**
     * Cria um novo campo do tipo {@link STypePassword} com o nome informado.
     */
    public STypePassword addFieldPassword(String fieldSimpleName) {
        return addField(fieldSimpleName, STypePassword.class);
    }

    public SSelectionBuilder selection() {
        this.withView(SViewSelectionBySelect::new);
        return new SSelectionBuilder(this);
    }

    public SSelectionBuilder autocomplete() {
        this.withView(SViewAutoComplete::new);
        return new SSelectionBuilder(this);
    }

    public <T extends Serializable> SelectionBuilder<T, INSTANCE_TYPE, INSTANCE_TYPE> selectionOf(Class<T> clazz, SView view) {
        this.withView(() -> view);
        return new SelectionBuilder<>(this);
    }

    public <T extends Serializable> SelectionBuilder<T, INSTANCE_TYPE, INSTANCE_TYPE> selectionOf(Class<T> clazz) {
        return selectionOf(clazz, new SViewSelectionBySelect());
    }

    public <T extends Serializable> SelectionBuilder<T, INSTANCE_TYPE, INSTANCE_TYPE> autocompleteOf(Class<T> clazz) {
        return selectionOf(clazz, new SViewAutoComplete());
    }

    public <T extends Serializable> SelectionBuilder<T, INSTANCE_TYPE, INSTANCE_TYPE> lazyAutocompleteOf(Class<T> clasz) {
        this.withView(() -> new SViewAutoComplete(SViewAutoComplete.Mode.DYNAMIC));
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

        private List<SType<?>>                  fieldsList;

        public int size() {
            return (fields == null) ? 0 : fields.size();
        }

        public boolean isEmpty() {
            return (fields == null) || fields.isEmpty();
        }

        @Nonnull
        public List<SType<?>> getFields() {
            return (fields == null) ? Collections.emptyList() : ensureList();
        }

        @Nullable
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
                toBeAdded.fields.values().forEach(fr -> addInternal(fr.getField()));
            }
        }

        public void addAll(Map<String, SType<?>> toBeAdded) {
            toBeAdded.values().forEach(f -> addInternal(f));
        }

        private void addInternal(SType<?> field) {
            if (fields == null) {
                fields = new LinkedHashMap<>();
            }
            fields.put(field.getNameSimple(), new FieldRef(field));
        }

        public int findIndex(String fieldName) {
            if (fields != null) {
                ensureList();
                FieldRef fr = fields.get(fieldName);
                if (fr != null) {
                    return fr.getIndex();
                }
            }
            return -1;
        }

        public SType<?> getByIndex(int fieldIndex) {
            if (fields != null) {
                return ensureList().get(fieldIndex);
            }
            throw new SingularFormException("Indice do campo incorreto: " + fieldIndex, this);
        }

        @Nonnull
        private List<SType<?>> ensureList() {
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
        private int            index = -1;

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
    public void init(Supplier<INSTANCE_TYPE> instanceRef) {
        initFields(instanceRef);
        super.init(instanceRef);
    }

    private void initFields(Supplier<INSTANCE_TYPE> instanceRef) {
        Collection<SType> fields = (Collection) getFields();
        for (SType t : fields) {
            //Passa uma referência lazy demodo que não precisa fazer busca se o tipo não possui inicialização
            t.init(() -> instanceRef.get().getField(t.getNameSimple()));
        }
    }
}
