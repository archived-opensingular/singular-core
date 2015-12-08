package br.net.mirante.singular.form.mform;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import br.net.mirante.singular.form.mform.document.SDocument;
import br.net.mirante.singular.form.mform.io.PersistenceBuilderXML;
import br.net.mirante.singular.form.util.xml.MElement;

public abstract class MInstancia implements MAtributoEnabled {

    private MInstancia pai;

    private MInstancia attributeOwner;

    private MTipo<?> mTipo;

    private Map<String, MInstancia> atributos;

    private SDocument document;

    private Integer id;

    /** Mapa de bits de flags. Veja {@link FlagsInstancia} */
    private int flags;

    public MTipo<?> getMTipo() {
        return mTipo;
    }

    public SDocument getDocument() {
        return document;
    }

    /**
     * Retorna um ID único dentre as instâncias do mesmo documento. Um ID nunca
     * é reutilizado, mesmo se a instancia for removida de dentro do documento.
     * Funcionamento semelhante a uma sequence de banco de dados.
     *
     * @return Nunca Null
     */
    public Integer getId() {
        if (id == null) {
            id = document.nextId();
        }
        return id;
    }

    /**
     * Apenas para uso nas soluções de persistencia. Não deve ser usado fora
     * dessa situação.
     */
    public void setId(Integer id) {
        this.id = id;
    }

    final void setDocument(SDocument document) {
        this.document = document;
        if (id == null && document != null) {
            id = document.nextId();
        }
    }

    @Override
    public MDicionario getDicionario() {
        return getMTipo().getDicionario();
    }

    /**
     * Indica se a instância constitui um dado do documento ou se se é um
     * atributo de uma instância ou tipo. Também retorna true se a instância for
     * um campo ou item de lista de uma instanância pai que é um atributo. Ou
     * seja, todos os subcampos de um instancia onde isAtribute == true,
     * retornam true.
     */
    public boolean isAttribute() {
        return getFlag(FlagsInstancia.IsAtributo);
    }

    final void setAsAttribute(MInstancia attributeOwner) {
        setFlag(FlagsInstancia.IsAtributo, true);
        this.attributeOwner = attributeOwner;
    }

    /**
     * Se a instância for um atributo ou sub campo de uma atributo, retorna a
     * instancia ao qual pertence o atributo. Retorna null, se a instancia não
     * for um atributo ou se atributo pertencer a um tipo em vez de uma
     * instância.
     */
    public MInstancia getAttributeOwner() {
        return attributeOwner;
    }

    final void setPai(MInstancia pai) {
        this.pai = pai;
        if (pai.isAttribute()) {
            setAsAttribute(pai.getAttributeOwner());
        }
    }

    final void setTipo(MTipo<?> tipo) {
        this.mTipo = tipo;
    }

    public void setValor(Object valor) {
        throw new RuntimeException(erroMsgMetodoNaoSuportado());
    }

    public abstract Object getValor();

    /**
     * <p>
     * Retorna true se a instancia não conter nenhuma informação diferente de
     * null. A pesquisa é feita em profundidade, ou seja, em todos os subitens
     * (se houverem) da intância atual serão verificados.
     * </p>
     * <p>
     * Para o tipo simples retorna true se o valor for null.
     * </p>
     * <p>
     * Para o tipo lista retorna true se a lista for vazia ou se todos os seus
     * elementos retornarem isEmptyOfData() como true.
     * </p>
     * <p>
     * Para o tipo registro (composto) retorna true se todos so seus campos
     * retornarem isEmptyOfData().
     * </p>
     *
     * @return
     */
    public abstract boolean isEmptyOfData();

    public Object getValorWithDefault() {
        throw new RuntimeException(erroMsgMetodoNaoSuportado());
    }

    @SuppressWarnings("unchecked")
    public final <T extends Object> T getValorWithDefault(Class<T> classeDestino) {
        if (classeDestino == null) {
            return (T) getValor();
        }
        return getMTipo().converter(getValorWithDefault(), classeDestino);
    }

