package br.net.mirante.singular.form.processor;

import br.net.mirante.singular.form.*;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Garante que os campos publicos nas classes derivadas de {@link STypeComposite} são corretamente configurados e
 * preenchidos, na primeira definição do tipo, e garante que os campos públicos são preenchidos corretamente  quanto
 * das extenssão do tipo.
 *
 * @author Daniel C. Bordin
 */
public class TypeProcessorPublicFieldsReferences implements TypeProcessorPosRegister {

    public final static TypeProcessorPublicFieldsReferences INSTANCE = new TypeProcessorPublicFieldsReferences();

    /** Guarda informações da classe em cache para poupar processamento. */
    private static LoadingCache<Class<?>, CompositePublicInfo> classInfoCache;

    @Override
    public void processTypePosRegister(SType<?> type, boolean onLoadCalled) {
        if (!(type instanceof STypeComposite) || type.getClass() == STypeComposite.class) {
            return;
        }
        STypeComposite composite = (STypeComposite) type;
        CompositePublicInfo info = getCompositePublicInfo(composite.getClass());
        if (! info.isEmpty()) {
            if (onLoadCalled) {
                if (!info.isPublicFieldsMatched()) {
                    verifyIfAllCompositeFieldsArePublicJavaFields(composite, info);
                    verifyIfAllPublicFieldsAreValid(composite, info);
                    info.setPublicFieldsMatched();
                }
                if (!info.isEmpty()) {
                    verifyAllFieldsCorrectedFilled(composite, info);
                }
            } else {
                propagatePublicFieldsToExtendedComposite(composite, info);
            }
        }
    }

    private void propagatePublicFieldsToExtendedComposite(STypeComposite composite, CompositePublicInfo info) {
        for (PublicFieldRef ref : info) {
            SType<?> newFieldValue = composite.getField(ref.getField().getName());
            if (newFieldValue == null) {
                if (composite.getSuperType().getClass() == composite.getClass()) {
                    SType<?> parentValue;
                    try {
                        parentValue = (SType<?>) ref.getField().get(composite.getSuperType());
                    } catch (IllegalAccessException e) {
                        throw new SingularFormException(erroValue(composite, null, ref, null,
                                "Erro tentando ler valor do campo Java " + ref.getField().getName() + " em " +
                                        composite.getSuperType() + ", que é a instância pai de " + composite), e);
                    }
                    if (parentValue == null) {
                        throw new SingularFormException(erroValue(composite, null, ref, null,
                                "O valor do campo Java " + ref.getField().getName() + " está null em " +
                                        composite.getSuperType() + ", que é a instância pai de " + composite + ""));
                    }
                    newFieldValue = tryToFindInHierarchy(composite, parentValue.getParentScope(),
                            parentValue.getNameSimple());
                    if (newFieldValue != null) {
                        //Verificação de sanidade do resultado
                        if (newFieldValue.getSuperType() != parentValue) {
                            throw new SingularFormException(erroValue(composite, null, ref, null,
                                    "O valor encontrado para atribuir ao campo Java '" + ref.getField().getName() +
                                            "' em " + composite + " foi\n       encontrado: " + newFieldValue +
                                            "\ne esse não é uma extensão da referência do pai\n             pai: " +
                                            parentValue));
                        }
                    }
                }
            }

            if (newFieldValue == null) {
                throw new SingularFormException(erroValue(composite, null, ref, null,
                        "Erro tentando setar valor na instância extendida de " + composite +
                                " pois não foi encontrado o valor para atribuir ao campo " + ref.getField().getName()));
            }
            try {
                ref.getField().set(composite, newFieldValue);
            } catch (IllegalAccessException e) {
                throw new SingularFormException(erroValue(composite, newFieldValue, ref, null,
                        "Erro tentando setar valor na instância extendida de " + composite.getClass().getName()), e);
            }
        }
    }

    private SType<?> tryToFindInHierarchy(STypeComposite composite, SScope scope, String nextType) {
        if (!(scope instanceof SType)) {
            return null;
        }
        SType<?> parent = (SType<?>) scope;
        if (composite.getSuperType() == parent) {
            return composite.getLocalType(nextType);
        }
        SType<?> newParent = tryToFindInHierarchy(composite, parent.getParentScope(), parent.getNameSimple());
        return (newParent == null) ? null : newParent.getLocalType(nextType);
    }

