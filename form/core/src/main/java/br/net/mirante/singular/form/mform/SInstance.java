/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.mform;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import br.net.mirante.singular.form.mform.core.SPackageCore;
import br.net.mirante.singular.form.mform.document.SDocument;
import br.net.mirante.singular.form.mform.io.PersistenceBuilderXML;
import br.net.mirante.singular.form.mform.options.SOptionsConfig;
import br.net.mirante.singular.form.mform.options.SSelectionableInstance;
import br.net.mirante.singular.form.mform.options.SSelectionableType;
import br.net.mirante.singular.form.util.xml.MElement;

public abstract class SInstance implements SAttributeEnabled, SSelectionableInstance {

    private SInstance parent;

    private SInstance attributeOwner;

    private SType<?> type;

    private Map<String, SInstance> attributes;

    private SDocument document;

    private Integer id;

    /**
     * Configurador de opções da instancia para o provider de opções do tipo
     */
    private SOptionsConfig optionsConfig;

    /** Mapa de bits de flags. Veja {@link InstanceFlags} */
    private int flags;

    @Override
    public SType<?> getType() {
        return type;
    }

    @Override
    public SOptionsConfig getOptionsConfig() {
        if (optionsConfig == null){
            optionsConfig = new SOptionsConfig(this);
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
            if (getType() instanceof SSelectionableType) {
                SSelectionableType type = (SSelectionableType) getType();
                String label =  type.getSelectLabel();
                Object valor = this.getValue();
                if (valor instanceof Iterable) {
                    for (SInstance mi : (Iterable<SInstance>)valor) {
                        if (label.equals(mi.getName())) {
                            Object valorCampo = mi.getValue();
                            return valorCampo == null ? "" : valorCampo.toString();
                        }
                    }
                } else {
                    final String valorString = String.valueOf(valor);
                    final String labelFromKey = getOptionsConfig().getLabelFromKey(valorString);
                    if(labelFromKey == null){
                        return valorString;
                    } else {
                        return labelFromKey;
                    }
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
    public SDictionary getDictionary() {
        return getType().getDictionary();
    }

    /**
     * Indica se a instância constitui um dado do documento ou se se é um
     * atributo de uma instância ou tipo. Também retorna true se a instância for
     * um campo ou item de lista de uma instanância pai que é um atributo. Ou
     * seja, todos os subcampos de um instancia onde isAtribute == true,
     * retornam true.
     */
    public boolean isAttribute() {
        return getFlag(InstanceFlags.IsAtributo);
    }

    final void setAsAttribute(SInstance attributeOwner) {
        setFlag(InstanceFlags.IsAtributo, true);
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

    final void setParent(SInstance pai) {
        /*
         * exceção adicionada por vinicius nunes, para adicionar uma instancia a
         * outra hierarquia deveria haver uma chamada para 'destacar' a
         * minstancia da sua hierarquia atual
         */
        if (this.parent != null && pai != null){
            throw new SingularFormException(
                    String.format(
" Não é possível adicionar uma MIstancia criada em uma hierarquia à outra."
                    + " MInstancia adicionada a um objeto do tipo %s já pertence à outra hierarquia de MInstancia."
                    + " O pai atual é do tipo %s. ",
                            this.getClass().getName(),
                            this.parent.getClass().getName()));
        }
        this.parent = pai;
        if (pai != null && pai.isAttribute()) {
            setAsAttribute(pai.getAttributeOwner());
        }
    }

    final void setType(SType<?> type) {
        this.type = type;
    }

    public void setValue(Object value) {
        throw new RuntimeException(erroMsgMethodUnsupported());
    }

    public abstract Object getValue();

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

    public Object getValueWithDefault() {
        return getValueWithDefault(null);
    }

    @SuppressWarnings("unchecked")
    public final <T extends Object> T getValueWithDefault(Class<T> resultClass) {
        if (resultClass == null) {
            return (T) getValue();
        }
        return getType().convert(getValueWithDefault(), resultClass);
    }

    @SuppressWarnings("unchecked")
    public final <T extends Object> T getValue(Class<T> resultClass) {
        if (resultClass == null) {
            return (T) getValue();
        }
        return getType().convert(getValue(), resultClass);
    }

    final <T extends Object> T getValue(PathReader pathReader, Class<T> resultClass) {
        SInstance instance = this;
        while (true) {
            if (pathReader.isEmpty()) {
                return instance.getValue(resultClass);
            }
            SInstance children = instance.getFieldLocalWithoutCreating(pathReader);
            if (children == null) {
                SFormUtil.resolveFieldType(instance.getType(), pathReader);
                return null;
            }
            instance = children;
            pathReader = pathReader.next();
        }
    }

    <T extends Object> SInstance getFieldLocalWithoutCreating(PathReader pathReader) {
        throw new RuntimeException(erroMsgMethodUnsupported());
    }

    <T extends Object> T getValueWithDefaultIfNull(PathReader pathReader, Class<T> resultClass) {
        throw new RuntimeException(erroMsgMethodUnsupported());
    }

    void setValue(PathReader pathReader, Object value) {
        throw new RuntimeException(erroMsgMethodUnsupported());
    }

    final SInstance getField(PathReader pathReader) {
        SInstance instance = this;
        while (true) {
            instance = instance.getFieldLocal(pathReader);
            if (pathReader.isLast()) {
                return instance;
            } else if (!(instance instanceof ICompositeInstance)) {
                throw new RuntimeException(pathReader.getErroMsg(instance, "Não suporta leitura de subCampos"));
            }
            pathReader = pathReader.next();
        }
    }

    SInstance getFieldLocal(PathReader pathReader) {
        throw new RuntimeException(erroMsgMethodUnsupported());
    }

    public String toStringDisplay() {
        throw new RuntimeException(erroMsgMethodUnsupported());
    }

    @Override
    public void setAttributeValue(String fullNameAttribute, String subPath, Object value) {
        SInstance instanceAtr = null;
        if (attributes == null) {
            attributes = new HashMap<>();
        } else {
            instanceAtr = attributes.get(fullNameAttribute);
        }
        if (instanceAtr == null) {
            SAttribute attributeType = getType().getAttributeDefinedHierarchy(fullNameAttribute);
            instanceAtr = attributeType.newInstance(getDocument());
            instanceAtr.setAsAttribute(this);
            attributes.put(fullNameAttribute, instanceAtr);
        }
        if (subPath != null) {
            instanceAtr.setValue(new PathReader(subPath), value);
        } else {
            instanceAtr.setValue(value);
        }
    }

    @Override
    public <V extends Object> V getAttributeValue(String fullName, Class<V> resultClass) {
        if (attributes != null) {
            SInstance inst = attributes.get(fullName);
            if (inst != null) {
                return inst.getValue(resultClass);
            }
        }
        return getType().getAttributeValue(fullName, resultClass);
    }

    public Map<String, SInstance> getAttributes() {
        return attributes == null ? Collections.emptyMap() : attributes;
    }

    public SInstance getParent() {
        return this.parent;
    }

    public <K extends SInstance> K getBrother(SType<K> tipoPai) {
        throw new RuntimeException("implementar");
    }

    public <A extends SInstance & ICompositeInstance> A getAncestor(SType<A> ancestorType) {
        return findAncestor(ancestorType).get();
    }
    public <A extends SInstance & ICompositeInstance> Optional<A> findAncestor(SType<A> ancestorType) {
        return SInstances.findAncestor(this, ancestorType);
    }
    public <A extends SInstance> Optional<A> findNearest(SType<A> targetType) {
        return SInstances.findNearest(this, targetType);
    }
    @SuppressWarnings("unchecked")
    public <V> Optional<V> findNearestValue(SType<?> targetType) {
        Optional<? extends SInstance> nearest = SInstances.findNearest(this, targetType);
        return (Optional<V>) nearest.map(it -> it.getValueWithDefault());
    }
    public <V> Optional<V> findNearestValue(SType<?> targetType, Class<V> classeValor) {
        Optional<? extends SInstance> nearest = SInstances.findNearest(this, targetType);
        return nearest.map(it -> classeValor.cast(it.getValueWithDefault(classeValor)));
    }

    public boolean isDescentantOf(SInstance ancestor) {
        SInstance node = this;
        for (SInstance parent = node.getParent(); parent != null; parent = parent.getParent())
            if (parent == ancestor)
                return true;
        return false;
    }

    @SuppressWarnings("unchecked")
    public <T extends Object> T as(Class<T> classeAlvo) {
        if (STranslatorForAttribute.class.isAssignableFrom(classeAlvo)) {
            return (T) STranslatorForAttribute.of(this, (Class<STranslatorForAttribute>) classeAlvo);
        }
        throw new RuntimeException(
                "Classe '" + classeAlvo + "' não funciona como aspecto. Deve extender " + STranslatorForAttribute.class.getName());
    }
    public <T> T as(Function<? super SInstance, T> aspectFactory) {
        return aspectFactory.apply(this);
    }

    public boolean isRequired() {
        return SInstances.attributeValue(this, SPackageCore.ATR_REQUIRED, false);
    }
    public void setRequired(Boolean value) {
        setAttributeValue(SPackageCore.ATR_REQUIRED, value);
    }
    public void updateRequired() {
        SInstances.updateBooleanAttribute(this, SPackageCore.ATR_REQUIRED, SPackageCore.ATR_OBRIGATORIO_FUNCTION);
    }
    public boolean exists() {
        return SInstances.attributeValue(this, SPackageCore.ATR_EXISTS, true);
    }
    public void setExists(Boolean value) {
        setAttributeValue(SPackageCore.ATR_EXISTS, value);
    }
    public void updateExists() {
        SInstances.updateBooleanAttribute(this, SPackageCore.ATR_EXISTS, SPackageCore.ATR_EXISTS_FUNCTION);
        if (!exists())
            SInstances.visitAll(this, true, SInstance::resetValue);
    }

    protected void resetValue() {}

    public String getName() {
        return getType().getNameSimple();
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
        return SFormUtil.generatePath(this, i -> i.parent == null);
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
        return SFormUtil.generatePath(this, i -> i == null);
    }

    public void debug() {
        MElement xml = new PersistenceBuilderXML().withPersistId(false).toXML(this);
        if (xml == null) {
            System.out.println("null");
        } else {
            xml.printTabulado();
        }
    }

    final String erroMsgMethodUnsupported() {
        return errorMsg("Método não suportado por " + getClass().getName());
    }

    /**
     * Cria uma mensagem de erro com o path da instância atual acrescido da
     * mensagem fornecida.
     */
    protected final String errorMsg(String msgToBeAppended) {
        return "'" + getPathFull() + "' do tipo " + getType().getName() + "(" + getType().getClass().getSimpleName() + ") : "
            + msgToBeAppended;
    }

    /**
     * Signals this Component that it is removed from the Component hierarchy.
     */
    final void internalOnRemove() {
        setFlag(InstanceFlags.RemovendoInstancia, true);
        onRemove();
        if (getFlag(InstanceFlags.RemovendoInstancia)) {
            throw new SingularFormException(SInstance.class.getName() + " não foi corretamente removido. Alguma classe na hierarquia de "
                    + getClass().getName() + " não chamou super.onRemove() em algum método que sobreescreve onRemove()");
        }
        setParent(null);
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
        setFlag(InstanceFlags.RemovendoInstancia, false);
    }

    final void setFlag(InstanceFlags flag, boolean value) {
        if (value) {
            flags |= flag.bit();
        } else {
            flags &= ~flag.bit();
        }
    }

    final boolean getFlag(InstanceFlags flag) {
        return (flags & flag.bit()) != 0;
    }

}
