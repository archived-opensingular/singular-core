package br.net.mirante.singular.form.mform;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.NotImplementedException;

import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.ui.AtrBootstrap;
import br.net.mirante.singular.form.mform.basic.ui.SPackageBasic;
import br.net.mirante.singular.form.mform.basic.view.MView;
import br.net.mirante.singular.form.mform.context.UIComponentMapper;
import br.net.mirante.singular.form.mform.core.AtrCore;
import br.net.mirante.singular.form.mform.core.SPackageCore;
import br.net.mirante.singular.form.mform.document.SDocument;
import br.net.mirante.singular.form.mform.function.IBehavior;
import br.net.mirante.singular.form.mform.options.MOptionsProvider;
import br.net.mirante.singular.form.validation.IInstanceValidator;
import br.net.mirante.singular.form.validation.ValidationErrorLevel;

@MInfoTipo(nome = "MTipo", pacote = SPackageCore.class)
public class SType<I extends SInstance> extends MEscopoBase implements MAtributoEnabled {

    private static final Logger LOGGER = Logger.getLogger(SType.class.getName());

    /**
     * contabiliza a quantidade de instancias desse tipo.
     */
    protected long instanceCount;

    private String nomeSimples;

    private String nomeCompleto;

    private SDictionary dicionario;

    private MEscopo escopo;

    private MapaAtributos atributosDefinidos = new MapaAtributos();

    private MapaResolvedorDefinicaoAtributo atributosResolvidos;

    //    private Map<IValueValidator<?>, ValidationErrorLevel> valueValidators = new LinkedHashMap<>();
    private Map<IInstanceValidator<I>, ValidationErrorLevel> instanceValidators = new LinkedHashMap<>();
    private Set<SType<?>> dependentTypes;

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

    private Class<SType> classeSuperTipo;

    private final Class<? extends I> classeInstancia;

    private SType<I> superTipo;

    private MView view;

    private UIComponentMapper customMapper;

    public SType() {
        this(null, (Class<SType>) null, null);
    }

    protected SType(Class<? extends I> classeInstancia) {
        this(null, (Class<SType>) null, classeInstancia);
    }

    protected SType(String nomeSimples, Class<SType> classeSuperTipo, Class<? extends I> classeInstancia) {
        if (nomeSimples == null) {
            nomeSimples = getAnotacaoMFormTipo().nome();
        }
        MFormUtil.checkNomeSimplesValido(nomeSimples);
        this.nomeSimples = nomeSimples;
        this.classeSuperTipo = classeSuperTipo;
        this.classeInstancia = classeInstancia;
        atributosResolvidos = new MapaResolvedorDefinicaoAtributo(this);
    }

    protected SType(String nomeSimples, SType<I> superTipo, Class<I> classeInstancia) {
        this(nomeSimples, (Class<SType>) (superTipo == null ? null : superTipo.getClass()), classeInstancia);
        this.superTipo = superTipo;
    }

    protected void onLoadType(TypeBuilder tb) {
    }

    final MInfoTipo getAnotacaoMFormTipo() {
        return SDictionary.getAnotacaoMFormTipo(getClass());
    }

    private final <TT extends SType<I>> TypeBuilder extender(String nomeSimples, Class<TT> classePai) {
        MFormUtil.checkNomeSimplesValido(nomeSimples);
        if (!classePai.equals(getClass())) {
            throw new RuntimeException("Erro Interno");
        }
        TypeBuilder tb = new TypeBuilder(classePai);
        ((SType<I>) tb.getTipo()).nomeSimples = nomeSimples;
        ((SType<I>) tb.getTipo()).superTipo = this;
        return tb;
    }

    final <TT extends SType<?>> TypeBuilder extender(String nomeSimples) {
        return (TypeBuilder) extender(nomeSimples, getClass());
    }

