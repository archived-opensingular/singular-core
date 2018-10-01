package org.opensingular.form.calculation;

import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.form.SingularFormException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;

/**
 * Represents a context call for a {@link SType} or a {@link SInstance}. The type will never be null, but the {@link
 * SInstance} may not be available.
 *
 * @author Daniel C. Bordin
 * @since 2018-09-27
 */
public class CalculationContextInstanceOptional {

    private final SInstance instanceBeingCalculated;
    private final SType<?> typeContext;
    private final SInstance instanceContext;

    public CalculationContextInstanceOptional(@Nonnull SInstance instanceContext) {
        this(instanceContext.getType(), Objects.requireNonNull(instanceContext), null);
    }

    public CalculationContextInstanceOptional(@Nonnull SType<?> typeContext) {
        this(typeContext, null, null);
    }

    public CalculationContextInstanceOptional(@Nonnull SType<?> typeContext, @Nullable SInstance instanceContext,
            @Nullable SInstance instanceBeingCalculated) {
        this.instanceBeingCalculated = instanceBeingCalculated;
        this.typeContext = Objects.requireNonNull(typeContext);
        this.instanceContext = instanceContext;
    }

    /** Return the {@link SInstance} that will receive the result the value of the calculation. */
    @Nonnull
    public Optional<SInstance> instanceBeingCalculated() {
        return Optional.ofNullable(instanceBeingCalculated);
    }

    /** Returns the {@link SType} for which the calculation will be executed. */
    @Nonnull
    public SType<?> typeContext() {
        return typeContext;
    }

    /** Passar a usar {@link #instanceContextOpt()} or {@link CalculationContext#instanceContext}. */
    @Nonnull
    @Deprecated
    public SInstance instance() {
        return instanceContextInternal();
    }

    @Nonnull
    final SInstance instanceContextInternal() {
        if (instanceContext == null) {
            throw new SingularFormException(
                    getErrorContext() + "Esse contexto não é baseado em instância, mas referece-se ao tipo " +
                            typeContext());
        }
        return instanceContext;
    }

    /**
     * True only if the calculation will the executed for a {@link SInstance} context and that instance isa
     * available.
     */
    public boolean hasInstanceContext() {
        return instanceContext != null;
    }

    /**
     * {@link SInstance} to which the context is working on.
     * <p>May be null if the context is for oly for a specific {@link SType}.</p>
     */
    @Nonnull
    public Optional<SInstance> instanceContextOpt() {
        return Optional.ofNullable(instanceContext);
    }

    @Nonnull
    private String getErrorContext() {
        if (instanceBeingCalculated == null) {
            return "";
        }
        String msg = "Erro calculado " + instanceBeingCalculated.getPathFull();
        if (instanceBeingCalculated.isAttribute()) {
            msg += ", atribudo ";
            if (instanceBeingCalculated.getAttributeInstanceInfo().getInstanceOwner() != null) {
                msg += "da instancia " + instanceBeingCalculated.getAttributeInstanceInfo().getInstanceOwner();
            } else {
                msg += "do tipo " + instanceBeingCalculated.getAttributeInstanceInfo().getTypeOwner();
            }
        }
        return msg + ". ";
    }

    /**
     * If the context is in reference of {@link SInstance}, returns a more specific context. If not, throws a
     * exception.
     */
    @Nonnull
    public CalculationContext asCalculationContext() {
        return new CalculationContext(typeContext, instanceContextInternal(), instanceBeingCalculated);
    }

    /** Creates a new context but indicating the instance that will receive the result of the calculation. */
    @Nonnull
    public CalculationContextInstanceOptional asCalculatingFor(@Nonnull SInstance instanceBeingCalculated) {
        return new CalculationContextInstanceOptional(typeContext, instanceContext, instanceBeingCalculated);
    }
}
