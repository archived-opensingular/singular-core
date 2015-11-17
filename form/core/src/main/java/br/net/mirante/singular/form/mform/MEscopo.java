package br.net.mirante.singular.form.mform;

import java.util.Optional;

public interface MEscopo {

    public String getNome();

    public Optional<MTipo<?>> getTipoLocalOpcional(String nomeSimples);

    public MTipo<?> getTipoLocal(String nomeSimples);

    public MEscopo getEscopoPai();

    public default MPacote getPacote() {
        MEscopo atual = this;
        while (atual != null && !(atual instanceof MPacote)) {
            atual = atual.getEscopoPai();
        }
        return (MPacote) atual;
    }

    public MDicionario getDicionario();
}
