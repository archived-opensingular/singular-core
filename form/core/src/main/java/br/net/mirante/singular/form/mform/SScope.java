package br.net.mirante.singular.form.mform;

import java.util.Optional;

public interface SScope {

    public String getName();

    public Optional<SType<?>> getLocalTypeOptional(String simpleName);

    public SType<?> getLocalType(String simpleName);

    public SScope getParentScope();

    public default SPackage getPackage() {
        SScope atual = this;
        while (atual != null && !(atual instanceof SPackage)) {
            atual = atual.getParentScope();
        }
        return (SPackage) atual;
    }

    public SDictionary getDictionary();
}
