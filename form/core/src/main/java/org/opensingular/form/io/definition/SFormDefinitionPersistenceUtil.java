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

package org.opensingular.form.io.definition;

import com.google.common.collect.Lists;
import org.opensingular.form.*;
import org.opensingular.lib.commons.internal.function.SupplierUtil;

import java.util.*;
import java.util.function.Supplier;

/**
 * Transforma a definição de um tipo ou mesmo de um pacote inteiro em uma
 * estrutura de dados equivalente. Tipicamente essa estrutura é utilizada para
 * persistir a definição do tipo ou mesmo transmitir pela rede.
 *
 * @author Daniel C. Bordin
 */
public class SFormDefinitionPersistenceUtil {

    private final static Supplier<STypePersistenceArchive> typePresistenceArchive = SupplierUtil.cached(
            SFormDefinitionPersistenceUtil::createTypePersistence);

    private SFormDefinitionPersistenceUtil() {
    }

    public static SIPersistenceArchive toArchive(SType<?> type) {
        ContextArchive ctx = new ContextArchive(type.getDictionary());

        ctx.getArchive().setRootTypeName(type.getName());
        ensureType(ctx, type);

        return ctx.getArchive();
    }


    private static void ensureType(ContextArchive ctx, SType<?> type) {
        SType<?> currentType = type;
        for (SScope t = type.getParentScope(); t instanceof SType; t = ((SType<?>) t).getParentScope()) {
            currentType = (SType<?>) t;
        }

        if (!ctx.isNecessaryToArchive(currentType) || ctx.isAlreadyArchived(currentType)) {
            return;
        }
        SIPersistenceType pType = ctx.createTypeInPackage(currentType);
        writeType(ctx, pType, currentType);
    }

    private static void writeType(ContextArchive ctx, SIPersistenceType pType, SType<?> type) {
        SType<?> superType = type.getSuperType();
        pType.setSuperType(ctx.translateImport(superType));

        ensureType(ctx, superType);

        for (SInstance attrInstance : type.getAttributes()) {
            SIPersistenceAttribute pAttribute = pType.newAttribute();
            writeAttr(ctx, pAttribute, attrInstance);
        }

        if (type.isComposite()) {
            //TODO (por Daniel Bordin) O código abaixo ainda precisa resolver a questão de field que foram extendido
            // e tiveram apenas uma atributo alterado
            for (SType<?> localField : ((STypeComposite<?>) type).getFieldsLocal()) {
                SIPersistenceType pMember = pType.addMember(localField.getNameSimple());
                writeType(ctx, pMember, localField);
            }
        }
    }

    private static void writeAttr(ContextArchive ctx, SIPersistenceAttribute attributeArchive, SInstance attribute) {
        attributeArchive.setAttrType(attribute.getType().getName());
        attributeArchive.setAttrValue(attribute.getValue());
    }
//
//    private static Collection<SInstance> collectAttributes(SType<?> sType) {
//        Map<String, SInstance> attrs = new HashMap<>();
//        for (SType<?> current = sType; current != null; current = current.getSuperType()) {
//            for (SInstance s : current.getAttributes()) {
//                if (!attrs.containsKey(s.getType().getName())) {
//                    attrs.put(s.getType().getName(), s);
//                }
//            }
//        }
//        return attrs.values();
//    }

    public static SType<?> fromArchive(SIPersistenceArchive persistenceArchive) {
        ContextUnarchive ctx = new ContextUnarchive(persistenceArchive);
        List<SIPersistencePackage> children = persistenceArchive.getPackages().getChildren();
        Lists.reverse(children).forEach(ctx::createNewPackage);

        for (SIPersistencePackage pPackage : Lists.reverse(children)) {
            PackageBuilder pkg = ctx.getPackage(pPackage.getPackageName());
            for (SIPersistenceType pType : Lists.reverse(pPackage.getTypes().getChildren())) {
                SType<?> superType = resolveSuperType(ctx, pkg.getPackage(), pType);
                SType<?> newType = pkg.createType(pType.getSimpleName(), superType);
                readType(ctx, newType, pType);
            }
        }
        return ctx.getDictionary().getType(persistenceArchive.getRootTypeName());
    }


    private static void readType(ContextUnarchive ctx, SType<?> newType, SIPersistenceType pType) {
        for (SIPersistenceAttribute attribute : pType.getAttributesList()){
            readAttribute(ctx, newType, attribute);
        }
        if (newType.isComposite()) {
            readMembers(ctx, (STypeComposite<?>) newType, pType.getMembers());
        }
    }

