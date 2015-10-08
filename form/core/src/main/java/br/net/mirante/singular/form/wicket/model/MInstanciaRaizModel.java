package br.net.mirante.singular.form.wicket.model;

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
        setObject(object);
    }
    protected abstract MTipo<I> getTipoRaiz();
    protected I hydrate(MTipo<I> tipoRaiz, MElement xml) {
        I instancia = tipoRaiz.novaInstancia();
        if (xml != null)
            MformPersistenciaXML.fromXML(tipoRaiz, instancia, xml);
        return instancia;
    }
    protected MElement dehydrate(I raiz) {
        return MformPersistenciaXML.toXML(raiz);
    }
    @Override
    public I getObject() {
        if (this.object == null) {
            this.object = hydrate(getTipoRaiz(), this.serial);
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
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((serial == null) ? 0 : serial.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MInstanciaRaizModel<?> other = (MInstanciaRaizModel<?>) obj;
        if (serial == null) {
            if (other.serial != null)
                return false;
        } else if (!serial.equals(other.serial))
            return false;
        return true;
    }
}