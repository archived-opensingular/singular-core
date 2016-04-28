/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.mform;

public class PackageBuilder {

    private final SPackage sPackage;

    PackageBuilder(SPackage sPackage) {
        this.sPackage = sPackage;
    }

    public SDictionary getDictionary() {
        return sPackage.getDictionary();
    }
    public SPackage getPackage() {
        return sPackage;
    }

    public <T extends SType<X>, X extends SInstance> T createType(String name, Class<T> parentClass) {
        return sPackage.extendType(name, parentClass);
    }

    final <T extends SType<?>> T creatType(String simpleNameNewType, T parentType) {
        return sPackage.extendType(simpleNameNewType, parentType);
    }

    public <T extends SType<?>> T createType(Class<T> newTypeClass) {
        getDictionary().getTypesInternal().verifyMustNotBePresent(newTypeClass);
        TypeBuilder tb = new TypeBuilder(newTypeClass);
        return sPackage.registerType(tb, newTypeClass);
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
        T elementsType = (T) getDictionary().getType(elementsTypeClass);
        return createListTypeOf(simpleNameNewType, elementsType);
    }

    public <I extends SInstance, T extends SType<I>> STypeList<T, I> createListTypeOf(String simpleNameNewType, T elementsType) {
        return sPackage.createTypeListOf(simpleNameNewType, elementsType);
    }

    @SuppressWarnings("rawtypes")
    public <T extends SType<?>> void addAttribute(Class<? extends SType> typeClass, AtrRef<T, ?, ?> atr) {
        addAttributeInternal(typeClass, atr);
    }

    @SuppressWarnings("rawtypes")
    public <T extends SType<?>, V extends Object> void addAttribute(Class<? extends SType> typeClass, AtrRef<T, ?, V> atr, V attributeValue) {
        SAttribute attribute = addAttributeInternal(typeClass, atr);
        SType<?> targetType = getDictionary().getType(typeClass);
        targetType.setAttributeValue(attribute, attributeValue);
    }

    @SuppressWarnings("rawtypes")
    private <T extends SType<?>> SAttribute addAttributeInternal(Class<? extends SType> typeClass, AtrRef<T, ?, ?> atr) {
        SType<?> targetType = getDictionary().getType(typeClass);

        SAttribute attribute = findAttribute(atr);
        targetType.addAttribute(attribute);
        return attribute;
    }

    public SAttribute getAttribute(AtrRef<?, ?, ?> atr) {
        return findAttribute(atr);
        // return new AtributoBuilder(null, findAtributo(atr));
    }

    private SAttribute findAttribute(AtrRef<?, ?, ?> atr) {
        SAttribute attribute = getAttributeOptional(atr);
        if (attribute == null) {
            throw new RuntimeException("O atributo '" + atr.getNameFull() + "' não está definido");
        }
        return attribute;
    }

    private SAttribute getAttributeOptional(AtrRef<?, ?, ?> atr) {
        getDictionary().loadPackage(atr.getPackageClass());

        if (!atr.isBinded()) {
            return null;
        }
        SType<?> type = getDictionary().getTypeOptional(atr.getNameFull());
        if (type != null && !(type instanceof SAttribute)) {
            throw new RuntimeException("O tipo '" + atr.getNameFull() + "' não é um tipo de MAtributo. É " + type.getClass().getName());
        }
        return (SAttribute) type;
    }

    public <T extends SType<?>> SAttribute createAttributeIntoType(Class<? extends SType> targetTypeClass, AtrRef<T, ?, ?> atr) {
        T attributeType;
        if (atr.isSelfReference()) {
            attributeType = (T) getDictionary().getType((Class) targetTypeClass);
        } else {
            attributeType = (T) getDictionary().getType((Class) atr.getTypeClass());
        }
        SType<?> targetType = getDictionary().getType(targetTypeClass);
        return createAttributeIntoType(targetType, targetTypeClass, atr, attributeType);
    }

    public <T extends SType<?>> SAttribute createAttributeIntoType(Class<? extends SType<?>> targetTypeClass, String attributeSimpleName,
            Class<T> attributeTypeClass) {
        SType<?> targetType = getDictionary().getType(targetTypeClass);
        return createAttributeIntoType(targetType, attributeSimpleName, attributeTypeClass);
    }

