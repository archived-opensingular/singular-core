package br.net.mirante.singular.form.mform;

import com.google.common.base.Function;

public abstract class MTranslatorParaAtributo {

    private MAtributoEnabled alvo;

    static <T extends MTranslatorParaAtributo> T of(MAtributoEnabled original, Class<T> classeAspecto) {
        T instancia;
        try {
            instancia = classeAspecto.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("Erro criando classe de aspecto '" + classeAspecto.getName() + "'", e);
        }
        return of(original, instancia);
    }
    static <T extends MTranslatorParaAtributo> T of(MAtributoEnabled original, T instancia) {
        instancia.setAlvo(original);
        return instancia;
    }

    protected MTranslatorParaAtributo() {}

    protected MTranslatorParaAtributo(MAtributoEnabled alvo) {
        this.alvo = alvo;
    }

    final void setAlvo(MAtributoEnabled alvo) {
        this.alvo = alvo;
    }

    public MAtributoEnabled getAlvo() {
        if (alvo == null) {
            throw new RuntimeException("O objeto alvo dos atributos não foi definido");
        }
        return alvo;
    }

    public SType<?> getTipo() {
        if (alvo == null) {
            throw new RuntimeException("O objeto alvo dos atributos não foi definido");
        }
        if (alvo instanceof SType) {
            return (SType<?>) alvo;
        }
        return ((SInstance) alvo).getType();
    }

    public <TR> TR as(Function<MAtributoEnabled, TR> wrapper) {
        return wrapper.apply(getAlvo());
    }
}
