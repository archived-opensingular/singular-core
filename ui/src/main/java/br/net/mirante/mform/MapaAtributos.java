package br.net.mirante.mform;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

class MapaAtributos implements Iterable<MAtributo> {

    private Map<String, MAtributo> atributos;

    final void add(MAtributo atributo) {
        if (atributos == null) {
            atributos = new TreeMap<>();
        } else if (atributos.containsKey(atributo.getNome())) {
            throw new RuntimeException("Já existe um atributo '" + atributo.getNome() + "' definido");
        }
        atributos.put(atributo.getNome(), atributo);
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
