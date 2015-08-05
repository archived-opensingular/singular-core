package br.net.mirante.mform;

public abstract class MTranslatorParaAtributo {

    private MAtributoEnabled alvo;

    static <T extends MTranslatorParaAtributo> T of(MAtributoEnabled original, Class<T> classeAlvo) {
        T instancia;
        try {
            instancia = classeAlvo.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("Erro criando classe de aspecto '" + classeAlvo.getName() + "'", e);
        }
        return of(original, instancia);
    }

    static <T extends MTranslatorParaAtributo> T of(MAtributoEnabled original, T instancia) {
        instancia.setAlvo(original);
        return instancia;
    }

    protected MTranslatorParaAtributo() {
    }

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

    public MTipo<?> getTipo() {
        if (alvo == null) {
            throw new RuntimeException("O objeto alvo dos atributos não foi definido");
        }
        if (alvo instanceof MTipo) {
            return (MTipo<?>) alvo;
        }
        return ((MInstancia) alvo).getMTipo();
    }
}
