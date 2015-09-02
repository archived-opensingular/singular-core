package br.net.mirante.singular.form.wicket;

import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.mform.io.MformPersistenciaXML;
import br.net.mirante.singular.form.util.xml.MElement;

public abstract class MInstanciaRaizModel<I extends MInstancia> implements IModel<I> {
    private MElement    serial;
    private transient I object;
    public MInstanciaRaizModel() {}
    public MInstanciaRaizModel(I object) {
        serial = dehydrate(object);
    }
    protected abstract MTipo<I> getTipoRaiz();
    protected I hydrate(MElement xml) {
        MTipo<I> tipoRaiz = getTipoRaiz();
        I instancia = tipoRaiz.novaInstancia();
        MformPersistenciaXML.fromXML(tipoRaiz, instancia, xml);
        return instancia;
    }
    protected MElement dehydrate(I raiz) {
        return MformPersistenciaXML.toXML(raiz);
    }
    @Override
    public I getObject() {
        if (this.object == null) {
            this.object = hydrate(this.serial);
        }
        return this.object;
    }
    @Override
    public void setObject(I object) {
        this.serial = dehydrate(object);
        this.object = object;
    }
    @Override
    public void detach() {
        this.serial = dehydrate(this.object);
    }
}