package br.net.mirante.singular.flow.core;

import br.net.mirante.singular.flow.core.variable.VarInstanceMap;
import br.net.mirante.singular.flow.core.variable.VarType;

/**
 * Representa a montagem (preparação) para execução de uma transanção a partir
 * de uma Task específica.
 *
 * @author Daniel C. Bordin
 */
public interface TransitionCall {

    /**
     * Retorna o mapa de parametros da chamada atual.
     */
    public VarInstanceMap<?> vars();

    /**
     * Executa a transição sobre a task sendo referenciada.
     */
    public void go();

    /**
     * Set o valor na variável ou cria a variável senão existir.
     */
    public default TransitionCall addParamString(String ref, String value) {
        vars().addValorString(ref, value);
        return this;
    }

    /**
     * Set o valor na variável ou cria a variável senão existir.
     */
    public default TransitionCall addParam(String ref, VarType type, Object value) {
        vars().addValor(ref, type, value);
        return this;
    }

    /**
     * Set o valor na variável ou lança exception se a variável não existir na
     * transição.
     */
    public default TransitionCall setValor(String ref, Object value) {
        vars().setValor(ref, value);
        return this;
    }

}