    @SuppressWarnings("unchecked")
    final void resolverSuperTipo(SDictionary dicionario) {
        if (superTipo != null || getClass() == SType.class) {
            return;
        }
        Class<SType> c = (Class<SType>) getClass().getSuperclass();
        if (c != null) {
            this.superTipo = dicionario.getType(c);
        }
    }

    @Override
    public String getName() {
        return nomeCompleto;
    }

    public String getSimpleName() {
        return nomeSimples;
    }

    public SType<I> getSuperType() {
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
        this.nomeCompleto = pacote.getName() + "." + nomeSimples;
    }

    @Override
    public MEscopo getEscopoPai() {
        if (escopo == null) {
            throw new SingularFormException(
                    "O escopo do tipo ainda não foi configurado. \n" + "Se você estiver tentando configurar o tipo no construtor do mesmo, "
                            + "dê override no método onLoadType() e mova as chamada de configuração para ele.");
        }
        return escopo;
    }

    @Override
    public SDictionary getDictionary() {
        if (dicionario == null) {
            dicionario = getPacote().getDictionary();
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
    public boolean isTypeOf(SType<?> parentTypeCandidate) {
        SType<I> atual = this;
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
            throw new SingularFormException("O Atributo '" + atributo.getName() + "' pertence excelusivamente ao tipo '"
                    + atributo.getTipoDono().getName() + "'. Assim não pode ser reassociado a classe '" + getName());
        }

        atributosDefinidos.add(atributo);
    }

    final MAtributo getAtributoDefinidoLocal(String nomeCompleto) {
        return atributosDefinidos.get(nomeCompleto);
    }

    final MAtributo getAtributoDefinidoHierarquia(String nomeCompleto) {
        for (SType<?> atual = this; atual != null; atual = atual.superTipo) {
            MAtributo att = atual.getAtributoDefinidoLocal(nomeCompleto);
            if (att != null) {
                return att;
            }
        }
        throw new SingularFormException("Não existe atributo '" + nomeCompleto + "' em " + getName());
    }

    public <MI extends SInstance> MI getInstanciaAtributo(AtrRef<?, MI, ?> atr) {
        Class<MI> classeInstancia = atr.isSelfReference() ? (Class<MI>) getClasseInstanciaResolvida() : atr.getClasseInstancia();
        SInstance instancia = getInstanciaAtributoInterno(atr.getNomeCompleto());
        return classeInstancia.cast(instancia);
    }

    final SInstance getInstanciaAtributoInterno(String nomeCompleto) {
        for (SType<?> atual = this; atual != null; atual = atual.superTipo) {
            SInstance instancia = atual.atributosResolvidos.get(nomeCompleto);
            if (instancia != null) {
                return instancia;
            }
        }
        return null;
    }

    @Override
    public void setValorAtributo(String nomeAtributo, String subPath, Object valor) {
        SInstance instancia = atributosResolvidos.getCriando(mapearNome(nomeAtributo));
        if (subPath != null) {
            instancia.setValor(new PathReader(subPath), valor);
        } else {
            instancia.setValue(valor);
        }
    }

    @Override
    public <V extends Object> V getValorAtributo(String nomeCompleto, Class<V> classeDestino) {
        nomeCompleto = mapearNome(nomeCompleto);
        SInstance instancia = getInstanciaAtributoInterno(nomeCompleto);
        if (instancia != null) {
            return (classeDestino == null) ? (V) instancia.getValue() : instancia.getValorWithDefault(classeDestino);
        }
        MAtributo atr = getAtributoDefinidoHierarquia(nomeCompleto);
        if (classeDestino == null) {
            return (V) atr.getValorAtributoOrDefaultValueIfNull();
        }
        return atr.getValorAtributoOrDefaultValueIfNull(classeDestino);
    }

    private String mapearNome(String nomeOriginal) {
        if (nomeOriginal.indexOf('.') == -1) {
            return getName() + '.' + nomeOriginal;
        }
        return nomeOriginal;
    }

    public SType<I> with(AtrRef<?, ?, ? extends Object> atributo, Object valor) {
        setValorAtributo((AtrRef<?, ?, Object>) atributo, valor);
        return this;
    }

    public SType<I> with(String pathAtributo, Object valor) {
        setValorAtributo(pathAtributo, valor);
        return this;
    }

    public SType<I> with(String valuesExpression) {
        throw new NotImplementedException("Este tipo não implementa o método `with`");
    }

    public SType<I> withCode(String pathCampo, IBehavior<I> comportamento) {
        throw new NotImplementedException("Este tipo não implementa o método `withCode`");
    }

    public SType<I> withValorInicial(Object valor) {
        return with(SPackageCore.ATR_VALOR_INICIAL, valor);

    }

    public SType<I> withDefaultValueIfNull(Object valor) {
        return with(SPackageCore.ATR_DEFAULT_IF_NULL, valor);
    }

    public Object getValorAtributoOrDefaultValueIfNull() {
        if (Objects.equals(nomeSimples, SPackageCore.ATR_DEFAULT_IF_NULL.getNomeSimples())) {
            return null;
        }
        return getValorAtributo(SPackageCore.ATR_DEFAULT_IF_NULL);
    }

    public <V extends Object> V getValorAtributoOrDefaultValueIfNull(Class<V> classeDestino) {
        if (Objects.equals(nomeSimples, SPackageCore.ATR_DEFAULT_IF_NULL.getNomeSimples())) {
            return null;
        }
        return getValorAtributo(SPackageCore.ATR_DEFAULT_IF_NULL, classeDestino);
    }

    public Object getValorAtributoValorInicial() {
        return getValorAtributo(SPackageCore.ATR_VALOR_INICIAL);
    }

    public SType<I> withObrigatorio(Boolean valor) {
        return with(SPackageCore.ATR_OBRIGATORIO, valor);
    }

    public final Boolean isObrigatorio() {
        return getValorAtributo(SPackageCore.ATR_OBRIGATORIO);
    }

    public SType<I> withExists(Boolean valor) {
        return with(SPackageCore.ATR_EXISTS, valor);
    }

    public SType<I> withExists(Predicate<I> predicate) {
        return with(SPackageCore.ATR_EXISTS_FUNCTION, predicate);
    }

    public final boolean exists() {
        return !Boolean.FALSE.equals(getValorAtributo(SPackageCore.ATR_EXISTS));
    }

    //    public MTipo<I> withOnChange(IBehavior<I> behavior) {
    //        return as
    //    }
    //
    //    public <T> MTipo<I> withFunction(String pathCampo, Function<I, T> funcao) {
    //        // TODO implementar
    //        throw new NotImplementedException("TODO implementar");
    //    }
    //
    //    public <T> MTipo<I> withFunction(String pathCampo, Function<I, T> funcao, MISimples dependencias) {
    //        // TODO implementar
    //        throw new NotImplementedException("TODO implementar");
    //    }

    @SuppressWarnings("unchecked")
    public <T> T as(Class<T> classeAlvo) {
        if (MTranslatorParaAtributo.class.isAssignableFrom(classeAlvo)) {
            return (T) MTranslatorParaAtributo.of(this, (Class<MTranslatorParaAtributo>) classeAlvo);
        }
        throw new SingularFormException("Classe '" + classeAlvo + "' não funciona como aspecto");
    }

    public AtrBasic asAtrBasic() {
        return as(AtrBasic::new);
    }

    public AtrBootstrap asAtrBootstrap() {
        return as(AtrBootstrap::new);
    }

    public AtrCore asAtrCore() {
        return as(AtrCore::new);
    }


    public <T> T as(Function<? super SType<I>, T> aspectFactory) {
        return aspectFactory.apply(this);
    }

    public final <T extends MView> SType<I> withView(Supplier<T> factory) {
        withView(factory.get());
        return this;
    }

    @SafeVarargs
    public final <T extends MView> SType<I> withView(T mView, Consumer<T>... initializers) {
        for (Consumer<T> initializer : initializers) {
            initializer.accept(mView);
        }
        setView(mView);
        return this;
    }

    public final <T extends MView> T setView(Supplier<T> factory) {
        T v = factory.get();
        setView(v);
        return v;
    }

    private void setView(MView view) {
        if (view.aplicavelEm(this)) {
            this.view = view;
        } else {
            throw new SingularFormException("A view '" + view.getClass().getName() + "' não é aplicável ao tipo: '" + getClass().getName() + "'");
        }
    }

    public MView getView() {
        return this.view;
    }

    public Set<SType<?>> getDependentTypes() {
        if (dependentTypes == null)
            dependentTypes = new LinkedHashSet<>();
        return dependentTypes;
    }

    public boolean hasDependentTypes() {
        return (dependentTypes != null) && (!dependentTypes.isEmpty());
    }

    public boolean dependsOnAnyType() {
        return Optional.ofNullable(getValorAtributo(SPackageBasic.ATR_DEPENDS_ON_FUNCTION))
                .map(Supplier::get)
                .map(it -> !it.isEmpty())
                .orElse(false);
    }

    public boolean dependsOnAnyTypeInHierarchy() {
        return MTypes.listAscendants(this, true).stream()
                .anyMatch(SType::dependsOnAnyType);
    }

    public SType<I> addInstanceValidator(IInstanceValidator<I> validador) {
        return addInstanceValidator(ValidationErrorLevel.ERROR, validador);
    }

    public SType<I> addInstanceValidator(ValidationErrorLevel level, IInstanceValidator<I> validador) {
        this.instanceValidators.put(validador, level);
        return this;
    }

    public Collection<IInstanceValidator<I>> getValidators() {
        return instanceValidators.keySet();
    }

    public ValidationErrorLevel getValidatorErrorLevel(IInstanceValidator<I> validator) {
        return instanceValidators.get(validator);
    }

    @SuppressWarnings("unchecked")
    public I castInstancia(SInstance instancia) {
        // TODO verificar se essa é a verificação correta
        if (instancia.getType() != this)
            throw new IllegalArgumentException("A instância " + instancia + " não é do tipo " + this);
        return (I) instancia;
    }

    public final I novaInstancia() {
        SDocument owner = new SDocument();
        I instance = newInstance(this, owner);
        owner.setRoot(instance);
        return instance;
    }

    /**
     * Cria uma nova instância pertencente ao documento informado.
     */
    I newInstance(SDocument owner) {
        return newInstance(this, owner);
    }

    public SIList<?> novaLista() {
        return SIList.of(this);
    }

    private I newInstance(SType<?> original, SDocument owner) {
        Class<? extends I> c = classeInstancia;
        if (c == null && superTipo != null) {
            return superTipo.newInstance(original, owner);
        }
        if (classeInstancia == null) {
            throw new SingularFormException("O tipo '" + original.getName() + (original == this ? "" : "' que é do tipo '" + getName())
                    + "' não pode ser instanciado por esse ser abstrato (classeInstancia==null)");
        }
        try {
            I novo = classeInstancia.newInstance();
            novo.setDocument(owner);
            novo.setType(this);
            if (novo instanceof SISimple) {
                Object valorInicial = original.getValorAtributoValorInicial();
                if (valorInicial != null) {
                    novo.setValue(valorInicial);
                }
            }
            instanceCount++;
            return novo;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new SingularFormException("Erro instanciando o tipo '" + getName() + "' para o tipo '" + original.getName() + "'", e);
        }
    }

    @Override
    public void debug(int nivel) {
        debug(System.out, nivel);
    }

    @Override
    public void debug(Appendable appendable, int nivel) {
        try {
            MAtributo at = this instanceof MAtributo ? (MAtributo) this : null;
            pad(appendable, nivel).append(at == null ? "def " : "defAtt ");
            appendable.append(getSimpleName());
            if (at != null) {
                if (at.getTipoDono() != null && at.getTipoDono() != at.getEscopoPai()) {
                    appendable.append(" for ").append(suprimirPacote(at.getTipoDono().getName()));
                }
            }
            if (at == null) {
                if (superTipo == null || superTipo.getClass() != getClass()) {
                    appendable.append(" (").append(getClass().getSimpleName());
                    if (classeInstancia != null && (superTipo == null || !classeInstancia.equals(superTipo.classeInstancia))) {
                        appendable.append(":").append(classeInstancia.getSimpleName());
                    }
                    appendable.append(")");
                }
            } else if (at.isSelfReference()) {
                appendable.append(" (SELF)");
            }
            if (superTipo != null && (at == null || !at.isSelfReference())) {
                appendable.append(" extend ").append(suprimirPacote(superTipo.getName()));
                if (this instanceof STypeLista) {
                    STypeLista<?, ?> lista = (STypeLista<?, ?>) this;
                    if (lista.getTipoElementos() != null) {
                        appendable.append(" of ").append(suprimirPacote(lista.getTipoElementos().getName()));
                    }
                }
            }
            debugAtributos(appendable, nivel);
            appendable.append("\n");

            if (this instanceof STypeSimple && ((STypeSimple<?, ?>) this).getProviderOpcoes() != null) {
                pad(appendable, nivel + 2).append("selection of ").append(((STypeSimple<?, ?>) this).getProviderOpcoes().toDebug()).append("\n");
            }

            atributosDefinidos
                    .getAtributos()
                    .stream()
                    .filter(att -> !getLocalTypeOptional(att.getSimpleName()).isPresent())
                    .forEach(att -> {
                        try {
                            pad(appendable, nivel + 1)
                                    .append("att ")
                                    .append("\n")
                                    .append(suprimirPacote(att.getName()))
                                    .append(":")
                                    .append(suprimirPacote(att.getSuperType().getName()))
                                    .append(att.isSelfReference() ? " SELF" : "");
                        } catch (IOException ex) {
                            LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
                        }
                    });

            super.debug(appendable, nivel + 1);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    private void debugAtributos(Appendable appendable, int nivel) {
        try {
            Map<String, SInstance> vals = atributosResolvidos.getAtributos();
            if (vals.size() != 0) {
                appendable.append(" {");
                vals.entrySet().stream().forEach(e -> {
                    try {
                        appendable.append(suprimirPacote(e.getKey(), true))
                                .append("=")
                                .append(e.getValue().getDisplayString())
                                .append("; ");
                    } catch (IOException ex) {
                        LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
                    }
                });
                appendable.append("}");
            }
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    private String suprimirPacote(String nome) {
        return suprimirPacote(nome, false);
    }

    private String suprimirPacote(String nome, boolean agressivo) {
        if (isInicioIgual(nome, getName())) {
            return nome.substring(getName().length() + 1);
        } else if (isInicioIgual(nome, escopo.getName())) {
            return nome.substring(escopo.getName().length() + 1);
        } else if (isInicioIgual(nome, SPackageCore.NOME)) {
            String v = nome.substring(SPackageCore.NOME.length() + 1);
            if (agressivo) {
                if (isInicioIgual(v, "MTipo")) {
                    v = v.substring(6);
                }
            }
            return v;
        } else if (agressivo) {
            if (isInicioIgual(nome, SPackageBasic.NOME)) {
                return nome.substring(SPackageBasic.NOME.length() + 1);
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

    public boolean hasValidation() {
        return isObrigatorio() || !instanceValidators.isEmpty();
    }

    public MOptionsProvider getProviderOpcoes() {
        throw new UnsupportedOperationException();
    }

    public <T extends UIComponentMapper> SType<I> withCustomMapper(Supplier<T> factory) {
        this.customMapper = factory.get();
        return this;
    }

    public <T extends UIComponentMapper> SType<I> withCustomMapper(UIComponentMapper uiComponentMapper) {
        this.customMapper = uiComponentMapper;
        return this;
    }

    public UIComponentMapper getCustomMapper() {
        return customMapper;
    }
}
