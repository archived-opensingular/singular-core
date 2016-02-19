package br.net.mirante.singular.form.mform;

import java.util.Collection;
import java.util.Optional;

import br.net.mirante.singular.form.mform.basic.view.ViewResolver;
import br.net.mirante.singular.form.mform.core.SPackageCore;
import br.net.mirante.singular.form.mform.document.SDocument;

public class SDictionary implements ITypeContext {

    private MapaNomeClasseValor<SPackage> pacotes = new MapaNomeClasseValor<>(p -> p.getName());

    private MapaNomeClasseValor<SType<?>> tipos = new MapaNomeClasseValor<>(t -> t.getName());

    private final SDocument internalDocument = new SDocument();

    private ViewResolver viewResolver;

    /**
     * Refência a si próprio que pode ser serializada. Pode ser null. É
     * configurada pelo {{@link SDictionaryLoader} se o mesmo der suporte.
     */
    private SDictionaryRef serializableDictionarySelfReference;

    private SDictionary() {
    }

    /**
     * Apenas para uso interno do dicionario de modo que os atributos dos tipos
     * tenha um documento de referencia.
     */
    final SDocument getInternalDicionaryDocument() {
        return internalDocument;
    }

    public Collection<SPackage> getPackages() {
        return pacotes.getValores();
    }

    /**
     * Retorna o registro e resolvedor (calculador) de views para as instâncias.
     * Permite registra view e decidir qual a view mais pertinente para a
     * instância alvo.
     */
    public ViewResolver getViewResolver() {
        if (viewResolver == null) {
            viewResolver = new ViewResolver();
        }
        return viewResolver;
    }

    public static SDictionary create() {
        SDictionary dicionario = new SDictionary();
        dicionario.loadPackage(SPackageCore.class);
        return dicionario;
    }

    public <T extends SPackage> T loadPackage(Class<T> packageClass) {
        if (packageClass == null){
            throw new SingularFormException("Classe pacote não pode ser nula");
        }
        T novo = pacotes.get(packageClass);
        if (novo == null) {
            pacotes.vericaNaoDeveEstarPresente(packageClass);
            novo = MapaNomeClasseValor.instanciar(packageClass);
            pacotes.vericaNaoDeveEstarPresente(novo);
            carregarInterno(novo);
        }
        return novo;
    }

    public PackageBuilder createNewPackage(String nome) {
        pacotes.vericaNaoDeveEstarPresente(nome);
        SPackage novo = new SPackage(nome);
        novo.setDictionary(this);
        pacotes.add(novo);
        return new PackageBuilder(novo);
    }

    final static MInfoTipo getAnotacaoMFormTipo(Class<?> classeAlvo) {
        MInfoTipo mFormTipo = classeAlvo.getAnnotation(MInfoTipo.class);
        if (mFormTipo == null) {
            throw new SingularFormException("O tipo '" + classeAlvo.getName() + " não possui a anotação @" + MInfoTipo.class.getSimpleName()
                    + " em sua definição.");
        }
        return mFormTipo;
    }

    private static Class<? extends SPackage> getAnotacaoPacote(Class<?> classeAlvo) {
        Class<? extends SPackage> pacote = getAnotacaoMFormTipo(classeAlvo).pacote();
        if (pacote == null) {
            throw new SingularFormException(
                    "O tipo '" + classeAlvo.getName() + "' não define o atributo 'pacote' na anotação @"
                    + MInfoTipo.class.getSimpleName());
        }
        return pacote;
    }

    @Override
    public <T extends SType<?>> T getTypeOptional(Class<T> classeTipo) {
        T tipoRef = tipos.get(classeTipo);
        if (tipoRef == null) {
            Class<? extends SPackage> classPacote = getAnotacaoPacote(classeTipo);
            loadPackage(classPacote);

            tipoRef = tipos.get(classeTipo);
        }
        return tipoRef;
    }

    public <I extends SInstance, T extends SType<I>> I newInstance(Class<T> classeTipo) {
        return getType(classeTipo).novaInstancia();
    }

    final MapaNomeClasseValor<SType<?>> getTiposInterno() {
        return tipos;
    }

    @SuppressWarnings("unchecked")
    final <T extends SType<?>> T registrarTipo(MEscopo escopo, T novo, Class<T> classeDeRegistro) {
        if (classeDeRegistro != null) {
            Class<? extends SPackage> classePacoteAnotado = getAnotacaoPacote(classeDeRegistro);
            SPackage pacoteAnotado = pacotes.getOrInstanciar(classePacoteAnotado);
            SPackage pacoteDestino = findPacote(escopo);
            if (!pacoteDestino.getName().equals(pacoteAnotado.getName())) {
                throw new SingularFormException("Tentativa de carregar o tipo '" + novo.getSimpleName() + "' anotado para o pacote '"
                    + pacoteAnotado.getName() + "' como sendo do pacote '" + pacoteDestino.getName() + "'");
            }
        }
        novo.setEscopo(escopo);
        novo.resolverSuperTipo(this);
        tipos.vericaNaoDeveEstarPresente(novo);
        ((MEscopoBase) escopo).registrar(novo);
        tipos.add(novo, (Class<SType<?>>) classeDeRegistro);
        return novo;
    }

    private static SPackage findPacote(MEscopo escopo) {
        while (escopo != null && !(escopo instanceof SPackage)) {
            escopo = escopo.getEscopoPai();
        }
        return (SPackage) escopo;
    }

    @Override
    public SType<?> getTypeOptional(String pathNomeCompleto) {
        return tipos.get(pathNomeCompleto);
    }

    private void carregarInterno(SPackage novo) {
        PackageBuilder pb = new PackageBuilder(novo);
        novo.setDictionary(this);
        pacotes.add(novo);
        novo.carregarDefinicoes(pb);
    }

    public void debug() {
        System.out.println("=======================================================");
        pacotes.forEach(p -> p.debug());
        System.out.println("=======================================================");
    }

    /**
     * Obtem referência serializável ao dicionário atual. Essa referência deve
     * ser capaz, depois de ser deserializada, recarregar o dicionário, ou
     * recriando-o (nova instância) ou recuperando de algum cache em memória
     * (ex. referência estáticas).
     */
    public final Optional<SDictionaryRef> getSerializableDictionarySelfReference() {
        return Optional.ofNullable(serializableDictionarySelfReference);
    }

    /**
     * Define a referência serializável ao dicionário atual. Essa referência
     * deve ser capaz, depois de ser deserializada, recarregar o dicionário, ou
     * recriando-o (nova instância) ou recuperando de algum cache em memória
     * (ex. referência estáticas).
     */
    public final void setSerializableDictionarySelfReference(SDictionaryRef serializableDictionarySelfReference) {
        this.serializableDictionarySelfReference = serializableDictionarySelfReference;
    }
}
