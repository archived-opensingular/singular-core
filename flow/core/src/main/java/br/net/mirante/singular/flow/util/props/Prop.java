package br.net.mirante.singular.flow.util.props;

import java.io.Serializable;

/**
 * Representa um par propriedade e seu valor.
 *
 * @author Daniel C. Bordin
 */
public class Prop implements Serializable {

    private final String name;
    private Object value;

    public Object getValue() {
        return value;
    }

    public Prop(PropRef<?> propRef) {
        this.name = propRef.getName();
    }

    final void setValue(Object value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }
}