    private void verifyAllFieldsCorrectedFilled(STypeComposite<?> composite, CompositePublicInfo info) {
        for (PublicFieldRef ref : info) {
            SType<?> currentValue = ref.getCurrentFieldValue(composite);
            SType<?> expectedType = composite.getField(ref.getField().getName());
            if (expectedType == null) {
                if (currentValue == null) {
                    throw new SingularFormException(erroValue(composite, null, ref, null,
                            "O campo java deveria ter um valor, mas está null"));
                } else if (!isTypeChildrenOf(composite, currentValue)) {
                    throw new SingularFormException(erroValue(composite, null, ref, currentValue,
                            "O campo java tem um tipo que não é filho direto (ou indireto) de " + composite));
                }
            } else if (currentValue != expectedType) {
                throw new SingularFormException(erroValue(composite, expectedType, ref, currentValue,
                        "O campo público da classe deveria ter o valor do atributo " + expectedType.getNameSimple()));
            }
        }
    }

    private boolean isTypeChildrenOf(STypeComposite<?> composite, SType<?> currentValue) {
        for (SScope parent = currentValue.getParentScope(); parent != null; parent = parent.getParentScope()) {
            if (parent == composite) {
                return true;
            }
        }
        return false;
    }

    private void verifyIfAllCompositeFieldsArePublicJavaFields(STypeComposite<?> composite, CompositePublicInfo info) {
        for (SType<?> field : composite.getFields()) {
            PublicFieldRef ref = info.getPublicField(field.getNameSimple());
            if (ref == null) {
                throw new SingularFormException(erroMsg(composite, field, null, true,
                        "Não foi encontrado o campo publico esperado"));
            }

            if (!field.getClass().isAssignableFrom(ref.getField().getType())) {
                throw new SingularFormException(erroMsg(composite, field, ref, true,
                        "Foi encontrado o campo na classe mas o mesmo não é da classe " + field.getClass()));
            }
            if (Modifier.isStatic(ref.getField().getModifiers())) {
                throw new SingularFormException(erroMsg(composite, field, ref, true,
                        "Foi encontrado o campo na classe mas o mesmo não pode ser static"));

            } else if (!Modifier.isPublic(ref.getField().getModifiers())) {
                throw new SingularFormException(erroMsg(composite, field, ref, true,
                        "Foi encontrado o campo na classe mas o mesmo têm que ser public e não é"));
            }
        }
    }

    private void verifyIfAllPublicFieldsAreValid(STypeComposite composite, CompositePublicInfo info) {
        for (PublicFieldRef ref : info) {
            SType<?> type = composite.getField(ref.getField().getName());
            //if (type == null && ! isInsideChildrenTypes(composite, ref.getField().getName())) {
            //    throw new SingularFormException(erroMsg(composite, null, ref, true,
            //            "Foi encontrado um campo na classe para o qual não existe campo na estrutura de dados do " +
            //                   "composite\n Esperado  : que existisse o campo '" + ref.getField().getName() + "' em
            // " +
            //                    composite + " ou em um campo filho"));
            //}
            if (!SType.class.isAssignableFrom(ref.getField().getType()) &&
                    (type == null || ref.getField().getType().isAssignableFrom(type.getClass()))) {
                throw new SingularFormException(erroMsg(composite, type, ref, type != null,
                        "Foi encontrado um campo na classe o qual se esperava que fosse de um tipo derivado de SType," +
                                " mas em vez disso é do tipo " + ref.getField().getType()));

            }
        }
    }

    private boolean isInsideChildrenTypes(SType<?> type, String typeName) {
        if (type.getNameSimple().equals(typeName)) {
            return true;
        } else if (type instanceof STypeComposite) {
            for (SType<?> children : ((STypeComposite<?>) type).getFields()) {
                if (isInsideChildrenTypes(children, typeName)) {
                    return true;
                }
            }
        } else if (type instanceof STypeList) {
            return isInsideChildrenTypes(((STypeList<?, ?>) type).getElementsType(), typeName);
        }
        return false;
    }

    private String erroMsg(STypeComposite composite, String msg) {
        return "Na classe " + composite.getClass().getName() + ": " + msg;
    }

