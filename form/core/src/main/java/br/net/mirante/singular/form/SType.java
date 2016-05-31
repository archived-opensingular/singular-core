/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form;

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
import org.apache.commons.lang3.StringUtils;

import br.net.mirante.singular.form.builder.selection.SelectionBuilder;
import br.net.mirante.singular.form.calculation.SimpleValueCalculation;
import br.net.mirante.singular.form.context.UIComponentMapper;
import br.net.mirante.singular.form.document.SDocument;
import br.net.mirante.singular.form.function.IBehavior;
import br.net.mirante.singular.form.provider.SimpleProvider;
import br.net.mirante.singular.form.type.basic.SPackageBasic;
import br.net.mirante.singular.form.type.core.SPackageCore;
import br.net.mirante.singular.form.validation.IInstanceValidator;
import br.net.mirante.singular.form.validation.ValidationErrorLevel;
import br.net.mirante.singular.form.view.SView;
import br.net.mirante.singular.form.view.SViewSelectionBySelect;

@SInfoType(name = "SType", spackage = SPackageCore.class)
public class SType<I extends SInstance> extends SScopeBase implements SAttributeEnabled {

    private static final Logger LOGGER = Logger.getLogger(SType.class.getName());

    /**
     * contabiliza a quantidade de instancias desse tipo.
     */
    protected long instanceCount;

    private String nameSimple;

    private String nameFull;

    private SDictionary dictionary;

    /** Representa um identficado único de cada tipo dentro de um dicionário. */
    private int typeId;

    private SScope scope;

    private AttributeMap attributesDefined = new AttributeMap();

    private MapAttributeDefinitionResolver attributesResolved;

    private Map<IInstanceValidator<I>, ValidationErrorLevel> instanceValidators = new LinkedHashMap<>();

    private Set<SType<?>> dependentTypes;

    private Consumer<SInstance> initListener;

    private AttributeDefinitionInfo attributeDefinitionInfo;

    /**
     * Se true, representa um campo sem criar um tipo para ser reutilizado em outros pontos.
     */
    private boolean onlyAField;

    /**
     * Representa um campo que não será persistido. Se aplica somente se apenasCampo=true.
     */
    private boolean transientField;

    /**
     * Classe a  ser usada para criar as instâncias do tipo atual. Pode ser null, indicado que o tipo atual é abstrato.
     */
    private final Class<? extends I> instanceClass;

    /** Representa o tipo ao qual o tipo atual extende. Pode ou não ser da mesma classe do tipo atual. */
    private SType<I> superType;

    private SView view;

    private UIComponentMapper customMapper;

    private Consumer<SInstance> updateListener;

    /** Indica se o tipo está no meio da execução do seu método {@link #onLoadType(TypeBuilder)}. */
    private boolean callingOnLoadType;

    public SType() {
        this(null, null);
    }

    protected SType(Class<? extends I> instanceClass) {
        this(null, instanceClass);
    }

    protected SType(String simpleName, Class<? extends I> instanceClass) {
        if (simpleName == null) {
            simpleName = getInfoType().name();
            if (StringUtils.isEmpty(simpleName)) {
                simpleName = getClass().getSimpleName();
            }
        }
        SFormUtil.validateSimpleName(simpleName);
        this.nameSimple = simpleName;
        this.instanceClass = instanceClass;
        attributesResolved = new MapAttributeDefinitionResolver(this);
    }

    protected SType(String simpleName, SType<I> superType, Class<I> instanceClass) {
        this(simpleName, instanceClass);
        this.superType = superType;
    }

    /**
     * Esse método é chamado quando do primeiro registro de um classe Java que extende SType. Esse método é chamado
     * apenas uma única vez para o registro de cada classe. Se forem criadas extensões do mesmo tipo sem criar uma nova
     * classe Java, esse método não será chamado para o tipos filhos.
     *
     * @param tb Classe utilitária de apoio na configuração do tipo
     */
    protected void onLoadType(TypeBuilder tb) { }

    final SInfoType getInfoType() {
        return SFormUtil.getInfoType((Class<? extends SType<?>>) getClass());
    }

