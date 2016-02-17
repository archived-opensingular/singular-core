package br.net.mirante.singular.form.mform;

public class TypeBuilder {

    private final SType<?> tipoAlvo;
    private final Class<? extends SType<?>> classeAlvo;
    private boolean carregar;
    public <X extends SType<?>>TypeBuilder(Class<X> classeAlvo, X tipo) {
        this.classeAlvo = classeAlvo;
        this.tipoAlvo = tipo;
    }
    
    public <X extends SType<?>>TypeBuilder(Class<X> classeAlvo) {
        this.classeAlvo = classeAlvo;
        this.tipoAlvo = MapaNomeClasseValor.instanciar(classeAlvo);
        this.carregar = true;
    }

    public SType<?> getTipo() {
        return tipoAlvo;
    }
    
    public Class<? extends SType<?>> getClasseAlvo() {
        return classeAlvo;
    }
    
    public SType<?> configure() {
        if(carregar){
            tipoAlvo.onLoadType(this);
        }
        return tipoAlvo;
    }
    
}
