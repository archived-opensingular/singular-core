package br.net.mirante.singular.form.mform;

public class TipoBuilder {

    private final MTipo<?> tipoAlvo;
    private final Class<? extends MTipo<?>> classeAlvo;
    private boolean carregar;
    public <X extends MTipo<?>>TipoBuilder(Class<X> classeAlvo, X tipo) {
        this.classeAlvo = classeAlvo;
        this.tipoAlvo = tipo;
    }
    
    public <X extends MTipo<?>>TipoBuilder(Class<X> classeAlvo) {
        this.classeAlvo = classeAlvo;
        this.tipoAlvo = MapaNomeClasseValor.instanciar(classeAlvo);
        this.carregar = true;
    }

    public MTipo<?> getTipo() {
        return tipoAlvo;
    }
    
    public Class<? extends MTipo<?>> getClasseAlvo() {
        return classeAlvo;
    }
    
    public MTipo<?> configure() {
        if(carregar){
            tipoAlvo.onCargaTipo(this);
        }
        return tipoAlvo;
    }
    
}
