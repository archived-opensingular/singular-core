package br.net.mirante.singular.form.mform;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

import org.apache.commons.lang3.NotImplementedException;

import br.net.mirante.singular.form.mform.basic.ui.MPacoteBasic;
import br.net.mirante.singular.form.mform.basic.view.MView;
import br.net.mirante.singular.form.mform.core.MPacoteCore;
import br.net.mirante.singular.form.mform.function.IComportamento;
import br.net.mirante.singular.form.validation.IValidatable;
import br.net.mirante.singular.form.validation.IValidator;

@MInfoTipo(nome = "MTipo", pacote = MPacoteCore.class)
public class MTipo<I extends MInstancia> extends MEscopoBase implements MAtributoEnabled {

    private String                          nomeSimples;

    private String                          nomeCompleto;

    private MDicionario                     dicionario;

    private MEscopo                         escopo;

    private MapaAtributos                   atributosDefinidos = new MapaAtributos();

    private MapaResolvedorDefinicaoAtributo atributosResolvidos;

    private List<IValidator<?>>             validadores        = new ArrayList<>();

    /**
     * Se true, representa um campo sem criar um tipo para ser reutilizado em
     * outros pontos.
     */
    private boolean                         apenasCampo;

    /**
     * Representa um campo que não será persistido. Se aplica somente se
     * apenasCampo=true.
     */
    private boolean                         seCampoTransiente;

    private Class<MTipo>                    classeSuperTipo;

    private final Class<? extends I>        classeInstancia;

    private MTipo<I>                        superTipo;

    private MView                           view;

    public MTipo() {
        this(null, (Class<MTipo>) null, null);
    }

    protected MTipo(Class<? extends I> classeInstancia) {
        this(null, (Class<MTipo>) null, classeInstancia);
    }

    protected MTipo(String nomeSimples, Class<MTipo> classeSuperTipo, Class<? extends I> classeInstancia) {
        if (nomeSimples == null) {
            nomeSimples = getAnotacaoMFormTipo().nome();
        }
        MFormUtil.checkNomeSimplesValido(nomeSimples);
        this.nomeSimples = nomeSimples;
        this.classeSuperTipo = classeSuperTipo;
        this.classeInstancia = classeInstancia;
        atributosResolvidos = new MapaResolvedorDefinicaoAtributo(this);
    }

    protected MTipo(String nomeSimples, MTipo<I> superTipo, Class<I> classeInstancia) {
        this(nomeSimples, (Class<MTipo>) (superTipo == null ? null : superTipo.getClass()), classeInstancia);
        this.superTipo = superTipo;
    }

    protected void onCargaTipo(TipoBuilder tb) {
        tb.chamouSuper = true;
    }

    final MInfoTipo getAnotacaoMFormTipo() {
        return MDicionario.getAnotacaoMFormTipo(getClass());
    }

    final <TT extends MTipo<?>> TT extender(String nomeSimples, Class<TT> classePai) {
        MFormUtil.checkNomeSimplesValido(nomeSimples);
        if (!classePai.equals(getClass())) {
            throw new RuntimeException("Erro Interno");
        }
        TT novo = MapaNomeClasseValor.instanciar(classePai);
        ((MTipo<I>) novo).nomeSimples = nomeSimples;
        ((MTipo<I>) novo).superTipo = this;
        return novo;
    }

    final <TT extends MTipo<?>> TT extender(String nomeSimples) {
        return (TT) extender(nomeSimples, getClass());
    }

    @SuppressWarnings("unchecked")
    final void resolverSuperTipo(MDicionario dicionario) {
        if (superTipo != null || getClass() == MTipo.class) {
            return;
        }
        Class<MTipo> c = (Class<MTipo>) getClass().getSuperclass();
        if (c != null) {
            this.superTipo = dicionario.getTipo(c);
        }
    }

    @Override
    public String getNome() {
        return nomeCompleto;
    }

    public String getNomeSimples() {
        return nomeSimples;
    }

    public MTipo<I> getSuperTipo() {
        return superTipo;
    }

    public Class<I> getClasseInstancia() {
        return (Class<I>) classeInstancia;
    }

    private Class<I> getClasseInstanciaResolvida() {
        if (classeInstancia == null && superTipo != null) {
            return superTipo.getClasseInstanciaResolvida();
        }
        return (Class<I>) classeInstancia;
    }

    final void setEscopo(MEscopo pacote) {
        this.escopo = pacote;
        this.nomeCompleto = pacote.getNome() + "." + nomeSimples;
    }