    @SuppressWarnings("unchecked")
    public final <T extends Object> T getValor(Class<T> classeDestino) {
        if (classeDestino == null) {
            return (T) getValor();
        }
        return getMTipo().converter(getValor(), classeDestino);
    }

    final <T extends Object> T getValor(LeitorPath leitor, Class<T> classeDestino) {
        MInstancia instancia = this;
        while (true) {
            if (leitor.isEmpty()) {
                return instancia.getValor(classeDestino);
            }
            MInstancia instanciaFilha = instancia.getCampoLocalSemCriar(leitor);
            if (instanciaFilha == null) {
                MFormUtil.resolverTipoCampo(instancia.getMTipo(), leitor);
                return null;
            }
            instancia = instanciaFilha;
            leitor = leitor.proximo();
        }
    }

    <T extends Object> MInstancia getCampoLocalSemCriar(LeitorPath leitor) {
        throw new RuntimeException(erroMsgMetodoNaoSuportado());
    }

    <T extends Object> T getValorWithDefaultIfNull(LeitorPath leitor, Class<T> classeDestino) {
        throw new RuntimeException(erroMsgMetodoNaoSuportado());
    }

    void setValor(LeitorPath leitorPath, Object valor) {
        throw new RuntimeException(erroMsgMetodoNaoSuportado());
    }

    final MInstancia getCampo(LeitorPath leitor) {
        MInstancia instancia = this;
        while (true) {
            instancia = instancia.getCampoLocal(leitor);
            if (leitor.isUltimo()) {
                return instancia;
            } else if (!(instancia instanceof ICompositeInstance)) {
                throw new RuntimeException(leitor.getTextoErro(instancia, "Não suporta leitura de subCampos"));
            }
            leitor = leitor.proximo();
        }
    }

    MInstancia getCampoLocal(LeitorPath leitor) {
        throw new RuntimeException(erroMsgMetodoNaoSuportado());
    }

    public String getDisplayString() {
        throw new RuntimeException(erroMsgMetodoNaoSuportado());
    }

    @Override
    public void setValorAtributo(String nomeCompletoAtributo, String subPath, Object valor) {
        MInstancia instanciaAtr = null;
        if (atributos == null) {
            atributos = new HashMap<>();
        } else {
            instanciaAtr = atributos.get(nomeCompletoAtributo);
        }
        if (instanciaAtr == null) {
            MAtributo tipoAtributo = getMTipo().getAtributoDefinidoHierarquia(nomeCompletoAtributo);
            instanciaAtr = tipoAtributo.newInstance(getDocument());
            instanciaAtr.setAsAttribute(this);
            atributos.put(nomeCompletoAtributo, instanciaAtr);
        }
        if (subPath != null) {
            instanciaAtr.setValor(new LeitorPath(subPath), valor);
        } else {
            instanciaAtr.setValor(valor);
        }
    }

    @Override
    public <V extends Object> V getValorAtributo(String nomeCompleto, Class<V> classeDestino) {
        if (atributos != null) {
            MInstancia inst = atributos.get(nomeCompleto);
            if (inst != null) {
                return inst.getValor(classeDestino);
            }
        }
        return getMTipo().getValorAtributo(nomeCompleto, classeDestino);
    }

    public Map<String, MInstancia> getAtributos() {
        return atributos == null ? Collections.emptyMap() : atributos;
    }

    public MInstancia getPai() {
        return this.pai;
    }

    public <K extends MInstancia> K getIrmao(MTipo<K> tipoPai) {
        throw new RuntimeException("implementar");
    }

    public <A extends MInstancia & ICompositeInstance> A getAncestor(MTipo<A> ancestorType) {
        return findAncestor(ancestorType).get();
    }
    public <A extends MInstancia & ICompositeInstance> Optional<A> findAncestor(MTipo<A> ancestorType) {
        return MInstances.findAncestor(this, ancestorType);
    }
    public <A extends MInstancia> Optional<A> findNearest(MTipo<A> targetType) {
        return MInstances.findNearest(this, targetType);
    }