    final <TT extends SType<?>> TT extend(String simpleName) {
        if (simpleName == null) {
            simpleName = nameSimple; //Extende usando o mesmo nome do tipo pai
        } else {
            SFormUtil.validateSimpleName(simpleName);
        }
        TT newType = (TT) MapByName.newInstance(getClass());
        ((SType<I>) newType).nameSimple = simpleName;
        ((SType<I>) newType).superType = this;

        return newType;
    }

    /**
     * Metodo invocado após um tipo ser extendido, de modo, que o mesmo possa extender sub referência que possua.
     */
    protected void extendSubReference() {
    }

    @SuppressWarnings("unchecked")
    final void resolvSuperType(SDictionary dictionary) {
        if (superType == null && getClass() != SType.class) {
            Class<SType> c = (Class<SType>) getClass().getSuperclass();
            if (c != null) {
                this.superType = dictionary.getType(c);
            }
        }
    }

    @Override
    public String getName() {
        return nameFull;
    }

    public String getNameSimple() {
        return nameSimple;
    }

    public SType<I> getSuperType() {
        return superType;
    }

    public Class<I> getInstanceClass() {
        return (Class<I>) instanceClass;
    }

    private Class<I> getInstanceClassResolved() {
        if (instanceClass == null && superType != null) {
            return superType.getInstanceClassResolved();
        }
        return (Class<I>) instanceClass;
    }

    final void setScope(SScope packageScope) {
        this.scope = packageScope;
        this.nameFull = packageScope.getName() + "." + nameSimple;
    }

    @Override
    public SScope getParentScope() {
        if (scope == null) {
            throw new SingularFormException("O escopo do tipo ainda não foi configurado. \n" +
                    "Se você estiver tentando configurar o tipo no construtor do mesmo, " +
                    "dê override no método onLoadType() e mova as chamada de configuração para ele.", this);
        }
        return scope;
    }

    @Override
    public SDictionary getDictionary() {
        if (dictionary == null) {
            dictionary = getPackage().getDictionary();
        }
        return dictionary;
    }

    public boolean isSelfReference() {
        return false;
    }

    /**
     * <p> Verificar se o tipo atual é do tipo informado, diretamente ou se é um tipo extendido. Para isso percorre toda
     * a hierarquia de derivação do tipo atual verificando se encontra parentTypeCandidate na hierarquia. </p> <p> Ambos
     * o tipo tem que pertencer à mesma instância de dicionário para serem considerado compatíveis, ou seja, se dois
     * tipo forem criados em dicionário diferentes, nunca serão considerado compatíveis mesmo se proveniente da mesma
     * classe de definição. </p>
     *
     * @return true se o tipo atual for do tipo informado.
     */
    public boolean isTypeOf(SType<?> parentTypeCandidate) {
        SType<I> atual = this;
        while (atual != null) {
            if (atual == parentTypeCandidate) {
                return true;
            }
            atual = atual.superType;
        }
        return false;
    }

    /**
     * Verificar se o tipo é um tipo lista ({@link STypeList}).
     */
    public boolean isList() {
        return this instanceof STypeList;
    }

    /**
     * Verificar se o tipo é um tipo composto ({@link STypeComposite}).
     */
    public boolean isComposite() {
        return this instanceof STypeComposite;
    }

    final AttributeDefinitionInfo getAttributeDefinitionInfo() {
        return attributeDefinitionInfo;
    }

    final void setAttributeDefinitionInfo(AttributeDefinitionInfo attributeDefinitionInfo) {
        this.attributeDefinitionInfo = attributeDefinitionInfo;
    }

    final SInstance newAttributeInstanceFor(SType<?> typeToBeAppliedAttribute) {
        checkIfIsAttribute();
        SInstance attrInstance;
        if (attributeDefinitionInfo.isSelfReference()) {
            attrInstance = typeToBeAppliedAttribute.newInstance(getDictionary().getInternalDicionaryDocument());
        } else {
            attrInstance = newInstance(getDictionary().getInternalDicionaryDocument());
        }
        attrInstance.setAsAttribute(getName(), this);
        return attrInstance;
    }


