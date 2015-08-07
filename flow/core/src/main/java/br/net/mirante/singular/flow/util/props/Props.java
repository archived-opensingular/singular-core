package br.net.mirante.singular.flow.util.props;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.stream.Stream;

/**
 * Representa um mapa de propriedade com controle e conversão de acordo o tipo
 * de cada propriedade.
 *
 * @author Daniel C. Bordin
 */
public class Props implements Iterable<Prop>, Serializable {

    private LinkedHashMap<String, Prop> props;

    public boolean isEmpty() {
        return props == null || props.isEmpty();
    }

    public Collection<Prop> asCollection() {
        return (props == null) ? Collections.emptyList() : props.values();
    }

    public Stream<Prop> stream() {
        return asCollection().stream();
    }

    @Override
    public Iterator<Prop> iterator() {
        return (props == null) ? Collections.emptyIterator() : props.values().iterator();
    }

    public <T> void set(PropRef<T> propRef, T value) {
        if (value == null) {
            remove(propRef);
        } else {
            Prop p = getOrCreate(propRef);
            p.setValue(value);
        }
    }

    private Prop getOrCreate(PropRef<?> propRef) {
        Prop p = null;
        if (props == null) {
            props = new LinkedHashMap<>();
        } else {
            p = props.get(propRef.getName());
        }
        if (p == null) {
            p = new Prop(propRef);
            props.put(propRef.getName(), p);
        }
        return p;
    }

    public void remove(PropRef<?> propRef) {
        if (props != null) {
            props.remove(propRef.getName());
        }
    }

    public <T> T get(PropRef<T> propRef) {
        if (props != null) {
            Prop p = props.get(propRef.getName());
            if (p != null) {
                return propRef.getValueClass().cast(p.getValue());
            }
        }
        return null;
    }
}
