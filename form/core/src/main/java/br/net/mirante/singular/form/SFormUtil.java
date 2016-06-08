/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form;

import static java.util.stream.Collectors.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import javax.lang.model.SourceVersion;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.ImmutableSet;

import br.net.mirante.singular.commons.internal.function.SupplierUtil;
import br.net.mirante.singular.form.type.basic.SPackageBasic;
import br.net.mirante.singular.form.type.core.SPackageBootstrap;
import br.net.mirante.singular.form.type.country.brazil.SPackageCountryBrazil;
import br.net.mirante.singular.form.type.util.SPackageUtil;

public final class SFormUtil {

    private static final Pattern idPattern = Pattern.compile("[_a-zA-Z][_a-zA-Z0-9]*");

    private SFormUtil() {}

    static boolean isValidSimpleName(String name) {
        return idPattern.matcher(name).matches();
    }

    static void validateSimpleName(String name) {
        if (!isValidSimpleName(name)) {
            throw new SingularFormException('\'' + name + "' não é um nome válido para tipo ou atributo");
        }
    }

    static void validatePackageName(String name) {
        if (!SourceVersion.isName(name)) {
            throw new SingularFormException('\'' + name + "' não é um nome válido para um pacote");
        }
    }

    static SType<?> resolveFieldType(SType<?> type, PathReader pathReader) {
        while (!pathReader.isEmpty()) {
            type = resolveFieldTypeInternal(type, pathReader);
            pathReader = pathReader.next();
        }
        return type;
    }

    private static SType<?> resolveFieldTypeInternal(SType<?> type, PathReader pathReader) {
        if (type instanceof STypeComposite) {
            if (pathReader.isIndex()) {
                throw new SingularFormException(pathReader.getTextoErro(type, "Índice de lista não se aplica a um tipo composto"));
            }
            SType<?> campo = ((STypeComposite<?>) type).getField(pathReader.getTrecho());
            if (campo == null) {
                throw new SingularFormException(pathReader.getTextoErro(type, "Não existe o campo '" + pathReader.getTrecho() + '\''));
            }
            return campo;
        } else if (type instanceof STypeList) {
            if (pathReader.isIndex()) {
                return ((STypeList<?, ?>) type).getElementsType();
            }
            throw new SingularFormException(pathReader.getTextoErro(type, "Não se aplica a um tipo lista"));
        } else if (type instanceof STypeSimple) {
            throw new SingularFormException(pathReader.getTextoErro(type, "Não se aplica um path a um tipo simples"));
        } else {
            throw new SingularFormException(pathReader.getTextoErro(type, "Não implementado para " + type.getClass()));
        }
    }

    /**
     * Retorna o nome do filho atual indo em direção ao raiz mas parando segundo
     * a condicão de parada informada.
     */
    public static String generatePath(SInstance current, Predicate<SInstance> stopCondition) {
        List<SInstance> sequencia = null;
        while (!stopCondition.test(current)) {
            if (sequencia == null) {
                sequencia = new ArrayList<>();
            }
            sequencia.add(current);
            current = current.getParent();
        }
        if (sequencia != null) {
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
        return null;
    }

    public static String generateUserFriendlyName(String simpleName) {
        final Pattern lowerUpper = Pattern.compile("(.*?[a-z])([A-Z].*?)");
        final Pattern prefixoSigla = Pattern.compile("([A-Z]+)([A-Z][a-z])");
        final ImmutableSet<String> upperCaseSpecialCases = ImmutableSet.of("id", "url");

        return StringUtils.capitalize(Arrays.asList(simpleName).stream().map(s -> lowerUpper.matcher(s).replaceAll(
                "$1-$2")).map(s -> prefixoSigla.matcher(s).replaceAll("$1-$2")).flatMap(s -> Arrays.asList(s.split(
                "[-_]+")).stream()).map(s -> (StringUtils.isAllUpperCase(s) ? s : StringUtils.uncapitalize(s))).map(
                s -> upperCaseSpecialCases.contains(s) ? StringUtils.capitalize(s) : s).collect(joining(" ")));
    }

    public static String generateUserFriendlyPath(SInstance instance) {
        return generateUserFriendlyPath(instance, null);
    }
    public static String generateUserFriendlyPath(SInstance instance, SInstance parentContext) {
        LinkedList<String> labels = new LinkedList<>();
        SInstance child = null;
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
    public static String getTypeName(Class<? extends SType<?>> typeClass) {
        SInfoType infoType = getInfoType(typeClass);
        if (StringUtils.isBlank(infoType.name())) {
            throw new SingularFormException("O tipo " + typeClass.getName() + " não define o nome do tipo por meio da anotação @"
                + SInfoType.class.getSimpleName());
        }
        Class<? extends SPackage> packageClass = getTypePackage(typeClass);
        String packageName = getInfoPackageNameOrException(packageClass);
        return packageName + '.' + infoType.name();
    }

    static SInfoType getInfoType(Class<? extends SType<?>> typeClass) {
        SInfoType mFormTipo = typeClass.getAnnotation(SInfoType.class);
        if (mFormTipo == null) {
            throw new SingularFormException(
                "O tipo '" + typeClass.getName() + " não possui a anotação @" + SInfoType.class.getSimpleName() + " em sua definição.");
        }
        return mFormTipo;
    }

    public static Class<? extends SPackage> getTypePackage(Class<? extends SType<?>> typeClass) {
        Class<? extends SPackage> sPackage = getInfoType(typeClass).spackage();
        if (sPackage == null) {
            throw new SingularFormException(
                "O tipo '" + typeClass.getName() + "' não define o atributo 'pacote' na anotação @" + SInfoType.class.getSimpleName());
        }
        return sPackage;
    }

    static SInfoPackage getInfoPackage(Class<? extends SPackage> packageClass) {
        return packageClass.getAnnotation(SInfoPackage.class);
    }

    static String getInfoPackageName(Class<? extends SPackage> packageClass) {
        SInfoPackage info = getInfoPackage(packageClass);
        return info != null && !StringUtils.isBlank(info.name()) ? info.name() : null;
    }

    static String getInfoPackageNameOrException(Class<? extends SPackage> packageClass) {
        String packageName = getInfoPackageName(packageClass);
        if (packageName == null) {
            throw new SingularFormException("A classe " + packageClass.getName() + " não define o nome do pacote por meio da anotação @"
                + SInfoPackage.class.getSimpleName());
        }
        return packageName;
    }

    private static Supplier<Map<String, Class<? extends SPackage>>> singularPackages;

    private static Map<String, Class<? extends SPackage>> getSingularPackages() {
        if (singularPackages == null) {
            synchronized (SFormUtil.class) {
                if (singularPackages == null) {
                    singularPackages = SupplierUtil.cached(() -> {
                        Builder<String, Class<? extends SPackage>> builder = ImmutableMap.builder();
                        // addPackage(builder, SPackageBasic.class);
                        addPackage(builder, SPackageUtil.class);
                        addPackage(builder, SPackageBootstrap.class);
                        addPackage(builder, SPackageCountryBrazil.class);
                        return builder.build();
                    });
                }
            }
        }
        return singularPackages.get();
    }

    private static void addPackage(Builder<String, Class<? extends SPackage>> builder, Class<? extends SPackage> packageClass) {
        builder.put(getInfoPackageNameOrException(packageClass), packageClass);
    }

    /**
     * Tentar descobrir um pacote padrões do singular ao qual provavelmente o
     * tipo informado pertence.
     * 
     * @return null se o tipo não for de um pacote do singular ou senão for
     *         encontrado um tipo compatível.
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
}