    /**
     * Informa se o tipo é a definição de um atributo (de outro tipo ou de instância) ou se é um tipo comum.
     */
    public final boolean isAttribute() {
        return attributeDefinitionInfo != null;
    }

    final void checkIfIsAttribute() {
        if (!isAttribute()) {
            throw new SingularFormException("O tipo '" + getName() + "' não é um tipo atributo", this);
        }
    }

    final void addAttribute(SType<?> attributeDef) {
        attributeDef.checkIfIsAttribute();
        SType<?> owner = attributeDef.getAttributeDefinitionInfo().getOwner();
        if (owner != null && owner != this) {
            throw new SingularFormException(
                    "O Atributo '" + attributeDef.getName() + "' pertence excelusivamente ao tipo '" + owner.getName() +
                            "'. Assim não pode ser reassociado a classe '" + getName(), this);
        }

        attributesDefined.add(attributeDef);
    }

    final SType<?> getAttributeDefinedLocally(String fullName) {
        return attributesDefined.get(fullName);
    }

    final SType<?> getAttributeDefinedHierarchy(String fullName) {
        for (SType<?> current = this; current != null; current = current.superType) {
            SType<?> att = current.getAttributeDefinedLocally(fullName);
            if (att != null) {
                return att;
            }
        }
        throw new SingularFormException("Não existe atributo '" + fullName + "' em " + getName(), this);
    }

    public <MI extends SInstance> MI getAttributeInstance(AtrRef<?, MI, ?> atr) {
        Class<MI> instanceClass =
                atr.isSelfReference() ? (Class<MI>) getInstanceClassResolved() : atr.getInstanceClass();
        SInstance instance = getAttributeInstanceInternal(atr.getNameFull());
        return instanceClass.cast(instance);
    }

    final SInstance getAttributeInstanceInternal(String fullName) {
        for (SType<?> current = this; current != null; current = current.superType) {
            SInstance instance = current.attributesResolved.get(fullName);
            if (instance != null) {
                return instance;
            }
        }
        return null;
    }

    @Override
    public void setAttributeValue(String attributeFullName, String subPath, Object value) {
        SInstance instance = attributesResolved.getCreating(mapName(attributeFullName));
        if (subPath != null) {
            instance.setValue(new PathReader(subPath), value);
        } else {
            instance.setValue(value);
        }
    }

    @Override
    public <V> void setAttributeCalculation(String attributeFullName, String subPath, SimpleValueCalculation<V> valueCalculation) {
        SInstance instance = attributesResolved.getCreating(mapName(attributeFullName));
        SInstance.setValueCalculation(instance, subPath, valueCalculation);
    }

    /**
     * Lista todos os atributos com valor associado diretamente ao tipo atual. Não retorna os atributos consolidado do
     * tipo pai.
     *
     * @return Nunca null
     */
    public Collection<SInstance> getAttributes() {
        return attributesResolved.getAttributes();
    }

    /**
     * Retorna a instancia do atributo se houver uma associada diretamente ao objeto atual.
     */
    public Optional<SInstance> getAttribute(String fullName) {
        return Optional.ofNullable(attributesResolved.get(fullName));
    }

    @Override
    public final <V extends Object> V getAttributeValue(String fullName, Class<V> resultClass) {
        return getValueInTheContextOf(null, fullName, resultClass);
    }

    final <V extends Object> V getValueInTheContextOf(SInstance contextInstance, String fullName, Class<V> resultClass) {
        fullName = mapName(fullName);
        SInstance instance = getAttributeInstanceInternal(fullName);
        if (instance != null) {
            if (contextInstance != null) {
                return instance.getValueInTheContextOf(contextInstance, resultClass);
            } else if (resultClass == null) {
                return (V) instance.getValue();
            } else {
                return instance.getValueWithDefault(resultClass);
            }
        }
        SType<?> atr = getAttributeDefinedHierarchy(fullName);
        if (resultClass == null) {
            return (V) atr.getAttributeValueOrDefaultValueIfNull();
        }
        return atr.getAttributeValueOrDefaultValueIfNull(resultClass);
    }

