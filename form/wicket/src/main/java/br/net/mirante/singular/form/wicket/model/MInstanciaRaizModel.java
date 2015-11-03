package br.net.mirante.singular.form.wicket.model;

import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.mform.io.MformPersistenciaXML;
import br.net.mirante.singular.form.util.xml.MElement;

public abstract class MInstanciaRaizModel<I extends MInstancia> extends AbstractMInstanciaModel<I> {

    private MElement    serial;
    private transient I object;
    public MInstanciaRaizModel() {}
    public MInstanciaRaizModel(I object) {
        setObject(object);
    }
    protected abstract MTipo<I> getTipoRaiz();
    protected I hydrate(MTipo<I> tipoRaiz, MElement xml) {
        if (xml != null) {
            return MformPersistenciaXML.fromXML(tipoRaiz, xml);
        }
        return tipoRaiz.novaInstancia();
    }
    protected MElement dehydrate(I raiz) {
        return MformPersistenciaXML.toXMLPreservingRuntimeEdition(raiz);
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