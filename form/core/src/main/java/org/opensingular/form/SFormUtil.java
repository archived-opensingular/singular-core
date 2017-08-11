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
import org.opensingular.form.context.ServiceRegistry;
import org.opensingular.form.context.ServiceRegistryLocator;
import org.opensingular.form.internal.PathReader;
import org.opensingular.form.type.core.SPackageBootstrap;
import org.opensingular.form.type.core.SPackagePersistence;
import org.opensingular.form.type.country.brazil.SPackageCountryBrazil;
import org.opensingular.form.type.util.SPackageUtil;
import org.opensingular.internal.lib.commons.injection.SingularInjector;
import org.opensingular.lib.commons.internal.function.SupplierUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.lang.model.SourceVersion;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

public final class SFormUtil {

    private static final Pattern idPattern = Pattern.compile("[_a-zA-Z][_a-zA-Z0-9]*");
    private static Supplier<Map<String, Class<? extends SPackage>>> singularPackages;

    private SFormUtil() {
    }

    public static boolean isNotValidSimpleName(@Nonnull String name) {
        Objects.requireNonNull(name);
        return !idPattern.matcher(name).matches();
    }

    @Nonnull
    static String validateSimpleName(@Nonnull String name) {
        if (isNotValidSimpleName(name)) {
            throw new SingularFormException('\'' + name + "' não é um nome válido para tipo ou atributo");
        }
        return name;
    }

    @Nonnull
    static String validatePackageName(@Nonnull String name) {
        Objects.requireNonNull(name);
        if (!SourceVersion.isName(name)) {
            throw new SingularFormException('\'' + name + "' não é um nome válido para um pacote");
        }
        return name;
    }

    @Nonnull
    public static String resolveName(@Nullable String simpleName, @Nonnull SType<?> type) {
        return simpleName == null ? type.getNameSimple() : simpleName;
    }

    static SType<?> resolveFieldType(SType<?> type, PathReader pathReader) {
        SType<?>   currentType       = type;
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
            SType<?> campo = ((STypeComposite<?>) type).getField(token);
            if (campo == null) {
                throw new SingularFormException(pathReader.getErrorMsg(type, "Não existe o campo '" + token + '\''), type);
            }
            return campo;
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
    public static String generatePath(SInstance instance, Predicate<SInstance> stopCondition) {
        SInstance       current   = instance;
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

    public static String generateUserFriendlyName(String simpleName) {
        final Pattern              lowerUpper            = Pattern.compile("(.*?[a-z])([A-Z].*?)");
        final Pattern              prefixoSigla          = Pattern.compile("([A-Z]+)([A-Z][a-z])");
        final ImmutableSet<String> upperCaseSpecialCases = ImmutableSet.of("id", "url");

        return StringUtils.capitalize(Stream.of(simpleName).map(s -> lowerUpper.matcher(s).replaceAll(
                "$1-$2")).map(s -> prefixoSigla.matcher(s).replaceAll("$1-$2")).flatMap(s -> Stream.of(s.split(
                "[-_]+"))).map(s -> (StringUtils.isAllUpperCase(s) ? s : StringUtils.uncapitalize(s))).map(
                s -> upperCaseSpecialCases.contains(s) ? StringUtils.capitalize(s) : s).collect(joining(" ")));
    }

    public static String generateUserFriendlyPath(SInstance instance) {
        return generateUserFriendlyPath(instance, null);
    }

    public static String generateUserFriendlyPath(SInstance instance, SInstance parentContext) {
        LinkedList<String> labels = new LinkedList<>();
        SInstance          child  = null;
        for (SInstance node = instance; node != null && !node.equals(parentContext); child = node, node = node.getParent()) {

            final String labelNode = node.asAtr().getLabel();

            if (node instanceof SIList<?>) {
                SIList<?> lista = (SIList<?>) node;
                String labelLista = lista.asAtr().getLabel();
                int index = lista.indexOf(child) + 1;
                labels.add(labelLista + ((index > 0) ? " [" + (index) + ']' : ""));
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
    public static String getTypeName(@Nonnull Class<? extends SType<?>> typeClass) {
        Class<? extends SPackage> packageClass = getTypePackage(typeClass);
        return getInfoPackageName(packageClass) + '.' + getTypeSimpleName(typeClass);
    }

    public static String getTypeSimpleName(Class<? extends SType<?>> typeClass) {
        SInfoType infoType = getInfoType(typeClass);
        String    typeName = infoType.name();
        if (StringUtils.isBlank(typeName)) {
            typeName = typeClass.getSimpleName();
        }
        return typeName;
    }

    public static Optional<String> getTypeLabel(Class<? extends SType> typeClass) {
        SInfoType infoType = getInfoType((Class<? extends SType<?>>) typeClass);
        if (StringUtils.isBlank(infoType.label())) {
            return Optional.empty();
        }
        return Optional.of(infoType.label());
    }

    @Nonnull
    static SInfoType getInfoType(Class<? extends SType<?>> typeClass) {
        SInfoType mFormTipo = typeClass.getAnnotation(SInfoType.class);
        if (mFormTipo == null) {
            throw new SingularFormException(
                    "O tipo '" + typeClass.getName() + " não possui a anotação @" + SInfoType.class.getSimpleName() + " em sua definição.");
        }
        return mFormTipo;
    }

    @Nonnull
    public static Class<? extends SPackage> getTypePackage(Class<? extends SType<?>> typeClass) {
        Class<? extends SPackage> sPackage = getInfoType(typeClass).spackage();
        if (sPackage == null) {
            throw new SingularFormException(
                    "O tipo '" + typeClass.getName() + "' não define o atributo 'pacote' na anotação @" + SInfoType.class.getSimpleName());
        }
        return sPackage;
    }

    @Nullable
    static SInfoPackage getInfoPackage(@Nonnull Class<? extends SPackage> packageClass) {
        return packageClass.getAnnotation(SInfoPackage.class);
    }

    @Nonnull
    static String getInfoPackageName(@Nonnull Class<? extends SPackage> packageClass) {
        SInfoPackage info = getInfoPackage(packageClass);
        return info != null && !StringUtils.isBlank(info.name()) ? info.name() : packageClass.getName();
    }

    @Nonnull
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
    static Class<? extends SPackage> getPackageClassOrException(@Nonnull Class<? extends SScope> scopeClass) {
        if (SPackage.class.isAssignableFrom(scopeClass)) {
            return (Class<SPackage>) scopeClass;
        } else if (SType.class.isAssignableFrom(scopeClass)) {
            return getTypePackage((Class<SType<?>>) scopeClass);
        } else {
            throw new SingularFormException("Unsupported class: " + scopeClass.getName());
        }
    }

    private synchronized static Map<String, Class<? extends SPackage>> getSingularPackages() {
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
        String                                 selected = null;
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
}