    @Override
    public MEscopo getEscopoPai() {
        if (escopo == null) {
            throw new RuntimeException(
                    "O escopo do tipo ainda não foi configurado. \n" + "Se você estiver tentando configurar o tipo no construtor do mesmo, "
                            + "dê override no método onCargaTipo() e mova as chamada de configuração para ele.");
        }
        return escopo;
    }

    @Override
    public MDicionario getDicionario() {
        if (dicionario == null) {
            dicionario = getPacote().getDicionario();
        }
        return dicionario;
    }

    public boolean isSelfReference() {
        return false;
    }

    /**
     * <p>
     * Verificar se o tipo atual é do tipo informado, diretamente ou se é um
     * tipo extendido. Para isso percorre toda a hierarquia de derivação do tipo
     * atual verificando se encontra parentTypeCandidate na hierarquia.
     * </p>
     * <p>
     * Ambos o tipo tem que pertencer à mesma instância de dicionário para serem
     * considerado compatíveis, ou seja, se dois tipo forem criados em
     * dicionário diferentes, nunca serão considerado compatíveis mesmo se
     * proveniente da mesma classe de definição.
     * </p>
     *
     * @return true se o tipo atual for do tipo informado.
     */
    public boolean isTypeOf(MTipo<?> parentTypeCandidate) {
        MTipo<I> atual = this;
        while (atual != null) {
            if (atual == parentTypeCandidate) {
                return true;
            }
            atual = atual.superTipo;
        }
        return false;
    }

    final void addAtributo(MAtributo atributo) {
        if (atributo.getTipoDono() != null && atributo.getTipoDono() != this) {
            throw new RuntimeException("O Atributo '" + atributo.getNome() + "' pertence excelusivamente ao tipo '"
                    + atributo.getTipoDono().getNome() + "'. Assim não pode ser reassociado a classe '" + getNome());
        }

        atributosDefinidos.add(atributo);
    }

    final MAtributo getAtributoDefinidoLocal(String nomeCompleto) {
        return atributosDefinidos.get(nomeCompleto);
    }

    final MAtributo getAtributoDefinidoHierarquia(String nomeCompleto) {
        for (MTipo<?> atual = this; atual != null; atual = atual.superTipo) {
            MAtributo att = atual.getAtributoDefinidoLocal(nomeCompleto);
            if (att != null) {
                return att;
            }
        }
        throw new RuntimeException("Não existe atributo '" + nomeCompleto + "' em " + getNome());
    }

    public <MI extends MInstancia> MI getInstanciaAtributo(AtrRef<?, MI, ?> atr) {
        Class<MI> classeInstancia = atr.isSelfReference() ? (Class<MI>) getClasseInstanciaResolvida() : atr.getClasseInstancia();
        MInstancia instancia = getInstanciaAtributoInterno(atr.getNomeCompleto());
        return classeInstancia.cast(instancia);
    }

    private MInstancia getInstanciaAtributoInterno(String nomeCompleto) {
        for (MTipo<?> atual = this; atual != null; atual = atual.superTipo) {
            MInstancia instancia = atual.atributosResolvidos.get(nomeCompleto);
            if (instancia != null) {
                return instancia;
            }
        }
        return null;
    }

    public <V extends Object> void setValorAtributo(String nomeAtributo, Object valor) {
        atributosResolvidos.set(mapearNome(nomeAtributo), valor);
    }

    public <V extends Object> void setValorAtributo(MAtributo defAtributo, Object valor) {
        atributosResolvidos.set(defAtributo.getNome(), valor);
    }

    @Override
    public <V extends Object> void setValorAtributo(AtrRef<?, ?, V> atr, String subPath, V valor) {
        getDicionario().carregarPacote(atr.getClassePacote());
        MInstancia instancia = atributosResolvidos.getCriando(atr.getNomeCompleto());
        if (subPath != null) {
            instancia.setValor(new LeitorPath(subPath), valor);
        } else {
            instancia.setValor(valor);
        }
    }

    @Override
    public <V extends Object> V getValorAtributo(String nomeCompleto, Class<V> classeDestino) {
        nomeCompleto = mapearNome(nomeCompleto);
        MInstancia instancia = getInstanciaAtributoInterno(nomeCompleto);
        if (instancia != null) {
            return (classeDestino == null) ? (V) instancia.getValor() : instancia.getValorWithDefault(classeDestino);
        }
        MAtributo atr = getAtributoDefinidoHierarquia(nomeCompleto);
        if (classeDestino == null) {
            return (V) atr.getValorAtributoOrDefaultValueIfNull();
        }
        return atr.getValorAtributoOrDefaultValueIfNull(classeDestino);
    }

