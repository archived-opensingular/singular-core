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

import com.google.common.base.Preconditions;
import org.opensingular.form.internal.PathReader;
import org.opensingular.form.processor.ClassInspectionCache;
import org.opensingular.form.processor.ClassInspectionCache.CacheKey;
import org.opensingular.form.processor.TypeProcessorAttributeReadFromFile;
import org.opensingular.form.processor.TypeProcessorBeanInjector;
import org.opensingular.form.processor.TypeProcessorPublicFieldsReferences;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class SScopeBase implements SScope {

    private Map<String, SType<?>> localTypes;

    private static final Logger LOGGER = Logger.getLogger(SScopeBase.class.getName());

    /**
     * Indica que o escopo têm uma referência recursiva para ele mesmo. Somente é marcado true na referência interna, ou
     * seja, a primeira definição do tipo não será marcado como recursivo, mas o campo filho que aponta para ele mesmo,
     * será.
     */
    private boolean recursiveReference;

    /**
     * Retorna os tipos criados localmente. Se for um pacote, retorna o tipos do
     * pacote. Se for um tipo, então retorna o tipo criados no escopo local do
     * tipo (tipo dentro de tipo).
     */
    @Nonnull
    public Collection<SType<?>> getLocalTypes() {
        return getLocalTypesMap().values();
    }

    private Map<String, SType<?>> getLocalTypesMap() {
        if (isRecursiveReference()) {
            //Se é uma referência recursiva, não deve criar um novo mapa local e sim usar o mapa do tipo original
            // para o qual aponta
            if (this instanceof SType) {
                SType<?> superType = ((SType<?>) this).getSuperType();
                if (superType == null || superType.getClass() != getClass()) {
                    //Verificação de sanidade
                    throw new SingularFormException(
                            "Erro interno: uma referência recursiva não extende um tipo que é da mesma classe " +
                                    "(superType()=" + superType + ")", this);
                } else {
                    return ((SScopeBase) superType).getLocalTypesMap();
                }
            }
        }
        return localTypes == null ? Collections.emptyMap() : localTypes;
    }

    @Override
    @Nonnull
    public Optional<SType<?>> getLocalTypeOptional(@Nonnull String path) {
        return getLocalTypeOptional(new PathReader(path));
    }

    @Nonnull
    final Optional<SType<?>> getLocalTypeOptional(@Nonnull PathReader pathReader) {
        SType<?> type = getLocalTypesMap().get(pathReader.getToken());
        if (type == null) {
            return Optional.empty();
        } else if (pathReader.isLast()) {
            return Optional.of(type);
        }
        return type.getLocalTypeOptional(pathReader.next());
    }


    @Override
    @Nonnull
    public SType<?> getLocalType(@Nonnull String path) {
        // Não utiliza getTipoLocalOpcional, pois da forma abaixo é possível
        // apontar precisamente onde deu erro no path passado.
        return getLocalType(new PathReader(path));
    }

    @Nonnull
    final SType<?> getLocalType(@Nonnull PathReader pathReader) {
        SType<?> type = getLocalTypesMap().get(pathReader.getToken());
        if (type != null) {
            if (pathReader.isLast()) {
                return type;
            }
            return type.getLocalType(pathReader.next());
        }
        throw new SingularFormException(pathReader
                .getErrorMsg(this, "Não foi encontrado o tipo '" + pathReader.getToken() + "' em '" + this + "'"));
    }

    /** Registro o tipo informado neste escopo. */
    @Nonnull
    final <T extends SType<?>> T registerType(@Nonnull Class<T> typeClass) {
        Objects.requireNonNull(typeClass);
        T t;
        try {
            t = typeClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new SingularFormException("Erro instanciando " + typeClass.getName(), e);
        }
        t = registerTypeInternal(t, typeClass);
        TypeProcessorAttributeReadFromFile.INSTANCE.onRegisterTypeByClass(t, typeClass);
        return t;
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    final <T extends SType<?>> T registerTypeInternal(@Nonnull T newType, @Nullable Class<T> typeClass) {
        getDictionary().registerType(this, newType, typeClass);
        TypeProcessorBeanInjector.INSTANCE.onRegisterTypeByClass(newType, (Class)newType.getClass());
        /*
        (by Daniel Bordin) O If abaixo impede que o onLoadType seja chamado mais de uma vezes caso o novo tipo seja
        apenas uma extensão da classe já carregada anteriormente, ou seja, impede que o mesmo onLoadType seja
        invocado múltiplas vezes. Esse controle é especialmente importante para eveitar entrar em loop quando
        um tipo tem uma referência para ele mesmo, além de evitar recargas e duplicação de atributos.
        */
        newType.setRecursiveReference(isRecursiveReference(newType));
        SType<?> superType = newType.getSuperType();
        if (superType == null || superType.getClass() != newType.getClass()) {
            newType.extendSubReference();
            TypeProcessorPublicFieldsReferences.INSTANCE.processTypePreOnLoadTypeCall(newType);
            newType.setCallingOnLoadType(true);
            callOnLoadTypeIfNecessary(newType);
            newType.setCallingOnLoadType(false);
            TypeProcessorPublicFieldsReferences.INSTANCE.processTypePosRegister(newType, true);
            getDictionary().runPendingTypeProcessorExecution(newType);
        } else {
            if (newType.isRecursiveReference()) {
                if(isSuperTypeCallingOnLoadType(newType)) {
                    //Não pode rodar os processadores em quanto nao tiver terminado o onLoadType do tipo pai
                    newType.setCallingOnLoadType(true);
                    getDictionary().addTypeProcessorForLatterExecution(superType, () -> {
                        TypeProcessorPublicFieldsReferences.INSTANCE.processTypePosRegister(newType, false);
                        getDictionary().runPendingTypeProcessorExecution(newType);
                        newType.setCallingOnLoadType(false);
                    });
                } else {
                    TypeProcessorPublicFieldsReferences.INSTANCE.processTypePosRegister(newType, false);
                }
            } else {
                newType.extendSubReference();
                TypeProcessorPublicFieldsReferences.INSTANCE.processTypePosRegister(newType, false);
            }
        }
        return newType;
    }

    /**
     * Chama o método onLoadType somente se o tipo for um classe que implementa o método de forma específica, ou seja,
     * não chama o onLoadType se a classe apenas derivar de um classe que implementou o onLoadType e cuja a chamada
     * já foi executada uma vez na carga do tipo pai.
     */
    private <T extends SType<?>> void callOnLoadTypeIfNecessary(T newType) {
        if (newType.getSuperType() == null) {
            return; //Não precisa chamar onLoadType no SType
        }
        Class<?> c = newType.getClass();
        while (true) {
            if (cachedHasDeclaredMethodOnClass(c)) {
                break; //Então deve chamar onLoadType, pois há uma implementação específica para a classe
            }
            c = c.getSuperclass();
            if (c == newType.getSuperType().getClass()) {
                return; //Não é necessário chamar onLoadType, pois já foi chamado no tipo pai
            }
        }
        newType.onLoadType(new TypeBuilder(newType));
    }

    @Nonnull
    private static boolean cachedHasDeclaredMethodOnClass(@Nonnull Class<?> typeClass) {
        return ClassInspectionCache.getInfo(typeClass, CacheKey.HAS_ON_LOAD_TYPE_METHOD,
                SScopeBase::hasDeclaredMethodOnClass);
    }

    @Nonnull
    private static Boolean hasDeclaredMethodOnClass(@Nonnull Class<?> typeClass) {
        try {
            //This call is expensive and must have its result cached
            typeClass.getDeclaredMethod("onLoadType", TypeBuilder.class);
            return Boolean.TRUE;
        } catch (NoSuchMethodException e) {
            return Boolean.FALSE;
        }
    }

    /** Verificar se o tipo super já terminiu a chamada do onLoadType ou se está no meio da execução do mesmo. */
    private boolean isSuperTypeCallingOnLoadType(SType<?> type) {
        return type.getSuperType().isCallingOnLoadType();
    }

    /**
     * Verifica se o tipo é uma extensão de um tipo da mesma classe e que possui uma referência a ele mesmo. Vai
     * indicar true somente para a referência interna dentro da referência circular, ou seja, o tipo (ou classe) que
     * contêm o campo não será marcado como referência circular, somente o campo em si.
     */
    private boolean isRecursiveReference(@Nonnull SType<?> type) {
        if (type.getSuperType() != null && type.getSuperType().isRecursiveReference()) {
            return true;
        }
        for(SScope parent = type.getParentScope(); parent instanceof SType; parent = parent.getParentScope()) {
            if(parent == type || parent == type.getSuperType()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Creates new type in the current scope extending the informed {@link SType}.
     *
     * @param simpleNameNewType If the name isn't informed, uses the same name of the parent type.
     */
    @Nonnull
    final <T extends SType<?>> T extendType(@Nullable SimpleName simpleNameNewType, @Nonnull T parentType) {
        SFormUtil.verifySameDictionary(this, parentType);
        T newType = parentType.extend(simpleNameNewType, null);
        return registerTypeInternal(newType, null);
    }

    /**
     * Creates new type in the current scope extending the informed {@link SType}.
     *
     * @param simpleNameNewType If the name isn't informed, uses the same name of the parent type.
     */
    @Nonnull
    final <T extends SType<?>> T extendType(@Nullable String simpleNameNewType, @Nonnull T parentType) {
        return extendType(SimpleName.ofNullable(simpleNameNewType), parentType);
    }

    /**
     * Creates new type in the current scope extending the informed {@link SType}.
     *
     * @param simpleNameNewType If the name isn't informed, uses the same name of the parent type.
     */
    @Nonnull
    final <T extends SType<?>> T extendType(@Nullable String simpleNameNewType, @Nonnull Class<T> parentTypeClass) {
        T parentType = resolveType(parentTypeClass);
        return extendType(SimpleName.ofNullable(simpleNameNewType), parentType);
    }

    /**
     * Creates new type in the current scope extending directly the informed {@link SType} and also extending
     * the second informed type as complementary (secondary) super type.
     *
     * @param simpleNameNewType      If the name isn't informed, uses the same name of the parent type.
     * @param complementarySuperType The Java class of complementary type, must be the same Java class of the parentType
     *                               or a super class of the parent type's Java class.
     */
    @Nonnull
    final <T extends SType<?>> T extendMultipleTypes(@Nullable String simpleNameNewType, @Nonnull T parentType,
            @Nonnull SType<?> complementarySuperType) {
        return extendMultipleTypes(SimpleName.ofNullable(simpleNameNewType), parentType, complementarySuperType);
    }

    /**
     * Creates new type in the current scope extending directly the informed {@link SType} and also extending
     * the second informed type as complementary (secondary) super type.
     *
     * @param simpleNameNewType      If the name isn't informed, uses the same name of the parent type.
     * @param complementarySuperType The Java class of complementary type, must be the same Java class of the parentType
     *                               or a super class of the parent type's Java class.
     */
    @Nonnull
    final <T extends SType<?>> T extendMultipleTypes(@Nullable String simpleNameNewType, @Nonnull Class<T> parentType,
            @Nonnull SType<?> complementarySuperType) {
        return extendMultipleTypes(SimpleName.ofNullable(simpleNameNewType), resolveType(parentType),
                complementarySuperType);
    }

    /**
     * Creates new type in the current scope extending directly the informed {@link SType} and also extending
     * the second informed type as complementary (secondary) super type.
     *
     * @param simpleNameNewType      If the name isn't informed, uses the same name of the parent type.
     * @param complementarySuperType The Java class of complementary type, must be the same Java class of the parentType
     *                               or a super class of the parent type's Java class.
     */
    @Nonnull
    final <T extends SType<?>> T extendMultipleTypes(@Nullable SimpleName simpleNameNewType, @Nonnull T parentType,
            @Nonnull SType<?> complementarySuperType) {
        SFormUtil.verifySameDictionary(this, parentType);
        SFormUtil.verifySameDictionary(this, complementarySuperType);
        if (parentType.isTypeOf(complementarySuperType)) {
            throw new SingularFormException("Unnecessary multiple inheritance: " + parentType.getName() +
                    " is already a direct super type of " + complementarySuperType.getName());
        }
        if (complementarySuperType.isTypeOf(parentType)) {
            throw new SingularFormException("Unnecessary multiple inheritance: " + complementarySuperType.getName() +
                    " is already a direct super type of " + parentType.getName());
        }
        if (!complementarySuperType.getClass().isAssignableFrom(parentType.getClass())) {
            throw new SingularFormException("Can't do multiple inheritance because " + parentType.getName() + " (" +
                    parentType.getClass().getName() + ") java class isn't a derived class of the type " +
                    complementarySuperType.getName() + " (" + complementarySuperType.getClass().getName() + ")");
        }
        if (complementarySuperType.isComposite() && !complementarySuperType.isRecursiveReference()) {
            verifyComplementarySuperTypeHasCreateNewField(parentType, complementarySuperType);
        }
        T newType = parentType.extend(simpleNameNewType, complementarySuperType);
        return registerTypeInternal(newType, null);
    }

    /** Verifies if the complementary super type has created a new field. Throws exception in this case. */
    private <T extends SType<?>> void verifyComplementarySuperTypeHasCreateNewField(@Nonnull T parentType,
            @Nonnull SType<?> complementarySuperType) {
        SType<?> common = SFormUtil.findCommonType(parentType, complementarySuperType);
        for (SType<?> current = complementarySuperType; current != common; current = current.getSuperType()) {
            for (SType<?> field : ((STypeComposite<?>) current).getFieldsLocal()) {
                if (Objects.requireNonNull(field.getSuperType()).getParentScope() != current.getSuperType()) {
                    throw new SingularFormException(
                            current + " can't be a complementary super type because it has created a new field: " +
                                    field);
                }
            }
        }
    }

    /**
     * Extending the two types, as super type and complementary type, for the new type, also ensuring the type will be
     * reused if it already exists in the current scope.
     */
    @Nonnull
    final <T extends SType<?>> T findOrCreateExtendedType(@Nonnull Class<T> parentTypeClass,
            @Nonnull SType<?> complementarySuperType) {
        T parentType = resolveType(parentTypeClass);
        for (SType<?> type : getLocalTypes()) {
            if (type.getSuperType() == parentType && type.getComplementarySuperType().orElse(null) ==
                    complementarySuperType) {
                return parentTypeClass.cast(type);
            }
        }
        return extendMultipleTypes((SimpleName) null, parentType, complementarySuperType);
    }

    @SuppressWarnings("unchecked")
    final <I extends SIComposite> STypeList<STypeComposite<I>, I> createListOfNewTypeComposite(String simpleNameNewType,
            String simpleNameNewTypeComposite) {
        STypeList<STypeComposite<I>, I> listType = extendType(simpleNameNewType, STypeList.class);
        listType.setElementsType(simpleNameNewTypeComposite, resolveType(STypeComposite.class));
        return listType;
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    final <I extends SInstance, T extends SType<I>> STypeList<T, I> createTypeListOf(@Nonnull String simpleNameNewType, @Nullable String elementSimpleName, @Nonnull T elementsType) {
        Preconditions.checkNotNull(elementsType);
        STypeList<T, I> listType = extendType(simpleNameNewType, STypeList.class);
        listType.setElementsType(elementSimpleName, elementsType);
        return listType;
    }

    @Nonnull
    final <T extends SType<?>> T resolveType(@Nonnull Class<T> typeClass) {
        return getDictionary().getType(typeClass);
    }

    final void register(SType<?> type) {
        if(isRecursiveReference()) {
            ((SScopeBase) getParentScope()).register(type);
        } else {
            verifyIfMayAddNewType(type.getNameSimpleObj());
            if (localTypes == null) {
                localTypes = new LinkedHashMap<>();
            }
            localTypes.put(type.getNameSimple(), type);
        }
    }

    final void verifyIfMayAddNewType(SimpleName simpleName) {
        if (localTypes != null && localTypes.containsKey(simpleName.get())) {
            throw new SingularFormException("A definição '" + simpleName + "' já está criada no escopo " + getName());
        }
    }

    /**
     * Imprime informação de debug para o console com a estrutura de dados do escopo (incluindo sub definições de forma
     * recursiva).
     */
    public final void debug() {
        debug(0);
    }

    /**
     * Imprime informação de debug (com margem de espaço esquerda, tabulado) para o console com a estrutura de dados do
     * escopo (incluindo sub definições de forma recursiva).
     */
    public void debug(int level) {
        debug(System.out, level);
    }

    /**
     * Imprime informação de debug para a saída informada com a estrutura de dados do escopo (incluindo sub
     * definições de forma recursiva).
     */
    public final void debug(Appendable appendable) {
        debug(appendable, 0);
    }

    void debug(Appendable appendable, int level) {
        Collection<SType<?>> local = getLocalTypes();
        if (!isRecursiveReference() && !local.isEmpty()) {
            local.stream().filter(t -> t.isAttribute()).forEach(t -> t.debug(appendable, level));
            local.stream().filter(t -> !t.isAttribute()).forEach(t -> t.debug(appendable, level));
        }
    }

    protected static Appendable pad(Appendable appendable, int level) {
        try {
            for (int i = level * 3; i > 0; i--) {
                appendable.append(' ');
            }
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
        }
        return appendable;
    }

    final public boolean hasAnyValidation() {
        for (Map.Entry<String, SType<?>> entry : getLocalTypesMap().entrySet()) {
            if(entry.getValue().hasValidation() || entry.getValue().hasAnyValidation()){
                return true;
            }
        }
        return false;
    }

    /**
     * Indica se o tipo é um referência interna (um subcampo ou tipo de lista) que apenas para um tipo que o encapsula.
     * Será marcado como recusivo apenas a referência interna. O tipo que encapsula não será marcado.
     */
    public boolean isRecursiveReference() {
        return recursiveReference;
    }

    final void setRecursiveReference(boolean recursiveReference) {
        this.recursiveReference = recursiveReference;
    }

    @Override
    public String toString() {
        return getName();
    }
}