    public <T extends SType<?>> SAttribute createAttributeIntoType(SType<?> targetType, String attributeSimpleName,
            Class<T> attributeTypeClass) {
        SType<?> attributeType = getDictionary().getType(attributeTypeClass);
        return createAttributeIntoType(targetType, attributeSimpleName, attributeType);
    }

    public SAttribute createAttributeIntoType(SType<?> targetType, String attributeSimpleName, SType<?> attributeType) {
        if (targetType.getPackage() == sPackage) {
            return createAttributeIntoTypeInternal(targetType, attributeSimpleName, false, attributeType);
        } else {
            getDictionary().getTypesInternal().verifyMustNotBePresent(sPackage.getName() + "." + attributeSimpleName);

            SAttribute atributo = new SAttribute(attributeSimpleName, attributeType, targetType, false);
            atributo = sPackage.registerType(atributo, null);
            targetType.addAttribute(atributo);
            return atributo;
        }
    }

    final <T extends SType<?>> SAttribute createAttributeIntoType(SType<?> targetType, Class<? extends SType> classeAlvo,
            AtrRef<T, ?, ?> atr, T attributeType) {
        if (targetType.getPackage() == sPackage) {
            resolveBind(targetType, (Class<SType<?>>) classeAlvo, atr, attributeType);
            return createAttributeIntoTypeInternal(targetType, atr.getNameSimple(), atr.isSelfReference(), attributeType);
        } else {
            resolveBind(sPackage, (Class<SType<?>>) classeAlvo, atr, attributeType);
            getDictionary().getTypesInternal().verifyMustNotBePresent(atr.getNameFull());

            SAttribute attribute = new SAttribute(atr.getNameSimple(), attributeType, targetType, atr.isSelfReference());
            attribute = sPackage.registerType(attribute, null);
            targetType.addAttribute(attribute);
            return attribute;
        }

    }

    private SAttribute createAttributeIntoTypeInternal(SType<?> targetType, String attributeSimpleName, boolean selfReference,
            SType<?> attributeType) {
        getDictionary().getTypesInternal().verifyMustNotBePresent(targetType.getName() + "." + attributeSimpleName);

        SAttribute attribute = targetType.registerType(new SAttribute(attributeSimpleName, attributeType, targetType, selfReference), null);
        targetType.addAttribute(attribute);
        return attribute;
    }

    public <I extends SInstance, T extends SType<I>> SAttribute createAttributeType(AtrRef<T, ?, ?> atr) {
        if (atr.isSelfReference()) {
            throw new RuntimeException("Não pode ser criado um atributo global que seja selfReference");
        }
        return createAttributeType(atr, getDictionary().getType(atr.getTypeClass()));
    }

    private <T extends SType<?>> SAttribute createAttributeType(AtrRef<T, ?, ?> atr, T attributeType) {
        resolveBind(sPackage, null, atr, attributeType);
        getDictionary().getTypesInternal().verifyMustNotBePresent(atr.getNameFull());

        SAttribute attribute = new SAttribute(atr.getNameSimple(), attributeType);
        return sPackage.registerType(attribute, null);
    }

    private void resolveBind(SScope scope, Class<SType<?>> ownerTypeClass, AtrRef<?, ?, ?> atr, SType<?> attributeType) {
        if (atr.getPackageClass() == sPackage.getClass()) {
            atr.bind(scope.getName());
        } else {
            throw new RuntimeException("Tentativa de criar o atributo '" + atr.getNameSimple() + "' do pacote "
                    + atr.getPackageClass().getName() + " durante a construção do pacote " + sPackage.getName());
        }
        if (!atr.isSelfReference() && !(atr.getTypeClass().isInstance(attributeType))) {
            throw new RuntimeException("O atributo " + atr.getNameFull() + " esperava ser do tipo " + atr.getTypeClass().getName()
                    + " mas foi associado a uma instância de " + attributeType.getClass().getName());
        }
    }

    public void debug() {
        getDictionary().debug();
    }
}
