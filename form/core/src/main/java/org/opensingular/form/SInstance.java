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
import org.opensingular.form.internal.PathReader;
import org.opensingular.internal.lib.commons.xml.MElement;
import org.opensingular.form.io.PersistenceBuilderXML;
import org.opensingular.form.type.basic.SPackageBasic;
import org.opensingular.form.validation.IValidationError;
import org.opensingular.lib.commons.lambda.IConsumer;
import org.opensingular.lib.commons.lambda.IFunction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;

public abstract class SInstance implements SAttributeEnabled {

    private SInstance parent;

    private AttributeInstanceInfo attributeInstanceInfo;

    private SType<?> type;

    private Map<String, SInstance> attributes;

    private SDocument document;

    private Integer id;

    /**
     * Mapa de bits de flags. Veja {@link InstanceFlags}
     */
    private int flags;

    /**
     * Informações encontradas na persitência, mas sem correspondência no tipo na instância atual.
     */
    private List<MElement> unreadInfo;
    private InstanceSerializableRef<SInstance> serializableRef;
    private ISInstanceListener.EventCollector eventCollector;

    public SType<?> getType() {
        return type;
    }

    /** Retorna o documento ao qual pertence a instância atual. */
    @Nonnull
    public SDocument getDocument() {
        return document;
    }

    /** Retorna a instância raiz da instância atual.
     *  @see SDocument#getRoot()
     */
    @Nonnull
    public SInstance getRoot() {
        return document.getRoot();
    }

