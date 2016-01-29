package br.net.mirante.singular.form.mform;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import br.net.mirante.singular.form.mform.core.SPackageCore;
import br.net.mirante.singular.form.mform.document.SDocument;
import br.net.mirante.singular.form.mform.io.PersistenceBuilderXML;
import br.net.mirante.singular.form.mform.options.MOptionsConfig;
import br.net.mirante.singular.form.mform.options.MSelectionableInstance;
import br.net.mirante.singular.form.mform.options.MSelectionableType;
import br.net.mirante.singular.form.util.xml.MElement;

public abstract class SInstance implements MAtributoEnabled, MSelectionableInstance {

    private SInstance parent;

    private SInstance attributeOwner;

    private SType<?> SType;

    private Map<String, SInstance> atributos;

    private SDocument document;

    private Integer id;

    /**
     * Configurador de opções da instancia
     * para o provider de opções do tipo
     */
    private MOptionsConfig optionsConfig;

    /** Mapa de bits de flags. Veja {@link FlagsInstancia} */
    private int flags;

    @Override
    public SType<?> getMTipo() {
        return SType;
    }

    @Override
    public MOptionsConfig getOptionsConfig() {
        if (optionsConfig == null){
            optionsConfig = new MOptionsConfig(this);
        }
        return optionsConfig;
    }

    public SDocument getDocument() {
        return document;
    }

    private String selectLabel;

    @Override
    public void setSelectLabel(String selectLabel) {
        this.selectLabel = selectLabel;
    }

