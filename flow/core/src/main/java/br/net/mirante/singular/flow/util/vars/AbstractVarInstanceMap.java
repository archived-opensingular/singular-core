package br.net.mirante.singular.flow.util.vars;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;

import br.net.mirante.singular.flow.core.SingularFlowException;

public abstract class AbstractVarInstanceMap<K extends VarInstance> implements VarInstanceMap<K> {

    private LinkedHashMap<String, K> variaveis = new LinkedHashMap<>();

    private final VarService varService;

    public AbstractVarInstanceMap(VarService varService) {
        this.varService = varService;
    }

    public AbstractVarInstanceMap(VarInstanceMap<?> instances) {
        varService = VarService.getVarService(instances);
        for (VarInstance var : instances) {
            addDefinicao(var.getDefinicao()).setValor(var.getValor());
        }
    }

    public AbstractVarInstanceMap(VarDefinitionMap<?> definitions) {
        varService = VarService.getVarService(definitions);
        for (VarDefinition def : definitions) {
            addDefinicao(def);
        }
    }

    @Override
    public K addDefinicao(VarDefinition def) {
        K v = newVarInstance(def);
        addInstance(v);
        return v;
    }

    protected final void addInstance(K varInstance) {
        if (variaveis == null) {
            variaveis = new LinkedHashMap<>();
        }
        variaveis.put(varInstance.getRef(), varInstance);
        if (wantToKnowAboutChanges()) {
            varInstance.setChangeListner(this);
        }
    }

    protected boolean wantToKnowAboutChanges() {
        return false;
    }

    protected K newVarInstance(VarDefinition def) {
        throw new SingularFlowException("Esse metodo ou addDefinicao() deve ser sobre escrito");
    }

    @Override
    public VarService getVarService() {
        return varService;
    }

    @Override
    public K getVariavel(String ref) {
        if (variaveis == null) {
            return null;
        }
        return variaveis.get(ref);
    }

    @Override
    public boolean isEmpty() {
        return variaveis == null || variaveis.isEmpty();
    }

    @Override
    public int size() {
        return variaveis == null ? 0 : variaveis.size();
    }

    @Override
    public Collection<K> asCollection() {
        return variaveis == null ? Collections.emptyList() : variaveis.values();
    }

    @Override
    public String toString() {
        return getClass().getName() + " [" + asCollection() + "]";
    }
}
