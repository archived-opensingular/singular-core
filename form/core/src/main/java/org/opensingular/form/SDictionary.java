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

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.opensingular.form.aspect.MasterAspectRegistry;
import org.opensingular.form.document.SDocument;
import org.opensingular.form.type.core.SPackageCore;
import org.opensingular.form.view.ViewResolver;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class SDictionary {

    private final MapByName<SPackage> packages = new MapByName<>(p -> p.getName());

    private final Map<Class<? extends SType<?>>, SType<?>> typeByClass = new HashMap<>();
    private final Map<String, SType<?>> typeCacheByFullName = new HashMap<>();

    private final Map<String, AttrInternalRef> attributes = new HashMap<>(currentAvarageAttributes);

    private final SDocument internalDocument = new SDocument();

    private ViewResolver viewResolver;

    public static final String SINGULAR_PACKAGES_PREFIX = "singular.form.";

    /** Identficador único e sequencial do tipo dentro do dicionário. */
    private int idCount;

    /** Lista de processadores pendentes para execução para novos SType. */
    Multimap<SType<?>, Runnable> pendingTypeProcessorExecution;

    /** List of aspect registry associated to this {@link SDictionary}. */
    private MasterAspectRegistry masterAspectRegistry;

    private SDictionary() {
    }

    /**
     * Apenas para uso interno do dicionario de modo que os atributos dos tipos
     * tenha um documento de referencia.
     */
    final SDocument getInternalDicionaryDocument() {
        return internalDocument;
    }

    public Collection<SPackage> getPackages() {
        return packages.getValues();
    }

    /**
     * Retorna o registro e resolvedor (calculador) de views para as instâncias.
     * Permite registra view e decidir qual a view mais pertinente para a
     * instância alvo.
     */
    public ViewResolver getViewResolver() {
        if (viewResolver == null) {
            viewResolver = new ViewResolver();
        }
        return viewResolver;
    }

    public static SDictionary create() {
        SDictionary dictionary = new SDictionary();
        dictionary.loadPackage(SPackageCore.class);
        return dictionary;
    }

    /**
     * Carrega no dicionário o pacote informado e todas as definições do mesmo,
     * se ainda não tiver sido carregado. É seguro chamar é método mais de uma
     * vez para o mesmo pacote.
     *
     * @return O pacote carregado
     */
    @Nonnull
    public <T extends SPackage> T loadPackage(@Nonnull Class<T> packageClass) {
        T newPackage = packages.get(packageClass);
        if (newPackage == null) {
            try {
                newPackage = packageClass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new SingularFormException("Erro instanciando " + packageClass.getName(), e);
            }
            packages.verifyMustNotBePresent(newPackage, this);
            loadInternal(newPackage);
        }
        return newPackage;
    }

    /**
     * Carrega no dicionário o pacote do atributo informado, se ainda não tiver sido carregado. É seguro chamar é
     * método mais de uma vez para o mesmo pacote.
     *
     * @return O pacote carregado
     */
    @Nonnull
    final SPackage loadPackageFor(@Nonnull AtrRef<?, ?, ?> atr) {
        return loadPackage(atr.getPackageClass());
    }

    public PackageBuilder createNewPackage(String name) {
        packages.verifyMustNotBePresent(name, this);
        SPackage newPackage = new SPackage(name);
        newPackage.setDictionary(this);
        packages.add(newPackage);
        return new PackageBuilder(newPackage);
    }

    /**
     * Recupera o tipo, já carregado no dicionário, da classe informada. Senão
     * estiver carregado ainda, busca carregá-lo e as definições do pacote a que
     * pertence. Senão encontrar no dicionário e nem conseguir encontrar para
     * carregar, então dispara Exception.
     */
    @Nonnull
    public <T extends SType<?>> T getType(@Nonnull Class<T> typeClass) {
        Objects.requireNonNull(typeClass);
        SType<?> typeRef = typeByClass.get(typeClass);
        if (typeRef == null) {
            Class<? extends SPackage> classPacote = SFormUtil.getTypePackage(typeClass);
            SPackage typePackage = loadPackage(classPacote);
            typeRef = typeByClass.get(typeClass);
            if (typeRef == null) {
                //O tipo é de carga lazy e não se auto registrou no pacote, então regista agora
                typeRef = registerLazyTypeIntoPackage(typePackage, typeClass);
            }
        }
        return typeClass.cast(typeRef);
    }

    /** Adiciona o tipo informado no pacote. */
    @Nonnull
    private <T extends SType<?>> T registerLazyTypeIntoPackage(@Nonnull SPackage typePackage,
            @Nonnull Class<T> typeClass) {
        Objects.requireNonNull(typePackage);
        Objects.requireNonNull(typeClass);
        return typePackage.registerType(typeClass);
    }

    public SType<?> getType(String fullNamePath) {
        return getTypeOptional(fullNamePath).orElseThrow(
                () -> new SingularFormException("Tipo '" + fullNamePath + "' não encontrado"));
    }

    public Optional<SType<?>> getTypeOptional(String pathFullName) {
        Optional<SType<?>> t = findTypeByFullName(pathFullName);
        if (!t.isPresent()) {
            // Verifica se é um tipo dos pacotes com carga automática do
            // singular
            Class<? extends SPackage> singularPackage = SFormUtil.getSingularPackageForType(pathFullName);
            if (singularPackage != null) {
                loadPackage(singularPackage);
                t = findTypeByFullName(pathFullName);
            }
        }
        return t;
    }

    @Nonnull
    private Optional<SType<?>> findTypeByFullName(String pathFullName) {
        SType<?> t = typeCacheByFullName.get(pathFullName);
        if (t != null) {
            return Optional.of(t);
        }
        int pos = pathFullName.lastIndexOf('.');
        while (pos != -1) {
            String candidatePackage = pathFullName.substring(0, pos);
            SPackage p = packages.get(candidatePackage);
            if (p != null) {
                Optional<SType<?>> tt = p.getLocalTypeOptional(pathFullName.substring(pos + 1));
                if (tt.isPresent()) {
                    typeCacheByFullName.put(pathFullName, tt.get());
                }
                return tt;
            }
            pos = pathFullName.lastIndexOf('.', pos-1);
        }
        return Optional.empty();
    }

    public <I extends SInstance, T extends SType<I>> I newInstance(Class<T> typeClass) {
        return getType(typeClass).newInstance();
    }

    @SuppressWarnings("unchecked")
    final <T extends SType<?>> void registerType(@Nonnull SScope scope, @Nonnull T newType,
            @Nullable Class<T> classForRegister) {
        if (classForRegister != null) {
            Class<? extends SPackage> packageClass = SFormUtil.getTypePackage(classForRegister);
            SPackage packageAnnotation = packages.getOrNewInstance(packageClass);
            SPackage packageDestiny = scope.getPackage();
            if (!packageDestiny.getName().equals(packageAnnotation.getName())) {
                throw new SingularFormException(
                        "Tentativa de carregar o tipo '" + newType.getNameSimple() + "' anotado para o pacote '" +
                                packageAnnotation.getName() + "' como sendo do pacote '" + packageDestiny.getName() + "'",
                        newType);
            }
        }
        newType.setScope(scope);
        newType.resolveSuperType(this);
        newType.setTypeId(++idCount);
        ((SScopeBase) scope).register(newType);
        typeByClass.put(classForRegister, newType);
    }

    private void loadInternal(SPackage newPackage) {
        newPackage.setDictionary(this);
        packages.add(newPackage);
        PackageBuilder pb = new PackageBuilder(newPackage);
        newPackage.onLoadPackage(pb);
    }

    public void debug() {
        System.out.println("=======================================================");
        packages.forEach(p -> p.debug());
        System.out.println("=======================================================");
    }

    /** Executa todos os processadores para o tipo informado que estiverem pendentes de execução (se existirem). */
    final void runPendingTypeProcessorExecution(SType<?> type) {
        if(pendingTypeProcessorExecution != null) {
            Collection<Runnable> tasks = pendingTypeProcessorExecution.removeAll(type);
            if(pendingTypeProcessorExecution.isEmpty()) {
                pendingTypeProcessorExecution = null;
            }
            tasks.forEach(Runnable::run);
        }
    }

    /**
     * Registra que existem processadores pendentes de execução para o tipo, os quais deverão ser executados depois de
     * concluir a execução do onLoadType da classe.
     */
    final void addTypeProcessorForLatterExecution(SType<?> type, Runnable runnable) {
        if(pendingTypeProcessorExecution == null) {
            pendingTypeProcessorExecution = ArrayListMultimap.create();
        }
        pendingTypeProcessorExecution.put(type, runnable);
    }

    /**
     * Registra um atributo que pode ser associado a vários {@link SType} diferentes.
     */
    @Nonnull
    final AttrInternalRef registerAttribute(@Nonnull SType<?> attr) {
        AttrInternalRef ref = registerAttribute(attr.getName());
        ref.resolve(attr);
        return ref;
    }

    /**
     * Registra um atributo que pertence a um único tipo (não pode ser associado novamente a outro tipo) e que pode ser
     * do tipo auto referência (atributo cujo valor é o mesmo do tipo ao qual está
     * associado).
     */
    @Nonnull
    final AttrInternalRef registerAttribute(@Nonnull SType<?> attr, @Nonnull SType<?> owner, boolean selfReference) {
        AttrInternalRef ref = registerAttribute(attr.getName());
        ref.resolve(attr, owner, selfReference);
        owner.addAttribute(attr);
        return ref;
    }

    /**
     * Faz um registro de atributo pendente, ou seja, sem especificar o tipo do mesmo. Esse registro provavelmente será
     * corretamente associado mais tarde.
     */
    @Nonnull
    final AttrInternalRef getAttribureRefereceOrCreateLazy(@Nonnull String attributeName) {
        AttrInternalRef ref = getAttributeReference(attributeName);
        if (ref == null) {
            ref = registerAttribute(attributeName);
        }
        return ref;
    }

    /**
     * Tenta registra um novo atributo com o nome informado. Se houver um registro de atributo pendente de resolução,
     * retorna esse em vez de criar um novo.
     */
    @Nonnull
    private AttrInternalRef registerAttribute(@Nonnull String attributeName) {
        AttrInternalRef ref = new AttrInternalRef(this, attributeName, attributes.size());
        AttrInternalRef previusValue = attributes.putIfAbsent(ref.getName(), ref);
        if (previusValue == null) {
            incAttribute(ref.getIndex().intValue() == 0);
            attributesArrayInicialSize = Math.max(attributes.size(), currentAvarageAttributes);
            return ref;
        } else if (previusValue.isResolved()) {
            throw new SingularFormException("Internal Error: attribute '" + attributeName + " already definied");
        }
        return previusValue;
    }

    /**
     * Atualiza a estatisticas de criação de atributos em dicionários a fim de viabilizar descobrir o tamanho métodos do
     * registro de atributos.
     */
    private static void incAttribute(boolean newDictionarie) {
        if (newDictionarie) {
            countDictionaries++;
        }
        countAttributes++;
        if (countDictionaries == 10000) {
            synchronized (SDictionary.class) {
                if (countDictionaries == 10000) {
                    currentAvarageAttributes = (countAttributes + countDictionaries - 1) / countDictionaries;
                    countDictionaries = 0;
                    countAttributes = 0;
                }
            }
        }
    }

    private static int countDictionaries = 0;
    private static int countAttributes = 0;
    private static int currentAvarageAttributes = 30;

    private int attributesArrayInicialSize = currentAvarageAttributes;

    /** Retorna o tamanho inicial para a criação de arrays para referência para atributos. */
    final int getAttributesArrayInitialSize() {
        return attributesArrayInicialSize;
    }

    /** Retorna a referência ao atributo solicitado se já existir registro de referência ao mesmo. */
    @Nullable
    final AttrInternalRef getAttributeReference(@Nonnull String fullName) {
        return attributes.get(fullName);
    }

    /**
     * Retorna a referência ao atributo solicitado se já existir registro de referência ao mesmo no dicionário ou
     * dispara uma exception se não existir.
     */
    @Nonnull
    final AttrInternalRef getAttributeReferenceOrException(@Nonnull String fullName) {
        AttrInternalRef ref = getAttributeReference(fullName);
        if (ref == null) {
            throw new SingularFormException("O atributo '" + fullName + "' não foi registrado");
        }
        return ref;
    }

    /**
     * Retorna a referência ao atributo solicitado se já existir registro de referência ao mesmo no dicionário. Senão
     * existir a referência ainda, então provoca a carga do pacote associado ao mesmo.
     */
    @Nonnull
    final AttrInternalRef getAttributeReferenceOrException(@Nonnull AtrRef<?, ?, ?> atr) {
        Objects.requireNonNull(atr);
        AttrInternalRef ref = getAttributeReference(atr.getNameFull());
        if (ref == null || !ref.isResolved()) {
            loadPackageFor(atr);
            ref = getAttributeReferenceOrException(atr.getNameFull());
        }
        return ref;
    }

    /** Returns the registry of aspect registries associeted to this {@link SDictionary}. */
    final MasterAspectRegistry getMasterAspectRegistry() {
        if (masterAspectRegistry == null) {
            masterAspectRegistry = new MasterAspectRegistry();
        }
        return masterAspectRegistry;
    }
}