    private String mapName(String originalName) {
        if (originalName.indexOf('.') == -1) {
            return getName() + '.' + originalName;
        }
        return originalName;
    }

    public SType<I> with(AtrRef<?, ?, ? extends Object> attribute, Object value) {
        setAttributeValue((AtrRef<?, ?, Object>) attribute, value);
        return this;
    }

    public SType<I> with(String attributePath, Object value) {
        setAttributeValue(attributePath, value);
        return this;
    }

    public SType<I> with(String valuesExpression) {
        throw new NotImplementedException("Este tipo não implementa o método `with`");
    }

    public SType<I> withCode(String fieldPath, IBehavior<I> behavior) {
        throw new NotImplementedException("Este tipo não implementa o método `withCode`");
    }

    public SType<I> withInitialValue(Object value) {
        return with(SPackageBasic.ATR_VALOR_INICIAL, value);

    }

    public SType<I> withDefaultValueIfNull(Object value) {
        return with(SPackageBasic.ATR_DEFAULT_IF_NULL, value);
    }

    public Object getAttributeValueOrDefaultValueIfNull() {
        if (Objects.equals(nameSimple, SPackageBasic.ATR_DEFAULT_IF_NULL.getNameSimple())) {
            return null;
        }
        return getAttributeValue(SPackageBasic.ATR_DEFAULT_IF_NULL);
    }

    public <V extends Object> V getAttributeValueOrDefaultValueIfNull(Class<V> resultClass) {
        if (Objects.equals(nameSimple, SPackageBasic.ATR_DEFAULT_IF_NULL.getNameSimple())) {
            return null;
        }
        return getAttributeValue(SPackageBasic.ATR_DEFAULT_IF_NULL, resultClass);
    }

    public Object getAttributeValueInitialValue() {
        return getAttributeValue(SPackageBasic.ATR_VALOR_INICIAL);
    }

    public SType<I> withRequired(Boolean value) {
        return with(SPackageBasic.ATR_REQUIRED, value);
    }

    public final Boolean isRequired() {
        return getAttributeValue(SPackageBasic.ATR_REQUIRED);
    }

    public SType<I> withExists(Boolean value) {
        return with(SPackageBasic.ATR_EXISTS, value);
    }

    public SType<I> withExists(Predicate<I> predicate) {
        return with(SPackageBasic.ATR_EXISTS_FUNCTION, predicate);
    }

