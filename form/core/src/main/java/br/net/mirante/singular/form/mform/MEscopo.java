package br.net.mirante.singular.form.mform;

import java.util.Optional;

public interface MEscopo {

    public String getNome();

    public Optional<SType<?>> getTipoLocalOpcional(String nomeSimples);

    public SType<?> getTipoLocal(String nomeSimples);

    public MEscopo getEscopoPai();

    public default SPackage getPacote() {
        MEscopo atual = this;
        while (atual != null && !(atual instanceof SPackage)) {
            atual = atual.getEscopoPai();
        }
        return (SPackage) atual;
    }

    public SDictionary getDicionario();
}
