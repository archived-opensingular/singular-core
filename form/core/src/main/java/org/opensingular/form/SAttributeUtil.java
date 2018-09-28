package org.opensingular.form;

import org.opensingular.form.calculation.CalculationContextInstanceOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;

/**
 * Helper methods for attribute operations in {@link SType} or {@link SInstance}.
 *
 * @author Daniel C. Bordin
 * @since 2018-09-26
 */
public final class SAttributeUtil {
    private SAttributeUtil() {}


    /**
     * Retorna a instancia do atributo se houver uma associada diretamente ao objeto atual. Não procura o atributo na
     * hierarquia.
     */
    @Nonnull
    public static Optional<SInstance> getAttributeDirectly(@Nonnull SAttributeEnabled target,
            @Nonnull String fullName) {
        if (target instanceof SInstance) {
            return getAttributeDirectly((SInstance) target, fullName);
        } else {
            return getAttributeDirectly((SType<?>) target, fullName);
        }
    }

    /**
     * Retorna a instancia do atributo se houver uma associada diretamente ao objeto atual. Não procura o atributo na
     * hierarquia.
     */
    @Nonnull
    public static Optional<SInstance> getAttributeDirectly(@Nonnull SType<?> target, @Nonnull String fullName) {
        return target.getAttributeDirectly(fullName);
    }

    /**
     * Retorna a instancia do atributo se houver uma associada diretamente ao objeto atual. Não procura o atributo na
     * hierarquia.
     */
    @Nonnull
    public static Optional<SInstance> getAttributeDirectly(@Nonnull SInstance target, @Nonnull String fullName) {
        return target.getAttributeDirectly(fullName);
    }

    /**
     * Verifies if there is a value for the attribute directly associated to the current target. It will return
     * false if even if the type's super type has a associated value for the attribute but the current type doesn't
     * have.
     * <p>Notice that the target may have a current value assigned as null. In this case, this method return true.</p>
     */
    public static boolean hasAttributeValueDirectly(@Nonnull SType<?> target, @Nonnull AtrRef<?, ?, ?> atr) {
        return target.hasAttributeValueDirectly(atr);
    }

    /**
     * Verifies if there is a value for the attribute directly associated to the current target. It will return
     * false if even if the instance's type ({@link SType} has a associated value for the attribute but the current
     * target don't have.
     * <p>Notice that the target may have a current value assigned as null. In this case, this method return true.</p>
     */
    public static boolean hasAttributeValueDirectly(@Nonnull SInstance target, @Nonnull AtrRef<?, ?, ?> atr) {
        return target.hasAttributeValueDirectly(atr);
    }


    /**
     * Verifies if the current target has associated the definition of attribute (create the attribute in the type).
     */
    public static boolean hasAttributeDefinedDirectly(@Nonnull SType<?> target, @Nonnull AtrRef<?, ?, ?> atr) {
        return target.getAttributeDefinedLocally(getReference(target, atr)) != null;

    }

    /**
     * Verifies if attribute is definite in the current target or in the parent context. In other words, is this a valid
     * attribute for the current target.
     */
    public static boolean hasAttributeDefinitionInHierarchy(@Nonnull SInstance target, @Nonnull AtrRef<?, ?, ?> atr) {
        return hasAttributeDefinitionInHierarchy(target.getType(), atr);
    }

    /**
     * Verifies if attribute is defined in the current target or in the parent context. In other words, is this a valid
     * attribute for the current target.
     */
    public static boolean hasAttributeDefinitionInHierarchy(@Nonnull SType<?> target, @Nonnull AtrRef<?, ?, ?> atr) {
        return getAttributeDefinitionInHierarchyOpt(target, getReference(target, atr)) != null;
    }

    /**
     * Tries to find the attribute defined in the type. It'll walk through the type hierarchy looking for the attribute.
     */
    @Nonnull
    public static <T extends SType<?>> Optional<T> getAttributeDefinitionInHierarchyOpt(@Nonnull SType<?> type,
            @Nonnull AtrRef<T, ?, ?> atr) {
        return Optional.ofNullable((T) getAttributeDefinitionInHierarchyOpt(type, getReference(type, atr)));

    }

    @Nonnull
    private static AttrInternalRef getReference(@Nonnull SType<?> type, @Nonnull AtrRef<?, ?, ?> atr) {
        return type.getDictionary().getAttributeReferenceOrException(atr);
    }

    @Nullable
    private static SType<?> getAttributeDefinitionInHierarchyOpt(@Nonnull SType<?> type, @Nonnull AttrInternalRef ref) {
        Objects.requireNonNull(type);
        Objects.requireNonNull(ref);
        for (SType<?> current = type; current != null; current = current.getSuperType()) {
            SType<?> attrType = current.getAttributeDefinedLocally(ref);
            if (attrType != null) {
                return attrType;
            }
        }
        return null;
    }

    @Nonnull
    static SType<?> getAttributeDefinitionInHierarchy(@Nonnull SType<?> type, @Nonnull AttrInternalRef ref) {
        SType<?> attrType = getAttributeDefinitionInHierarchyOpt(type, ref);
        if (attrType != null) {
            return attrType;
        }
        throw new SingularFormException("Não existe o atributo '" + ref.getName() + "' definido em '" + type.getName() +
                "' ou nos tipos pai do mesmo", type);
    }

    @Nullable
    @SuppressWarnings("unchecked")
    static <V> V getAttributeValueInTheContextOf(@Nonnull final SType<?> target, @Nullable SInstance contextInstance,
            @Nonnull AttrInternalRef ref, @Nullable Class<V> resultClass) {
        Objects.requireNonNull(target);
        Objects.requireNonNull(ref);
        SInstance instance = findAttributeInstance(target, ref);
        if (instance != null) {
            CalculationContextInstanceOptional ctx;
            if (contextInstance != null) {
                ctx = new CalculationContextInstanceOptional(contextInstance);
            } else {
                ctx = new CalculationContextInstanceOptional(target);
            }
            return instance.getValueInTheContextOf(ctx, resultClass);
        }
        SType<?> atr = SAttributeUtil.getAttributeDefinitionInHierarchy(target, ref);
        if (resultClass == null) {
            return (V) atr.getAttributeValueOrDefaultValueIfNull();
        }
        return atr.getAttributeValueOrDefaultValueIfNull(resultClass);
    }

    @Nullable
    static SInstance findAttributeInstance(@Nonnull SType<?> target, @Nonnull AttrInternalRef ref) {
        return findAttributeInstance(target, ref, null);
    }

    @Nullable
    private static SInstance findAttributeInstance(@Nonnull SType<?> target, @Nonnull AttrInternalRef ref,
            @Nullable SType<?> stopTypeNotIncluded) {
        SInstance instance = null;
        for (SType<?> type = target;
             instance == null && type != stopTypeNotIncluded && type != null; type = type.getSuperType()) {
            instance = type.getAttributeDirectly(ref);
            if (instance == null) {
                Optional<SType<?>> complementary = target.getComplementarySuperType();
                if (complementary.isPresent() && type.getSuperType() != null) {
                    SType<?> stop = SFormUtil.findCommonType(type.getSuperType(), complementary.get());
                    instance = findAttributeInstance(complementary.get(), ref, stop);
                }
            }
        }
        return instance;
    }
}
