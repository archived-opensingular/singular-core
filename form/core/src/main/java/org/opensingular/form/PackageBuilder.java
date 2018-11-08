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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

/**
 * Builder para configuração do tipos e atributos de um pacote, com diversos métodos de apoio.
 *
 * @author Daniel C. Bordin
 */
public class PackageBuilder {

    private final SPackage sPackage;
    private final SDictionary dictionary;

    PackageBuilder(@Nonnull SPackage sPackage) {
        this.sPackage = sPackage;
        dictionary = sPackage.getDictionary();
    }

    public SDictionary getDictionary() {
        return dictionary;
    }

    /**
     * Recupera o tipo, já carregado no dicionário, da classe informada. Senão estiver carregado ainda, busca carregá-lo
     * e as definições do pacote a que pertence. Senão encontrar no dicionário e nem conseguir encontrar para carregar,
     * então dispara Exception. É o mesmo que chamar {@link SDictionary#getType(Class)}.
     */
    @Nonnull
    public <T extends SType<?>> T getType(@Nonnull Class<T> typeClass) {
        return dictionary.getType(typeClass);
    }

    /**
     * Carrega no dicionário o pacote informado e todas as definições do mesmo, se ainda não tiver sido carregado. É
     * seguro chamar é método mais de uma vez para o mesmo pacote. É o mesmo que chamar {@link
     * SDictionary#loadPackage(Class)}.
     *
     * @return O pacote carregado
     */
    @Nonnull
    public <T extends SPackage> T loadPackage(Class<T> packageClass) {
        return getDictionary().loadPackage(packageClass);
    }

    public SPackage getPackage() {
        return sPackage;
    }

    public <T extends SType<X>, X extends SInstance> T createType(String name, Class<T> parentClass) {
        return sPackage.extendType(name, parentClass);
    }

    @Nonnull
    public <T extends SType<?>> T createType(@Nullable String simpleNameNewType, @Nonnull T parentType) {
        return sPackage.extendType(simpleNameNewType, parentType);
    }

    @Nonnull
    public <T extends SType<?>> T createType(@Nonnull Class<T> newTypeClass) {
        return sPackage.registerType(newTypeClass);
    }

    @SuppressWarnings("unchecked")
    public STypeComposite<SIComposite> createCompositeType(String simpleNameNewType) {
        return createType(simpleNameNewType, STypeComposite.class);
    }

    public <I extends SIComposite> STypeList<STypeComposite<I>, I> createListOfNewCompositeType(String simpleNameNewType,
            String simpleNameNewCompositeType) {
        return sPackage.createListOfNewTypeComposite(simpleNameNewType, simpleNameNewCompositeType);
    }

    public <I extends SInstance, T extends SType<I>> STypeList<T, I> createListTypeOf(String simpleNameNewType,
                                                                                        Class<T> elementsTypeClass) {
        return createListTypeOf(simpleNameNewType, getType(elementsTypeClass));
    }

    @Nonnull
    public <I extends SInstance, T extends SType<I>> STypeList<T, I> createListTypeOf(@Nonnull String simpleNameNewType, @Nonnull T elementsType) {
        return sPackage.createTypeListOf(simpleNameNewType, null, elementsType);
    }

    @Nonnull
    public <I extends SInstance, T extends SType<I>> STypeList<T, I> createListTypeOf(@Nonnull String simpleNameNewType, @Nonnull String elementSimpleName, @Nonnull T elementsType) {
        return sPackage.createTypeListOf(simpleNameNewType, elementSimpleName, elementsType);
    }

    @SuppressWarnings("rawtypes")
    public <T extends SType<?>> void addAttribute(Class<? extends SType> typeClass, AtrRef<T, ?, ?> atr) {
        addAttributeInternal(getType(typeClass), atr);
    }

    @SuppressWarnings("rawtypes")
    public <T extends SType<?>, V> void addAttribute(Class<? extends SType> typeClass, AtrRef<T, ?, V> atr, V attributeValue) {
        SType<?> targetType = getType(typeClass);
        SType<?> attribute = addAttributeInternal(targetType, atr);
        targetType.setAttributeValue(attribute, attributeValue);
    }

