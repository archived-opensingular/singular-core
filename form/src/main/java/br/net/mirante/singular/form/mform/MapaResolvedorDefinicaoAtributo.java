package br.net.mirante.singular.form.mform;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

final class MapaResolvedorDefinicaoAtributo {

    private Map<String, MInstancia> atributos;

    private final MTipo<?> dono;

    MapaResolvedorDefinicaoAtributo(MTipo<?> dono) {
        this.dono = dono;
    }

    public void set(String pathAtributo, Object valor) {
        MInstancia instancia = getCriando(pathAtributo);
        instancia.setValor(valor);
    }

    public MInstancia getCriando(String pathAtributo) {
        MInstancia entrada = get(pathAtributo);
        if (entrada != null) {
            return entrada;
        }

        for (MTipo<?> atual = dono; atual != null; atual = atual.getSuperTipo()) {
            MAtributo atributo = atual.getAtributoDefinidoLocal(pathAtributo);
            if (atributo != null) {
                MInstancia novo = atributo.novaInstanciaPara(dono);
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

    final Map<String, MInstancia> getAtributos() {
        if (atributos == null) {
            return Collections.emptyMap();
        }
        return atributos;
    }

    public MInstancia get(String nomeCompleto) {
        if (atributos == null) {
            return null;
        }
        return atributos.get(nomeCompleto);
    }
}
