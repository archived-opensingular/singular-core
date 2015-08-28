package br.net.mirante.singular.form.mform;

import com.google.common.base.Function;

public abstract class MTranslatorParaAtributo<ALVO extends MAtributoEnabled> {

    private ALVO alvo;

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

    protected MTranslatorParaAtributo(ALVO alvo) {
        this.alvo = alvo;
    }

    final void setAlvo(ALVO alvo) {
        this.alvo = alvo;
    }

    public ALVO getAlvo() {
        if (alvo == null) {
            throw new RuntimeException("O objeto alvo dos atributos não foi definido");
        }
        return alvo;
    }

    public MTipo<?> getTipo() {
        if (alvo == null) {
            throw new RuntimeException("O objeto alvo dos atributos não foi definido");
        }
        if (alvo instanceof MTipo) {
            return (MTipo<?>) alvo;
        }
        return ((MInstancia) alvo).getMTipo();
    }

    public <TR> TR as(Function<ALVO, TR> wrapper) {
        return wrapper.apply(getAlvo());
    }
}
