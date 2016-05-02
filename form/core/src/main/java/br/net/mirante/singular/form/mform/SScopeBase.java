/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.mform;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.base.Preconditions;

public abstract class SScopeBase implements SScope {

    private Map<String, SType<?>> localTypes;

    private static final Logger LOGGER = Logger.getLogger(SScopeBase.class.getName());

    /**
     * Retorna os tipos criados localmente. Se for um pacote, retorna o tipos do
     * pacote. Se for um tipo, então retorna o tipo criados no escopo local do
     * tipo (tipo dentro de tipo).
     * 
     * @return Nunca null
     */
    public Collection<SType<?>> getLocalTypes() {
        return localTypes != null ? localTypes.values() : Collections.emptyList();
    }

    @Override
    public Optional<SType<?>> getLocalTypeOptional(String path) {
        return getLocalTypeOptional(new PathReader(path));
    }

    final Optional<SType<?>> getLocalTypeOptional(PathReader pathReader) {
        if (localTypes == null) {
            return Optional.empty();
        }
        SType<?> tipo = localTypes.get(pathReader.getTrecho());
        if (tipo == null) {
            return Optional.empty();
        } else if (pathReader.isLast()) {
            return Optional.of(tipo);
        }
        return tipo.getLocalTypeOptional(pathReader.next());
    }


    @Override
    public SType<?> getLocalType(String path) {
        // Não utiliza getTipoLocalOpcional, pois da forma abaixo é possível
        // apontar precisamente onde deu erro no path passado.
        return getLocalType(new PathReader(path));
    }

    final SType<?> getLocalType(PathReader pathReader) {
        if (localTypes != null) {
            SType<?> tipo = localTypes.get(pathReader.getTrecho());
            if (tipo != null) {
                if (pathReader.isLast()) {
                    return tipo;
                }
                return tipo.getLocalType(pathReader.next());
            }
        }
        throw new SingularFormException(pathReader.getTextoErro(this, "Não existe o tipo"));
    }

    final <T extends SType<?>> T registerType(T newType, Class<T> classeDeRegistro) {
        return getDictionary().registeType(this, newType, classeDeRegistro);
    }

    final <T extends SType<?>> T registerType(TypeBuilder tb, Class<T> classeDeRegistro) {
        getDictionary().registeType(this, (T) tb.getType(), classeDeRegistro);
        return (T) tb.configure();
    }

    final <T extends SType<?>> T extendType(String simpleNameNewType, T parentType) {
        if (getDictionary() != parentType.getDictionary()) {
            throw new SingularFormException(
                    "O tipo " + parentType.getName() + " foi criado dentro de outro dicionário, que não o atual de " + getName());
        }
        TypeBuilder tb = parentType.extend(simpleNameNewType);
        return registerType(tb, null);
    }

    final <T extends SType<?>> T extendType(String simpleNameNewType, Class<T> parenteTypeClass) {
        T parentType = resolveType(parenteTypeClass);
        return extendType(simpleNameNewType, parentType);
    }

    @SuppressWarnings("unchecked")
    final <I extends SIComposite> STypeList<STypeComposite<I>, I> createListOfNewTypeComposite(String simpleNameNewType,
            String simpleNameNewTypeComposto) {
        STypeList<STypeComposite<I>, I> listType = extendType(simpleNameNewType, STypeList.class);
        listType.setElementsTypeAsNewCompositeType(simpleNameNewTypeComposto);
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
        if (localTypes == null) {
            if (this instanceof STypeComposite) {
                localTypes = new LinkedHashMap<>();
            } else {
                localTypes = new LinkedHashMap<>();
            }
        } else {
            if (localTypes.containsKey(type.getNameSimple())) {
                throw new RuntimeException("A definição '" + type.getNameSimple() + "' já está criada no escopo " + getName());
            }
        }
        localTypes.put(type.getNameSimple(), type);
    }

    public final void debug() {
        debug(0);
    }

    public void debug(int nivel) {
        debug(System.out, nivel);
    }

    public final void debug(Appendable appendable) {
        debug(appendable, 0);
    }

    protected void debug(Appendable appendable, int nivel) {
        if (localTypes != null) {
            localTypes.values().stream().filter(t -> t instanceof SAttribute).forEach(t -> t.debug(appendable, nivel));
            localTypes.values().stream().filter(t -> !(t instanceof SAttribute)).forEach(t -> t.debug(appendable, nivel));
        }
    }

    protected static Appendable pad(Appendable appendable, int nivel) {
        try {
            for (int i = nivel * 3; i > 0; i--) {
                appendable.append(' ');
            }
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
        }
        return appendable;
    }

    final public boolean hasAnyValidation() {
        if(localTypes != null) {
            for (Map.Entry<String, SType<?>> entry : localTypes.entrySet()) {
                if(entry.getValue().hasValidation() || entry.getValue().hasAnyValidation()){
                    return true;
                }
            }
        }
        return false;
    }
}
