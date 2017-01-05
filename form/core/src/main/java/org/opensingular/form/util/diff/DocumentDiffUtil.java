/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
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

package org.opensingular.form.util.diff;

import org.opensingular.form.*;
import org.opensingular.form.document.SDocument;

import java.util.*;

/**
 * Classe utilitária para cálculo da diferença na estrutura e conteúdo de SDocument ou SIntance. <p>Aceita comparar
 * instância de dicionários diferentes ou mesmo de SType diferentes.</p><p> Utiliza a seguinte heuristica: <ol> <li>Se
 * ambas instâncias forem {@link SISimple}, então compara o {@link SISimple#getValue()} das instâncias;</li> <li>Se
 * ambas instâncias forem {@link SIComposite}, então compara os campos com mesmo nome entre os composites, reaplicando
 * essa heurística para cada campo. Campos com nome sem correpondência no outro composite são considerados com inclusões
 * ou deleções.</li> <li>Se ambas instância forem {@link SIList}, então compara os sub itens da lista que possuirem o
 * mesmo ID de instância, reaplicando essa heurística para cada correspondência. Se uma instância da lista não tiver
 * correspondente na outra lista (não encontra um item na lista com mesmo ID), então será considerada uma inclusão ou
 * exclusão da lista.</li><li>Não caindo nos casos anteriores, então são considerados como itens totalmente diferentes e
 * ficam como um inclusão e exclusão.</li> </ol> </p>
 *
 * @author Daniel C. Bordin on 24/12/2016.
 */
public final class DocumentDiffUtil {

    private DocumentDiffUtil() {}

    /** Cálcula a diferença entre o conteúdo de dois documentos. */
    public static DocumentDiff calculateDiff(SDocument original, SDocument newer) {
        return calculateDiff(original.getRoot(), newer.getRoot());
    }

    /** Cálcula a diferença entre o conteúdo de duas instâncias. */
    public static DocumentDiff calculateDiff(SInstance original, SInstance newer) {
        DiffInfo info = calculateDiff(null, original, newer);
        return new DocumentDiff(info, false);
    }

    private static DiffInfo calculateDiff(DiffInfo parent, SInstance original, SInstance newer) {
        DiffInfo info = new DiffInfo(original, newer, DiffType.UNKNOWN_STATE);
        Class<?> originalClass = simplifyType(original);
        Class<?> newerClass = simplifyType(newer);
        if (originalClass != newerClass && original != null && newer != null) {
            if (parent == null) {
                calculateDiff(info, original, null);
                calculateDiff(info, null, newer);
                return info;
            }
            calculateDiff(parent, original, null);
            return calculateDiff(parent, null, newer);
        }

        Class<?> commonClass = originalClass != null ? originalClass : newerClass;
        if (commonClass == STypeSimple.class) {
            Object originalValue = original == null ? null : original.getValue();
            Object newerValue = newer == null ? null : newer.getValue();
            if (originalValue == null) {
                if (newerValue == null) {
                    info.setType(DiffType.UNCHANGED_EMPTY);
                } else {
                    info.setType(DiffType.CHANGED_NEW);
                }
            } else if (newerValue == null) {
                info.setType(DiffType.CHANGED_DELETED);
            } else if (originalValue.equals(newerValue)) {
                info.setType(DiffType.UNCHANGED_WITH_VALUE);
            } else {
                info.setType(DiffType.CHANGED_CONTENT);
            }
        } else if (commonClass == STypeComposite.class) {
            Set<String> names = new HashSet<>();
            if (newer != null) {
                for (SType<?> newerTypeField : ((STypeComposite<?>) newer.getType()).getFields()) {
                    SInstance newerField = ((SIComposite) newer).getField(newerTypeField);
                    SInstance originalField = null;
                    if (original != null) {
                        originalField = ((SIComposite) original).getFieldOpt(newerTypeField.getNameSimple()).orElse(
                                null);
                    }
                    calculateDiff(info, originalField, newerField);
                    names.add(newerTypeField.getNameSimple());
                }
            }
            if (original != null) {
                for (SType<?> originalTypeField : ((STypeComposite<?>) original.getType()).getFields()) {
                    if (!names.contains(originalTypeField.getNameSimple())) {
                        calculateDiff(info, ((SIComposite) original).getField(originalTypeField), null);
                    }
                }
            }
        } else if (commonClass == STypeList.class) {
            calculateDiffList(info, (SIList<?>) original, (SIList<?>) newer);
        }

        if (info.isUnknownState()) {
            throw new SingularFormException("Invalid internal state for " + info);
        }
        if (parent != null) {
            addToParent(parent, info);
        }
        return info;
    }

