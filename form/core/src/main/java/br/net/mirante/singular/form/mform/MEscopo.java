package br.net.mirante.singular.form.mform;

import java.util.Optional;

public interface MEscopo {

    public String getName();

    public Optional<SType<?>> getLocalTypeOptional(String nomeSimples);

    public SType<?> getLocalType(String nomeSimples);

    public MEscopo getEscopoPai();

    public default SPackage getPacote() {
        MEscopo atual = this;
        while (atual != null && !(atual instanceof SPackage)) {
            atual = atual.getEscopoPai();
        }
        return (SPackage) atual;
    }

    public SDictionary getDictionary();
}
