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

import org.opensingular.form.aspect.AspectRef;
import org.opensingular.form.calculation.CalculationContextInstanceOptional;
import org.opensingular.form.calculation.SimpleValueCalculation;
import org.opensingular.form.document.SDocument;
import org.opensingular.form.event.ISInstanceListener;
import org.opensingular.form.event.SInstanceEvent;
import org.opensingular.form.event.SInstanceEventType;
import org.opensingular.form.event.SInstanceListeners;
import org.opensingular.form.internal.PathReader;
import org.opensingular.form.io.PersistenceBuilderXML;
import org.opensingular.form.type.basic.SPackageBasic;
import org.opensingular.form.validation.ValidationError;
import org.opensingular.internal.lib.commons.xml.MElement;
import org.opensingular.lib.commons.lambda.IConsumer;
import org.opensingular.lib.commons.lambda.IFunction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

public abstract class SInstance implements SAttributeEnabled {

    private SInstance parent;

    private AttributeInstanceInfo attributeInstanceInfo;

    private SType<?> type;

    @Nullable
    private AttributeValuesManagerForSInstance attributes;

    private SDocument document;

    private Integer id;

    /**
     * Indica que a instância está no meio de uma exclusão.
     */
    private boolean removingInstance;

    /**
     * Se true, indica que o atributo é temporário e deve ser convertido para o tipo correto mais tarde.
     */
    private boolean attributeShouldMigrate;

    /**
     * Informações encontradas na persitência, mas sem correspondência no tipo na instância atual.
     */
    private List<MElement>                     unreadInfo;
    private InstanceSerializableRef<SInstance> serializableRef;
    private ISInstanceListener.EventCollector  eventCollector;

    @Nonnull
    public SType<?> getType() {
        return type;
    }

    /**
     * Retorna o documento ao qual pertence a instância atual.
     */
    @Nonnull
    public SDocument getDocument() {
        return document;
    }

    /**
     * Retorna a instância raiz da instância atual.
     *
     * @see SDocument#getRoot()
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
            if (id == null) {
                throw new SingularFormException("Id can't be read at this time");
            }
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
        return attributeInstanceInfo != null;
    }

    final void setAsAttribute(@Nonnull AttrInternalRef ref, @Nonnull SType<?> attributeOwner) {
        attributeInstanceInfo = new AttributeInstanceInfo(ref, attributeOwner);

    }

    final void setAsAttribute(@Nonnull AttrInternalRef ref, @Nonnull SInstance attributeOwner) {
        attributeInstanceInfo = new AttributeInstanceInfo(ref, attributeOwner);
    }

    /**
     * Indica se a instancia é um atributo cujo valor foi lido em carater temporário e que deve ser convertido para o
     * tipo correto quando o tipo do atributo estiver corretamente registrado no dicionário.
     */
    final void setAttributeShouldMigrate() {
        attributeShouldMigrate = true;
    }