    @SuppressWarnings("unchecked")
    public <T extends Object> T as(Class<T> classeAlvo) {
        if (MTranslatorParaAtributo.class.isAssignableFrom(classeAlvo)) {
            return (T) MTranslatorParaAtributo.of(this, (Class<MTranslatorParaAtributo>) classeAlvo);
        }
        throw new RuntimeException(
                "Classe '" + classeAlvo + "' não funciona como aspecto. Deve extender " + MTranslatorParaAtributo.class.getName());
    }
    public <T> T as(Function<? super MInstancia, T> aspectFactory) {
        return aspectFactory.apply(this);
    }

    public String getNome() {
        return getMTipo().getNomeSimples();
    }

    /**
     * <p>
     * Retorna o path da instancia atual relativa ao elemento raiz, ou seja, não
     * inclui o nome da instância raiz no path gerado.
     * </p>
     * Exemplos, supundo que enderecos e experiencias estao dentro de um
     * elemento raiz (vamos dizer chamado cadastro):
     * </p>
     *
     * <pre>
     *     "enderecos[0].rua"
     *     "experiencias[0].empresa.nome"
     *     "experiencias[1].empresa.ramo"
     * </pre>
     *
     * @return Null se chamado em uma instância raiz.
     */
    public final String getPathFromRoot() {
        return MFormUtil.generatePath(this, i -> i.pai == null);
    }

    /**
     * <p>
     * Retorna o path da instancia atual desde o raiz, incluindo o nome da
     * instancia raiz.
     * </p>
     * Exemplos, supundo que enderecos e experiencias estao dentro de um
     * elemento raiz (vamos dizer chamado cadastro):
     * </p>
     *
     * <pre>
     *     "cadastro.enderecos[0].rua"
     *     "cadastro.experiencias[0].empresa.nome"
     *     "cadastro.experiencias[1].empresa.ramo"
     * </pre>
     */
    public final String getPathFull() {
        return MFormUtil.generatePath(this, i -> i == null);
    }

    public void debug() {
        MElement xml = new PersistenceBuilderXML().withPersistId(false).toXML(this);
        if (xml == null) {
            System.out.println("null");
        } else {
            xml.printTabulado();
        }
    }

    final String erroMsgMetodoNaoSuportado() {
        return errorMsg("Método não suportado por " + getClass().getName());
    }

    /**
     * Cria uma mensagem de erro com o path da instância atual acrescido da
     * mensagem fornecida.
     */
    protected final String errorMsg(String msgToBeAppended) {
        return "'" + getPathFull() + "' do tipo " + getMTipo().getNome() + "(" + getMTipo().getClass().getSimpleName() + ") : "
            + msgToBeAppended;
    }

    /**
     * Signals this Component that it is removed from the Component hierarchy.
     */
    final void internalOnRemove() {
        setFlag(FlagsInstancia.RemovendoInstancia, true);
        onRemove();
        if (getFlag(FlagsInstancia.RemovendoInstancia)) {
            throw new SingularFormException(MInstancia.class.getName() + " não foi corretamente removido. Alguma classe na hierarquia de "
                    + getClass().getName() + " não chamou super.onRemove() em algum método que sobreescreve onRemove()");
        }
        removeChildren();
    }

    /**
     * Sinaliza essa instancia para remover da hierarquia todos os seus filhos.
     */
    void removeChildren() {
        if (this instanceof ICompositeInstance) {
            for (MInstancia child : ((ICompositeInstance) this).getChildren()) {
                child.internalOnRemove();
            }
        }
    }

    /**
     * <p>
     * Chamado para notificar que a instancia está sendo removida da hierarquia.
     * </p>
     * <p>
     * Métodos derivados devem chamar a implementação super, o lugar mais lógico
     * para fazer essa chamada é na última linha do método que sobreescreve.
     * </p>
     */
    protected void onRemove() {
        setFlag(FlagsInstancia.RemovendoInstancia, false);
    }

    final void setFlag(FlagsInstancia flag, boolean value) {
        if (value) {
            flags |= flag.bit();
        } else {
            flags &= ~flag.bit();
        }
    }

    final boolean getFlag(FlagsInstancia flag) {
        return (flags & flag.bit()) != 0;
    }

}
