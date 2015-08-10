package br.net.mirante.singular.ui.mform;

import java.util.Map;
import java.util.Objects;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import org.apache.commons.lang.NotImplementedException;
import org.apache.wicket.validation.IValidator;

import br.net.mirante.singular.ui.mform.basic.ui.MPacoteBasic;
import br.net.mirante.singular.ui.mform.basic.view.MView;
import br.net.mirante.singular.ui.mform.core.MPacoteCore;
import br.net.mirante.singular.ui.mform.function.IComportamento;

@MFormTipo(nome = "MTipo", pacote = MPacoteCore.class)
public class MTipo<I extends MInstancia> extends MEscopoBase implements MAtributoEnabled {

    private String nomeSimples;

    private String nomeCompleto;

    private MDicionario dicionario;

    private MEscopo escopo;

    private MapaAtributos atributosDefinidos = new MapaAtributos();

    private MapaResolvedorDefinicaoAtributo atributosResolvidos;

    /**
     * Se true, representa um campo sem criar um tipo para ser reutilizado em
     * outros pontos.
     */
    private boolean apenasCampo;

    /**
     * Representa um campo que não será persistido. Se aplica somente se
     * apenasCampo=true.
     */
    private boolean seCampoTransiente;

    private Class<MTipo> classeSuperTipo;

    private final Class<? extends I> classeInstancia;

    private MTipo<I> superTipo;

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

    final MFormTipo getAnotacaoMFormTipo() {
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
        Preconditions.checkNotNull(escopo);
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
        getDicionario().garantirPacoteCarregado(atr.getClassePacote());
        MInstancia instancia = atributosResolvidos.getCriando(atr.getNomeCompleto());
        if (subPath != null) {
            instancia.setValor(subPath, valor);
        } else {
            instancia.setValor(valor);
        }
    }

    public <T extends Object> T getValorAtributo(AtrRef<?, ?, ?> atr, Class<T> classeDestino) {
        return getValorAtributo(atr.getNomeCompleto(), classeDestino);
    }

    public <V extends Object> V getValorAtributo(AtrRef<?, ?, V> atr) {
        return getValorAtributo(atr.getNomeCompleto(), atr.getClasseValor());
    }

    public <V extends Object> V getValorAtributo(String nomeCompleto, Class<V> classeDestino) {
        nomeCompleto = mapearNome(nomeCompleto);
        MInstancia instancia = getInstanciaAtributoInterno(nomeCompleto);
        if (instancia != null) {
            return (classeDestino == null) ? (V) instancia.getValor() : instancia.getValorWithDefault(classeDestino);
        }
        MAtributo atr = getAtributoDefinidoHierarquia(nomeCompleto);
        if (classeDestino == null) {
            return (V) atr.getValorAtributoDefaultValueIfNull();
        }
        return atr.getValorAtributoDefaultValueIfNull(classeDestino);
    }

    public Object getValorAtributo(String nomeCompleto) {
        return getValorAtributo(nomeCompleto, null);
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
        throw new NotImplementedException();
    }

    public MTipo<I> withCode(String pathCampo, IComportamento<I> comportamento) {
        // TODO implementar
        throw new NotImplementedException();
    }

    public MTipo<I> withValorInicial(Object valor) {
        return with(MPacoteCore.ATR_VALOR_INICIAL, valor);

    }

    public MTipo<I> withDefaultValueIfNull(Object valor) {
        return with(MPacoteCore.ATR_DEFAULT_IF_NULL, valor);
    }

    public Object getValorAtributoDefaultValueIfNull() {
        if (Objects.equals(nomeSimples, MPacoteCore.ATR_DEFAULT_IF_NULL.getNomeSimples())) {
            return null;
        }
        return getValorAtributo(MPacoteCore.ATR_DEFAULT_IF_NULL);
    }

    public <V extends Object> V getValorAtributoDefaultValueIfNull(Class<V> classeDestino) {
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
        throw new NotImplementedException();
    }

    public <T> MTipo<I> withFunction(String pathCampo, Function<I, T> funcao, MISimples dependencias) {
        // TODO implementar
        throw new NotImplementedException();
    }

    public <T extends Object> T as(Class<T> classeAlvo) {
        if (MTranslatorParaAtributo.class.isAssignableFrom(classeAlvo)) {
            return (T) MTranslatorParaAtributo.of(this, (Class<MTranslatorParaAtributo>) classeAlvo);
        }
        throw new RuntimeException("Classe '" + classeAlvo + "' não funciona como aspecto");
    }

    public MTipo<I> withView(Class<? extends MView> classeAlvo) {
        // TODO implementar
        throw new NotImplementedException();
    }

    public <T extends MView> T setView(Class<T> classeAlvo) {
        // TODO implementar
        throw new NotImplementedException();
    }

    public <T> void addValidacao(IValidator<T> validador) {
        // TODO implementar
        throw new NotImplementedException();
    }

    public I castInstancia(MInstancia instancia) {
        // TODO implementar
        throw new NotImplementedException();
    }

    public I novaInstancia() {
        return novaInstancia(this);
    }

    public MILista novaLista() {
        return MILista.of(this);
    }

    private I novaInstancia(MTipo<?> original) {
        Class<? extends I> c = classeInstancia;
        if (c == null && superTipo != null) {
            return superTipo.novaInstancia(original);
        }
        if (classeInstancia == null) {
            throw new RuntimeException("O tipo '" + original.getNome() + (original == this ? "" : "' que é do tipo '" + getNome())
                    + "' não pode ser instanciado por esse ser abstrato (classeInstancia==null)");
        }
        try {
            I novo = classeInstancia.newInstance();
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