    private static void calculateDiffList(DiffInfo parent, SIList<?> original, SIList<?> newer) {
        List<? extends SInstance> originals = original == null ? Collections.emptyList() : new ArrayList<>(
                original.getValues());
        List<? extends SInstance> newers = newer == null ? Collections.emptyList() : new LinkedList<>(
                newer.getValues());
        boolean[] consumed = new boolean[newers.size()];
        int posNotConsumed = 0;
        for (int posO = 0; posO < originals.size(); posO++) {
            SInstance iO = originals.get(posO);
            int posN = findById(iO.getId(), newers);
            if (posN == -1) {
                posNotConsumed = diffLinha(parent, iO, posO, newers, consumed, posN, posNotConsumed);
            } else {
                for (int posD = posNotConsumed; posD < posN; posD++) {
                    if (!consumed[posD]) {
                        posNotConsumed = diffLinha(parent, null, -1, newers, consumed, posD, posNotConsumed);
                    }
                }
                posNotConsumed = diffLinha(parent, iO, posO, newers, consumed, posN, posNotConsumed);
            }
        }

        for (int posN = posNotConsumed; posN < newers.size(); posN++) {
            if (!consumed[posN]) {
                calculateDiff(parent, null, newers.get(posN)).setNewerIndex(posN);
            }
        }
        if (parent.isUnknownState()) {
            parent.setType(DiffType.UNCHANGED_EMPTY);
        }
    }

    private static int diffLinha(DiffInfo parent, SInstance instanceOriginal, int posO,
            List<? extends SInstance> newers, boolean[] consumed, int posN, int posNotConsumed) {
        SInstance instanceNewer = posN == -1 ? null : newers.get(posN);
        DiffInfo info = calculateDiff(parent, instanceOriginal, instanceNewer);
        info.setOriginalIndex(posO);
        if (posN != -1) {
            info.setNewerIndex(posN);
            consumed[posN] = true;
            if (posN == posNotConsumed + 1) {
                posNotConsumed = posN;
            }
        }
        return posNotConsumed;
    }

    private static int findById(Integer instanceId, List<? extends SInstance> list) {
        for (int i = 0; i < list.size(); i++) {
            if (instanceId.equals(list.get(i).getId())) {
                return i;
            }
        }
        return -1;
    }

    private static void addToParent(DiffInfo parent, DiffInfo info) {
        parent.addChild(info);
        if (parent.isUnknownState() || parent.isUnchangedEmpty()) {
            parent.setType(info.getType());
        } else if (parent.getType() != info.getType() && !info.isUnchangedEmpty()) {
            parent.setType(DiffType.CHANGED_CONTENT);
        }
    }

    private static Class<?> simplifyType(SInstance instance) {
        if (instance == null) {
            return null;
        }
        SType<?> type = instance.getType();
        while (true) {
            if (type.getClass() == STypeSimple.class || type.getClass() == STypeComposite.class ||
                    type.getClass() == STypeList.class) {
                return type.getClass();
            } else if (type.getClass() == SType.class) {
                throw new SingularFormException(
                        "O Diff não está preparado para tratar tipos da classe " + type.getClass(), instance);
            }
            type = type.getSuperType();
        }
    }

    /**
     * Retorna uma versão compactada do diff informado, onde serão removidas todas as comparações marcada como Unchanged
     * e em alguns casos colapsando um pai e um filho único, quando fizer sentido. Visa apresenta uma informação mais
     * enxuta.
     * @return Null se o Diff e seus subitens não tiver não uma alteração
     */
    static DiffInfo removeUnchangedAndCompact(DiffInfo info) {
        if (info.isUnknownState() || info.isUnchanged()) {
            return null;
        }
        if (!info.hasChildren() || isListDeletedOrNewElement(info)) {
            return copyWithoutChildren(info);
        }
        List<DiffInfo> children = info.getChildren();
        List<DiffInfo> newList = new ArrayList<>(children.size());
        for (DiffInfo child : children) {
            child = removeUnchangedAndCompact(child);
            if (child != null) {
                newList.add(child);
            }
        }
        if (newList.isEmpty()) {
            return copyWithoutChildren(info);
        } else if (newList.size() > 1) {
            DiffInfo newInfo = copyWithoutChildren(info);
            newList.stream().forEach(child -> newInfo.addChild(child));
            return newInfo;
        }
        DiffInfo newInfo = newList.get(0);
        newInfo.addPrePath(info);
        return newInfo;
    }

    private static boolean isListDeletedOrNewElement(DiffInfo info) {
        return info.isElementOfAList() && (info.isChangedDeleted() || info.isChangedNew());
    }

    private static DiffInfo copyWithoutChildren(DiffInfo info) {
        DiffInfo newInfo = new DiffInfo(info.getOriginal(), info.getNewer(), info.getType());
        newInfo.setOriginalIndex(info.getOriginalIndex());
        newInfo.setNewerIndex(info.getNewerIndex());
        return newInfo;
    }
}