    private String erroMsg(STypeComposite composite, SType<?> fieldType, PublicFieldRef ref, boolean showExpected,
            String msg) {
        String m = "Na classe " + composite.getClass().getName();
        if (fieldType != null) {
            m += " para o campo do composite '" + fieldType + "'";
        }
        m += ": " + msg;
        if (showExpected) {
            if (fieldType == null) {
                m += "\n Esperado  : 'nenhum field Java de nome '" + ref.getField().getName() + "'";
            } else {
                m += "\n Esperado  : public " + fieldType.getClass().getSimpleName() + " " + fieldType.getNameSimple() +
                        ";";
            }
        }
        if (ref != null) {
            m += "\n Encontrado: " + getFieldDescription(ref);
        }
        return m;
    }

    private String erroValue(STypeComposite composite, SType<?> expectedType, PublicFieldRef ref, SType<?> currentValue,
            String msg) {
        String m = "Erro no atributo público da classe do composite: " + msg;
        m += "\n SType           : " + composite;
        m += "\n Campo composite : " + expectedType;
        m += "\n Classe          : " + composite.getClass();
        m += "\n Field Java      : " + getFieldDescription(ref);
        m += "\n Valor Field Java: " + currentValue;
        return m;
    }

    private String getFieldDescription(PublicFieldRef ref) {
        return Modifier.toString(ref.getField().getModifiers()) + " " + ref.getField().getType().getSimpleName() + " " +
                ref.getField().getName() + ";";
    }

    private static CompositePublicInfo getCompositePublicInfo(Class<? extends STypeComposite> compositeClass) {
        if (classInfoCache == null) {
            synchronized (TypeProcessorPublicFieldsReferences.class) {
                if (classInfoCache == null) {
                    classInfoCache = CacheBuilder.newBuilder().weakValues().build(
                            new CacheLoader<Class<?>, CompositePublicInfo>() {
                                @Override
                                public CompositePublicInfo load(Class<?> aClass) throws Exception {
                                    return readPublicFields(aClass);
                                }
                            });
                }
            }
        }
        return classInfoCache.getUnchecked(compositeClass);
    }

    private static CompositePublicInfo readPublicFields(Class<?> aClass) {
        CompositePublicInfo info = new CompositePublicInfo();
        Field[] fields = aClass.getFields();
        for (Field field : fields) {
            int mods = field.getModifiers();
            if (Modifier.isPublic(mods) && !(Modifier.isStatic(mods) && Modifier.isFinal(mods) &&
                    !SType.class.isAssignableFrom(field.getType()))) {
                info.add(new PublicFieldRef(field));
            }
        }
        info.finisheLoad();
        return info;
    }

    private static class CompositePublicInfo implements Iterable<PublicFieldRef> {

        private Map<String, PublicFieldRef> refs;

        /**
         * Indica que a classe já foi especionada com sucesso uma vez.
         */
        private boolean publicFieldsMatched;

        public boolean isPublicFieldsMatched() {
            return publicFieldsMatched;
        }

        public void setPublicFieldsMatched() {publicFieldsMatched = true;}

        private void add(PublicFieldRef ref) {
            if (refs == null) {
                refs = new HashMap<>();
            }
            refs.put(ref.field.getName(), ref);
        }

        final void finisheLoad() {
            refs = refs == null ? Collections.emptyMap() : ImmutableMap.copyOf(refs);
        }

        @Override
        public Iterator<PublicFieldRef> iterator() {
            return refs == null ? Collections.emptyIterator() : refs.values().iterator();
        }

        public PublicFieldRef getPublicField(String nameSimple) {
            return refs == null ? null : refs.get(nameSimple);
        }

        public boolean isEmpty() {
            return refs.isEmpty();
        }
    }

    /**
     * Representa um field público da classe
     */
    private static class PublicFieldRef {

        private final Field field;

        private PublicFieldRef(Field field) {this.field = field;}

        /**
         * Retorna, via reflection, o valor atualmente atribuido ao campo público.
         */
        public SType<?> getCurrentFieldValue(STypeComposite composite) {
            try {
                return (SType<?>) field.get(composite);
            } catch (IllegalAccessException e) {
                throw new SingularFormException("Erro lendo campo '" + field + " em " + composite, e);
            }
        }

        public Field getField() {
            return field;
        }
    }
}
