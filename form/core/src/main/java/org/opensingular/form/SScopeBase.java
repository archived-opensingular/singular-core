/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.form;

import org.opensingular.form.processor.TypeProcessorPublicFieldsReferences;
import com.google.common.base.Preconditions;

import java.io.IOException;
import java.util.*;
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
     *
     * @return Nunca null
     */
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
    public Optional<SType<?>> getLocalTypeOptional(String path) {
        return getLocalTypeOptional(new PathReader(path));
    }

    final Optional<SType<?>> getLocalTypeOptional(PathReader pathReader) {
        SType<?> type = getLocalTypesMap().get(pathReader.getTrecho());
        if (type == null) {
            return Optional.empty();
        } else if (pathReader.isLast()) {
            return Optional.of(type);
        }
        return type.getLocalTypeOptional(pathReader.next());
    }


    @Override
    public SType<?> getLocalType(String path) {
        // Não utiliza getTipoLocalOpcional, pois da forma abaixo é possível
        // apontar precisamente onde deu erro no path passado.
        return getLocalType(new PathReader(path));
    }

    final SType<?> getLocalType(PathReader pathReader) {
        SType<?> type = getLocalTypesMap().get(pathReader.getTrecho());
        if (type != null) {
            if (pathReader.isLast()) {
                return type;
            }
            return type.getLocalType(pathReader.next());
        }
        throw new SingularFormException(pathReader.getTextoErro(this, "Não foi encontrado o tipo '" + pathReader.getTrecho() + "' em '"  + getName() + "'"));
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
        if (newType.getSuperType() == null || newType.getSuperType().getClass() != newType.getClass()) {
            newType.setCallingOnLoadType(true);
            newType.onLoadType(new TypeBuilder(newType));
            newType.setCallingOnLoadType(false);
            TypeProcessorPublicFieldsReferences.INSTANCE.processTypePosRegister(newType, true);
            getDictionary().runPendingTypeProcessorExecution(newType);
        } else{
            if (newType.isRecursiveReference()) {
                if(isSuperTypeCallingOnLoadType(newType)) {
                    //Não pode rodar os processadores em quanto nao tiver terminado o onLoadType do tipo pai
                    newType.setCallingOnLoadType(true);
                    getDictionary().addTypeProcessorForLatterExecutuion(newType, () -> {
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
            if (localTypes == null) {
                localTypes = new LinkedHashMap<>();
            } else {
                if (localTypes.containsKey(type.getNameSimple())) {
                    throw new SingularFormException(
                            "A definição '" + type.getNameSimple() + "' já está criada no escopo " + getName());
                }
            }
            localTypes.put(type.getNameSimple(), type);
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

    protected void debug(Appendable appendable, int level) {
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
