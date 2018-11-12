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

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang3.StringUtils;
import org.opensingular.form.internal.PathReader;
import org.opensingular.form.processor.ClassInspectionCache;
import org.opensingular.form.processor.ClassInspectionCache.CacheKey;
import org.opensingular.form.type.core.SPackageBootstrap;
import org.opensingular.form.type.core.SPackagePersistence;
import org.opensingular.form.type.country.brazil.SPackageCountryBrazil;
import org.opensingular.form.type.util.SPackageUtil;
import org.opensingular.internal.lib.commons.injection.SingularInjector;
import org.opensingular.lib.commons.context.ServiceRegistry;
import org.opensingular.lib.commons.context.ServiceRegistryLocator;
import org.opensingular.lib.commons.internal.function.SupplierUtil;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

public final class SFormUtil {

    private static Supplier<Map<String, Class<? extends SPackage>>> singularPackages;

    private SFormUtil() {
    }

    /**
     * Executa o update listener dos tipos depentens da instancia informada, sendo chamada recursivamente para os tipos
     * que foram atualizados.
     * <p>
     * Motivação: Tendo um tipo composto com tres tipos filhos (a,b e c),
     * onde "b" é dependente de "a" e "c" é dependente de "b", "b" possui update listener que modifica o seu valor,
     * e "c" será visivel se o valor de "b" não for nulo.  Ao atualizar "a" é necessario executar o listener dos seus
     * tipos dependentes("b") e também dos tipos dependentes do seu dependente("c") para que a avaliação de visibilidade
     * seja avaliada corretamente.
     *
     * @param i the instance from which all dependents types must be notified
     * @return List of dependants SInstances
     */
    public static Iterable<SInstance> evaluateUpdateListeners(SInstance i) {
        return SingularFormProcessing.evaluateUpdateListeners(i);
    }

