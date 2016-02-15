package br.net.mirante.singular.form.mform;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

class MapaAtributos implements Iterable<MAtributo> {

    private Map<String, MAtributo> atributos;

    final void add(MAtributo atributo) {
        if (atributos == null) {
            atributos = new LinkedHashMap<>();
        } else if (atributos.containsKey(atributo.getName())) {
            throw new RuntimeException("Já existe um atributo '" + atributo.getName() + "' definido");
        }
        atributos.put(atributo.getName(), atributo);
    }

    public MAtributo get(String nome) {
        if (atributos == null) {
            return null;
        }
        return atributos.get(nome);
    }

    public Collection<MAtributo> getAtributos() {
        if (atributos == null) {
            return Collections.emptyList();
        }
        return atributos.values();
    }

    @Override
    public Iterator<MAtributo> iterator() {
        if (atributos == null) {
            return Collections.emptyListIterator();
        }
        return atributos.values().iterator();
    }

}
