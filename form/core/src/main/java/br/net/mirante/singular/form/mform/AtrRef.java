package br.net.mirante.singular.form.mform;

import com.google.common.base.Preconditions;

@SuppressWarnings("rawtypes")
public class AtrRef<T extends SType, I extends SInstance, V extends Object> {

    private final Class<? extends SPackage> classePacote;

    private final String                   nomeSimples;

    private final Class<T>                 classeTipo;

    private final Class<I>                 classeInstancia;

    private final Class<V>                 classeValor;

    private Class<? extends SType<?>>      classeDono;

    private String                         nomeEscopo;

    private String                         nomeCompleto;

    private final boolean                  selfReference;

    public static AtrRef<?, ?, Object> ofSelfReference(Class<? extends SPackage> classePacote, String nomeSimples) {
        return new AtrRef(classePacote, nomeSimples, null, null, null);
    }

    public AtrRef(Class<? extends SPackage> classePacote, String nomeSimples, Class<T> classeTipo, Class<I> classeInstancia,
                  Class<V> classeValor) {
        MFormUtil.checkNomeSimplesValido(nomeSimples);
        this.classePacote = classePacote;
        this.nomeSimples = nomeSimples;
        this.classeTipo = classeTipo;
        this.classeInstancia = classeInstancia;
        this.classeValor = classeValor;
        selfReference = (classeTipo == null) && (classeInstancia == null) && (classeValor == null);
    }

    public String getNomeSimples() {
        return nomeSimples;
    }

    public Class<T> getClasseTipo() {
        return classeTipo;
    }

    public Class<? extends SPackage> getClassePacote() {
        return classePacote;
    }

    public String getNomeCompleto() {
        if (!isBinded()) {
            throw new RuntimeException("Atributo '" + getNomeSimples() + "' ainda não associado a um pacote");
        }

        return nomeCompleto;
    }

    public boolean isSelfReference() {
        return selfReference;
    }

    public final boolean isBinded() {
        return nomeEscopo != null;
    }

    final void bind(String nomeEscopo) {
        if (!isBinded()) {
            Preconditions.checkNotNull(nomeEscopo);
            this.nomeEscopo = nomeEscopo;
            nomeCompleto = nomeEscopo + "." + nomeSimples;
        } else {
            if (!this.nomeEscopo.equals(nomeEscopo)) {
                throw new RuntimeException("O Atributo '" + nomeSimples + "' já está associado ao pacote '" + this.nomeEscopo
                    + "' não podendo ser reassoaciado ao pacote " + nomeEscopo);
            }
        }
    }

    public Class<I> getClasseInstancia() {
        return classeInstancia;
    }

    public Class<V> getClasseValor() {
        return classeValor;
    }
}