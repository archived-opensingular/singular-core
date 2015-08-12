package br.net.mirante.singular.form.mform;

import java.io.PrintStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import com.google.common.base.Preconditions;

public abstract class MEscopoBase implements MEscopo {

    private Map<String, MTipo<?>> tiposLocais;

    @Override
    public MTipo<?> getTipoLocalOpcional(String path) {
        if (tiposLocais == null) {
            return null;
        }
        int pos = path.indexOf('.');
        if (pos != -1) {
            MTipo<?> tipo = tiposLocais.get(path.substring(0, pos));
            if (tipo == null) {
                return null;
            }
            return tipo.getTipoLocalOpcional(path.substring(pos + 1));
        }
        return tiposLocais.get(path);
    }

    @Override
    public MTipo<?> getTipoLocal(String path) {
        return getTipoLocal(new LeitorPath(path));
    }

    public MTipo<?> getTipoLocal(LeitorPath leitor) {
        MTipo<?> tipo = getTipoLocalLocal(leitor.getTrecho());
        if (tipo == null) {
            throw new RuntimeException(leitor.getTextoErro(this, "Não existe o tipo"));
        } else if (leitor.isUltimo()) {
            return tipo;
        }
        return tipo.getTipoLocal(leitor.proximo());
    }

    private MTipo<?> getTipoLocalLocal(String nomeSimples) {
        if (tiposLocais == null) {
            return null;
        }
        return tiposLocais.get(nomeSimples);
    }

    final <T extends MTipo<?>> T registrarTipo(T novo, Class<T> classeDeRegistro) {
        return getDicionario().registrarTipo(this, novo, classeDeRegistro);
    }

    final <T extends MTipo<?>> T extenderTipo(String nomeSimplesNovoTipo, T tipoPai) {
        T novo = tipoPai.extender(nomeSimplesNovoTipo);
        return registrarTipo(novo, null);
    }

    final <T extends MTipo<?>> T extenderTipo(String nomeSimplesNovoTipo, Class<T> classePai) {
        MTipo<?> tipoPai = resolverTipo((Class) classePai);

        T novo = tipoPai.extender(nomeSimplesNovoTipo, classePai);

        return registrarTipo(novo, null);
    }

    final MTipoLista<MTipoComposto<?>> createTipoListaOfNovoTipoComposto(String nomeSimplesNovoTipo, String nomeSimplesNovoTipoComposto) {
        MTipoLista<MTipoComposto<?>> tipoLista = extenderTipo(nomeSimplesNovoTipo, MTipoLista.class);
        tipoLista.setTipoElementosNovoTipoComposto(nomeSimplesNovoTipoComposto);
        return tipoLista;
    }

    final <T extends MTipo<?>> MTipoLista<T> createTipoListaOf(String nomeSimplesNovoTipo, T tipoElementos) {
        Preconditions.checkNotNull(tipoElementos);
        MTipoLista<T> tipoLista = extenderTipo(nomeSimplesNovoTipo, MTipoLista.class);
        tipoLista.setTipoElementos(tipoElementos);
        return tipoLista;
    }

    final <I extends MInstancia, T extends MTipo<I>> T resolverTipo(Class<T> classeTipo) {
        return getDicionario().getTipo(classeTipo);
    }

    final void registrar(MTipo<?> tipo) {
        if (tiposLocais == null) {
            if (this instanceof MTipoComposto) {
                tiposLocais = new LinkedHashMap<>();
            } else {
                tiposLocais = new TreeMap<>();
            }
        } else {
            if (tiposLocais.containsKey(tipo.getNomeSimples())) {
                throw new RuntimeException("A definição '" + tipo.getNomeSimples() + "' já está criada no escopo " + getNome());
            }
        }
        tiposLocais.put(tipo.getNomeSimples(), tipo);
    }

    public final void debug() {
        debug(0);
    }

    protected void debug(int nivel) {
        if (tiposLocais != null) {
            tiposLocais.values().stream().filter(t -> t instanceof MAtributo).forEach(t -> t.debug(nivel));
            tiposLocais.values().stream().filter(t -> !(t instanceof MAtributo)).forEach(t -> t.debug(nivel));
        }
    }

    protected static PrintStream pad(PrintStream out, int nivel) {
        for (int i = nivel * 3; i > 0; i--) {
            out.print(' ');
        }
        return out;
    }
}
