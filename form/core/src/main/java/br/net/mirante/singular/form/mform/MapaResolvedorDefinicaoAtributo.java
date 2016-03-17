package br.net.mirante.singular.form.mform;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

final class MapaResolvedorDefinicaoAtributo {

    private Map<String, SInstance> atributos;

    private final SType<?> dono;

    MapaResolvedorDefinicaoAtributo(SType<?> dono) {
        this.dono = dono;
    }

    public void set(String pathAtributo, Object valor) {
        SInstance instancia = getCriando(pathAtributo);
        instancia.setValue(valor);
    }

    public SInstance getCriando(String pathAtributo) {
        SInstance entrada = get(pathAtributo);
        if (entrada != null) {
            return entrada;
        }

        for (SType<?> atual = dono; atual != null; atual = atual.getSuperType()) {
            MAtributo atributo = atual.getAtributoDefinidoLocal(pathAtributo);
            if (atributo != null) {
                SInstance novo = atributo.novaInstanciaPara(dono);
                if (atributos == null) {
                    atributos = new LinkedHashMap<>();
                }
                atributos.put(pathAtributo, novo);
                return novo;
            }
        }
        if(dono != null) {
            throw new RuntimeException("Não existe o atributo '" + pathAtributo + "' definido em '" + dono.getName()
                    + "' ou nos tipos extendidos");
        } else {
            throw new RuntimeException("Não existe o atributo '" + pathAtributo +"'");
        }
    }

    final Map<String, SInstance> getAtributos() {
        if (atributos == null) {
            return Collections.emptyMap();
        }
        return atributos;
    }

    public SInstance get(String nomeCompleto) {
        if (atributos == null) {
            return null;
        }
        return atributos.get(nomeCompleto);
    }
}
