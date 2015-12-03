package br.net.mirante.singular.form.mform;

import org.apache.commons.lang3.NotImplementedException;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MPacote extends MEscopoBase {

    private static final Logger LOGGER = Logger.getLogger(MTipo.class.getName());

    private final String nome;

    private MDicionario dicionario;

    protected MPacote() {
        this.nome = getClass().getName();
        MFormUtil.checkNomePacoteValido(nome);
        if (getClass() == MPacote.class) {
            throw new SingularFormException("Deve ser utilizado o construtor " + MPacote.class.getSimpleName() + "(String) ou "
                    + MPacote.class.getSimpleName() + " deve ser derivado");
        }
    }

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
        throw new NotImplementedException("TODO implementar");
    }

    @Override
    protected void debug(Appendable appendable, int nivel) {
        try {
            pad(appendable, nivel).append(getNome()).append("\n");
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
        }
        super.debug(appendable, nivel + 1);
    }

    protected static boolean isNull(MISimples<?> campo) {
        return campo == null || campo.isNull();
    }

    protected static boolean isNotNull(MISimples<?> campo) {
        return campo != null && !campo.isNull();
    }

    protected static boolean isTrue(MISimples<?> campo) {
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