    private static boolean isNotValidSimpleName(@Nullable String name) {
        if (name == null || name.length() == 0 || !isLetter(name.charAt(0))) {
            return true;
        }
        for (int i = name.length() - 1; i != 0; i--) {
            char c = name.charAt(i);
            if (!isLetter(c) && !isDigit(c)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    static boolean isLetter(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';  //NOSONAR
    }

    /** Verifies if the name is a valid for a simple name otherwise throws a exception. */
    @Nonnull
    public static String validateSimpleName(@Nonnull String name) {
        if (isNotValidSimpleName(name)) {
            throw new SingularFormException('\'' + name + "' não é um nome válido para pacote, tipo ou atributo");
        }
        return name;
    }

    @Nonnull
    static String validatePackageName(@Nonnull String name) {
        Objects.requireNonNull(name);
        int pos = name.lastIndexOf('.');
        if (pos == -1) {
            return validateSimpleName(name);
        } else if (!isValidFullPackageName(name)) {
            throw new SingularFormException('\'' + name + "' não é um nome válido para um pacote");
        }
        return name.substring(pos + 1);
    }

    private static boolean isValidFullPackageName(String name) {
        boolean waitingBegin = true;
        for (int i = 0, max = name.length(); i < max; i++) {
            char c = name.charAt(i);
            if (waitingBegin) {
                if (!isLetter(c)) {
                    return false;
                }
                waitingBegin = false;
            } else {
                if (c == '.') {
                    waitingBegin = true;
                } else if (!isLetter(c) && !isDigit(c) && !isSpecialCharacter(c)) {
                    return false;
                }
            }
        }
        return !waitingBegin; //Can't end after a dot or have length zero
    }

    private static boolean isSpecialCharacter(char c) {
        return c == '$';
    }

    /** Checks if the instance is in the same dictionary of scope. If not, throws a exception. */
    static void verifySameDictionary(@Nonnull SScope scope, @Nonnull SInstance instance) {
        if (scope.getDictionary() != instance.getDictionary()) {
            throw new SingularFormException(
                    "O dicionário da instância " + instance + " não é o mesmo dicionário do tipo " + scope +
                            ". Foram carregados em separado", instance);
        }
    }

    /** Checks if both scopes are using the same dictionary. If not, throws a exception. */
    static void verifySameDictionary(@Nonnull SScope scope1, @Nonnull SScope scope2) {
        if (scope1.getDictionary() != scope2.getDictionary()) {
            throw new SingularFormException(scope2.getName() + "(" + scope2.getClass().getName() +
                    ") foi criado em outro dicionário, que não o de " + scope1.getName() + "(" +
                    scope1.getClass().getName() + ")");
        }
    }

    /** Finds the common parent type of the two types. */
    @Nonnull
    static SType<?> findCommonType(@Nonnull SType<?> type1, @Nonnull SType<?> type2) {
        verifySameDictionary(type1, type2);
        for (SType<?> current = type1; ; current = current.getSuperType()) {
            if (type2.isTypeOf(current)) {
                return current;
            }
        }
    }

    @Nonnull
    static SimpleName resolveName(@Nullable SimpleName simpleName, @Nonnull SType<?> type) {
        return simpleName == null ? type.getNameSimpleObj() : simpleName;
    }

    static SType<?> resolveFieldType(SType<?> type, PathReader pathReader) {
        SType<?> currentType = type;
        PathReader currentPathReader = pathReader;
        while (!currentPathReader.isEmpty()) {
            currentType = resolveFieldTypeInternal(currentType, currentPathReader);
            currentPathReader = currentPathReader.next();
        }
        return currentType;
    }

    private static SType<?> resolveFieldTypeInternal(@Nonnull SType<?> type, PathReader pathReader) {
        if (type.isComposite()) {
            if (pathReader.isIndex()) {
                throw new SingularFormException(pathReader.getErrorMsg(type, "Índice de lista não se aplica a um tipo composto"), type);
            }
            String token = pathReader.getToken();
            SType<?> field = ((STypeComposite<?>) type).getField(token);
            if (field == null) {
                throw new SingularFormException(pathReader.getErrorMsg(type, "Não existe o campo '" + token + '\''), type);
            }
            return field;
        } else if (type.isList()) {
            if (pathReader.isIndex()) {
                return ((STypeList<?, ?>) type).getElementsType();
            }
            throw new SingularFormException(pathReader.getErrorMsg(type, "Não se aplica a um tipo lista"), type);
        } else if (type instanceof STypeSimple) {
            throw new SingularFormException(pathReader.getErrorMsg(type, "Não se aplica um path a um tipo simples"), type);
        } else {
            throw new SingularFormException(pathReader.getErrorMsg(type, "Não implementado para " + type.getClass()), type);
        }
    }

    /**
     * Retorna o nome do filho atual indo em direção ao raiz mas parando segundo
     * a condicão de parada informada.
     */
    @Nullable
    public static String generatePath(@Nonnull SInstance instance, @Nonnull Predicate<SInstance> stopCondition) {
        SInstance current = instance;
        List<SInstance> sequencia = null;
        while (!stopCondition.test(current)) {
            if (sequencia == null) {
                sequencia = new ArrayList<>();
            }
            sequencia.add(current);
            current = current.getParent();
        }
        if (sequencia == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = sequencia.size() - 1; i != -1; i--) {
            current = sequencia.get(i);
            if (current.getParent() instanceof SIList) {
                int pos = ((SIList<?>) current.getParent()).indexOf(current);
                if (pos == -1) {
                    throw new SingularFormException(current.getName() + " não é mais filho de " + current.getParent().getName());
                }
                sb.append('[').append(pos).append(']');
            } else {
                if (current.getParent() != null && sb.length() != 0) {
                    sb.append('.');
                }
                sb.append(current.getName());
            }
        }
        return sb.toString();
    }

    /**
     * Retorna o nome do filho atual indo em direção ao raiz mas parando segundo
     * a condicão de parada informada.
     */
    @Nullable
    public static String generatePath(@Nonnull SType<?> type, @Nonnull Predicate<SType<?>> stopCondition) {
        return generatePath(type, stopCondition, ' ', '.', SType::getNameSimple);
    }

    /**
     * Retorna o nome do filho atual indo em direção ao raiz mas parando segundo
     * a condicão de parada informada.
     */
    @Nullable
    public static String generatePath(@Nonnull SType<?> type, @Nonnull Predicate<SType<?>> stopCondition,
            char rootPrefix, char separator, Function<SType<?>, String> nameFunc) {
        SType<?> current = type;
        List<SType<?>> sequencia = null;
        while (!stopCondition.test(current)) {
            if (sequencia == null) {
                sequencia = new ArrayList<>();
            }
            sequencia.add(current);
            current = current.getParent().orElse(null);
        }
        if (sequencia == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = sequencia.size() - 1; i != -1; i--) {
            current = sequencia.get(i);
            if (sb.length() == 0) {
                if (rootPrefix != ' ' && !current.getParent().isPresent()) {
                    sb.append(rootPrefix);
                }
            } else if (sb.length() != 0) {
                sb.append(separator);
            }
            String n = nameFunc.apply(current);
            sb.append(n);
        }
        return sb.toString();
    }

    public static String generateUserFriendlyName(String simpleName) {
        final Pattern lowerUpper = Pattern.compile("(.*?[a-z])([A-Z].*?)");
        final Pattern abbreviationPrefix = Pattern.compile("([A-Z]+)([A-Z][a-z])");
        final ImmutableSet<String> upperCaseSpecialCases = ImmutableSet.of("id", "url");

        return StringUtils.capitalize(Stream.of(simpleName)
                .map(s -> lowerUpper.matcher(s).replaceAll("$1-$2"))
                .map(s -> abbreviationPrefix.matcher(s).replaceAll("$1-$2"))
                .flatMap(s -> Stream.<String>of(s.split("[-_]+")))
                .map(s -> (StringUtils.isAllUpperCase(s)) ? s : StringUtils.uncapitalize(s))
                .map(s -> upperCaseSpecialCases.contains(s) ? StringUtils.capitalize(s) : s)
                .collect(joining(" ")));
    }

    public static String generateUserFriendlyPath(SInstance instance) {
        return generateUserFriendlyPath(instance, null);
    }

    public static String generateUserFriendlyPath(SInstance instance, SInstance parentContext) {
        LinkedList<String> labels = new LinkedList<>();
        SInstance child = null;
        for (SInstance node = instance; (node != null) && (node != parentContext); child = node, node = node.getParent()) {

            final String labelNode = node.asAtr().getLabel();

            if (node instanceof SIList<?>) {
                SIList<?> list = (SIList<?>) node;
                String listLabel = Optional.ofNullable(list.asAtr().getLabel()).orElse(list.getType().getNameSimple());
                int index = list.indexOf(child) + 1;
                labels.add(listLabel + ((index > 0) ? " [" + (index) + ']' : ""));
            } else {
                if (StringUtils.isNotBlank(labelNode)) {
                    labels.add(labelNode);
                }
            }
        }
        Collections.reverse(labels);

        if (!labels.isEmpty()) {
            return StringUtils.join(labels, " > ");
        }
        return null;
    }

    /**
     * Retorna o nome completo do tipo sem precisar carregar a definição
     * mediante a leitura das anotações {@link SInfoType} e {@link SInfoPackage}.
     */
    @Nonnull
    public static String getTypeName(@Nonnull Class<? extends SType> typeClass) {
        return ClassInspectionCache.getInfo(typeClass, CacheKey.FULL_NAME, SFormUtil::getTypeNameInternal);
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    private static String getTypeNameInternal(@Nonnull Class<?> typeClass) {
        return getTypePackageName(typeClass) + '.' + getTypeSimpleName((Class<? extends SType<?>>) typeClass);
    }

    /** Calculates the package name for the type class. */
    @Nonnull
    public static String getTypePackageName(@Nonnull Class<?> typeClass) {
        return ClassInspectionCache.getInfo(typeClass, CacheKey.PACKAGE_NAME, SFormUtil::getTypePackageNameInternal);
    }

    @Nonnull
    private static String getTypePackageNameInternal(@Nonnull Class<?> typeClass) {
        Optional<Class<? extends SPackage>> packageClass = getTypePackageClass((Class<? extends SType<?>>) typeClass);
        if (packageClass.isPresent()) {
            return getInfoPackageName(packageClass.get());
        }
        return typeClass.getPackage().getName();
    }

    @Nonnull
    public static <T extends SType<?>> SimpleName getTypeSimpleName(Class<T> typeClass) {
        return ClassInspectionCache.getInfo(typeClass, CacheKey.SIMPLE_NAME,
                SFormUtil::getTypeSimpleNameInternal);
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    private static SimpleName getTypeSimpleNameInternal(@Nonnull Class<?> typeClass) {
        Optional<SInfoType> infoType = getInfoTypeOpt((Class<? extends SType<?>>) typeClass);
        String typeName = infoType.isPresent() ? StringUtils.trimToNull(infoType.get().name()) : null;
        if (typeName == null) {
            typeName = typeClass.getSimpleName();
        }
        return new SimpleName(typeName);
    }

    @Nonnull
    public static Optional<String> getTypeLabel(Class<? extends SType<?>> typeClass) {
        Optional<String> label = getInfoTypeOpt(typeClass).map(SInfoType::label);
        if (label.isPresent() && StringUtils.isBlank(label.get())) {
            return Optional.empty();
        }
        return label;
    }

    @Nonnull
    static Optional<SInfoType> getInfoTypeOpt(@Nonnull Class<? extends SType<?>> typeClass) {
        return Optional.ofNullable(typeClass.getAnnotation(SInfoType.class));
    }

    @Nonnull
    public static Optional<Class<? extends SPackage>> getTypePackageClass(
            @Nonnull Class<? extends SType<?>> typeClass) {
        return ClassInspectionCache.getInfo(typeClass, CacheKey.PACKAGE_CLASS, SFormUtil::getTypePackageClassInternal);
    }

    @Nonnull
    private static Optional<Class<? extends SPackage>> getTypePackageClassInternal(@Nonnull Class<?> typeClass) {
        Optional<SInfoType> infoType = getInfoTypeOpt((Class<? extends SType<?>>) typeClass);
        if (infoType.isPresent()) {
            Class<? extends SPackage> sPackage = infoType.get().spackage();
            if (sPackage == SPackage.class) {
                sPackage = null;
            }
            return Optional.ofNullable(sPackage);
        }
        return Optional.empty();
    }

    @Nullable
    private static SInfoPackage getInfoPackage(@Nonnull Class<? extends SPackage> packageClass) {
        return packageClass.getAnnotation(SInfoPackage.class);
    }

    @Nonnull
    static String getInfoPackageName(@Nonnull Class<? extends SPackage> packageClass) {
        return ClassInspectionCache.getInfo(packageClass, CacheKey.FULL_NAME, SFormUtil::getInfoPackageNameInternal);
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    private static String getInfoPackageNameInternal(@Nonnull Class<?> packageClass) {
        SInfoPackage info = getInfoPackage((Class<? extends SPackage>) packageClass);
        return info != null && !StringUtils.isBlank(info.name()) ? info.name() : packageClass.getName();
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    static String getScopeNameOrException(@Nonnull Class<? extends SScope> scopeClass) {
        if (SPackage.class.isAssignableFrom(scopeClass)) {
            return getInfoPackageName((Class<SPackage>) scopeClass);
        } else if (SType.class.isAssignableFrom(scopeClass)) {
            return getTypeName((Class<SType<?>>) scopeClass);
        } else {
            throw new SingularFormException("Unsupported class: " + scopeClass.getName());
        }
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    static Class<? extends SPackage> getPackageClassOrException(@Nonnull Class<? extends SScope> scopeClass) {
        if (SPackage.class.isAssignableFrom(scopeClass)) {
            return (Class<SPackage>) scopeClass;
        } else if (SType.class.isAssignableFrom(scopeClass)) {
            Optional<Class<? extends SPackage>> packageClass = getTypePackageClass((Class<SType<?>>) scopeClass);
            if (packageClass.isPresent()) {
                return packageClass.get();
            }
            throw new SingularFormException(
                    scopeClass.getName() + " doesn't have a package define with a " + SInfoType.class.getName() +
                            " annotation");
        } else {
            throw new SingularFormException("Unsupported class: " + scopeClass.getName());
        }
    }

    private static synchronized Map<String, Class<? extends SPackage>> getSingularPackages() {
        if (singularPackages == null) {
            singularPackages = SupplierUtil.cached(() -> {
                Builder<String, Class<? extends SPackage>> builder = ImmutableMap.builder();
                addPackage(builder, SPackageUtil.class);
                addPackage(builder, SPackageBootstrap.class);
                addPackage(builder, SPackagePersistence.class);
                addPackage(builder, SPackageCountryBrazil.class);
                return builder.build();
            });
        }
        return singularPackages.get();
    }

    private static void addPackage(Builder<String, Class<? extends SPackage>> builder, Class<? extends SPackage> packageClass) {
        builder.put(getInfoPackageName(packageClass), packageClass);
    }

    /**
     * Tentar descobrir um pacote padrões do singular ao qual provavelmente o
     * tipo informado pertence.
     *
     * @return null se o tipo não for de um pacote do singular ou senão for
     * encontrado um tipo compatível.
     */
    static Class<? extends SPackage> getSingularPackageForType(String pathFullName) {
        if (!pathFullName.startsWith(SDictionary.SINGULAR_PACKAGES_PREFIX)) {
            return null;
        }
        Map<String, Class<? extends SPackage>> packages = getSingularPackages();
        String selected = null;
        for (String candidate : packages.keySet()) {
            if (pathFullName.startsWith(candidate) && pathFullName.charAt(candidate.length()) == '.'
                    && (selected == null || selected.length() < candidate.length())) {
                selected = candidate;
            }
        }
        return selected == null ? null : packages.get(selected);
    }


    /**
     * Indica se o tipo é um definição do próprio singular (true) ou se é uma definição de terceiros ou do usuário.
     */
    public static boolean isSingularBuiltInType(SType<?> type) {
        return type.getPackage().getName().startsWith(SDictionary.SINGULAR_PACKAGES_PREFIX);
    }

    static void inject(@Nonnull SInstance newInstance) {
        ServiceRegistry registry = ServiceRegistryLocator.locate();
        if (registry == null) {
            SingularInjector.getEmptyInjector().inject(newInstance);
        } else {
            registry.lookupSingularInjector().inject(newInstance);
        }
    }

    /**
     * Find a child by the name ({@link SType#getName()})
     * @param parent the parent instance
     * @param childName the complete name of the child {@link SType#getName()}
     * @return the chield
     */
    public static Optional<? extends SInstance> findChildByName(@Nonnull SInstance parent, @Nonnull String childName) {
        if(parent instanceof SIComposite) {
            SType<?> sType = parent.getDictionary().getType(childName);
            return ((SIComposite)parent).findDescendant(sType);
        }
        return Optional.empty();
    }

    @Nonnull
    static <T> T newInstance(@Nonnull Class<T> targetClass) {
        //It doesn't uses ObjectUtil.newInstance() because SType has a protected constructor
        try {
            return targetClass.newInstance();
        } catch (Exception e) {
            throw new SingularFormException("Erro instanciando " + targetClass.getName(), e);
        }
    }

    /**
     * Tries to load a {@link SType} class using the name of the class.
     *
     * @return Empty if the class doesn't exists or a error  occurs while loading the class.
     */
    @Nonnull
    public static Optional<Class<? extends SType<?>>> tryFindTypeByClassName(@Nonnull String typeClassName) {
        Objects.requireNonNull(typeClassName);
        try {
            Class<?> c = Class.forName(typeClassName);
            if (SType.class.isAssignableFrom(c)) {
                return Optional.of((Class<? extends SType<?>>) c);
            }
            return Optional.empty();
        } catch (Exception e) {
            LoggerFactory.getLogger(SFormUtil.class).error(e.getMessage(), e);
            return Optional.empty();
        }
    }
}