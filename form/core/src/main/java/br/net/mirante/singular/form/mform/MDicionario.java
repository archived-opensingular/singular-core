package br.net.mirante.singular.form.mform;

import java.util.Collection;

import br.net.mirante.singular.form.mform.core.MPacoteCore;

public class MDicionario implements IContextoTipo {

    private MapaNomeClasseValor<MPacote> pacotes = new MapaNomeClasseValor<>(p -> p.getNome());

    private MapaNomeClasseValor<MTipo<?>> tipos = new MapaNomeClasseValor<>(t -> t.getNome());

    private final SDocument internalDocument = new SDocument();

    private MDicionario() {
    }

    /**
     * Apenas para uso interno do dicionario de modo que os atributos dos tipos
     * tenha um documento de referencia.
     */
    final SDocument getInternalDicionaryDocument() {
        return internalDocument;
    }

    public Collection<MPacote> getPacotes() {
        return pacotes.getValores();
    }

    public static MDicionario create() {
        MDicionario dicionario = new MDicionario();
        dicionario.carregarPacote(MPacoteCore.class);
        return dicionario;
    }

    public <T extends MPacote> T carregarPacote(Class<T> classePacote) {
        if (classePacote == null){
            throw new SingularFormException("Classe pacote não pode ser nula");
        }
        T novo = pacotes.get(classePacote);
        if (novo == null) {
            novo = pacotes.vericaNaoDeveEstarPresente(classePacote);
            pacotes.vericaNaoDeveEstarPresente(novo);
            carregarInterno(novo);
        }
        return novo;
    }

    public PacoteBuilder criarNovoPacote(String nome) {
        pacotes.vericaNaoDeveEstarPresente(nome);
        MPacote novo = new MPacote(nome);
        novo.setDicionario(this);
        pacotes.add(novo);
        return new PacoteBuilder(this, novo);
    }

    final static MInfoTipo getAnotacaoMFormTipo(Class<?> classeAlvo) {
        MInfoTipo mFormTipo = classeAlvo.getAnnotation(MInfoTipo.class);
        if (mFormTipo == null) {
            throw new SingularFormException("O tipo '" + classeAlvo.getName() + " não possui a anotação @" + MInfoTipo.class.getSimpleName()
                    + " em sua definição.");
        }
        return mFormTipo;
    }

    private static Class<? extends MPacote> getAnotacaoPacote(Class<?> classeAlvo) {
        Class<? extends MPacote> pacote = getAnotacaoMFormTipo(classeAlvo).pacote();
        if (pacote == null) {
            throw new SingularFormException(
                    "O tipo '" + classeAlvo.getName() + "' não define o atributo 'pacote' na anotação @"
                    + MInfoTipo.class.getSimpleName());
        }
        return pacote;
    }

    @Override
    public <T extends MTipo<?>> T getTipoOpcional(Class<T> classeTipo) {
        T tipoRef = tipos.get(classeTipo);
        if (tipoRef == null) {
            Class<? extends MPacote> classPacote = getAnotacaoPacote(classeTipo);
            carregarPacote(classPacote);

            tipoRef = tipos.get(classeTipo);
        }
        return tipoRef;
    }

    public <I extends MInstancia, T extends MTipo<I>> I novaInstancia(Class<T> classeTipo) {
        return getTipo(classeTipo).novaInstancia();
    }

    final MapaNomeClasseValor<MTipo<?>> getTiposInterno() {
        return tipos;
    }

    @SuppressWarnings("unchecked")
    final <T extends MTipo<?>> T registrarTipo(MEscopo escopo, T novo, Class<T> classeDeRegistro) {
        if (classeDeRegistro != null) {
            Class<? extends MPacote> classePacoteAnotado = getAnotacaoPacote(classeDeRegistro);
            MPacote pacoteAnotado = pacotes.getOrInstanciar(classePacoteAnotado);
            MPacote pacoteDestino = findPacote(escopo);
            if (!pacoteDestino.getNome().equals(pacoteAnotado.getNome())) {
                throw new SingularFormException("Tentativa de carregar o tipo '" + novo.getNomeSimples() + "' anotado para o pacote '"
                    + pacoteAnotado.getNome() + "' como sendo do pacote '" + pacoteDestino.getNome() + "'");
            }
        }
        novo.setEscopo(escopo);
        novo.resolverSuperTipo(this);
        tipos.vericaNaoDeveEstarPresente(novo);
        ((MEscopoBase) escopo).registrar(novo);
        tipos.add(novo, (Class<MTipo<?>>) classeDeRegistro);
        return novo;
    }

    private static MPacote findPacote(MEscopo escopo) {
        while (escopo != null && !(escopo instanceof MPacote)) {
            escopo = escopo.getEscopoPai();
        }
        return (MPacote) escopo;
    }

    @Override
    public MTipo<?> getTipoOpcional(String pathNomeCompleto) {
        return tipos.get(pathNomeCompleto);
    }

    private void carregarInterno(MPacote novo) {
        PacoteBuilder pb = new PacoteBuilder(this, novo);
        pacotes.add(novo);
        novo.setDicionario(this);
        novo.carregarDefinicoes(pb);
    }

    public void debug() {
        System.out.println("=======================================================");
        pacotes.forEach(p -> p.debug());
        System.out.println("=======================================================");
    }
}
