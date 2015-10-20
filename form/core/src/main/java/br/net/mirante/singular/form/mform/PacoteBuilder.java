package br.net.mirante.singular.form.mform;

import br.net.mirante.singular.form.mform.core.MPacoteCore;

public class PacoteBuilder {

    private final MDicionario dicionario;
    private final MPacote     pacote;

    PacoteBuilder(MDicionario dicionario, MPacote pacote) {
        this.dicionario = dicionario;
        this.pacote = pacote;
    }

    public MPacote getPacote() {
        return pacote;
    }

    public <T extends MTipo<?>> T createTipo(String nome, Class<T> classePai) {
        return pacote.extenderTipo(nome, classePai);
    }

    public <T extends MTipo<?>> T createTipo(Class<T> classeNovoTipo) {
        T novo = dicionario.getTiposInterno().vericaNaoDeveEstarPresente(classeNovoTipo);
        novo = pacote.registrarTipo(novo, classeNovoTipo);

        TipoBuilder tb = new TipoBuilder();
        novo.onCargaTipo(tb);
        if (!tb.chamouSuper) {
            throw new RuntimeException("O tipo da classe " + classeNovoTipo.getName() + " não chama o super no método onCargaTipo()");
        }
        return novo;
    }

    @SuppressWarnings("unchecked")
    public MTipoComposto<? extends MIComposto> createTipoComposto(String nomeSimplesNovoTipo) {
        return createTipo(nomeSimplesNovoTipo, MTipoComposto.class);
    }

    public MTipoLista<MTipoComposto<?>> createTipoListaOfNovoTipoComposto(String nomeSimplesNovoTipo, String nomeSimplesNovoTipoComposto) {
        return pacote.createTipoListaOfNovoTipoComposto(nomeSimplesNovoTipo, nomeSimplesNovoTipoComposto);
    }

    public <I extends MInstancia, T extends MTipo<I>> MTipoLista<T> createTipoListaOf(String nomeSimplesNovoTipo,
            Class<T> classeTipoLista) {
        T tipoLista = (T) dicionario.getTipo(classeTipoLista);
        return pacote.createTipoListaOf(nomeSimplesNovoTipo, tipoLista);
    }

    public <T extends MTipo<?>> MTipoLista<T> createTipoListaOf(String nomeSimplesNovoTipo, T tipoElementos) {
        return pacote.createTipoListaOf(nomeSimplesNovoTipo, tipoElementos);
    }

    @SuppressWarnings("rawtypes")
    public <T extends MTipo<?>> void addAtributo(Class<? extends MTipo> classeTipo, AtrRef<T, ?, ?> atr) {
        addAtributoInterno(classeTipo, atr);
    }

    @SuppressWarnings("rawtypes")
    public <T extends MTipo<?>> void addAtributo(Class<? extends MTipo> classeTipo, AtrRef<T, ?, ?> atr, Object valorAtributo) {
        MAtributo atributo = addAtributoInterno(classeTipo, atr);
        MTipo<?> tipoAlvo = dicionario.getTipo(classeTipo);
        tipoAlvo.setValorAtributo(atributo, valorAtributo);
    }

    @SuppressWarnings("rawtypes")
    private <T extends MTipo<?>> MAtributo addAtributoInterno(Class<? extends MTipo> classeTipo, AtrRef<T, ?, ?> atr) {
        MTipo<?> tipoAlvo = dicionario.getTipo(classeTipo);

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
        dicionario.garantirPacoteCarregado(atr.getClassePacote());

        if (!atr.isBinded()) {
            return null;
        }
        MTipo<?> tipo = dicionario.getTipoOpcional(atr.getNomeCompleto());
        if (tipo != null && !(tipo instanceof MAtributo)) {
            throw new RuntimeException("O tipo '" + atr.getNomeCompleto() + "' não é um tipo de MAtributo. É " + tipo.getClass().getName());
        }
        return (MAtributo) tipo;
    }

    public <T extends MTipo<?>> MAtributo createTipoAtributo(Class<? extends MTipo> classeAlvo, AtrRef<T, ?, ?> atr) {
        T tipoAtributo;
        if (atr.isSelfReference()) {
            tipoAtributo = (T) dicionario.getTipo(classeAlvo);
        } else {
            tipoAtributo = (T) dicionario.getTipo(atr.getClasseTipo());
        }
        return createTipoAtributo(classeAlvo, atr, tipoAtributo);
    }

