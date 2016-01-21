package br.net.mirante.singular.form.mform.util.transformer;

import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.mform.MTipoComposto;

import java.util.LinkedHashMap;
import java.util.Map;

public class FromPojo<T> {

    protected MTipoComposto<? extends MIComposto> target;
    private T pojo;
    protected Map<MTipo, FromPojoFiedlBuilder> mappings = new LinkedHashMap<>();

    public FromPojo(MTipoComposto<? extends MIComposto> target, T pojo) {
        this.target = target;
        this.pojo = pojo;
    }

    public FromPojo(MTipoComposto<? extends MIComposto> target) {
        this.target = target;
    }

    public <K extends MTipo<?>> FromPojo<T> map(K type, FromPojoFiedlBuilder<T> mapper) {
        mappings.put(type, mapper);
        return this;
    }

    public <K extends MTipo<?>> FromPojo<T> map(K type, Object value) {
        mappings.put(type, p -> value);
        return this;
    }

    public <R extends MInstancia> R build() {
        MIComposto instancia = target.novaInstancia();
        for (Map.Entry<MTipo, FromPojoFiedlBuilder> e : mappings.entrySet()) {
            instancia.setValor(e.getKey().getNome(), e.getValue().value(pojo));
        }
        return (R)instancia;
    }

    @FunctionalInterface
    public static interface FromPojoFiedlBuilder<T> {
        Object value(T pojo);
    }
}
