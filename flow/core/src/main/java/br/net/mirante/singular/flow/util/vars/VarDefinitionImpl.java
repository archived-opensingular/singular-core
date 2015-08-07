package br.net.mirante.singular.flow.util.vars;

public class VarDefinitionImpl implements VarDefinition {

    private final String ref;

    private final String nome;

    private final VarType tipo;

    private boolean obrigatorio;

    public VarDefinitionImpl(VarDefinition toCopy) {
        this(toCopy.getRef(), toCopy.getName(), toCopy.getType(), toCopy.isRequired());
        copy(toCopy);
    }

    public VarDefinitionImpl(String ref, String nome, VarType tipo, boolean obrigatorio) {
        this.ref = ref;
        this.nome = nome;
        this.tipo = tipo;
        this.obrigatorio = obrigatorio;
    }

    @Override
    public VarDefinition copy() {
        return new VarDefinitionImpl(this);
    }

    protected void copy(VarDefinition toCopy) {
        obrigatorio = toCopy.isRequired();
    }

    @Override
    public String getRef() {
        return ref;
    }

    @Override
    public String getName() {
        return nome;
    }

    @Override
    public VarType getType() {
        return tipo;
    }

    @Override
    public void setRequired(boolean value) {
        obrigatorio = value;
    }

    @Override
    public VarDefinition required() {
        obrigatorio = true;
        return this;
    }

    @Override
    public boolean isRequired() {
        return obrigatorio;
    }

    @Override
    public int hashCode() {
        return ref.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof VarDefinition))
            return false;
        return ref.equalsIgnoreCase(((VarDefinition) obj).getRef());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [ref_=" + ref + ", nome_=" + nome + ", tipo_=" + tipo + "]";
    }
}