    private String mapearNome(String nomeOriginal) {
        if (nomeOriginal.indexOf('.') == -1) {
            return getNome() + '.' + nomeOriginal;
        }
        return nomeOriginal;
    }

    public MTipo<I> with(AtrRef<?, ?, ? extends Object> atributo, Object valor) {
        setValorAtributo((AtrRef<?, ?, Object>) atributo, valor);
        return this;
    }

    public MTipo<I> with(String pathAtributo, Object valor) {
        setValorAtributo(pathAtributo, valor);
        return this;
    }

    public MTipo<I> with(String valuesExpression) {
        // TODO implementar
        throw new NotImplementedException("TODO implementar");
    }

    public MTipo<I> withCode(String pathCampo, IComportamento<I> comportamento) {
        // TODO implementar
        throw new NotImplementedException("TODO implementar");
    }

    public MTipo<I> withValorInicial(Object valor) {
        return with(MPacoteCore.ATR_VALOR_INICIAL, valor);

    }

    public MTipo<I> withDefaultValueIfNull(Object valor) {
        return with(MPacoteCore.ATR_DEFAULT_IF_NULL, valor);
    }

    public Object getValorAtributoOrDefaultValueIfNull() {
        if (Objects.equals(nomeSimples, MPacoteCore.ATR_DEFAULT_IF_NULL.getNomeSimples())) {
            return null;
        }
        return getValorAtributo(MPacoteCore.ATR_DEFAULT_IF_NULL);
    }

    public <V extends Object> V getValorAtributoOrDefaultValueIfNull(Class<V> classeDestino) {
        if (Objects.equals(nomeSimples, MPacoteCore.ATR_DEFAULT_IF_NULL.getNomeSimples())) {
            return null;
        }
        return getValorAtributo(MPacoteCore.ATR_DEFAULT_IF_NULL, classeDestino);
    }

    public Object getValorAtributoValorInicial() {
        return getValorAtributo(MPacoteCore.ATR_VALOR_INICIAL);
    }

    public MTipo<I> withObrigatorio(Boolean valor) {
        return with(MPacoteCore.ATR_OBRIGATORIO, valor);
    }

    public final Boolean isObrigatorio() {
        return getValorAtributo(MPacoteCore.ATR_OBRIGATORIO);
    }

    public MTipo<I> withOnChange(IComportamento<I> comportamento) {
        return withCode("onChange", comportamento);
    }

    public <T> MTipo<I> withFunction(String pathCampo, Function<I, T> funcao) {
        // TODO implementar
        throw new NotImplementedException("TODO implementar");
    }

    public <T> MTipo<I> withFunction(String pathCampo, Function<I, T> funcao, MISimples dependencias) {
        // TODO implementar
        throw new NotImplementedException("TODO implementar");
    }

    @SuppressWarnings("unchecked")
    public <T extends Object> T as(Class<T> classeAlvo) {
        if (MTranslatorParaAtributo.class.isAssignableFrom(classeAlvo)) {
            return (T) MTranslatorParaAtributo.of(this, (Class<MTranslatorParaAtributo>) classeAlvo);
        }
        throw new RuntimeException("Classe '" + classeAlvo + "' não funciona como aspecto");
    }

    public <T> T as(Function<? super MTipo<I>, T> aspectFactory) {
        return aspectFactory.apply(this);
    }

    public MTipo<I> withView(Supplier<MView> factory) {
        this.view = factory.get();
        return this;
    }

    public <T extends MView> T setView(Supplier<T> factory) {
        T v = factory.get();
        this.view = v;
        return v;
    }
    public MView getView() {
        return (this.view != null) ? this.view : MView.DEFAULT;
    }
    public MTipo<I> addValidacao(IValidator<?> validador) {
        this.validadores.add(validador);
        return this;
    }
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void validar(IValidatable<?> validatable) {
        for (IValidator<?> validador : this.validadores)
            validador.validate((IValidatable) validatable);
    }

    public I castInstancia(MInstancia instancia) {
        // TODO implementar
        throw new NotImplementedException("TODO implementar");
    }

    public final I novaInstancia() {
        SDocument owner = new SDocument();
        I instance = newInstance(this, owner);
        owner.setRoot(instance);
        return instance;
    }

    /** Cria uma nova instância pertencente ao documento informado. */
    I newInstance(SDocument owner) {
        return newInstance(this, owner);
    }

    public MILista<?> novaLista() {
        return MILista.of(this);
    }