    public final boolean exists() {
        return !Boolean.FALSE.equals(getAttributeValue(SPackageBasic.ATR_EXISTS));
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
    public <T> T as(Class<T> targetClass) {
        if (STranslatorForAttribute.class.isAssignableFrom(targetClass)) {
            return (T) STranslatorForAttribute.of(this, (Class<STranslatorForAttribute>) targetClass);
        }
        throw new SingularFormException("Classe '" + targetClass + "' não funciona como aspecto", this);
    }

    @Override
    public <T> T as(Function<SAttributeEnabled, T> aspectFactory) {
        return aspectFactory.apply(this);
    }

    /**
     * Faz cast do tipo atual para a classe da variável que espera receber o valor ou dispara ClassCastException se o
     * destino não for compatível. É um método de conveniência para a interface fluente de construção do tipo, mas com o
     * porem de acusar o erro de cast apenas em tempo de execução.
     */
    public <T> T cast() {
        // TODO (from Daniel) decidir se esse método fica. Quem que devater o
        // assunto?
        return (T) this;
    }

    public final <T extends SView> SType<I> withView(Supplier<T> factory) {
        withView(factory.get());
        return this;
    }

    @SafeVarargs
    public final <T extends SView> SType<I> withView(T mView, Consumer<T>... initializers) {
        for (Consumer<T> initializer : initializers) {
            initializer.accept(mView);
        }
        setView(mView);
        return this;
    }

    public SType<I> withUpdateListener(Consumer<I> consumer) {
        this.updateListener = (Consumer<SInstance>) consumer;
        return this;
    }

    public final <T extends SView> T setView(Supplier<T> factory) {
        T v = factory.get();
        setView(v);
        return v;
    }

    private void setView(SView view) {
        if (view.isApplicableFor(this)) {
            this.view = view;
        } else {
            throw new SingularFormException("A view '" + view.getClass().getName() + "' não é aplicável ao tipo", this);
        }
    }

    public SView getView() {
        return this.view;
    }

    public Set<SType<?>> getDependentTypes() {
        if (dependentTypes == null) dependentTypes = new LinkedHashSet<>();
        return dependentTypes;
    }

    public boolean hasDependentTypes() {
        return (dependentTypes != null) && (!dependentTypes.isEmpty());
    }

    public boolean dependsOnAnyType() {
        return Optional.ofNullable(getAttributeValue(SPackageBasic.ATR_DEPENDS_ON_FUNCTION)).map(Supplier::get).map(
                it -> !it.isEmpty()).orElse(false);
    }

    public boolean dependsOnAnyTypeInHierarchy() {
        return STypes.listAscendants(this, true).stream().anyMatch(SType::dependsOnAnyType);
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
    public I castInstance(SInstance instance) {
        // TODO verificar se essa é a verificação correta
        if (instance.getType() != this) throw new IllegalArgumentException(
                "A instância " + instance + " não é do tipo " + this);
        return (I) instance;
    }

    public final I newInstance() {
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

    public SIList<?> newList() {
        return SIList.of(this);
    }

    private I newInstance(SType<?> original, SDocument owner) {
        Class<? extends I> c = instanceClass;
        if (c == null && superType != null) {
            return superType.newInstance(original, owner);
        }
        if (instanceClass == null) {
            throw new SingularFormException(
                    "O tipo '" + original.getName() + (original == this ? "" : "' que é do tipo '" + getName()) +
                            "' não pode ser instanciado por esse ser abstrato (classeInstancia==null)", this);
        }
        try {
            I newInstance = instanceClass.newInstance();
            newInstance.setDocument(owner);
            newInstance.setType(this);
            if (newInstance instanceof SISimple) {
                Object valorInicial = original.getAttributeValueInitialValue();
                if (valorInicial != null) {
                    newInstance.setValue(valorInicial);
                }
            }
            instanceCount++;

            //            init(newInstance);

            return newInstance;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new SingularFormException(
                    "Erro instanciando o tipo '" + getName() + "' para o tipo '" + original.getName() + "'", e);
        }
    }

    /**
     * Run initialization code for new created instance.
     *
     * @param newInstance Instance recently created.
     */
    public void init(I newInstance) {
        if (initListener != null) {
            initListener.accept(newInstance);
        }
    }

    /**
     * Verifica se a instância informada é do tipo atual, senão dispara exception.
     */
    public void checkIfIsInstanceOf(SInstance instance) {
        if (getDictionary() != instance.getDictionary()) {
            throw new SingularFormException("O dicionário da instância " + instance + " não é o mesmo do tipo " + this +
                    ". Foram carregados em separado", instance);
        }
        for (SType<?> current = instance.getType(); current != null; current = current.getSuperType()) {
            if (current == this) {
                return;
            }
        }
        throw new SingularFormException("A instância " + instance + " não é do tipo " + this, instance);
    }

    @Override
    public void debug(int level) {
        debug(System.out, level);
    }

    @Override
    public void debug(Appendable appendable, int level) {
        try {
            SType<?> at = this.isAttribute() ? this : null;
            pad(appendable, level).append(at == null ? "def " : "defAtt ");
            appendable.append(getNameSimple());
            if (at != null) {
                SType<?> owner = at.getAttributeDefinitionInfo().getOwner();
                if (owner != null && owner != at.getParentScope()) {
                    appendable.append(" for ").append(suppressPackage(owner.getName()));
                }
            }
            if (at == null) {
                if (superType == null || superType.getClass() != getClass()) {
                    appendable.append(" (").append(getClass().getSimpleName());
                    if (instanceClass != null && (superType == null || !instanceClass.equals(superType.instanceClass))) {
                        appendable.append(":").append(instanceClass.getSimpleName());
                    }
                    appendable.append(")");
                }
            } else if (at.isSelfReference()) {
                appendable.append(" (SELF)");
            }
            if (superType != null && (at == null || !at.isSelfReference())) {
                appendable.append(" extend ").append(suppressPackage(superType.getName()));
                if (this instanceof STypeList) {
                    STypeList<?, ?> lista = (STypeList<?, ?>) this;
                    if (lista.getElementsType() != null) {
                        appendable.append(" of ").append(suppressPackage(lista.getElementsType().getName()));
                    }
                }
            }
            debugAttributes(appendable, level);
            appendable.append("\n");

            if (this instanceof STypeSimple && asAtrProvider().getProvider() != null) {
                pad(appendable, level + 2).append("selection of ").append(asAtrProvider().getProvider().toString())
                        .append("\n");
            }

            attributesDefined.getAttributes().stream().filter(att -> !getLocalTypeOptional(att.getNameSimple())
                    .isPresent()).forEach(att -> {
                try {
                    pad(appendable, level + 1).append("att ").append("\n").append(suppressPackage(att.getName()))
                            .append(":").append(suppressPackage(att.getSuperType().getName())).append(
                            att.isSelfReference() ? " SELF" : "");
                } catch (IOException ex) {
                    LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
                }
            });

            super.debug(appendable, level + 1);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    private void debugAttributes(Appendable appendable, int nivel) {
        try {
            Collection<SInstance> attrs = getAttributes();
            if (attrs.size() != 0) {
                appendable.append(" {");
                attrs.stream().forEach(attr -> {
                    try {
                        appendable.append(suppressPackage(attr.getAttributeInstanceInfo().getName(), true)).append("=")
                                .append(attr.toStringDisplay()).append("; ");
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

    private String suppressPackage(String name) {
        return suppressPackage(name, false);
    }

    private String suppressPackage(String name, boolean aggressive) {
        if (isEqualsStart(name, getName())) {
            return name.substring(getName().length() + 1);
        } else if (isEqualsStart(name, getParentScope().getName())) {
            return name.substring(getParentScope().getName().length() + 1);
        } else if (isEqualsStart(name, SPackageCore.NAME)) {
            String v = name.substring(SPackageCore.NAME.length() + 1);
            if (aggressive) {
                if (isEqualsStart(v, "SType")) {
                    v = v.substring(6);
                }
            }
            return v;
        } else if (aggressive) {
            if (isEqualsStart(name, SPackageBasic.NAME)) {
                return name.substring(SPackageBasic.NAME.length() + 1);
            }
        }
        return name;
    }

    private static boolean isEqualsStart(String name, String prefixo) {
        return name.startsWith(prefixo) && name.length() > prefixo.length() && name.charAt(prefixo.length()) == '.';
    }

    public <T extends Object> T convert(Object value, Class<T> resultClass) {
        throw new RuntimeException("Método não suportado");
    }

    public boolean hasValidation() {
        return isRequired() ||
                getAttributeValue(SPackageBasic.ATR_OBRIGATORIO_FUNCTION) != null ||
                !instanceValidators.isEmpty();
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

    public Consumer<SInstance> getUpdateListener() {
        return updateListener;
    }

    public void withInitListener(Consumer<SInstance> initListener) {
        this.initListener = initListener;
    }

    public SelectionBuilder typelessSelection() {
        this.setView(SViewSelectionBySelect::new);
        return new SelectionBuilder(this);
    }

    public void withSelectionFromProvider(Class<SimpleProvider> providerClass) {
        this.typelessSelection().selfIdAndDisplay().provider(providerClass);
    }

    public void withSelectionFromProvider(String providerName) {
        this.typelessSelection().selfIdAndDisplay().provider(providerName);
    }

    final void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    @Override
    public String toString() {
        return nameFull + '(' + getClass().getSimpleName() + '@' + typeId + ')';
    }

    final void setCallingOnLoadType(boolean callingOnLoadType) {
        this.callingOnLoadType = callingOnLoadType;
    }

    final boolean isCallingOnLoadType() {
        return callingOnLoadType;
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this;
    }

    @Override
    public int hashCode() {
        return typeId;
    }
}