    @SuppressWarnings("rawtypes")
    private <T extends SType<?>> SType<?> addAttributeInternal(SType<?> targetType, AtrRef<T, ?, ?> atr) {
        SType<?> attribute = getAttribute(atr);
        targetType.addAttribute(attribute);
        return attribute;
    }

    @Nonnull
    public SType<?> getAttribute(@Nonnull AtrRef<?, ?, ?> atr) {
        AttrInternalRef ref = getDictionary().getAttributeReferenceOrException(atr);
        return Objects.requireNonNull(ref.getType());
    }

    public <T extends SType<?>> T createAttributeIntoType(@Nonnull Class<? extends SType> targetTypeClass, @Nonnull AtrRef<T, ?, ?> atr) {

        Class attributeTypeClass = atr.isSelfReference() ? targetTypeClass : atr.getTypeClass();
        T attributeType = (T) getType(attributeTypeClass);
        SType<?> targetType = getType(targetTypeClass);

        SScopeBase scope = (targetType.getPackage() == sPackage) ? targetType : sPackage;
        resolveBind(scope, (Class<SType<?>>) targetTypeClass, atr, attributeType);
        return createAttributeIntoTypeInternal(targetType, atr.getNameSimple(), attributeType, atr.isSelfReference());
    }

    public <T extends SType<?>> T createAttributeIntoType(Class<? extends SType<?>> targetTypeClass,
                                                          String attributeSimpleName, Class<T> attributeTypeClass) {
        return createAttributeIntoType(getType(targetTypeClass), attributeSimpleName, attributeTypeClass);
    }

    public <T extends SType<?>> T createAttributeIntoType(@Nonnull SType<?> targetType,
            @Nonnull String attributeSimpleName, @Nonnull Class<T> attributeTypeClass) {
        return createAttributeIntoType(targetType, attributeSimpleName, getType(attributeTypeClass));
    }

    public <T extends SType<?>> T createAttributeIntoType(@Nonnull SType<?> targetType, @Nonnull String attributeSimpleName, @Nonnull T attributeType) {
        return createAttributeIntoTypeInternal(targetType, new SimpleName(attributeSimpleName), attributeType, false);
    }

    @Nonnull
    private <T extends SType<?>> T createAttributeIntoTypeInternal(@Nonnull SType<?> targetType,
            @Nonnull SimpleName attrSimpleName, @Nonnull T attributeType, boolean selfReference) {
        sPackage.verifyIfMayAddNewType(attrSimpleName);
        SScopeBase scope = Objects.equals(targetType.getPackage(), sPackage) ? targetType : sPackage;

        T attributeDef = scope.extendType(attrSimpleName, attributeType);
        getDictionary().registerAttribute(attributeDef, targetType, selfReference);
        return attributeDef;
    }

    public <I extends SInstance, T extends SType<I>> T createAttributeType(AtrRef<T, I, ?> atr) {
        if (atr.isSelfReference()) {
            throw new SingularFormException("Não pode ser criado um atributo global que seja selfReference");
        }
        return createAttributeType(atr, getType(atr.getTypeClass()));
    }

    private <T extends SType<?>> T createAttributeType(AtrRef<T, ?, ?> atr, T attributeType) {
        resolveBind(sPackage, null, atr, attributeType);
        sPackage.verifyIfMayAddNewType(atr.getNameSimple());

        T attributeDef = sPackage.extendType(atr.getNameSimple(), attributeType);
        getDictionary().registerAttribute(attributeDef);
        return attributeDef;
    }

    private void resolveBind(SScope scope, Class<SType<?>> ownerTypeClass, AtrRef<?, ?, ?> atr, SType<?> attributeType) {
        if (atr.getPackageClass() == sPackage.getClass()) {
            atr.bind(scope.getName());
        } else {
            throw new SingularFormException("Tentativa de criar o atributo '" + atr.getNameSimple() + "' do pacote "
                    + atr.getPackageClass().getName() + " durante a construção do pacote " + sPackage.getName());
        }
        if (!atr.isSelfReference() && !(atr.getTypeClass().isInstance(attributeType))) {
            throw new SingularFormException("O atributo " + atr.getNameFull() + " esperava ser do tipo " + atr.getTypeClass().getName()
                    + " mas foi associado a uma instância de " + attributeType.getClass().getName());
        }
    }

    public void debug() {
        getDictionary().debug();
    }
}
