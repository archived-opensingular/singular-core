package br.net.mirante.singular.form.mform;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

final class MapAttributeDefinitionResolver {

    private Map<String, SInstance> attributes;

    private final SType<?> owner;

    MapAttributeDefinitionResolver(SType<?> owner) {
        this.owner = owner;
    }

    public void set(String attributePath, Object value) {
        SInstance instancia = getCreating(attributePath);
        instancia.setValue(value);
    }

    public SInstance getCreating(String attributePath) {
        SInstance entrada = get(attributePath);
        if (entrada != null) {
            return entrada;
        }

        for (SType<?> atual = owner; atual != null; atual = atual.getSuperType()) {
            SAttribute atributo = atual.getAttributeDefinedLocally(attributePath);
            if (atributo != null) {
                SInstance novo = atributo.newInstanceFor(owner);
                if (attributes == null) {
                    attributes = new LinkedHashMap<>();
                }
                attributes.put(attributePath, novo);
                return novo;
            }
        }
        if(owner != null) {
            throw new RuntimeException(
                    "Não existe o atributo '" + attributePath + "' definido em '" + owner.getName()
                    + "' ou nos tipos extendidos");
        } else {
            throw new RuntimeException("Não existe o atributo '" + attributePath + "'");
        }
    }

    final Map<String, SInstance> getAttributes() {
        if (attributes == null) {
            return Collections.emptyMap();
        }
        return attributes;
    }

    public SInstance get(String fullPathName) {
        if (attributes == null) {
            return null;
        }
        return attributes.get(fullPathName);
    }
}
