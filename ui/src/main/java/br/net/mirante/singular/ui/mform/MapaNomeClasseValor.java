package br.net.mirante.singular.ui.mform;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Representa um mapa que pode ser acesso por um nome ou uma classe que
 * representa esse nome.
 *
 * @author Daniel C. Bordin
 */

class MapaNomeClasseValor<K> implements Iterable<K> {

    private final Function<K, String> mapeadorNome;

    private final Map<String, K> porNome = new LinkedHashMap<>();
    private final Map<Class<? extends K>, K> porClasse = new HashMap<>();

    MapaNomeClasseValor(Function<K, String> mapeadorNome) {
        this.mapeadorNome = mapeadorNome;
    }

    @SuppressWarnings("unchecked")
    public void add(K novo) {
        add(novo, (Class<K>) novo.getClass());
    }

    public void add(K novo, Class<K> classeDeRegistro) {
        String nome = getNome(novo);
        porNome.put(nome, novo);
        if (classeDeRegistro != null) {
            porClasse.put(classeDeRegistro, novo);
        }
    }

    public <T extends K> T get(Class<T> classeAlvo) {
        K valor = porClasse.get(classeAlvo);
        return classeAlvo.cast(valor);
    }

    public <T extends K> T get(String nome) {
        return (T) porNome.get(nome);
    }

    public Collection<K> getValores() {
        return porNome.values();
    }

    final <T extends K> T vericaNaoDeveEstarPresente(Class<T> classeAlvo) {
        T valor = get(classeAlvo);
        if (valor != null) {
            throw new RuntimeException("A definição '" + getNome(valor) + "' já está carregada");
        }
        return instanciar(classeAlvo);
    }

    final <T extends K> T getOrInstanciar(Class<T> classeAlvo) {
        T valor = get(classeAlvo);
        if (valor == null) {
            return instanciar(classeAlvo);
        }
        return valor;
    }

    final static <TT extends Object> TT instanciar(Class<TT> classeAlvo) {
        try {
            return classeAlvo.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("Erro instanciando " + classeAlvo.getName(), e);
        }
    }

    final void vericaNaoDeveEstarPresente(K alvo) {
        vericaNaoDeveEstarPresente(getNome(alvo));
    }

    final void vericaNaoDeveEstarPresente(String nomeCompleto) {
        if (porNome.containsKey(nomeCompleto)) {
            throw new RuntimeException("A definição '" + nomeCompleto + "' já está criada");
        }
    }

    private String getNome(K val) {
        return mapeadorNome.apply(val);
    }

    @Override
    public Iterator<K> iterator() {
        return porNome.values().iterator();
    }

}