    /**
     * Indica se a instancia é um atributo cujo valor foi lido em carater temporário e que deve ser convertido para o
     * tipo correto quando o tipo do atributo estiver corretamente registrado no dicionário.
     */
    final boolean isAttributeShouldMigrate() {
        return attributeShouldMigrate;
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

    final void setParent(SInstance parent) {
        /*
         * exceção adicionada por vinicius nunes, para adicionar uma instancia a
         * outra hierarquia deveria haver uma chamada para 'destacar' a
         * minstancia da sua hierarquia atual
         */
        if (this.parent != null && parent != null) {
            throw new SingularFormException(String.format(" Não é possível adicionar uma MIstancia criada em uma hierarquia à outra."
                    + " MInstancia adicionada a um objeto do tipo %s já pertence à outra hierarquia de MInstancia."
                    + " O pai atual é do tipo %s. ", this.getClass().getName(), this.parent.getClass().getName()), this);
        }
        this.parent = parent;
        if (parent != null && parent.isAttribute()) {
            attributeInstanceInfo = parent.attributeInstanceInfo;
        }
    }

    /**
     * Executa as inicilização de atribuição de valor da instância (ver {@link SType#withInitListener(IConsumer)}). Pode
     * sobrepor valores preexistentes.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public final void init() {
        //Não deve chamar o init se estiver no modo de leitura do XML
        if (!getDocument().isRestoreMode()) {
            ((SType) getType()).init(() -> this);
        }
    }

    final void setType(@Nonnull SType<?> type) {
        //This method must not be visible outside the package
        this.type = type;
    }

    public abstract void setValue(Object value);

    public abstract Object getValue();

    /**
     * Resolves a field instance and sets its value.
     *
     * @param value              new value
     * @param rootTypeClass      root type class
     * @param targetTypeFunction function that receives the root type and returns the field type
     * @param <RT>               root type
     * @param <RI>               root instance
     * @param <TT>               target type
     * @param <TI>               target instance
     * @param <VAL>              value
     * @throws ClassCastException     if this instance type doesn't match rootTypeClass
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
     *
     * @param rootTypeClass      root type class
     * @param targetTypeFunction function that receives the root type and returns the field type
     * @param <RT>               root type
     * @param <RI>               root instance
     * @param <TT>               target type
     * @param <TI>               target instance
     * @param <VAL>              value
     * @throws ClassCastException     if this instance type doesn't match rootTypeClass
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
     *
     * @param rootTypeClass      root type class
     * @param targetTypeFunction function that receives the root type and returns the field type
     * @param <RT>               root type
     * @param <RI>               root instance
     * @param <TT>               target type
     * @param <TI>               target instance
     * @param <VAL>              value
     * @throws ClassCastException     if this instance type doesn't match rootTypeClass
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
     *
     * @param rootTypeClass      root type class
     * @param targetTypeFunction function that receives the root type and returns the field type
     * @param <RT>               root type
     * @param <RI>               root instance
     * @param <TT>               target type
     * @param <TI>               target instance
     * @throws ClassCastException     if this instance type doesn't match rootTypeClass
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
     *
     * @param rootTypeClass      root type class
     * @param targetTypeFunction function that receives the root type and returns the field type
     * @param <RT>               root type
     * @param <RI>               root instance
     * @param <TT>               target type
     * @param <TI>               target instance
     * @throws ClassCastException     if this instance type doesn't match rootTypeClass
     * @throws NoSuchElementException if type returned by the function doesn't match a descendant type
     */
    @SuppressWarnings("unchecked")
    public <RT extends SType<RI>,
            RI extends SInstance,
            TT extends SType<TI>,
            TI extends SInstance> Optional<TI> findField(
            Class<RT> rootTypeClass,
            IFunction<RT, TT> targetTypeFunction) {

        if (!rootTypeClass.isAssignableFrom(this.getType().getClass())) {
            throw new SingularInvalidTypeException(this, rootTypeClass);
        }
        final RI rootInstance = (RI) this;
        final RT rootType     = (RT) rootInstance.getType();
        final TT targetType   = targetTypeFunction.apply(rootType);

        if (!STypes.listAscendants(targetType, true).contains(rootType)) {
            throw new SingularInvalidFieldTypeException(rootType, targetType);
        } else if (rootType == targetType) {
            return Optional.of((TI) rootInstance);
        } else if (rootInstance instanceof SIComposite) {
            return ((SIComposite) rootInstance).findDescendant(targetType);
        }
        return Optional.empty();
    }


    <V> V getValueInTheContextOf(@Nonnull CalculationContextInstanceOptional context, Class<V> resultClass) {
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

    public boolean isNotEmptyOfData(){
        return  !isEmptyOfData();
    }

    public Object getValueWithDefault() {
        return getValue((Class<?>) null);
    }

    public final <T> T getValueWithDefault(Class<T> resultClass) {
        return convert(getValueWithDefault(), resultClass);
    }

    public final <T> T getValue(@Nullable Class<T> resultClass) {
        return convert(getValue(), resultClass);
    }

    @SuppressWarnings("unchecked")
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
        SInstance  instance    = this;
        PathReader currentPath = pathReader;
        while (true) {
            if (currentPath.isEmpty()) {
                return instance.getValue(resultClass);
            }
            SInstance children = instance.getFieldLocalWithoutCreating(currentPath);
            if (children == null) {
                SFormUtil.resolveFieldType(instance.getType(), currentPath);
                return null;
            }
            instance = children;
            currentPath = currentPath.next();
        }
    }

    SInstance getFieldLocalWithoutCreating(PathReader pathReader) {
        throw new SingularFormException(pathReader.getErrorMsg(this, "Não suporta leitura de subCampos"), this);
    }

    <T> T getValueWithDefaultIfNull(PathReader pathReader, Class<T> resultClass) {
        throw new SingularFormException(erroMsgMethodUnsupported(), this);
    }

    void setValue(PathReader pathReader, Object value) {
        throw new SingularFormException(erroMsgMethodUnsupported(), this);
    }

    public SInstance getField(String path) {
        return getField(new PathReader(path));
    }

    @Nonnull
    public Optional<SInstance> getFieldOpt(String path) {
        return getFieldOpt(new PathReader(path));
    }

    /**
     * Retorna o campo cujo o nome seja igual ao do tipo informado e verifica se o campo encontrado é do mesmo tipo
     * informado. Caso não seja do mesmo tipo, dispara uma exception.
     */
    @SuppressWarnings("unchecked")
    public <II extends SInstance> II getField(SType<II> type) {
        SInstance instance = getField(type.getNameSimple());
        type.checkIfIsInstanceOf(instance);
        return (II) instance;
    }
    
    /**
     * Retorna o campo cujo o nome seja igual ao do tipo informado e verifica se o campo encontrado é do mesmo tipo
     * informado. Caso não seja do mesmo tipo, dispara uma exception.
     */
    public <II extends SISimple<T>, T extends Serializable> T getFieldValue(STypeSimple<II, T> type) {
        return getField(type).getValue();
    }

    @Nonnull
    final SInstance getField(@Nonnull PathReader pathReader) {
        SInstance instance = this;
        for (PathReader currentPath = pathReader; ; currentPath = currentPath.next()) {
            instance = instance.getFieldLocal(currentPath);
            if (currentPath.isLast()) {
                return instance;
            }
        }
    }

    @Nullable
    SInstance getFieldLocal(@Nonnull PathReader pathReader) {
        throw new SingularFormException(pathReader.getErrorMsg(this, "Não suporta leitura de subCampos"), this);
    }

    @Nonnull
    final Optional<SInstance> getFieldOpt(@Nonnull PathReader pathReader) {
        SInstance instance = this;
        for (PathReader currentPath = pathReader; ; currentPath = currentPath.next()) {
            Optional<SInstance> result = instance.getFieldLocalOpt(currentPath);
            if (!result.isPresent() || currentPath.isLast()) {
                return result;
            }
            instance = result.get();
        }
    }

    @Nonnull
    Optional<SInstance> getFieldLocalOpt(@Nonnull PathReader pathReader) {
        throw new SingularFormException(pathReader.getErrorMsg(this, "Não suporta leitura de subCampos"), this);
    }

    @Nonnull
    public List<? extends SInstance> getChildren() {
        return Collections.emptyList();
    }

    @Nonnull
    public Stream<? extends SInstance> stream() {
        return Stream.empty();
    }

    @Nonnull
    public Iterator<? extends SInstance> iterator() {
        return getChildren().iterator();
    }

    public void forEachChild(@Nonnull Consumer<? super SInstance> action) {
        getChildren().forEach(action);
    }

    public String toStringDisplayDefault() {
        return null;
    }

    public final String toStringDisplay() {
        return asAtr().getDisplayString();
    }

    @Override
    public final <V> void setAttributeValue(@Nonnull AtrRef<?, ?, V> atr, @Nullable V value) {
        getAttributesMap().setAttributeValue(atr, value);
    }

    @Override
    public void setAttributeValue(@Nonnull String attributeFullName, @Nullable String subPath, @Nullable Object value) {
        getAttributesMap().setAttributeValue(attributeFullName, subPath, value);
    }

    @Override
    public final <V> void setAttributeCalculation(@Nonnull AtrRef<?, ?, V> atr,
                                                  @Nullable SimpleValueCalculation<V> value) {
        getAttributesMap().setAttributeCalculation(atr, value);
    }

//    @Override
//    public <V> void setAttributeCalculation(@Nonnull String attributeFullName, @Nullable String subPath,
//                                            @Nullable SimpleValueCalculation<V> valueCalculation) {
//        getAttributesMap().setAttributeCalculation(attributeFullName, subPath, valueCalculation);
//    }

    final void setAttributeValueSavingForLatter(@Nonnull String attributeName, @Nullable Object value) {
        AttrInternalRef ref = getDictionary().getAttribureRefereceOrCreateLazy(attributeName);
        getAttributesMap().setAttributeValue(ref, null, value);
    }

    @Nonnull
    private AttributeValuesManagerForSInstance getAttributesMap() {
        if (attributes == null) {
            attributes = new AttributeValuesManagerForSInstance(this);
        }
        return attributes;
    }

    /**
     * Retorna a instancia do atributo se houver uma associada diretamente ao objeto atual. Não procura o atributo na
     * hierarquia.
     */
    @Nonnull
    final Optional<SInstance> getAttributeDirectly(@Nonnull String fullName) {
        return AttributeValuesManager.staticGetAttributeDirectly(this, attributes, fullName);
    }

    @Override
    public final <V> V getAttributeValue(@Nonnull String attributeFullName, @Nullable Class<V> resultClass) {
        return getAttributeValue(getDictionary().getAttributeReferenceOrException(attributeFullName), resultClass);
    }

    @Nullable
    @Override
    public final <T> T getAttributeValue(@Nonnull AtrRef<?, ?, ?> atr, @Nullable Class<T> resultClass) {
        return getAttributeValue(getDictionary().getAttributeReferenceOrException(atr), resultClass);
    }

    @Nullable
    @Override
    public final <V> V getAttributeValue(@Nonnull AtrRef<?, ?, V> atr) {
        return getAttributeValue(getDictionary().getAttributeReferenceOrException(atr), atr.getValueClass());
    }

    final boolean hasAttributeValueDirectly(@Nonnull AtrRef<?, ?, ?> atr) {
        AttrInternalRef ref = getDictionary().getAttributeReferenceOrException(atr);
        return AttributeValuesManager.staticGetAttributeDirectly(attributes, ref) != null;
    }

    @Nullable
    private <V> V getAttributeValue(@Nonnull AttrInternalRef ref, @Nullable Class<V> resultClass) {
        if (attributes != null) {
            return attributes.getAttributeValue(ref, resultClass);
        }
        return AttributeValuesManagerForSInstance.getAttributeValueFromType(this, ref, resultClass);
    }

    /**
     * Lista todos os atributos com valor associado diretamente à instância atual.
     */
    @Nonnull
    @Override
    public Collection<SInstance> getAttributes() {
        return AttributeValuesManager.staticGetAttributes(attributes);
    }

    /**
     * Looks for the best match implementation of the aspect being request.
     * <p>To understand the registration and retrieval process see {@link AspectRef}.</p>
     */
    @Nonnull
    public <T> Optional<T> getAspect(@Nonnull AspectRef<T> aspectRef) {
        return getDictionary().getMasterAspectRegistry().getAspect(this, aspectRef);
    }

    @Nullable
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
    
    /**
     * Finds the first descendant of the specified type.
     * @param targetType tipo do descendente
     * @return the first descendant of type {@code targetType}
     */
    public <A extends SInstance> Optional<A> find(@Nonnull SType<A> targetType) {
        return SInstances.findDescendant(this, targetType);
    }

    /**
     * Returns the nearest SInstance for the given type in the form SInstance tree.
     * The search is performed like described in {@link SInstances#findNearest(SInstance, SType)}
     *
     * @param targetType the SType to look for
     * @return An optional instance of the given type
     */
    public <A extends SInstance> Optional<A> findNearest(@Nonnull SType<A> targetType) {
        return SInstances.findNearest(this, targetType);
    }

    /**
     * Returns the nearest instance for the given type or throws an Exception if it is not found.
     * This method works exactly as the {@link this#findNearest(SType)}
     */
    @Nonnull
    public <A extends SInstance> A findNearestOrException(@Nonnull SType<A> targetType) {
        return findNearest(targetType).orElseThrow(() -> new SingularFormException(String.format("O tipo %s não foi encontrado", targetType.getName())));
    }

    public <A extends SInstance> Optional<A> findNearest(@Nonnull Class<? extends SType<A>> targetTypeClass) {
        return SInstances.findNearest(this, targetTypeClass);
    }


    /**
     * Returns the nearest instance value for the given type or throws an Exception if it is not found.
     * This method works exactly as the {@link this#findNearestValue(SType)}
     */
    @SuppressWarnings("unchecked")
    public <V> V findNearestValueOrException(SType<?> targetType){
        return (V) findNearestOrException(targetType).getValueWithDefault();
    }

    /**
     * Do the same search as in {@link this#findNearest(SType)} but return {@link SInstance#getValue} instead of the SInstance
     */
    @SuppressWarnings("unchecked")
    public <V> Optional<V> findNearestValue(SType<?> targetType) {
        return (Optional<V>) findNearest(targetType).map(SInstance::getValueWithDefault);
    }

    public <V> Optional<V> findNearestValue(SType<?> targetType, Class<V> valueClass) {
        return findNearest(targetType).map(it -> valueClass.cast(it.getValueWithDefault(valueClass)));
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
        return SFormUtil.generatePath(this, Objects::isNull);
    }

    public void debug() {
        MElement xml = new PersistenceBuilderXML().withPersistId(true).toXML(this);
        if (xml == null) {
            System.out.println("null");
        } else {
            xml.printTabulado();
        }
    }

    @Nonnull
    private String erroMsgMethodUnsupported() {
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
        removingInstance = true;
        onRemove();
        if (removingInstance) {
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
            ((ICompositeInstance) this).getChildren().forEach(SInstance::internalOnRemove);
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
        removingInstance = false;
    }

    public boolean hasValidationErrors() {
        return !getValidationErrors().isEmpty();
    }

    public boolean hasNestedValidationErrors() {
        return SInstances.hasAny(this, SInstance::hasValidationErrors);
    }

    public Collection<ValidationError> getValidationErrors() {
        return getDocument().getValidationErrors(getId());
    }

    public Collection<ValidationError> getNestedValidationErrors() {
        List<ValidationError> errors = new ArrayList<>();
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
        StringBuilder sb   = new StringBuilder();
        String        name = getClass().getName();
        if (name.startsWith(SInstance.class.getPackage().getName())) {
            sb.append(getClass().getSimpleName());
        } else {
            sb.append(getClass().getName());
        }
        sb.append('@').append(id);
        sb.append('(');
        sb.append("path=").append(getPathFull());
        sb.append("; type=");
        if (type != null) {
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
            listeners.add(SInstanceEventType.BEFORE_RUN_UPDATE_LISTENER, this.eventCollector);
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

    /**
     * Replaced by {@link #root()}
     */
    @Deprecated
    public SInstance getDocumentRoot() {
        return root();
    }
    public SInstance root() {
        return document.getRoot();
    }


    /**
     * Check if this types is child in any level of the candidate type class
     *
     * @param candidate the candidate
     * @return if is descendant
     */
    public boolean isDescendantOf(Class<? extends SType<?>> candidate) {
        List<SInstance> ascendants = SInstances.listAscendants(this);
        return ascendants.stream().anyMatch(parent -> parent.getType().getClass().equals(candidate));
    }

    public boolean isSameOrDescendantOf(SInstance candidate) {
        return (this == candidate) || isDescendantOf(candidate);
    }

    /**
     * Checks if the instance is of the type informed or type derived of the informed type. See more in {@link
     * SType#isTypeOf(SType)}.
     */
    public boolean isTypeOf(@Nonnull SType<?> parentTypeCandidate) {
        return getType().isTypeOf(parentTypeCandidate);
    }
}