    private I newInstance(MTipo<?> original, SDocument owner) {
        Class<? extends I> c = classeInstancia;
        if (c == null && superTipo != null) {
            return superTipo.newInstance(original, owner);
        }
        if (classeInstancia == null) {
            throw new RuntimeException("O tipo '" + original.getNome() + (original == this ? "" : "' que é do tipo '" + getNome())
                    + "' não pode ser instanciado por esse ser abstrato (classeInstancia==null)");
        }
        try {
            I novo = classeInstancia.newInstance();
            novo.setDocument(owner);
            novo.setTipo(this);
            if (novo instanceof MISimples) {
                Object valorInicial = original.getValorAtributoValorInicial();
                if (valorInicial != null) {
                    novo.setValor(valorInicial);
                }
            }
            return novo;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("Erro instanciando o tipo '" + getNome() + "' para o tipo '" + original.getNome() + "'", e);
        }
    }

    @Override
    public void debug(int nivel) {
        MAtributo at = this instanceof MAtributo ? (MAtributo) this : null;
        pad(System.out, nivel).print(at == null ? "def " : "defAtt ");
        System.out.append(getNomeSimples());
        if (at != null) {
            if (at.getTipoDono() != null && at.getTipoDono() != at.getEscopoPai()) {
                System.out.append(" for ").append(suprimirPacote(at.getTipoDono().getNome()));
            }
        }
        if (at == null) {
            if (superTipo == null || superTipo.getClass() != getClass()) {
                System.out.append(" (").append(getClass().getSimpleName());
                if (classeInstancia != null && (superTipo == null || !classeInstancia.equals(superTipo.classeInstancia))) {
                    System.out.print(":" + classeInstancia.getSimpleName());
                }
                System.out.print(")");
            }
        } else if (at.isSelfReference()) {
            System.out.append(" (SELF)");
        }
        if (superTipo != null && (at == null || !at.isSelfReference())) {
            System.out.print(" extend " + suprimirPacote(superTipo.getNome()));
            if (this instanceof MTipoLista) {
                MTipoLista lista = (MTipoLista) this;
                if (lista.getTipoElementos() != null) {
                    System.out.append(" of ").append(suprimirPacote(lista.getTipoElementos().getNome()));
                }
            }
        }
        debugAtributos(nivel);
        System.out.println();

        if (this instanceof MTipoSimples && ((MTipoSimples<?, ?>) this).getProviderOpcoes() != null) {
            pad(System.out, nivel + 2).append("selection of ").println(((MTipoSimples<?, ?>) this).getProviderOpcoes().toDebug());
        }

        atributosDefinidos
            .getAtributos()
            .stream()
            .filter(att -> getTipoLocalOpcional(att.getNomeSimples()) == null)
            .forEach(
                att -> pad(System.out, nivel + 1).println(
                    "att " + suprimirPacote(att.getNome()) + ":" + suprimirPacote(att.getSuperTipo().getNome())
                        + (att.isSelfReference() ? " SELF" : "")));

        super.debug(nivel + 1);
    }

    private void debugAtributos(int nivel) {
        Map<String, MInstancia> vals = atributosResolvidos.getAtributos();
        if (vals.size() != 0) {
            System.out.append(" {");
            vals.entrySet().stream()
                .forEach(e -> System.out.append(suprimirPacote(e.getKey(), true) + "=" + e.getValue().getDisplayString() + "; "));
            System.out.append("}");
        }
    }

    private String suprimirPacote(String nome) {
        return suprimirPacote(nome, false);
    }

    private String suprimirPacote(String nome, boolean agressivo) {
        if (isInicioIgual(nome, getNome())) {
            return nome.substring(getNome().length() + 1);
        } else if (isInicioIgual(nome, escopo.getNome())) {
            return nome.substring(escopo.getNome().length() + 1);
        } else if (isInicioIgual(nome, MPacoteCore.NOME)) {
            String v = nome.substring(MPacoteCore.NOME.length() + 1);
            if (agressivo) {
                if (isInicioIgual(v, "MTipo")) {
                    v = v.substring(6);
                }
            }
            return v;
        } else if (agressivo) {
            if (isInicioIgual(nome, MPacoteBasic.NOME)) {
                return nome.substring(MPacoteBasic.NOME.length() + 1);
            }
        }
        return nome;
    }

    private static boolean isInicioIgual(String nome, String prefixo) {
        return nome.startsWith(prefixo) && nome.length() > prefixo.length() && nome.charAt(prefixo.length()) == '.';
    }

    public <T extends Object> T converter(Object valor, Class<T> classeDestino) {
        throw new RuntimeException("Método não suportado");
    }
}