    @Override
    public String getSelectLabel() {
        if (selectLabel == null) {
            if (getMTipo() instanceof MSelectionableType) {
                MSelectionableType type = (MSelectionableType) getMTipo();
                String label =  type.getSelectLabel();
                Object valor = this.getValor();
                if (valor instanceof Iterable) {
                    for (SInstance mi : (Iterable<SInstance>)valor) {
                        if (label.equals(mi.getNome())) {
                            Object valorCampo = mi.getValor();
                            return valorCampo == null ? "" : valorCampo.toString();
                        }
                    }
                } else {
                    return valor == null ? "" : valor.toString();
                }
            }
        }
        return selectLabel;
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
    public SDictionary getDicionario() {
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

    final void setAsAttribute(SInstance attributeOwner) {
        setFlag(FlagsInstancia.IsAtributo, true);
        this.attributeOwner = attributeOwner;
    }

    /**
     * Se a instância for um atributo ou sub campo de uma atributo, retorna a
     * instancia ao qual pertence o atributo. Retorna null, se a instancia não
     * for um atributo ou se atributo pertencer a um tipo em vez de uma
     * instância.
     */
    public SInstance getAttributeOwner() {
        return attributeOwner;
    }

    final void setPai(SInstance pai) {
        /* exceção adicionada por vinicius nunes, para adicionar uma instancia a outra hierarquia deveria haver
        * uma chamada para 'destacar' a minstancia da sua hierarquia atual*/
        if (this.parent != null && pai != null){
            throw new SingularFormException(
                    String.format(
                            " Não é possível adicionar uma MIstancia criada em uma hierarquia à outra." +
                            " MInstancia adicionada a um objeto do tipo %s já pertence à outra hierarquia de MInstancia." +
                            " O pai atual é do tipo %s. ",
                            this.getClass().getName(),
                            this.parent.getClass().getName()));
        }
        this.parent = pai;
        if (pai != null && pai.isAttribute()) {
            setAsAttribute(pai.getAttributeOwner());
        }
    }

    final void setTipo(SType<?> tipo) {
        this.SType = tipo;
    }

    public void setValor(Object valor) {
        throw new RuntimeException(erroMsgMetodoNaoSuportado());
    }

    public abstract Object getValor();

    public abstract void clearInstance();

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
        return getValorWithDefault(null);
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
        SInstance instancia = this;
        while (true) {
            if (leitor.isEmpty()) {
                return instancia.getValor(classeDestino);
            }
            SInstance instanciaFilha = instancia.getCampoLocalSemCriar(leitor);
            if (instanciaFilha == null) {
                MFormUtil.resolverTipoCampo(instancia.getMTipo(), leitor);
                return null;
            }
            instancia = instanciaFilha;
            leitor = leitor.proximo();
        }
    }

    <T extends Object> SInstance getCampoLocalSemCriar(LeitorPath leitor) {
        throw new RuntimeException(erroMsgMetodoNaoSuportado());
    }

    <T extends Object> T getValorWithDefaultIfNull(LeitorPath leitor, Class<T> classeDestino) {
        throw new RuntimeException(erroMsgMetodoNaoSuportado());
    }

    void setValor(LeitorPath leitorPath, Object valor) {
        throw new RuntimeException(erroMsgMetodoNaoSuportado());
    }

    final SInstance getCampo(LeitorPath leitor) {
        SInstance instancia = this;
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

    SInstance getCampoLocal(LeitorPath leitor) {
        throw new RuntimeException(erroMsgMetodoNaoSuportado());
    }

    public String getDisplayString() {
        throw new RuntimeException(erroMsgMetodoNaoSuportado());
    }

    @Override
    public void setValorAtributo(String nomeCompletoAtributo, String subPath, Object valor) {
        SInstance instanciaAtr = null;
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
            SInstance inst = atributos.get(nomeCompleto);
            if (inst != null) {
                return inst.getValor(classeDestino);
            }
        }
        return getMTipo().getValorAtributo(nomeCompleto, classeDestino);
    }

    public Map<String, SInstance> getAtributos() {
        return atributos == null ? Collections.emptyMap() : atributos;
    }

    public SInstance getParent() {
        return this.parent;
    }

    public <K extends SInstance> K getIrmao(SType<K> tipoPai) {
        throw new RuntimeException("implementar");
    }

    public <A extends SInstance & ICompositeInstance> A getAncestor(SType<A> ancestorType) {
        return findAncestor(ancestorType).get();
    }
    public <A extends SInstance & ICompositeInstance> Optional<A> findAncestor(SType<A> ancestorType) {
        return MInstances.findAncestor(this, ancestorType);
    }
    public <A extends SInstance> Optional<A> findNearest(SType<A> targetType) {
        return MInstances.findNearest(this, targetType);
    }
    @SuppressWarnings("unchecked")
    public <V> Optional<V> findNearestValue(SType<?> targetType) {
        Optional<? extends SInstance> nearest = MInstances.findNearest(this, targetType);
        return (Optional<V>) nearest.map(it -> it.getValorWithDefault());
    }
    public <V> Optional<V> findNearestValue(SType<?> targetType, Class<V> classeValor) {
        Optional<? extends SInstance> nearest = MInstances.findNearest(this, targetType);
        return nearest.map(it -> classeValor.cast(it.getValorWithDefault(classeValor)));
    }

    @SuppressWarnings("unchecked")
    public <T extends Object> T as(Class<T> classeAlvo) {
        if (MTranslatorParaAtributo.class.isAssignableFrom(classeAlvo)) {
            return (T) MTranslatorParaAtributo.of(this, (Class<MTranslatorParaAtributo>) classeAlvo);
        }
        throw new RuntimeException(
            "Classe '" + classeAlvo + "' não funciona como aspecto. Deve extender " + MTranslatorParaAtributo.class.getName());
    }
    public <T> T as(Function<? super SInstance, T> aspectFactory) {
        return aspectFactory.apply(this);
    }

    public boolean isObrigatorio() {
        return MInstances.attributeValue(this, SPackageCore.ATR_OBRIGATORIO, false);
    }
    public void setObrigatorio(Boolean value) {
        setValorAtributo(SPackageCore.ATR_OBRIGATORIO, value);
    }
    public void updateObrigatorio() {
        MInstances.updateBooleanAttribute(this, SPackageCore.ATR_OBRIGATORIO, SPackageCore.ATR_OBRIGATORIO_FUNCTION);
    }
    public boolean exists() {
        return MInstances.attributeValue(this, SPackageCore.ATR_EXISTS, true);
    }
    public void setExists(Boolean value) {
        setValorAtributo(SPackageCore.ATR_EXISTS, value);
    }
    public void updateExists() {
        MInstances.updateBooleanAttribute(this, SPackageCore.ATR_EXISTS, SPackageCore.ATR_EXISTS_FUNCTION);
        if (!exists())
            MInstances.visitAll(this, true, ins -> ins.resetValue());
    }

    protected void resetValue() {}

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
        return MFormUtil.generatePath(this, i -> i.parent == null);
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
            throw new SingularFormException(SInstance.class.getName() + " não foi corretamente removido. Alguma classe na hierarquia de "
                + getClass().getName() + " não chamou super.onRemove() em algum método que sobreescreve onRemove()");
        }
        this.setPai(null);
        removeChildren();
    }

    /**
     * Sinaliza essa instancia para remover da hierarquia todos os seus filhos.
     */
    void removeChildren() {
        if (this instanceof ICompositeInstance) {
            for (SInstance child : ((ICompositeInstance) this).getChildren()) {
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
