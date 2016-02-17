package br.net.mirante.singular.form.mform;

import br.net.mirante.singular.form.mform.core.SPackageCore;

public class PackageBuilder {

    private final SPackage pacote;

    PackageBuilder(SPackage pacote) {
        this.pacote = pacote;
    }

    public SDictionary getDicionario() {
        return pacote.getDictionary();
    }
    public SPackage getPacote() {
        return pacote;
    }

    public <T extends SType<?>> T createTipo(String nome, Class<T> classePai) {
        return pacote.extenderType(nome, classePai);
    }

    final <T extends SType<?>> T createTipo(String nomeSimplesNovoTipo, T tipoPai) {
        return pacote.extenderType(nomeSimplesNovoTipo, tipoPai);
    }

    public <T extends SType<?>> T createTipo(Class<T> classeNovoTipo) {
        getDicionario().getTiposInterno().vericaNaoDeveEstarPresente(classeNovoTipo);
        TypeBuilder tb = new TypeBuilder(classeNovoTipo);
        return pacote.registerType(tb, classeNovoTipo);
    }

    @SuppressWarnings("unchecked")
    public STypeComposite<? extends SIComposite> createTipoComposto(String nomeSimplesNovoTipo) {
        return createTipo(nomeSimplesNovoTipo, STypeComposite.class);
    }

    public <I extends SIComposite> STypeLista<STypeComposite<I>, I> createTipoListaOfNovoTipoComposto(String nomeSimplesNovoTipo, String nomeSimplesNovoTipoComposto) {
        return pacote.createTipoListaOfNovoTipoComposto(nomeSimplesNovoTipo, nomeSimplesNovoTipoComposto);
    }

    public <I extends SInstance, T extends SType<I>> STypeLista<T, I> createTipoListaOf(String nomeSimplesNovoTipo,
                                                                                        Class<T> classeTipoLista) {
        T tipoLista = (T) getDicionario().getType(classeTipoLista);
        return pacote.createTipoListaOf(nomeSimplesNovoTipo, tipoLista);
    }

    public <I extends SInstance, T extends SType<I>> STypeLista<T, I> createTipoListaOf(String nomeSimplesNovoTipo, T tipoElementos) {
        return pacote.createTipoListaOf(nomeSimplesNovoTipo, tipoElementos);
    }

    @SuppressWarnings("rawtypes")
    public <T extends SType<?>> void addAtributo(Class<? extends SType> classeTipo, AtrRef<T, ?, ?> atr) {
        addAtributoInterno(classeTipo, atr);
    }

    @SuppressWarnings("rawtypes")
    public <T extends SType<?>, V extends Object> void addAtributo(Class<? extends SType> classeTipo, AtrRef<T, ?, V> atr, V valorAtributo) {
        MAtributo atributo = addAtributoInterno(classeTipo, atr);
        SType<?> tipoAlvo = getDicionario().getType(classeTipo);
        tipoAlvo.setValorAtributo(atributo, valorAtributo);
    }

    @SuppressWarnings("rawtypes")
    private <T extends SType<?>> MAtributo addAtributoInterno(Class<? extends SType> classeTipo, AtrRef<T, ?, ?> atr) {
        SType<?> tipoAlvo = getDicionario().getType(classeTipo);

        MAtributo atributo = findAtributo(atr);
        tipoAlvo.addAtributo(atributo);
        return atributo;
    }

    public MAtributo getAtributo(AtrRef<?, ?, ?> atr) {
        return findAtributo(atr);
        // return new AtributoBuilder(null, findAtributo(atr));
    }

    private MAtributo findAtributo(AtrRef<?, ?, ?> atr) {
        MAtributo atributo = getAtributoOpcional(atr);
        if (atributo == null) {
            throw new RuntimeException("O atributo '" + atr.getNomeCompleto() + "' não está definido");
        }
        return atributo;
    }

    private MAtributo getAtributoOpcional(AtrRef<?, ?, ?> atr) {
        getDicionario().loadPackage(atr.getClassePacote());

        if (!atr.isBinded()) {
            return null;
        }
        SType<?> tipo = getDicionario().getTypeOptional(atr.getNomeCompleto());
        if (tipo != null && !(tipo instanceof MAtributo)) {
            throw new RuntimeException("O tipo '" + atr.getNomeCompleto() + "' não é um tipo de MAtributo. É " + tipo.getClass().getName());
        }
        return (MAtributo) tipo;
    }

    public <T extends SType<?>> MAtributo createTipoAtributo(Class<? extends SType> classeAlvo, AtrRef<T, ?, ?> atr) {
        T tipoAtributo;
        if (atr.isSelfReference()) {
            tipoAtributo = (T) getDicionario().getType((Class) classeAlvo);
        } else {
            tipoAtributo = (T) getDicionario().getType((Class) atr.getClasseTipo());
        }
        SType<?> tipoAlvo = getDicionario().getType(classeAlvo);
        return createTipoAtributo(tipoAlvo, classeAlvo, atr, tipoAtributo);
    }

