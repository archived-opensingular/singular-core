package br.net.mirante.singular.form.mform;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.base.Preconditions;

public abstract class MEscopoBase implements MEscopo {

    private Map<String, MTipo<?>> tiposLocais;

    private static final Logger LOGGER = Logger.getLogger(MEscopoBase.class.getName());

    @Override
    public Optional<MTipo<?>> getTipoLocalOpcional(String path) {
        return getTipoLocalOpcional(new LeitorPath(path));
    }

    final Optional<MTipo<?>> getTipoLocalOpcional(LeitorPath leitor) {
        if (tiposLocais == null) {
            return Optional.empty();
        }
        MTipo<?> tipo = tiposLocais.get(leitor.getTrecho());
        if (tipo == null) {
            return Optional.empty();
        } else if (leitor.isUltimo()) {
            return Optional.of(tipo);
        }
        return tipo.getTipoLocalOpcional(leitor.proximo());
    }


    @Override
    public MTipo<?> getTipoLocal(String path) {
        // Não utiliza getTipoLocalOpcional, pois da forma abaixo é possível
        // apontar precisamente onde deu erro no path passado.
        return getTipoLocal(new LeitorPath(path));
    }

    final MTipo<?> getTipoLocal(LeitorPath leitor) {
        if (tiposLocais != null) {
            MTipo<?> tipo = tiposLocais.get(leitor.getTrecho());
            if (tipo != null) {
                if (leitor.isUltimo()) {
                    return tipo;
                }
                return tipo.getTipoLocal(leitor.proximo());
            }
        }
        throw new SingularFormException(leitor.getTextoErro(this, "Não existe o tipo"));
    }

    final <T extends MTipo<?>> T registrarTipo(T novo, Class<T> classeDeRegistro) {
        return getDicionario().registrarTipo(this, novo, classeDeRegistro);
    }

    final <T extends MTipo<?>> T registrarTipo(TipoBuilder tb, Class<T> classeDeRegistro) {
        getDicionario().registrarTipo(this, (T)tb.getTipo(), classeDeRegistro);
        return (T) tb.configure();
    }

    final <T extends MTipo<?>> T extenderTipo(String nomeSimplesNovoTipo, T tipoPai) {
        if (getDicionario() != tipoPai.getDicionario()) {
            throw new SingularFormException("O tipo " + tipoPai.getNome() + " foi criado dentro de outro dicionário, que não o atual de " + getNome());
        }
        TipoBuilder tb = tipoPai.extender(nomeSimplesNovoTipo);
        return registrarTipo(tb, null);
    }

    final <T extends MTipo<?>> T extenderTipo(String nomeSimplesNovoTipo, Class<T> classePai) {
        T tipoPai = resolverTipo(classePai);
        return extenderTipo(nomeSimplesNovoTipo, tipoPai);
    }

    @SuppressWarnings("unchecked")
    final <I extends MIComposto> MTipoLista<MTipoComposto<I>, I> createTipoListaOfNovoTipoComposto(String nomeSimplesNovoTipo, String nomeSimplesNovoTipoComposto) {
        MTipoLista<MTipoComposto<I>, I> tipoLista = extenderTipo(nomeSimplesNovoTipo, MTipoLista.class);
        tipoLista.setTipoElementosNovoTipoComposto(nomeSimplesNovoTipoComposto);
        return tipoLista;
    }

    @SuppressWarnings("unchecked")
    final <I extends MInstancia, T extends MTipo<I>> MTipoLista<T, I> createTipoListaOf(String nomeSimplesNovoTipo, T tipoElementos) {
        Preconditions.checkNotNull(tipoElementos);
        MTipoLista<T, I> tipoLista = extenderTipo(nomeSimplesNovoTipo, MTipoLista.class);
        tipoLista.setTipoElementos(tipoElementos);
        return tipoLista;
    }

    final <T extends MTipo<?>> T resolverTipo(Class<T> classeTipo) {
        return getDicionario().getTipo(classeTipo);
    }

    final void registrar(MTipo<?> tipo) {
        if (tiposLocais == null) {
            if (this instanceof MTipoComposto) {
                tiposLocais = new LinkedHashMap<>();
            } else {
                tiposLocais = new LinkedHashMap<>();
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

    public void debug(int nivel) {
        debug(System.out, nivel);
    }

    public final void debug(Appendable appendable) {
        debug(appendable, 0);
    }

    protected void debug(Appendable appendable, int nivel) {
        if (tiposLocais != null) {
            tiposLocais.values().stream().filter(t -> t instanceof MAtributo).forEach(t -> t.debug(appendable, nivel));
            tiposLocais.values().stream().filter(t -> !(t instanceof MAtributo)).forEach(t -> t.debug(appendable, nivel));
        }
    }

    protected static Appendable pad(Appendable appendable, int nivel) {
        try {
            for (int i = nivel * 3; i > 0; i--) {
                appendable.append(' ');
            }
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
        }
        return appendable;
    }

    final public boolean hasAnyValidation() {
        if(tiposLocais != null) {
            for (Map.Entry<String, MTipo<?>> entry : tiposLocais.entrySet()) {
                if(entry.getValue().hasValidation() || entry.getValue().hasAnyValidation()){
                    return true;
                }
            }
        }
        return false;
    }
}
