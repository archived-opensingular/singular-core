package br.net.mirante.singular.form.mform;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.base.Preconditions;

public abstract class MEscopoBase implements MEscopo {

    private Map<String, SType<?>> tiposLocais;

    private static final Logger LOGGER = Logger.getLogger(MEscopoBase.class.getName());

    @Override
    public Optional<SType<?>> getLocalTypeOptional(String path) {
        return getLocalTypeOptional(new PathReader(path));
    }

    final Optional<SType<?>> getLocalTypeOptional(PathReader leitor) {
        if (tiposLocais == null) {
            return Optional.empty();
        }
        SType<?> tipo = tiposLocais.get(leitor.getTrecho());
        if (tipo == null) {
            return Optional.empty();
        } else if (leitor.isUltimo()) {
            return Optional.of(tipo);
        }
        return tipo.getLocalTypeOptional(leitor.proximo());
    }


    @Override
    public SType<?> getLocalType(String path) {
        // Não utiliza getTipoLocalOpcional, pois da forma abaixo é possível
        // apontar precisamente onde deu erro no path passado.
        return getLocalType(new PathReader(path));
    }

    final SType<?> getLocalType(PathReader leitor) {
        if (tiposLocais != null) {
            SType<?> tipo = tiposLocais.get(leitor.getTrecho());
            if (tipo != null) {
                if (leitor.isUltimo()) {
                    return tipo;
                }
                return tipo.getLocalType(leitor.proximo());
            }
        }
        throw new SingularFormException(leitor.getTextoErro(this, "Não existe o tipo"));
    }

    final <T extends SType<?>> T registerType(T novo, Class<T> classeDeRegistro) {
        return getDictionary().registrarTipo(this, novo, classeDeRegistro);
    }

    final <T extends SType<?>> T registerType(TypeBuilder tb, Class<T> classeDeRegistro) {
        getDictionary().registrarTipo(this, (T)tb.getTipo(), classeDeRegistro);
        return (T) tb.configure();
    }

    final <T extends SType<?>> T extenderType(String nomeSimplesNovoTipo, T tipoPai) {
        if (getDictionary() != tipoPai.getDictionary()) {
            throw new SingularFormException("O tipo " + tipoPai.getName() + " foi criado dentro de outro dicionário, que não o atual de " + getName());
        }
        TypeBuilder tb = tipoPai.extender(nomeSimplesNovoTipo);
        return registerType(tb, null);
    }

    final <T extends SType<?>> T extenderType(String nomeSimplesNovoTipo, Class<T> classePai) {
        T tipoPai = resolverTipo(classePai);
        return extenderType(nomeSimplesNovoTipo, tipoPai);
    }

    @SuppressWarnings("unchecked")
    final <I extends SIComposite> STypeLista<STypeComposite<I>, I> createTipoListaOfNovoTipoComposto(String nomeSimplesNovoTipo, String nomeSimplesNovoTipoComposto) {
        STypeLista<STypeComposite<I>, I> tipoLista = extenderType(nomeSimplesNovoTipo, STypeLista.class);
        tipoLista.setTipoElementosNovoTipoComposto(nomeSimplesNovoTipoComposto);
        return tipoLista;
    }

    @SuppressWarnings("unchecked")
    final <I extends SInstance, T extends SType<I>> STypeLista<T, I> createTipoListaOf(String nomeSimplesNovoTipo, T tipoElementos) {
        Preconditions.checkNotNull(tipoElementos);
        STypeLista<T, I> tipoLista = extenderType(nomeSimplesNovoTipo, STypeLista.class);
        tipoLista.setTipoElementos(tipoElementos);
        return tipoLista;
    }

    final <T extends SType<?>> T resolverTipo(Class<T> classeTipo) {
        return getDictionary().getType(classeTipo);
    }

    final void registrar(SType<?> tipo) {
        if (tiposLocais == null) {
            if (this instanceof STypeComposite) {
                tiposLocais = new LinkedHashMap<>();
            } else {
                tiposLocais = new LinkedHashMap<>();
            }
        } else {
            if (tiposLocais.containsKey(tipo.getSimpleName())) {
                throw new RuntimeException("A definição '" + tipo.getSimpleName() + "' já está criada no escopo " + getName());
            }
        }
        tiposLocais.put(tipo.getSimpleName(), tipo);
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
            for (Map.Entry<String, SType<?>> entry : tiposLocais.entrySet()) {
                if(entry.getValue().hasValidation() || entry.getValue().hasAnyValidation()){
                    return true;
                }
            }
        }
        return false;
    }
}
