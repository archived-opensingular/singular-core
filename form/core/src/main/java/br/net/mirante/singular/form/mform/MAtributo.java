package br.net.mirante.singular.form.mform;

public class MAtributo extends SType<SInstance2> {

    private final boolean selfReference;

    private final SType<?> tipoDono;

    MAtributo(String nome, SType<? extends SInstance2> tipo) {
        this(nome, tipo, null, false);
    }

    MAtributo(String nome, SType<? extends SInstance2> tipo, SType<?> tipoDono, boolean selfReference) {
        super(nome, (SType<SInstance2>) tipo, null);
        this.tipoDono = tipoDono;
        this.selfReference = selfReference;
    }

    @Override
    public boolean isSelfReference() {
        return selfReference;
    }

    final SInstance2 novaInstanciaPara(SType<?> dono) {
        SInstance2 instance;
        if (selfReference) {
            instance = dono.newInstance(getDicionario().getInternalDicionaryDocument());
        } else {
            instance = super.newInstance(getDicionario().getInternalDicionaryDocument());
        }
        instance.setAsAttribute(null);
        return instance;
    }

    public SType<?> getTipoDono() {
        return tipoDono;
    }
}
