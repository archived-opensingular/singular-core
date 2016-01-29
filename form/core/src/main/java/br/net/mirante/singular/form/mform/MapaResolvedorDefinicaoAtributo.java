package br.net.mirante.singular.form.mform;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

final class MapaResolvedorDefinicaoAtributo {

    private Map<String, SInstance2> atributos;

    private final SType<?> dono;

    MapaResolvedorDefinicaoAtributo(SType<?> dono) {
        this.dono = dono;
    }

    public void set(String pathAtributo, Object valor) {
        SInstance2 instancia = getCriando(pathAtributo);
        instancia.setValor(valor);
    }

    public SInstance2 getCriando(String pathAtributo) {
        SInstance2 entrada = get(pathAtributo);
        if (entrada != null) {
            return entrada;
        }

        for (SType<?> atual = dono; atual != null; atual = atual.getSuperTipo()) {
            MAtributo atributo = atual.getAtributoDefinidoLocal(pathAtributo);
            if (atributo != null) {
                SInstance2 novo = atributo.novaInstanciaPara(dono);
                if (atributos == null) {
                    atributos = new LinkedHashMap<>();
                }
                atributos.put(pathAtributo, novo);
                return novo;
            }
        }
        throw new RuntimeException("Não existe o atributo '" + pathAtributo + "' definido em '" + dono.getNome()
                + "' ou nos tipos extendidos");
    }

    final Map<String, SInstance2> getAtributos() {
        if (atributos == null) {
            return Collections.emptyMap();
        }
        return atributos;
    }

    public SInstance2 get(String nomeCompleto) {
        if (atributos == null) {
            return null;
        }
        return atributos.get(nomeCompleto);
    }
}
