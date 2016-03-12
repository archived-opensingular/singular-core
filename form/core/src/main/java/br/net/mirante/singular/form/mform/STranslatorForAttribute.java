package br.net.mirante.singular.form.mform;

import com.google.common.base.Function;

public abstract class STranslatorForAttribute {

    private SAttributeEnabled target;

    static <T extends STranslatorForAttribute> T of(SAttributeEnabled original, Class<T> aspectClass) {
        T instance;
        try {
            instance = aspectClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("Erro criando classe de aspecto '" + aspectClass.getName() + "'", e);
        }
        return of(original, instance);
    }
    static <T extends STranslatorForAttribute> T of(SAttributeEnabled original, T instance) {
        instance.setTarget(original);
        return instance;
    }

    protected STranslatorForAttribute() {}

    protected STranslatorForAttribute(SAttributeEnabled target) {
        this.target = target;
    }

    final void setTarget(SAttributeEnabled target) {
        this.target = target;
    }

    public SAttributeEnabled getTarget() {
        if (target == null) {
            throw new RuntimeException("O objeto alvo dos atributos não foi definido");
        }
        return target;
    }

    public SType<?> getTipo() {
        if (target == null) {
            throw new RuntimeException("O objeto alvo dos atributos não foi definido");
        }
        if (target instanceof SType) {
            return (SType<?>) target;
        }
        return ((SInstance) target).getType();
    }

    public <TR> TR as(Function<SAttributeEnabled, TR> wrapper) {
        return wrapper.apply(getTarget());
    }
}
