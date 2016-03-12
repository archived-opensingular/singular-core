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
import br.net.mirante.singular.form.mform.basic.view.SView;
import br.net.mirante.singular.form.mform.context.UIComponentMapper;
import br.net.mirante.singular.form.mform.core.AtrCore;
import br.net.mirante.singular.form.mform.core.SPackageCore;
import br.net.mirante.singular.form.mform.document.SDocument;
import br.net.mirante.singular.form.mform.function.IBehavior;
import br.net.mirante.singular.form.mform.options.SOptionsProvider;
import br.net.mirante.singular.form.validation.IInstanceValidator;
import br.net.mirante.singular.form.validation.ValidationErrorLevel;

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

    private SScope scope;

    private AttributeMap attributesDefined = new AttributeMap();

    private MapAttributeDefinitionResolver attributesResolved;

    private Map<IInstanceValidator<I>, ValidationErrorLevel> instanceValidators = new LinkedHashMap<>();

    private Set<SType<?>> dependentTypes;

    /**
     * Se true, representa um campo sem criar um tipo para ser reutilizado em
     * outros pontos.
     */
    private boolean onlyAField;

    /**
     * Representa um campo que não será persistido. Se aplica somente se
     * apenasCampo=true.
     */
    private boolean transientField;

    private Class<SType> superTypeClass;

    private final Class<? extends I> instanceClass;

    private SType<I> superType;

    private SView view;

    private UIComponentMapper customMapper;

    public SType() {
        this(null, (Class<SType>) null, null);
    }

    protected SType(Class<? extends I> instanceClass) {
        this(null, (Class<SType>) null, instanceClass);
    }

    protected SType(String simpleName, Class<SType> superTypeClass, Class<? extends I> instanceClass) {
        if (simpleName == null) {
            simpleName = getInfoType().name();
        }
        SFormUtil.validateSimpleName(simpleName);
        this.nameSimple = simpleName;
        this.superTypeClass = superTypeClass;
        this.instanceClass = instanceClass;
        attributesResolved = new MapAttributeDefinitionResolver(this);
    }

    protected SType(String simpleName, SType<I> superType, Class<I> instanceClass) {
        this(simpleName, (Class<SType>) (superType == null ? null : superType.getClass()), instanceClass);
        this.superType = superType;
    }

    protected void onLoadType(TypeBuilder tb) {
    }

    final SInfoType getInfoType() {
        return SDictionary.getInfoType(getClass());
    }

    private final <TT extends SType<I>> TypeBuilder extend(String simpleName, Class<TT> parentClass) {
        SFormUtil.validateSimpleName(simpleName);
        if (!parentClass.equals(getClass())) {
            throw new RuntimeException("Erro Interno");
        }
        TypeBuilder tb = new TypeBuilder(parentClass);
        ((SType<I>) tb.getType()).nameSimple = simpleName;
        ((SType<I>) tb.getType()).superType = this;
        return tb;
    }

    final <TT extends SType<?>> TypeBuilder extend(String simpleName) {
        return (TypeBuilder) extend(simpleName, getClass());
    }

    @SuppressWarnings("unchecked")
    final void resolvSuperType(SDictionary dictionary) {
        if (superType != null || getClass() == SType.class) {
            return;
        }
        Class<SType> c = (Class<SType>) getClass().getSuperclass();
        if (c != null) {
            this.superType = dictionary.getType(c);
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
            throw new SingularFormException(
                    "O escopo do tipo ainda não foi configurado. \n" + "Se você estiver tentando configurar o tipo no construtor do mesmo, "
                            + "dê override no método onLoadType() e mova as chamada de configuração para ele.");
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
            atual = atual.superType;
        }
        return false;
    }

    final void addAttribute(SAttribute attribute) {
        if (attribute.getOwnerType() != null && attribute.getOwnerType() != this) {
            throw new SingularFormException("O Atributo '" + attribute.getName() + "' pertence excelusivamente ao tipo '"
                    + attribute.getOwnerType().getName() + "'. Assim não pode ser reassociado a classe '" + getName());
        }

        attributesDefined.add(attribute);
    }

    final SAttribute getAttributeDefinedLocally(String fullName) {
        return attributesDefined.get(fullName);
    }

    final SAttribute getAttributeDefinedHierarchy(String fullName) {
        for (SType<?> current = this; current != null; current = current.superType) {
            SAttribute att = current.getAttributeDefinedLocally(fullName);
            if (att != null) {
                return att;
            }
        }
        throw new SingularFormException("Não existe atributo '" + fullName + "' em " + getName());
    }

    public <MI extends SInstance> MI getAttributeInstance(AtrRef<?, MI, ?> atr) {
        Class<MI> instanceClass = atr.isSelfReference() ? (Class<MI>) getInstanceClassResolved() : atr.getInstanceClass();
        SInstance instancia = getAttributeInstanceInternal(atr.getNameFull());
        return instanceClass.cast(instancia);
    }

    final SInstance getAttributeInstanceInternal(String fullName) {
        for (SType<?> current = this; current != null; current = current.superType) {
            SInstance instancia = current.attributesResolved.get(fullName);
            if (instancia != null) {
                return instancia;
            }
        }
        return null;
    }

    @Override
    public void setAttributeValue(String attributeName, String subPath, Object value) {
        SInstance instancia = attributesResolved.getCreating(mapName(attributeName));
        if (subPath != null) {
            instancia.setValue(new PathReader(subPath), value);
        } else {
            instancia.setValue(value);
        }
    }

    @Override
    public <V extends Object> V getAttributeValue(String fullName, Class<V> resultClass) {
        fullName = mapName(fullName);
        SInstance instance = getAttributeInstanceInternal(fullName);
        if (instance != null) {
            return (resultClass == null) ? (V) instance.getValue() : instance.getValueWithDefault(resultClass);
        }
        SAttribute atr = getAttributeDefinedHierarchy(fullName);
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
        return with(SPackageCore.ATR_VALOR_INICIAL, value);

    }

    public SType<I> withDefaultValueIfNull(Object value) {
        return with(SPackageCore.ATR_DEFAULT_IF_NULL, value);
    }

    public Object getAttributeValueOrDefaultValueIfNull() {
        if (Objects.equals(nameSimple, SPackageCore.ATR_DEFAULT_IF_NULL.getNameSimple())) {
            return null;
        }
        return getAttributeValue(SPackageCore.ATR_DEFAULT_IF_NULL);
    }

    public <V extends Object> V getAttributeValueOrDefaultValueIfNull(Class<V> resultClass) {
        if (Objects.equals(nameSimple, SPackageCore.ATR_DEFAULT_IF_NULL.getNameSimple())) {
            return null;
        }
        return getAttributeValue(SPackageCore.ATR_DEFAULT_IF_NULL, resultClass);
    }

    public Object getAttributeValueInitialValue() {
        return getAttributeValue(SPackageCore.ATR_VALOR_INICIAL);
    }

    public SType<I> withRequired(Boolean value) {
        return with(SPackageCore.ATR_REQUIRED, value);
    }

    public final Boolean isRequired() {
        return getAttributeValue(SPackageCore.ATR_REQUIRED);
    }

    public SType<I> withExists(Boolean value) {
        return with(SPackageCore.ATR_EXISTS, value);
    }

    public SType<I> withExists(Predicate<I> predicate) {
        return with(SPackageCore.ATR_EXISTS_FUNCTION, predicate);
    }

    public final boolean exists() {
        return !Boolean.FALSE.equals(getAttributeValue(SPackageCore.ATR_EXISTS));
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
        throw new SingularFormException("Classe '" + targetClass + "' não funciona como aspecto");
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

    public final <T extends SView> T setView(Supplier<T> factory) {
        T v = factory.get();
        setView(v);
        return v;
    }

    private void setView(SView view) {
        if (view.isApplicableFor(this)) {
            this.view = view;
        } else {
            throw new SingularFormException(
                    "A view '" + view.getClass().getName() + "' não é aplicável ao tipo: '" + getClass().getName() + "'");
        }
    }

    public SView getView() {
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
        return Optional.ofNullable(getAttributeValue(SPackageBasic.ATR_DEPENDS_ON_FUNCTION))
                .map(Supplier::get)
                .map(it -> !it.isEmpty())
                .orElse(false);
    }

    public boolean dependsOnAnyTypeInHierarchy() {
        return STypes.listAscendants(this, true).stream()
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
    public I castInstance(SInstance instance) {
        // TODO verificar se essa é a verificação correta
        if (instance.getType() != this)
            throw new IllegalArgumentException("A instância " + instance + " não é do tipo " + this);
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
            throw new SingularFormException("O tipo '" + original.getName() + (original == this ? "" : "' que é do tipo '" + getName())
                    + "' não pode ser instanciado por esse ser abstrato (classeInstancia==null)");
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
            return newInstance;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new SingularFormException("Erro instanciando o tipo '" + getName() + "' para o tipo '" + original.getName() + "'", e);
        }
    }

    @Override
    public void debug(int nivel) {
        debug(System.out, nivel);
    }

    @Override
    public void debug(Appendable appendable, int level) {
        try {
            SAttribute at = this instanceof SAttribute ? (SAttribute) this : null;
            pad(appendable, level).append(at == null ? "def " : "defAtt ");
            appendable.append(getNameSimple());
            if (at != null) {
                if (at.getOwnerType() != null && at.getOwnerType() != at.getParentScope()) {
                    appendable.append(" for ").append(suppressPackage(at.getOwnerType().getName()));
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

            if (this instanceof STypeSimple && ((STypeSimple<?, ?>) this).getOptionsProvider() != null) {
                pad(appendable, level + 2).append("selection of ").append(((STypeSimple<?, ?>) this).getOptionsProvider().toDebug())
                        .append("\n");
            }

            attributesDefined
                    .getAttributes()
                    .stream()
                    .filter(att -> !getLocalTypeOptional(att.getNameSimple()).isPresent())
                    .forEach(att -> {
                        try {
                            pad(appendable, level + 1)
                                    .append("att ")
                                    .append("\n")
                                    .append(suppressPackage(att.getName()))
                                    .append(":")
                                    .append(suppressPackage(att.getSuperType().getName()))
                                    .append(att.isSelfReference() ? " SELF" : "");
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
            Map<String, SInstance> vals = attributesResolved.getAttributes();
            if (vals.size() != 0) {
                appendable.append(" {");
                vals.entrySet().stream().forEach(e -> {
                    try {
                        appendable.append(suppressPackage(e.getKey(), true))
                                .append("=")
                                .append(e.getValue().toStringDisplay())
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

    private String suppressPackage(String name) {
        return suppressPackage(name, false);
    }

    private String suppressPackage(String name, boolean aggressive) {
        if (isEqualsStart(name, getName())) {
            return name.substring(getName().length() + 1);
        } else if (isEqualsStart(name, scope.getName())) {
            return name.substring(scope.getName().length() + 1);
        } else if (isEqualsStart(name, SPackageCore.NOME)) {
            String v = name.substring(SPackageCore.NOME.length() + 1);
            if (aggressive) {
                if (isEqualsStart(v, "SType")) {
                    v = v.substring(6);
                }
            }
            return v;
        } else if (aggressive) {
            if (isEqualsStart(name, SPackageBasic.NOME)) {
                return name.substring(SPackageBasic.NOME.length() + 1);
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
        return isRequired() || !instanceValidators.isEmpty();
    }

    public SOptionsProvider getOptionsProvider() {
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