    public MAtributo createTipoAtributo(MTipo<?> tipoAlvo, String nomeSimplesAtributo, Class<? extends MTipo<?>> classeTipoAtributo) {
        MTipo<?> tipoAtributo = dicionario.getTipo(classeTipoAtributo);

        if (tipoAlvo.getPacote() == pacote) {
            return createAtributoInterno(tipoAlvo, nomeSimplesAtributo, false, tipoAtributo);
        } else {
            dicionario.getTiposInterno().vericaNaoDeveEstarPresente(pacote.getNome() + "." + nomeSimplesAtributo);

            MAtributo atributo = new MAtributo(nomeSimplesAtributo, tipoAtributo, tipoAlvo, false);
            atributo = pacote.registrarTipo(atributo, null);
            tipoAlvo.addAtributo(atributo);
            return atributo;
        }
    }

    public <T extends MTipo<?>> MAtributo createTipoAtributo(Class<? extends MTipo> classeAlvo, AtrRef<T, ?, ?> atr, T tipoAtributo) {
        MTipo<?> tipoAlvo = dicionario.getTipo(classeAlvo);

        if (tipoAlvo.getPacote() == pacote) {
            resolverBind(tipoAlvo, (Class<MTipo>) classeAlvo, atr, tipoAtributo);
            return createAtributoInterno(tipoAlvo, atr.getNomeSimples(), atr.isSelfReference(), tipoAtributo);
        } else {
            resolverBind(pacote, (Class<MTipo>) classeAlvo, atr, tipoAtributo);
            dicionario.getTiposInterno().vericaNaoDeveEstarPresente(atr.getNomeCompleto());

            MAtributo atributo = new MAtributo(atr.getNomeSimples(), tipoAtributo, tipoAlvo, atr.isSelfReference());
            atributo = pacote.registrarTipo(atributo, null);
            tipoAlvo.addAtributo(atributo);
            return atributo;
        }

    }

    private MAtributo createAtributoInterno(MTipo<?> tipoAlvo, String nomeSimples, boolean selfReference, MTipo<?> tipoAtributo) {
        dicionario.getTiposInterno().vericaNaoDeveEstarPresente(tipoAlvo.getNome() + "." + nomeSimples);

        MAtributo novo = tipoAlvo.registrarTipo(new MAtributo(nomeSimples, tipoAtributo, tipoAlvo, selfReference), null);
        tipoAlvo.addAtributo(novo);
        return novo;
    }

    public <T extends MTipo<?>> MAtributo createTipoAtributo(AtrRef<T, ?, ?> atr) {
        if (atr.isSelfReference()) {
            throw new RuntimeException("Não pode ser criado um atributo global que seja selfReference");
        }
        return createTipoAtributo(atr, dicionario.getTipo(atr.getClasseTipo()));
    }

    public <T extends MTipo<?>> MAtributo createTipoAtributo(AtrRef<T, ?, ?> atr, T tipoAtributo) {
        resolverBind(pacote, null, atr, tipoAtributo);
        dicionario.getTiposInterno().vericaNaoDeveEstarPresente(atr.getNomeCompleto());

        MAtributo novo = new MAtributo(atr.getNomeSimples(), tipoAtributo);
        return pacote.registrarTipo(novo, null);
    }

    private void resolverBind(MEscopo escopo, Class<MTipo> classeDono, AtrRef<?, ?, ?> atr, MTipo tipoAtributo) {
        if (atr.getClassePacote() == pacote.getClass()) {
            atr.bind(escopo.getNome());
        } else {
            throw new RuntimeException("Tentativa de criar o atributo '" + atr.getNomeSimples() + "' do pacote " + atr.getClassePacote().getName()
 + " durante a construção do pacote " + pacote.getNome());
        }
        if (!atr.isSelfReference() && !(atr.getClasseTipo().isInstance(tipoAtributo))) {
            throw new RuntimeException("O atributo " + atr.getNomeCompleto() + " esperava ser do tipo " + atr.getClasseTipo().getName()
                    + " mas foi associado a uma instância de " + tipoAtributo.getClass().getName());
        }
    }

    public void debug() {
        dicionario.debug();
    }

    public interface ManipuladorAtributo<K> {

        public default K withValorInicial(Object valor) {
            return with(MPacoteCore.ATR_VALOR_INICIAL, valor);
        }

        public default K withDefaultValueIfNull(Object valor) {
            return with(MPacoteCore.ATR_DEFAULT_IF_NULL, valor);
        }

        public default K withObrigatorio(Boolean valor) {
            return with(MPacoteCore.ATR_OBRIGATORIO, valor);
        }

        public default K with(AtrRef<?, ?, ?> atributo, Object valor) {
            return with(atributo.getNomeSimples(), valor);
        }

        public K with(String pathAtributo, Object valor);
    }
}
