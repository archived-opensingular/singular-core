package br.net.mirante.singular.form.mform.io;

import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.util.xml.MElement;

public class PersistenceBuilderXML {

    private boolean gerarId = true;
    private boolean persistirNull = false;

    public PersistenceBuilderXML withGerarId(boolean v) {
        gerarId = v;
        return this;
    }

    public PersistenceBuilderXML withPersistirNull(boolean v) {
        persistirNull = v;
        return this;
    }

    public boolean isGerarId() {
        return gerarId;
    }

    public boolean isPersistirNull() {
        return persistirNull;
    }

    public MElement toXML(MInstancia instancia) {
        return MformPersistenciaXML.toXML(null, null, instancia, this);
    }

}
