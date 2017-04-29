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

import com.google.common.base.Preconditions;
import org.opensingular.form.internal.PathReader;
import org.opensingular.form.processor.TypeProcessorAttributeReadFromFile;
import org.opensingular.form.processor.TypeProcessorPublicFieldsReferences;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class SScopeBase implements SScope {

    private Map<String, SType<?>> localTypes;

    private static final Logger LOGGER = Logger.getLogger(SScopeBase.class.getName());

    /**
     * Indica que o escopo têm uma referência recursiva para ele mesmo. Somente é marcado true na referência interna, ou
     * seja, a primeira definição do tipo não será marcado como recursivo, mas o campo filho que aponta para ele mesmo,
     * será.
     */
    private boolean recursiveReference;

    @Override
    public SScope getParentScope() {
        return null;
    }

    /**
     * Retorna os tipos criados localmente. Se for um pacote, retorna o tipos do
     * pacote. Se for um tipo, então retorna o tipo criados no escopo local do
     * tipo (tipo dentro de tipo).
     */
    @Nonnull
    public Collection<SType<?>> getLocalTypes() {
        return getLocalTypesMap().values();
    }

    private Map<String, SType<?>> getLocalTypesMap() {
        if (isRecursiveReference()) {
            //Se é uma referência recursiva, não deve criar um novo mapa local e sim usar o mapa do tipo original
            // para o qual aponta
            if (this instanceof SType) {
                SType<?> superType = ((SType<?>) this).getSuperType();
                if (superType == null || superType.getClass() != getClass()) {
                    //Verificação de sanidade
                    throw new SingularFormException(
                            "Erro interno: uma referência recursiva não extende um tipo que é da mesma classe " +
                                    "(superType()=" + superType + ")", this);
                } else {
                    return ((SScopeBase) superType).getLocalTypesMap();
                }
            }
        }
        return localTypes == null ? Collections.emptyMap() : localTypes;
    }

    @Override
    @Nonnull
    public Optional<SType<?>> getLocalTypeOptional(@Nonnull String path) {
        return getLocalTypeOptional(new PathReader(path));
    }

    @Nonnull
    final Optional<SType<?>> getLocalTypeOptional(@Nonnull PathReader pathReader) {
        SType<?> type = getLocalTypesMap().get(pathReader.getToken());
        if (type == null) {
            return Optional.empty();
        } else if (pathReader.isLast()) {
            return Optional.of(type);
        }
        return type.getLocalTypeOptional(pathReader.next());
    }


    @Override
    @Nonnull
    public SType<?> getLocalType(@Nonnull String path) {
        // Não utiliza getTipoLocalOpcional, pois da forma abaixo é possível
        // apontar precisamente onde deu erro no path passado.
        return getLocalType(new PathReader(path));
    }

    @Nonnull
    final SType<?> getLocalType(@Nonnull PathReader pathReader) {
        SType<?> type = getLocalTypesMap().get(pathReader.getToken());
        if (type != null) {
            if (pathReader.isLast()) {
                return type;
            }
            return type.getLocalType(pathReader.next());
        }
        throw new SingularFormException(pathReader.getErrorMsg(this,
                "Não foi encontrado o tipo '" + pathReader.getToken() + "' em '" + getName() + "'"), this);
    }

    /** Registro o tipo informado neste escopo. */
    final <T extends SType<?>> T registerType(Class<T> typeClass) {
        T t = registerType(MapByName.newInstance(typeClass), typeClass);
        TypeProcessorAttributeReadFromFile.INSTANCE.onRegisterTypeByClass(t, typeClass);
        return t;
    }

    final <T extends SType<?>> T registerType(T newType, Class<T> classeDeRegistro) {
        getDictionary().registeType(this, newType, classeDeRegistro);
        /*
        (by Daniel Bordin) O If abaixo impede que o onLoadType seja chamado mais de uma vezes caso o novo tipo seja
        apenas uma extensão da classe já carregada anteriormente, ou seja, impede que o mesmo onLoadType seja
        invocado múltiplas vezes. Esse controle é especialmente importante para eveitar entrar em loop quando
        um tipo tem uma referência para ele mesmo, além de evitar recargas e duplicação de atributos.
        */
        newType.setRecursiveReference(isRecursiveReference(newType));
        SType<?> superType = newType.getSuperType();
        if (superType == null || superType.getClass() != newType.getClass()) {
            newType.extendSubReference();
            TypeProcessorPublicFieldsReferences.INSTANCE.processTypePreOnLoadTypeCall(newType);
            newType.setCallingOnLoadType(true);
            callOnLoadTypeIfNecessary(newType);
            newType.setCallingOnLoadType(false);
            TypeProcessorPublicFieldsReferences.INSTANCE.processTypePosRegister(newType, true);
            getDictionary().runPendingTypeProcessorExecution(newType);
        } else {
            if (newType.isRecursiveReference()) {
                if(isSuperTypeCallingOnLoadType(newType)) {
                    //Não pode rodar os processadores em quanto nao tiver terminado o onLoadType do tipo pai
                    newType.setCallingOnLoadType(true);
                    getDictionary().addTypeProcessorForLatterExecutuion(superType, () -> {
                        TypeProcessorPublicFieldsReferences.INSTANCE.processTypePosRegister(newType, false);
                        getDictionary().runPendingTypeProcessorExecution(newType);
                        newType.setCallingOnLoadType(false);
                    });
                } else {
                    TypeProcessorPublicFieldsReferences.INSTANCE.processTypePosRegister(newType, false);
                }
            } else {
                newType.extendSubReference();
                TypeProcessorPublicFieldsReferences.INSTANCE.processTypePosRegister(newType, false);
            }
        }
        return newType;
    }

    /**
     * Chama o método onLoadType somente se o tipo for um classe que implementa o método de forma específica, ou seja,
     * não chama o onLoadType se a classe apenas derivar de um classe que implementou o onLoadType e cuja a chamada
     * já foi executada uma vez na carga do tipo pai.
     */
    private <T extends SType<?>> void callOnLoadTypeIfNecessary(T newType) {
        if (newType.getSuperType() == null) {
            return; //Não precisa chamar onLoadType no SType
        }
        Class<?> c = newType.getClass();
        while (true) {
            try {
                c.getDeclaredMethod("onLoadType", TypeBuilder.class);
                break; //Então deve chamar onLoadType, pois há uma implementação específica para a classe
            } catch (NoSuchMethodException e) {
                c = c.getSuperclass();
                if (c == newType.getSuperType().getClass()) {
                    return; //Não é necessário chamar onLoadType, pois já foi chamado no tipo pai
                }
            }
        }
        newType.onLoadType(new TypeBuilder(newType));
    }

    /** Verificar se o tipo super já terminiu a chamada do onLoadType ou se está no meio da execução do mesmo. */
    private boolean isSuperTypeCallingOnLoadType(SType<?> type) {
        return type.getSuperType().isCallingOnLoadType();
    }

    /**
     * Verifica se o tipo é uma extensão de um tipo da mesma classe e que possui uma referência a ele mesmo. Vai
     * indicar true somente para a referência interna dentro da referência circular, ou seja, o tipo (ou classe) que
     * contêm o campo não será marcado como referência circular, somente o campo em si.
     */
    private  boolean isRecursiveReference(SType<?> type) {
        for(SScope parent = type.getParentScope(); parent instanceof SType; parent = parent.getParentScope()) {
            if(parent == type || parent == type.getSuperType()) {
                return true;
            }
        }
        return false;
    }

    final <T extends SType<?>> T extendType(String simpleNameNewType, T parentType) {
        if (getDictionary() != parentType.getDictionary()) {
            throw new SingularFormException(
                    "O tipo " + parentType.getName() + " foi criado dentro de outro dicionário, que não o atual de " + getName());
        }
        T newType = parentType.extend(simpleNameNewType);
        return registerType(newType, null);
    }

    final <T extends SType<?>> T extendType(String simpleNameNewType, Class<T> parenteTypeClass) {
        T parentType = resolveType(parenteTypeClass);
        return extendType(simpleNameNewType, parentType);
    }

    @SuppressWarnings("unchecked")
    final <I extends SIComposite> STypeList<STypeComposite<I>, I> createListOfNewTypeComposite(String simpleNameNewType,
            String simpleNameNewTypeComposto) {
        STypeList<STypeComposite<I>, I> listType = extendType(simpleNameNewType, STypeList.class);
        listType.setElementsType(simpleNameNewTypeComposto, resolveType(STypeComposite.class));
        return listType;
    }

    @SuppressWarnings("unchecked")
    final <I extends SInstance, T extends SType<I>> STypeList<T, I> createTypeListOf(String simpleNameNewType, T elementsType) {
        Preconditions.checkNotNull(elementsType);
        STypeList<T, I> listType = extendType(simpleNameNewType, STypeList.class);
        listType.setElementsType(elementsType);
        return listType;
    }

    final <T extends SType<?>> T resolveType(Class<T> typeClass) {
        return getDictionary().getType(typeClass);
    }

    final void register(SType<?> type) {
        if(isRecursiveReference()) {
            ((SScopeBase) getParentScope()).register(type);
        } else {
            String nameSimple = type.getNameSimple();
            if (localTypes == null) {
                localTypes = new LinkedHashMap<>();
            } else {
                if (localTypes.containsKey(nameSimple)) {
                    throw new SingularFormException(
                            "A definição '" + nameSimple + "' já está criada no escopo " + getName());
                }
            }
            localTypes.put(nameSimple, type);
        }
    }

    /**
     * Imprime informação de debug para o console com a estrutura de dados do escopo (incluindo sub definições de forma
     * recursiva).
     */
    public final void debug() {
        debug(0);
    }

    /**
     * Imprime informação de debug (com margem de espaço esquerda, tabulado) para o console com a estrutura de dados do
     * escopo (incluindo sub definições de forma recursiva).
     */
    public void debug(int level) {
        debug(System.out, level);
    }

    /**
     * Imprime informação de debug para a saída informada com a estrutura de dados do escopo (incluindo sub
     * definições de forma recursiva).
     */
    public final void debug(Appendable appendable) {
        debug(appendable, 0);
    }

    void debug(Appendable appendable, int level) {
        Collection<SType<?>> local = getLocalTypes();
        if (!isRecursiveReference() && !local.isEmpty()) {
            local.stream().filter(t -> t.isAttribute()).forEach(t -> t.debug(appendable, level));
            local.stream().filter(t -> !t.isAttribute()).forEach(t -> t.debug(appendable, level));
        }
    }

    protected static Appendable pad(Appendable appendable, int level) {
        try {
            for (int i = level * 3; i > 0; i--) {
                appendable.append(' ');
            }
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
        }
        return appendable;
    }

    final public boolean hasAnyValidation() {
        for (Map.Entry<String, SType<?>> entry : getLocalTypesMap().entrySet()) {
            if(entry.getValue().hasValidation() || entry.getValue().hasAnyValidation()){
                return true;
            }
        }
        return false;
    }

    /**
     * Indica se o tipo é um referência interna (um subcampo ou tipo de lista) que apenas para um tipo que o encapsula.
     * Será marcado como recusivo apenas a referência interna. O tipo que encapsula não será marcado.
     */
    public boolean isRecursiveReference() {
        return recursiveReference;
    }

    final void setRecursiveReference(boolean recursiveReference) {
        this.recursiveReference = recursiveReference;
    }
}
