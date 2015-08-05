package br.net.mirante.mform;

import org.apache.commons.lang.NotImplementedException;

public class MPacote extends MEscopoBase {

    private final String nome;

    private MDicionario dicionario;

    protected MPacote(String nome) {
        MFormUtil.checkNomePacoteValido(nome);
        this.nome = nome;
    }

    @Override
    public String getNome() {
        return nome;
    }

    protected void carregarDefinicoes(PacoteBuilder pb) {
    }

    @Override
    public MEscopo getEscopoPai() {
        return null;
    }

    public <T extends MTipo<?>> T createTipo(String nomeSimplesNovoTipo, Class<T> tipoBase) {
        // TODO implementar
        throw new NotImplementedException();
    }

    @Override
    protected void debug(int nivel) {
        pad(System.out, nivel).println(getNome());
        super.debug(nivel + 1);
    }

    protected static boolean isNull(MISimples campo) {
        return campo == null || campo.isNull();
    }

    protected static boolean isNotNull(MISimples campo) {
        return campo != null && !campo.isNull();
    }

    protected static boolean isTrue(MISimples campo) {
        if (campo != null) {
            return campo.getValorWithDefault(Boolean.class);
        }
        return false;
    }

    @Override
    public MDicionario getDicionario() {
        return dicionario;
    }

    final void setDicionario(MDicionario dicionario) {
        this.dicionario = dicionario;
    }

}
