/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.form;

import org.opensingular.form.calculation.SimpleValueCalculation;
import org.opensingular.form.document.SDocument;
import org.opensingular.form.event.ISInstanceListener;
import org.opensingular.form.event.SInstanceEvent;
import org.opensingular.form.event.SInstanceEventType;
import org.opensingular.form.event.SInstanceListeners;
import org.opensingular.form.internal.xml.MElement;
import org.opensingular.form.io.PersistenceBuilderXML;
import org.opensingular.form.type.basic.SPackageBasic;
import org.opensingular.form.validation.IValidationError;
import org.opensingular.lib.commons.lambda.IConsumer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public abstract class SInstance implements SAttributeEnabled {

    private SInstance              parent;

    private AttributeInstanceInfo  attributeInstanceInfo;

    private SType<?>               type;

    private Map<String, SInstance> attributes;

    private SDocument document;

    private Integer id;

    /** Mapa de bits de flags. Veja {@link InstanceFlags} */
    private int flags;

    /** Informações encontradas na persitência, mas sem correspondência no tipo na instância atual. */
    private List<MElement> unreadInfo;
    private InstanceSerializableRef<SInstance> serializableRef;
    private ISInstanceListener.EventCollector eventCollector;

    public SType<?> getType() {
        return type;
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
    public SDictionary getDictionary() {
        return getType().getDictionary();
    }

    /**
     * Indica se a instância constitui um dado do documento ou se se é um
     * atributo de uma instância ou tipo. Também retorna true se a instância for
     * um campo ou item de lista de uma instância pai que é um atributo. Ou
     * seja, todos os subcampos de um instancia onde isAtribute == true,
     * retornam true.
     */
    public boolean isAttribute() {
        return getFlag(InstanceFlags.IsAtributo);
    }

    final void setAsAttribute(String fullName, SType<?> attributeOwner) {
        setFlag(InstanceFlags.IsAtributo, true);
        attributeInstanceInfo = new AttributeInstanceInfo(fullName, attributeOwner);

    }

    final void setAsAttribute(String fullName, SInstance attributeOwner) {
        setFlag(InstanceFlags.IsAtributo, true);
        attributeInstanceInfo = new AttributeInstanceInfo(fullName, attributeOwner);
    }

    /** Retorna informações de atributo extra se a instância atual for um atributo. */
    public final AttributeInstanceInfo getAttributeInstanceInfo() {
        return attributeInstanceInfo;
    }

    /**
     * Se a instância for um atributo ou sub campo de uma atributo, retorna a
     * instancia ao qual pertence o atributo. Retorna null, se a instancia não
     * for um atributo ou se atributo pertencer a um tipo em vez de uma
     * instância.
     */
    final SInstance getAttributeOwner() {
        return attributeInstanceInfo == null ? null : attributeInstanceInfo.getInstanceOwner();
    }

    final void setParent(SInstance pai) {
        /*
         * exceção adicionada por vinicius nunes, para adicionar uma instancia a
         * outra hierarquia deveria haver uma chamada para 'destacar' a
         * minstancia da sua hierarquia atual
         */
        if (this.parent != null && pai != null) {
            throw new SingularFormException(String.format(" Não é possível adicionar uma MIstancia criada em uma hierarquia à outra."
                + " MInstancia adicionada a um objeto do tipo %s já pertence à outra hierarquia de MInstancia."
                + " O pai atual é do tipo %s. ", this.getClass().getName(), this.parent.getClass().getName()));
        }
        this.parent = pai;
        if (pai != null && pai.isAttribute()) {
            setFlag(InstanceFlags.IsAtributo, true);
            attributeInstanceInfo = pai.attributeInstanceInfo;
        }
    }

    /**
     * Executa as inicilização de atribuição de valor da instância (ver {@link SType#withInitListener(IConsumer)}). Pode
     * sobrepor valores preexistentes.
     */
    public final void init() {
        //Não deve chamar o init se estiver no modo de leitura do XML
        if (getDocument().getLastId() != -1) {
            ((SType) getType()).init(() -> this);
        }
    }

    final void setType(SType<?> type) {
        this.type = type;
    }

    public void setValue(Object value) {
        throw new RuntimeException(erroMsgMethodUnsupported());
    }

    public abstract Object getValue();

    <V extends Object> V getValueInTheContextOf(SInstance contextInstance, Class<V> resultClass) {
        return convert(getValue(), resultClass);
    }

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
        return getValue(null);
    }

    public final <T extends Object> T getValueWithDefault(Class<T> resultClass) {
        return convert(getValueWithDefault(), resultClass);
    }

    public final <T extends Object> T getValue(Class<T> resultClass) {
        return convert(getValue(), resultClass);
    }

    <T> T convert(Object value, Class<T> resultClass) {
        if (resultClass == null || value == null) {
            return (T) value;
        } else if (resultClass.isInstance(value)) {
            return resultClass.cast(value);
        }
        return getType().convert(value, resultClass);
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
                throw new SingularFormException(pathReader.getErroMsg(instance, "Não suporta leitura de subCampos"), instance);
            }
            pathReader = pathReader.next();
        }
    }

    SInstance getFieldLocal(PathReader pathReader) {
        throw new RuntimeException(erroMsgMethodUnsupported());
    }

    final Optional<SInstance> getFieldOpt(PathReader pathReader) {
        SInstance instance = this;
        while (true) {
            Optional<SInstance> result = instance.getFieldLocalOpt(pathReader);
            if (!result.isPresent() || pathReader.isLast()) {
                return result;
            } else if (!(instance instanceof ICompositeInstance)) {
                throw new SingularFormException(pathReader.getErroMsg(instance, "Não suporta leitura de subCampos"), instance);
            }
            instance = result.get();
            pathReader = pathReader.next();
        }
    }

    Optional<SInstance> getFieldLocalOpt(PathReader pathReader) {
        throw new RuntimeException(erroMsgMethodUnsupported());
    }

    public String toStringDisplayDefault() {
        return null;
    }

    public final String toStringDisplay() {
        return asAtr().getDisplayString();
    }

    @Override
    public void setAttributeValue(String attributeFullName, String subPath, Object value) {
        SInstance instanceAtr = getOrCreateAttribute(attributeFullName);
        if (subPath != null) {
            instanceAtr.setValue(new PathReader(subPath), value);
        } else {
            instanceAtr.setValue(value);
        }
    }

    @Override
    public <V> void setAttributeCalculation(String attributeFullName, String subPath, SimpleValueCalculation<V> valueCalculation) {
        SInstance instanceAtr = getOrCreateAttribute(attributeFullName);
        setValueCalculation(instanceAtr, subPath, valueCalculation);
    }

    static <V> void setValueCalculation(SInstance instance, String subPath, SimpleValueCalculation<V> valueCalculation) {
        if (subPath != null) {
            instance = instance.getField(new PathReader(subPath));
        }
        if (!(instance instanceof SISimple)) {
            throw new SingularFormException("O atributo " + instance.getPathFull() + " não é do tipo " + SISimple.class.getName(),
                instance);
        }
        ((SISimple) instance).setValueCalculation(valueCalculation);
    }

    private SInstance getOrCreateAttribute(String attributeFullName) {
        SInstance instanceAtr = null;
        if (attributes == null) {
            attributes = new LinkedHashMap<>();
        } else {
            instanceAtr = attributes.get(attributeFullName);
        }
        if (instanceAtr == null) {
            SType<?> attributeType = getType().getAttributeDefinedHierarchy(attributeFullName);
            instanceAtr = attributeType.newInstance(getDocument());
            instanceAtr.setAsAttribute(attributeFullName, this);
            attributes.put(attributeFullName, instanceAtr);
        }
        return instanceAtr;
    }

    /** Retorna a instancia do atributo se houver uma associada diretamente ao objeto atual. */
    public Optional<SInstance> getAttribute(String fullName) {
        return attributes == null ? Optional.empty() : Optional.ofNullable(attributes.get(fullName));
    }

    @Override
    public final <V extends Object> V getAttributeValue(String fullName, Class<V> resultClass) {
        if (attributes != null) {
            SInstance attribute = attributes.get(fullName);
            if (attribute != null) {
                return attribute.getValueInTheContextOf(this, resultClass);
            }
        }
        return getType().getValueInTheContextOf(this, fullName, resultClass);
    }

    /**
     * Lista todos os atributos com valor associado diretamente à instância atual.
     * @return Nunca null
     */
    public Collection<SInstance> getAttributes() {
        return attributes == null ? Collections.emptyList() : attributes.values();
    }

    public SInstance getParent() {
        return this.parent;
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

    public boolean isDescendantOf(SInstance ancestor) {
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
    @Override
    public <T> T as(Function<SAttributeEnabled, T> aspectFactory) {
        return aspectFactory.apply(this);
    }

    public boolean isRequired() {
        return SInstances.attributeValue(this, SPackageBasic.ATR_REQUIRED, false);
    }
    public void setRequired(Boolean value) {
        setAttributeValue(SPackageBasic.ATR_REQUIRED, value);
    }
    public void updateRequired() {
        SInstances.updateBooleanAttribute(this, SPackageBasic.ATR_REQUIRED, SPackageBasic.ATR_REQUIRED_FUNCTION);
    }
    public boolean exists() {
        return SInstances.attributeValue(this, SPackageBasic.ATR_EXISTS, true);
    }
    public void setExists(Boolean value) {
        setAttributeValue(SPackageBasic.ATR_EXISTS, value);
    }
    public void updateExists() {
        SInstances.updateBooleanAttribute(this, SPackageBasic.ATR_EXISTS, SPackageBasic.ATR_EXISTS_FUNCTION);
        if (!exists())
            SInstances.visitPostOrder(this, (i, v) -> i.clearInstance());
    }

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
    public void removeChildren() {
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

    public boolean hasValidationErrors() {
        return !getValidationErrors().isEmpty();
    }

    public boolean hasNestedValidationErrors() {
        return SInstances.visit(this, (i, v) -> {
            if (i.hasValidationErrors())
                v.stop(true);
        }).isPresent();
    }

    public Collection<IValidationError> getValidationErrors() {
        return getDocument().getValidationErrors(getId());
    }

    public Collection<IValidationError> getNestedValidationErrors() {
        List<IValidationError> errors = new ArrayList<>();
        SInstances.visit(this, (i, v) -> errors.addAll(i.getValidationErrors()));
        return errors;
    }

    @Override
    public String toString() {
        return toStringInternal().append(')').toString();
    }

    /**
     * Pre-monta o contéudo do toString() e dá a chance às classes derivadas de acrescentar mais informação sobre
     * escrevendo-o.
     */
    StringBuilder toStringInternal() {
        StringBuilder sb = new StringBuilder();
        String name = getClass().getName();
        if (name.startsWith(SInstance.class.getPackage().getName())) {
            sb.append(getClass().getSimpleName());
        } else {
            sb.append(getClass().getName());
        }
        sb.append('@').append(id);
        sb.append('(');
        sb.append("path=").append(getPathFull());
        sb.append("; type=");
        if (getType() != null) {
            sb.append(getType().getClass().getSimpleName()).append('@').append(getType().getTypeId());
        }
        return sb;
    }

    //----------------------------------------------------
    // Métodos de uso interno expostos via InternalAccess
    //----------------------------------------------------

    /**
     * Salva uma informação lida da persitência para o qual não foi encontrado estruturada de dados correspondente no
     * tipo. Essa informações provavelmente será salva quando a instancia for persistida novamente. Ou seja, esse
     * dado não será perdido no ato de ler e regravar.
     */
    final void addUnreadInfo(MElement xmlInfo) {
        //TODO Está trabalhando com XML. Generalizar para permitir outras estruturas de dados como por exemplo JSON.
        // (by Daniel Bordin)
        if (unreadInfo == null) {
            unreadInfo = new ArrayList<>();
        }
        unreadInfo.add(xmlInfo);
    }

    /**
     * Retorna uma lista das informações encontrada para essa instância que não foram "consumidas" durante a leitura
     * a parti da persistência. Ou seja, dados "perdidos".
     * @return Nunca null
     */
    final List<MElement> getUnreadInfo() {
        return unreadInfo == null ? Collections.emptyList() : unreadInfo;
    }

    public InstanceSerializableRef<SInstance> getSerializableRef() {
        if (serializableRef == null){
            serializableRef = new InstanceSerializableRef<>(this);
        }
        return serializableRef;
    }

    /**
     * configura os instance listeners em uma instancia.
     * Geralmente chamado após uma deserialização;
     */
    public void attachEventCollector() {
        if (this.eventCollector == null) {
            this.eventCollector = new ISInstanceListener.EventCollector();
            SInstanceListeners listeners = this.getDocument().getInstanceListeners();
            listeners.add(SInstanceEventType.VALUE_CHANGED, this.eventCollector);
            listeners.add(SInstanceEventType.LIST_ELEMENT_ADDED, this.eventCollector);
            listeners.add(SInstanceEventType.LIST_ELEMENT_REMOVED, this.eventCollector);
        }
    }

    /**
     * Chamado durante deserialização para remover os listeners de um objeto
     */
    public void detachEventCollector() {
        if (eventCollector != null) {
            this.getDocument().getInstanceListeners().remove(SInstanceEventType.values(), this.eventCollector);
            this.eventCollector = null;
        }
    }

    public void clearInstanceEvents() {
        if (eventCollector != null) {
            eventCollector.clear();
        }
    }

    public List<SInstanceEvent> getInstanceEvents() {
        if (eventCollector != null) {
            return eventCollector.getEvents();
        }
        return Collections.emptyList();
    }
}
