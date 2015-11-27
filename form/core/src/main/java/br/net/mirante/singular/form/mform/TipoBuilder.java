package br.net.mirante.singular.form.mform;

public class TipoBuilder {

    private final PacoteBuilder pacoteBuilder;
    private final MTipo<?> targetType;
    private final Class<? extends MTipo> targetTypeClass;

    public TipoBuilder(MTipo<?> targetType, Class<? extends MTipo> targetTypeClass, PacoteBuilder pacoteBuilder) {
        this.pacoteBuilder = pacoteBuilder;
        this.targetType = targetType;
        this.targetTypeClass = targetTypeClass;
    }

    public <T extends MTipo<?>> MAtributo createTipoAtributo(AtrRef<T, ?, ?> atr) {
        return pacoteBuilder.createTipoAtributo(targetTypeClass, atr);
    }

    public <T extends MTipo<?>> MAtributo createTipoAtributo(String nomeSimplesAtributo, Class<T> classeTipoAtributo) {
        return pacoteBuilder.createTipoAtributo(targetType, nomeSimplesAtributo, classeTipoAtributo);
    }

}
