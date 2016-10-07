/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.form;

import java.util.Collection;

import org.opensingular.form.document.SDocument;
import org.opensingular.form.view.ViewResolver;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import org.opensingular.form.type.core.SPackageCore;

public class SDictionary {

    private MapByName<SPackage> packages = new MapByName<>(p -> p.getName());

    private MapByName<SType<?>> types = new MapByName<>(t -> t.getName());

    private final SDocument internalDocument = new SDocument();

    private ViewResolver viewResolver;

    public static final String SINGULAR_PACKAGES_PREFIX = "singular.form.";

    /** Configurações globais do dicionário. */
    private SDictionaryConfig dictionaryConfig = new SDictionaryConfig(this);

    /** Identficador único e sequencial do tipo dentro do dicionário. */
    private int idCount;

    /** Lista de processadores pendentes para execução para novos SType. */
    Multimap<SType<?>, Runnable> pendingTypeProcessorExecution;

    private SDictionary() {
    }

    final SDictionaryConfig getDictionaryConfig() {
        return dictionaryConfig;
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
        SDictionary dicionario = new SDictionary();
        dicionario.loadPackage(SPackageCore.class);
        return dicionario;
    }

    /**
     * Carrega no dicionário o pacote informado e todas as definições do mesmo,
     * se ainda não tiver sido carregado. É seguro chamar é método mais de uma
     * vez para o mesmo pacote.
     *
     * @return O pacote carregado
     */
    public <T extends SPackage> T loadPackage(Class<T> packageClass) {
        if (packageClass == null){
            throw new SingularFormException("Classe pacote não pode ser nula");
        }
        T novo = packages.get(packageClass);
        if (novo == null) {
            novo = MapByName.newInstance(packageClass);
            packages.verifyMustNotBePresent(novo, this);
            carregarInterno(novo);
        }
        return novo;
    }

    public PackageBuilder createNewPackage(String nome) {
        packages.verifyMustNotBePresent(nome, this);
        SPackage novo = new SPackage(nome);
        novo.setDictionary(this);
        packages.add(novo);
        return new PackageBuilder(novo);
    }

    /**
     * Recupera o tipo, já carregado no dicionário, da classe informada. Senão
     * estiver carregado ainda, busca carregá-lo e as definições do pacote a que
     * pertence. Senão encontrar no dicionário e nem conseguir encontrar para
     * carregar, então dispara Exception.
     *
     * @return Nunca Null.
     */
    public <T extends SType<?>> T getType(Class<T> typeClass) {
        T typeRef = getTypeOptional(typeClass);
        if (typeRef == null) {
            throw new SingularFormException("Tipo da classe '" + typeClass.getName() + "' não encontrado");
        }
        return typeRef;
    }

    public <T extends SType<?>> T getTypeOptional(Class<T> typeClass) {
        T tipoRef = types.get(typeClass);
        if (tipoRef == null) {
            Class<? extends SPackage> classPacote = SFormUtil.getTypePackage(typeClass);
            loadPackage(classPacote);
            tipoRef = types.get(typeClass);
        }
        return tipoRef;
    }

    public SType<?> getType(String fullNamePath) {
        SType<?> type = getTypeOptional(fullNamePath);
        if (type == null) {
            throw new SingularFormException("Tipo '" + fullNamePath + "' não encontrado");
        }
        return type;
    }

    public SType<?> getTypeOptional(String pathFullName) {
        SType<?> t = types.get(pathFullName);
        if (t == null) {
            // Verifica se é um tipo dos pacotes com carga automática do
            // singular
            Class<? extends SPackage> singularPackage = SFormUtil.getSingularPackageForType(pathFullName);
            if (singularPackage != null) {
                loadPackage(singularPackage);
                t = types.get(pathFullName);
            }
        }
        return t;
    }

    public <I extends SInstance, T extends SType<I>> I newInstance(Class<T> classeTipo) {
        return getType(classeTipo).newInstance();
    }

    final MapByName<SType<?>> getTypesInternal() {
        return types;
    }

    @SuppressWarnings("unchecked")
    final <T extends SType<?>> void registeType(SScope scope, T newType, Class<T> classForRegister) {
        if (classForRegister != null) {
            Class<? extends SPackage> classePacoteAnotado = SFormUtil.getTypePackage(classForRegister);
            SPackage pacoteAnotado = packages.getOrNewInstance(classePacoteAnotado);
            SPackage pacoteDestino = findPackage(scope);
            if (!pacoteDestino.getName().equals(pacoteAnotado.getName())) {
                throw new SingularFormException(
                        "Tentativa de carregar o tipo '" + newType.getNameSimple() + "' anotado para o pacote '" +
                                pacoteAnotado.getName() + "' como sendo do pacote '" + pacoteDestino.getName() + "'",
                        newType);
            }
        }
        newType.setScope(scope);
        newType.resolvSuperType(this);
        newType.setTypeId(++idCount);
        types.verifyMustNotBePresent(newType, this);
        ((SScopeBase) scope).register(newType);
        types.add(newType, (Class<SType<?>>) classForRegister);
    }

    private static SPackage findPackage(SScope scope) {
        while (scope != null && !(scope instanceof SPackage)) {
            scope = scope.getParentScope();
        }
        return (SPackage) scope;
    }

    private void carregarInterno(SPackage newPackage) {
        PackageBuilder pb = new PackageBuilder(newPackage);
        newPackage.setDictionary(this);
        packages.add(newPackage);
        newPackage.onLoadPackage(pb);
    }

    public void debug() {
        System.out.println("=======================================================");
        packages.forEach(p -> p.debug());
        System.out.println("=======================================================");
    }

    /** Executa todos os processadores para o tipo informado que estiverem pendentes de execução (se existirem). */
    public  void runPendingTypeProcessorExecution(SType<?> type) {
        if(pendingTypeProcessorExecution != null) {
            Collection<Runnable> tasks = pendingTypeProcessorExecution.removeAll(type);
            if(pendingTypeProcessorExecution.isEmpty()) {
                pendingTypeProcessorExecution = null;
            }
            for(Runnable task: tasks) {
                task.run();
            }
        }
    }

    /**
     * Registra que existem processadores pendentes de execução para o tipo, os quais deverão ser executados depois de
     * concluir a execução do onLoadType da classe.
     */
    public void addTypeProcessorForLatterExecutuion(SType<?> type, Runnable runnable) {
        if(pendingTypeProcessorExecution == null) {
            pendingTypeProcessorExecution = ArrayListMultimap.create();
        }
        SType<?> dependentType = type.getSuperType();
        pendingTypeProcessorExecution.put(dependentType, runnable);
    }
}
