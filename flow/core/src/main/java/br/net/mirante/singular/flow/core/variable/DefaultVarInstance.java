package br.net.mirante.singular.flow.core.variable;

import br.net.mirante.singular.commons.base.SingularException;

import java.util.Objects;

public class DefaultVarInstance extends AbstractVarInstance {

    private boolean historySaved = false;

    private Object valor;

    public DefaultVarInstance(VarDefinition definition) {
        super(definition);
    }

    @Override
    public VarInstance setValor(Object valor) {
        try {
            Object antes = this.valor;
            this.valor = valor;
            if (needToNotifyAboutValueChanged() && !Objects.equals(antes, this.valor)) {
                notifyValueChanged();
            }
            return this;
        } catch (RuntimeException e) {
            throw new SingularException("Erro setando valor '" + valor + "' em " + getRef() + " (" + getNome() + ")", e);
        }
    }

    @Override
    public Object getValor() {
        return valor;
    }
}
