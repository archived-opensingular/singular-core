package br.net.mirante.singular.ui.mform;

public class MAtributo extends MTipo<MInstancia> {

    private final boolean selfReference;

    private final MTipo<?> tipoDono;

    MAtributo(String nome, MTipo<? extends MInstancia> tipo) {
        this(nome, tipo, null, false);
    }

    MAtributo(String nome, MTipo<? extends MInstancia> tipo, MTipo<?> tipoDono, boolean selfReference) {
        super(nome, (MTipo<MInstancia>) tipo, null);
        this.tipoDono = tipoDono;
        this.selfReference = selfReference;
    }

    @Override
    public boolean isSelfReference() {
        return selfReference;
    }

    final MInstancia novaInstanciaPara(MTipo<?> dono) {
        if (selfReference) {
            return dono.novaInstancia();
        }
        return super.novaInstancia();
    }

    public MTipo<?> getTipoDono() {
        return tipoDono;
    }
}
