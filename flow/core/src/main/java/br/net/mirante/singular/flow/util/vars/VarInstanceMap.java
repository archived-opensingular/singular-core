package br.net.mirante.singular.flow.util.vars;

import br.net.mirante.singular.flow.core.SingularFlowException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.stream.Stream;

public interface VarInstanceMap<K extends VarInstance> extends VarServiceEnabled, Serializable, Iterable<K> {


    public K getVariavel(String ref);

    public Collection<K> asCollection();

    public K addDefinicao(VarDefinition def);

    public int size();

    public boolean isEmpty();

    public default void addDefinicoes(VarDefinitionMap<?> definicoes) {
        for (VarDefinition def : definicoes) {
            addDefinicao(def);
        }
    }

    public default K getVariavelOrException(String ref) {
        K cp = getVariavel(ref);
        if (cp == null) {
            throw new IllegalArgumentException("Variável '" + ref + "' não está definida");
        }
        return cp;
    }

    public default boolean contains(String ref) {
        return getVariavel(ref) != null;
    }

    public default void setValor(String ref, Object valor) {
        getVariavelOrException(ref).setValor(valor);
    }

    public default Stream<K> stream() {
        return asCollection().stream();
    }

    @Override
    public default Iterator<K> iterator() {
        return asCollection().iterator();
    }

    @SuppressWarnings("unchecked")
    public default <T extends Object> T getValor(String ref) {
        return (T) getVariavelOrException(ref).getValor();
    }

    @SuppressWarnings("unchecked")
    public default <T> T getValor(String ref, T valorDefault) {
        Object v = getVariavelOrException(ref).getValor();
        if (v == null) {
            return valorDefault;
        }
        return (T) v;
    }

    public default <T> T getValorTipo(String ref, Class<T> classeTipo) {
        return getValorTipo(ref, classeTipo, null);
    }

    public default <T> T getValorTipo(String ref, Class<T> classeTipo, T valorDefault) {
        K cp = getVariavelOrException(ref);
        Object o = cp.getValor();
        if (o == null) {
            return valorDefault;
        } else if (classeTipo.isInstance(o)) {
            return classeTipo.cast(o);
        }
        throw new SingularFlowException("'" + ref + "' é do tipo " + o.getClass().getName() + " e o esperado era " + classeTipo.getName());
    }

    public default void addValues(VarInstanceMap<?> vars, boolean createMissingTypes) {
        for (VarInstance var : vars) {
            VarInstance localVar = getVariavel(var.getRef());
            if (localVar == null) {
                if (createMissingTypes) {
                    localVar = addDefinicao(var.getDefinicao().copy());
                    localVar.setValor(var.getValor());
                }
            } else {
                localVar.setValor(var.getValor());
            }
        }
    }

    // ----------------------------------------------------------
    // Métodos de conveniência para criação dinâmica de váriáveis
    // ----------------------------------------------------------

    public default void addValor(String ref, VarType type, Object value) {
        K var = getVariavel(ref);
        if (var == null) {
            var = addDefinicao(getVarService().newDefinition(ref, ref, type));
        }
        var.setValor(value);
    }

    public default void addValorString(String ref, String value) {
        K var = getVariavel(ref);
        if (var == null) {
            var = addDefinicao(getVarService().newDefinitionString(ref, ref, null));
        }
        var.setValor(value);
    }

    public default void addValorDate(String ref, Date value) {
        K var = getVariavel(ref);
        if (var == null) {
            var = addDefinicao(getVarService().newDefinitionDate(ref, ref));
        }
        var.setValor(value);
    }

    public default void addValorInteger(String ref, Integer value) {
        K var = getVariavel(ref);
        if (var == null) {
            var = addDefinicao(getVarService().newDefinitionInteger(ref, ref));
        }
        var.setValor(value);
    }

    public default void addValorBoolean(String ref, Boolean value) {
        K var = getVariavel(ref);
        if (var == null) {
            var = addDefinicao(getVarService().newDefinitionBoolean(ref, ref));
        }
        var.setValor(value);
    }

    // ----------------------------------------------------------
    // Métodos de conveniência para leitura
    // ----------------------------------------------------------

    public default String getValorString(String ref) {
        return getValorTipo(ref, String.class, null);
    }

    public default String getValorString(String ref, String valorDefault) {
        return getValorTipo(ref, String.class, valorDefault);
    }

    public default Integer getValorInteger(String ref) {
        return getValorTipo(ref, Integer.class);
    }

    public default Double getValorDouble(String ref) {
        return getValorTipo(ref, Double.class);
    }

    public default Boolean getValorBoolean(String ref) {
        return getValorTipo(ref, Boolean.class);
    }

    public default boolean getValorBoolean(String ref, boolean valorDefault) {
        Boolean b = getValorTipo(ref, Boolean.class);
        if (b == null) {
            return valorDefault;
        }
        return b;
    }

    public default Date getValorData(String ref) {
        return getValorTipo(ref, Date.class);
    }

    public default ValidationResult validar() {
        ValidationResult result = new ValidationResult();
        for (VarInstance cp : this) {
            if (cp.isObrigatorio() && cp.getValor() == null) {
                result.addErro(cp, "Campo  obrigatório");
            }
        }
        return result;
    }

    public void onValueChanged(VarInstance changedVar);

    public static VarInstanceMap<?> empty() {
        return EMPTY;
    }

    public static final VarInstanceMap<?> EMPTY = new VarInstanceMap<VarInstance>() {

        @Override
        public VarInstance getVariavel(String ref) {
            return null;
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public void onValueChanged(VarInstance changedVar) {
            throw new SingularFlowException("Método não suportado");
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public Collection<VarInstance> asCollection() {
            return Collections.emptyList();
        }

        @Override
        public VarInstance addDefinicao(VarDefinition def) {
            throw new SingularFlowException("Método não suportado");
        }

        @Override
        public VarService getVarService() {
            throw new SingularFlowException("Método não suportado");
        }
    };
}