    public <T extends SType<?>> MAtributo createTipoAtributo(Class<? extends SType<?>> classeTipoAlvo, String nomeSimplesAtributo, Class<T> classeTipoAtributo) {
        SType<?> tipoAlvo = getDicionario().getType(classeTipoAlvo);
        return createTipoAtributo(tipoAlvo, nomeSimplesAtributo, classeTipoAtributo);
    }
    
    public <T extends SType<?>> MAtributo createTipoAtributo(SType<?> tipoAlvo, String nomeSimplesAtributo, Class<T> classeTipoAtributo) {
        SType<?> tipoAtributo = getDicionario().getType(classeTipoAtributo);
        return createTipoAtributo(tipoAlvo, nomeSimplesAtributo, tipoAtributo);
    }

    public MAtributo createTipoAtributo(SType<?> tipoAlvo, String nomeSimplesAtributo, SType<?> tipoAtributo) {
        if (tipoAlvo.getPacote() == pacote) {
            return createAtributoInterno(tipoAlvo, nomeSimplesAtributo, false, tipoAtributo);
        } else {
            getDicionario().getTiposInterno().vericaNaoDeveEstarPresente(pacote.getName() + "." + nomeSimplesAtributo);

            MAtributo atributo = new MAtributo(nomeSimplesAtributo, tipoAtributo, tipoAlvo, false);
            atributo = pacote.registerType(atributo, null);
            tipoAlvo.addAtributo(atributo);
            return atributo;
        }
    }

    final <T extends SType<?>> MAtributo createTipoAtributo(SType<?> tipoAlvo, Class<? extends SType> classeAlvo, AtrRef<T, ?, ?> atr,
                                                            T tipoAtributo) {
        if (tipoAlvo.getPacote() == pacote) {
            resolverBind(tipoAlvo, (Class<SType>) classeAlvo, atr, tipoAtributo);
            return createAtributoInterno(tipoAlvo, atr.getNomeSimples(), atr.isSelfReference(), tipoAtributo);
        } else {
            resolverBind(pacote, (Class<SType>) classeAlvo, atr, tipoAtributo);
            getDicionario().getTiposInterno().vericaNaoDeveEstarPresente(atr.getNomeCompleto());

            MAtributo atributo = new MAtributo(atr.getNomeSimples(), tipoAtributo, tipoAlvo, atr.isSelfReference());
            atributo = pacote.registerType(atributo, null);
            tipoAlvo.addAtributo(atributo);
            return atributo;
        }

    }

    private MAtributo createAtributoInterno(SType<?> tipoAlvo, String nomeSimples, boolean selfReference, SType<?> tipoAtributo) {
        getDicionario().getTiposInterno().vericaNaoDeveEstarPresente(tipoAlvo.getName() + "." + nomeSimples);

        MAtributo novo = tipoAlvo.registerType(new MAtributo(nomeSimples, tipoAtributo, tipoAlvo, selfReference), null);
        tipoAlvo.addAtributo(novo);
        return novo;
    }

    public <I extends SInstance, T extends SType<I>> MAtributo createTipoAtributo(AtrRef<T, ?, ?> atr) {
        if (atr.isSelfReference()) {
            throw new RuntimeException("Não pode ser criado um atributo global que seja selfReference");
        }
        return createTipoAtributo(atr, getDicionario().getType(atr.getClasseTipo()));
    }

    private <T extends SType<?>> MAtributo createTipoAtributo(AtrRef<T, ?, ?> atr, T tipoAtributo) {
        resolverBind(pacote, null, atr, tipoAtributo);
        getDicionario().getTiposInterno().vericaNaoDeveEstarPresente(atr.getNomeCompleto());

        MAtributo novo = new MAtributo(atr.getNomeSimples(), tipoAtributo);
        return pacote.registerType(novo, null);
    }
    
    private void resolverBind(MEscopo escopo, Class<SType> classeDono, AtrRef<?, ?, ?> atr, SType tipoAtributo) {
        if (atr.getClassePacote() == pacote.getClass()) {
            atr.bind(escopo.getName());
        } else {
            throw new RuntimeException("Tentativa de criar o atributo '" + atr.getNomeSimples() + "' do pacote " + atr.getClassePacote().getName()
 + " durante a construção do pacote " + pacote.getName());
        }
        if (!atr.isSelfReference() && !(atr.getClasseTipo().isInstance(tipoAtributo))) {
            throw new RuntimeException("O atributo " + atr.getNomeCompleto() + " esperava ser do tipo " + atr.getClasseTipo().getName()
                    + " mas foi associado a uma instância de " + tipoAtributo.getClass().getName());
        }
    }

    public void debug() {
        getDicionario().debug();
    }

    public interface ManipuladorAtributo<K> {

        public default K withValorInicial(Object valor) {
            return with(SPackageCore.ATR_VALOR_INICIAL, valor);
        }

        public default K withDefaultValueIfNull(Object valor) {
            return with(SPackageCore.ATR_DEFAULT_IF_NULL, valor);
        }

        public default K withObrigatorio(Boolean valor) {
            return with(SPackageCore.ATR_OBRIGATORIO, valor);
        }

        public default K with(AtrRef<?, ?, ?> atributo, Object valor) {
            return with(atributo.getNomeSimples(), valor);
        }

        public K with(String pathAtributo, Object valor);
    }
}