    private static void readAttribute(ContextUnarchive ctx, SType<?> newType, SIPersistenceAttribute attribute) {
        if (!attribute.isLambdaValue()){
            newType.setAttributeValue(attribute.getAttrType(), attribute.getAttrValue());
        } else {
            newType.setAttributeValue(attribute.getAttrType(), null);
        }
    }

    private static SType<?> resolveSuperType(ContextUnarchive ctx, SScopeBase scopeNewType, SIPersistenceType pType) {
        String superTypeName = ctx.translateTypeName(pType.getSuperType());
        Optional<SType<?>> superType = ctx.getDictionary().getTypeOptional(superTypeName);
        if (superType.isPresent()) {
            return superType.get();
        }
        throw new SingularFormException("Ao ler o tipo '" + scopeNewType.getName() + "." + pType.getSimpleName()
                + "' não foi encontrado a definição do seu tipo '" + superTypeName + "' nas definições sendo importadas.");
    }

    private static void readMembers(ContextUnarchive ctx, STypeComposite<?> newComposite, SIList<SIPersistenceType> members) {
        for (SIPersistenceType member : members) {
            SType<?> fieldType = resolveSuperType(ctx, newComposite, member);
            SType<?> newField = newComposite.addField(member.getSimpleName(), fieldType);
            readType(ctx, newField, member);
        }

    }

    /**
     * Cria o tipo e dicionário necessário para o tipo da estrutura de dados de
     * persistência da definição.
     */
    private static STypePersistenceArchive createTypePersistence() {
        return SDictionary.create().getType(STypePersistenceArchive.class);
    }

    private static class ContextArchive {

        private final SIPersistenceArchive pArchive;
        private final Map<String, SIPersistencePackage> packages = new HashMap<>();
        private final Map<String, SIPersistenceType> types = new HashMap<>();
        private final Set<String> imports = new HashSet<>();

        public ContextArchive(SDictionary dictionary) {
            this.pArchive = typePresistenceArchive.get().newInstance();
            prepareDefaultImports(dictionary);
        }

        private void prepareDefaultImports(SDictionary dictionary) {
            dictionary.getType(SType.class).getPackage().getLocalTypes().forEach(type -> imports.add(type.getName()));
        }

        public SIPersistenceArchive getArchive() {
            return pArchive;
        }

        public boolean isNecessaryToArchive(SType<?> type) {
            return !SFormUtil.isSingularBuiltInType(type);
        }

        public SIPersistenceType createTypeInPackage(SType<?> type) {
            SIPersistencePackage pkg = packages.get(type.getPackage().getName());
            if (pkg == null) {
                pkg = pArchive.addPackage(type.getPackage().getName());
                packages.put(type.getPackage().getName(), pkg);
            }
            SIPersistenceType pType = pkg.addType(type.getNameSimple());
            types.put(type.getName(), pType);
            return pType;
        }

        public boolean isAlreadyArchived(SType<?> type) {
            return types.containsKey(type.getName());
        }

        public String translateImport(SType<?> superType) {
            String nameFull = superType.getName();
            return imports.contains(nameFull) ? superType.getNameSimple() : nameFull;
        }
    }

    private static class ContextUnarchive {

        private final SDictionary dictionary = SDictionary.create();
        private final Map<String, PackageBuilder> pkgs = new HashMap<>();
        private final Map<String, String> imports = new HashMap<>();

        public ContextUnarchive(SIPersistenceArchive pArchive) {
            prepareDefaultImports(dictionary);
        }

        public SDictionary getDictionary() {
            return dictionary;
        }

        public PackageBuilder getPackage(String packageName) {
            return pkgs.get(packageName);
        }

        public void createNewPackage(SIPersistencePackage pPackage) {
            pkgs.put(pPackage.getPackageName(), dictionary.createNewPackage(pPackage.getPackageName()));
        }

        private void prepareDefaultImports(SDictionary dictionary) {
            for (SType<?> type : dictionary.getType(SType.class).getPackage().getLocalTypes()) {
                imports.put(type.getNameSimple(), type.getName());
            }
        }

        public String translateTypeName(String superTypeName) {
            if (superTypeName.indexOf('.') == -1) {
                String expandedName = imports.get(superTypeName);
                if (expandedName != null) {
                    return expandedName;
                }
            }
            return superTypeName;
        }
    }
}