    /**
     * Retorna um ID único dentre as instâncias do mesmo documento. Um ID nunca
     * é reutilizado, mesmo se a instancia for removida de dentro do documento.
     * Funcionamento semelhante a uma sequence de banco de dados.
     */
    @Nonnull
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
        return getFlag(InstanceFlags.IS_ATRIBUTO);
    }

    final void setAsAttribute(String fullName, SType<?> attributeOwner) {
        setFlag(InstanceFlags.IS_ATRIBUTO, true);
        attributeInstanceInfo = new AttributeInstanceInfo(fullName, attributeOwner);

    }

    final void setAsAttribute(String fullName, SInstance attributeOwner) {
        setFlag(InstanceFlags.IS_ATRIBUTO, true);
        attributeInstanceInfo = new AttributeInstanceInfo(fullName, attributeOwner);
    }

    /**
     * Retorna informações de atributo extra se a instância atual for um atributo.
     */
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
                    + " O pai atual é do tipo %s. ", this.getClass().getName(), this.parent.getClass().getName()), this);
        }
        this.parent = pai;
        if (pai != null && pai.isAttribute()) {
            setFlag(InstanceFlags.IS_ATRIBUTO, true);
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

    public abstract void setValue(Object value);

    public abstract Object getValue();

    /**
     * Resolves a field instance and sets its value.
     * @param value new value
     * @param rootTypeClass root type class
     * @param targetTypeFunction function that receives the root type and returns the field type
     * @param <RT> root type
     * @param <RI> root instance
     * @param <TT> target type
     * @param <TI> target instance
     * @param <VAL> value
     * @throws ClassCastException if this instance type doesn't match rootTypeClass
     * @throws NoSuchElementException if type returned by the function doesn't match a descendant type
     */
    public <RT extends SType<RI>,
            RI extends SInstance,
            TT extends STypeSimple<TI, VAL>,
            TI extends SISimple<VAL>,
            VAL extends Serializable> void setValue(
                    VAL value,
                    Class<RT> rootTypeClass,
                    IFunction<RT, TT> targetTypeFunction) {

        getField(rootTypeClass, targetTypeFunction).setValue(value);
    }

    /**
     * Resolves a field instance and returns its value, or null if empty.
     * @param rootTypeClass root type class
     * @param targetTypeFunction function that receives the root type and returns the field type
     * @param <RT> root type
     * @param <RI> root instance
     * @param <TT> target type
     * @param <TI> target instance
     * @param <VAL> value
     * @throws ClassCastException if this instance type doesn't match rootTypeClass
     * @throws NoSuchElementException if type returned by the function doesn't match a descendant type
     */
    public <RT extends SType<RI>,
            RI extends SInstance,
            TT extends SType<TI>,
            TI extends SISimple<VAL>,
            VAL extends Serializable> VAL getValue(
                    Class<RT> rootTypeClass,
                    IFunction<RT, TT> targetTypeFunction) {

        return findField(rootTypeClass, targetTypeFunction)
                .map(SISimple::getValue)
                .orElse(null);
    }

    /**
     * Resolves a field instance and returns its optional value.
     * @param rootTypeClass root type class
     * @param targetTypeFunction function that receives the root type and returns the field type
     * @param <RT> root type
     * @param <RI> root instance
     * @param <TT> target type
     * @param <TI> target instance
     * @param <VAL> value
     * @throws ClassCastException if this instance type doesn't match rootTypeClass
     * @throws NoSuchElementException if type returned by the function doesn't match a descendant type
     */
    public <RT extends SType<RI>,
            RI extends SInstance,
            TT extends SType<TI>,
            TI extends SISimple<VAL>,
            VAL extends Serializable> Optional<VAL> findValue(
                    Class<RT> rootTypeClass,
                    IFunction<RT, TT> targetTypeFunction) {

        return findField(rootTypeClass, targetTypeFunction).map(f -> (VAL) f.getValue());
    }


    /**
     * Resolves a field instance and returns it, or null if empty.
     * @param rootTypeClass root type class
     * @param targetTypeFunction function that receives the root type and returns the field type
     * @param <RT> root type
     * @param <RI> root instance
     * @param <TT> target type
     * @param <TI> target instance
     * @throws ClassCastException if this instance type doesn't match rootTypeClass
     * @throws NoSuchElementException if type returned by the function doesn't match a descendant type
     */
    private <RT extends SType<RI>,
            RI extends SInstance,
            TT extends SType<TI>,
            TI extends SInstance> TI getField(
                    Class<RT> rootTypeClass,
                    IFunction<RT, TT> targetTypeFunction) {

        return findField(rootTypeClass, targetTypeFunction).get();
    }

    /**
     * Resolves a field instance and returns it as an Optional (empty if type not found as a descendant).
     * @param rootTypeClass root type class
     * @param targetTypeFunction function that receives the root type and returns the field type
     * @param <RT> root type
     * @param <RI> root instance
     * @param <TT> target type
     * @param <TI> target instance
     * @throws ClassCastException if this instance type doesn't match rootTypeClass
     * @throws NoSuchElementException if type returned by the function doesn't match a descendant type
     */
    public  <RT extends SType<RI>,
            RI extends SInstance,
            TT extends SType<TI>,
            TI extends SInstance> Optional<TI> findField(
                    Class<RT> rootTypeClass,
                    IFunction<RT, TT> targetTypeFunction) {

        if (!rootTypeClass.isAssignableFrom(this.getType().getClass()))
            throw new SingularInvalidTypeException(this, rootTypeClass);
;
        final RI rootInstance = (RI) this;
        final RT rootType = (RT) rootInstance.getType();
        final TT targetType = targetTypeFunction.apply(rootType);

        if (!STypes.listAscendants(targetType, true).contains(rootType))
            throw new SingularInvalidFieldTypeException(rootType, targetType);
        else if (rootType == targetType)
            return Optional.of((TI) rootInstance);
        else if (rootInstance instanceof SIComposite)
            return ((SIComposite) rootInstance).findDescendant(targetType);
        else
            return Optional.empty();
    }


    <V> V getValueInTheContextOf(SInstance contextInstance, Class<V> resultClass) {
        return convert(getValue(), resultClass);
    }

    /**
     * Apaga os valores associados a instância. Se for uma lista ou composto, apaga os valores em profundidade.
     */
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
     */
    public abstract boolean isEmptyOfData();

    public Object getValueWithDefault() {
        return getValue((Class<?>) null);
    }

    public final <T> T getValueWithDefault(Class<T> resultClass) {
        return convert(getValueWithDefault(), resultClass);
    }

    public final <T> T getValue(@Nullable Class<T> resultClass) {
        return convert(getValue(), resultClass);
    }

    <T> T convert(@Nullable Object value, @Nullable Class<T> resultClass) {
        if (resultClass == null || value == null) {
            return (T) value;
        } else if (resultClass.isInstance(value)) {
            return resultClass.cast(value);
        }
        return getType().convert(value, resultClass);
    }

    public final <T> T getValue(@Nonnull String fieldPath) {
        return getValue(new PathReader(fieldPath), null);
    }

    public final <T> T getValue(@Nonnull String fieldPath, @Nullable Class<T> resultClass) {
        return getValue(new PathReader(fieldPath), resultClass);
    }

    final <T> T getValue(@Nonnull PathReader pathReader, @Nullable Class<T> resultClass) {
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

    SInstance getFieldLocalWithoutCreating(PathReader pathReader) {
        throw new SingularFormException(pathReader.getErrorMsg(this, "Não suporta leitura de subCampos"), this);
    }

    <T> T getValueWithDefaultIfNull(PathReader pathReader, Class<T> resultClass) {
        throw new SingularFormException(erroMsgMethodUnsupported(), this);
    }

    void setValue(PathReader pathReader, Object value) {
        throw new SingularFormException(erroMsgMethodUnsupported(),this);
    }

    public SInstance getField(String path) {
        return getField(new PathReader(path));
    }

    public Optional<SInstance> getFieldOpt(String path) {
        return getFieldOpt(new PathReader(path));
    }

    /**
     * Retorna o campo cujo o nome seja igual ao do tipo informado e verifica se o campo encontrado é do mesmo tipo
     * informado. Caso não seja do mesmo tipo, dispara uma exception.
     */
    public <II extends SInstance> II getField(SType<II> type) {
        SInstance instance = getField(type.getNameSimple());
        type.checkIfIsInstanceOf(instance);
        return (II) instance;
    }

    @Nonnull
    final SInstance getField(@Nonnull PathReader pathReader) {
        SInstance instance = this;
        while (true) {
            instance = instance.getFieldLocal(pathReader);
            if (pathReader.isLast()) {
                return instance;
            }
            pathReader = pathReader.next();
        }
    }

    @Nullable
    SInstance getFieldLocal(@Nonnull PathReader pathReader) {
        throw new SingularFormException(pathReader.getErrorMsg(this, "Não suporta leitura de subCampos"), this);
    }

    @Nonnull
    final Optional<SInstance> getFieldOpt(@Nonnull PathReader pathReader) {
        SInstance instance = this;
        while (true) {
            Optional<SInstance> result = instance.getFieldLocalOpt(pathReader);
            if (!result.isPresent() || pathReader.isLast()) {
                return result;
            }
            instance = result.get();
            pathReader = pathReader.next();
        }
    }

    @Nonnull
    Optional<SInstance> getFieldLocalOpt(@Nonnull PathReader pathReader) {
        throw new SingularFormException(pathReader.getErrorMsg(this, "Não suporta leitura de subCampos"), this);
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

    /**
     * Retorna a instancia do atributo se houver uma associada diretamente ao objeto atual.
     */
    @Nonnull
    public Optional<SInstance> getAttribute(@Nonnull String fullName) {
        return attributes == null ? Optional.empty() : Optional.ofNullable(attributes.get(fullName));
    }

    @Override
    public final <V> V getAttributeValue(String fullName, Class<V> resultClass) {
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
     *
     * @return Nunca null
     */
    public Collection<SInstance> getAttributes() {
        return attributes == null ? Collections.emptyList() : attributes.values();
    }

    public SInstance getParent() {
        return this.parent;
    }

    @Nonnull
    public <A extends SInstance & ICompositeInstance> A getAncestor(SType<A> ancestorType) {
        return SInstances.getAncestor(this, ancestorType);
    }

    @Nonnull
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
        for (SInstance current = getParent(); current != null; current = current.getParent()) {
            if (current == ancestor) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public <T> T as(Class<T> targetClass) {
        return (T) STranslatorForAttribute.of(this, (Class<STranslatorForAttribute>) targetClass);
    }

    @Override
    public <T> T as(Function<SAttributeEnabled, T> aspectFactory) {
        return aspectFactory.apply(this);
    }

    public boolean isRequired() {
        return SInstances.attributeValue(this, SPackageBasic.ATR_REQUIRED, Boolean.FALSE);
    }

    public void setRequired(Boolean value) {
        setAttributeValue(SPackageBasic.ATR_REQUIRED, value);
    }

    public void updateRequired() {
        SInstances.updateBooleanAttribute(this, SPackageBasic.ATR_REQUIRED, SPackageBasic.ATR_REQUIRED_FUNCTION);
    }

    public boolean exists() {
        return SInstances.attributeValue(this, SPackageBasic.ATR_EXISTS, Boolean.TRUE);
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
     * <p>
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
     * <p>
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
        setFlag(InstanceFlags.REMOVENDO_INSTANCIA, true);
        onRemove();
        if (getFlag(InstanceFlags.REMOVENDO_INSTANCIA)) {
            throw new SingularFormException(SInstance.class.getName() + " não foi corretamente removido. Alguma classe na hierarquia de "
                    + getClass().getName() + " não chamou super.onRemove() em algum método que sobreescreve onRemove()", this);
        }
        setParent(null);
        removeChildren();
    }

    /**
     * Sinaliza essa instancia para remover da hierarquia todos os seus filhos.
     */
    public void removeChildren() {
        if (this instanceof ICompositeInstance) {
            ((ICompositeInstance) this).getChildren().stream().forEach(child -> child.internalOnRemove());
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
        setFlag(InstanceFlags.REMOVENDO_INSTANCIA, false);
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
        return SInstances.hasAny(this, i -> hasValidationErrors());
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
     *
     * @return Nunca null
     */
    final List<MElement> getUnreadInfo() {
        return unreadInfo == null ? Collections.emptyList() : unreadInfo;
    }

    public InstanceSerializableRef<SInstance> getSerializableRef() {
        if (serializableRef == null) {
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

    ISInstanceListener.EventCollector getEventCollector() {
        return eventCollector;
    }

    public List<SInstanceEvent> getInstanceEvents() {
        if (eventCollector != null) {
            return eventCollector.getEvents();
        }
        return Collections.emptyList();
    }

    public SInstance getDocumentRoot(){
        return document.getRoot();
    }

}
