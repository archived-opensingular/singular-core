package br.net.mirante.singular.ui.mform;

public interface MEscopo {

    public String getNome();

    public MTipo<?> getTipoLocalOpcional(String nomeSimples);

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
