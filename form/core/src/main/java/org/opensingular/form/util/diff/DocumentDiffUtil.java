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

package org.opensingular.form.util.diff;

import org.opensingular.form.SIComposite;
import org.opensingular.form.SIList;
import org.opensingular.form.SISimple;
import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.STypeList;
import org.opensingular.form.STypeSimple;
import org.opensingular.form.SingularFormException;
import org.opensingular.form.document.SDocument;
import org.opensingular.form.type.core.attachment.SIAttachment;
import org.opensingular.form.type.core.attachment.STypeAttachment;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Classe utilitária para cálculo da diferença na estrutura e conteúdo de SDocument ou SInstance. <p>Aceita comparar
 * instância de dicionários diferentes ou mesmo de SType diferentes.</p>
 * <p> Utiliza a seguinte heuristica:
 * <ol>
 * <li>Se ambas instâncias forem {@link SISimple}, então compara o {@link SISimple#getValue()} das instâncias;</li>
 * <li>Se ambas instâncias forem {@link SIComposite}, então compara os campos com mesmo nome entre os composites,
 * reaplicando essa heurística para cada campo. Campos com nome sem correpondência no outro composite são considerados
 * com inclusões ou deleções.</li>
 * <li>Se ambas instância forem {@link SIList}, então compara os sub itens da lista que possuirem o
 * mesmo ID de instância, reaplicando essa heurística para cada correspondência. Se uma instância da lista não tiver
 * correspondente na outra lista (não encontra um item na lista com mesmo ID), então será considerada uma inclusão ou
 * exclusão da lista.</li>
 * <li>Não caindo nos casos anteriores, então são considerados como itens totalmente diferentes e ficam como um
 * inclusão e exclusão.</li>
 * </ol>
 * </p>
 *
 * @author Daniel C. Bordin on 24/12/2016.
 */
public final class DocumentDiffUtil {

    /**
     * Registro com os diferentes algorítmos de resolução de diff de acordo com a classe do SType.
     */
    private static TypeDiffRegister register;

    private DocumentDiffUtil() {
    }

    /**
     * Cálcula a diferença entre o conteúdo de dois documentos.
     */
    public static DocumentDiff calculateDiff(SDocument original, SDocument newer) {
        return calculateDiff(original.getRoot(), newer.getRoot());
    }

    /**
     * Cálcula a diferença entre o conteúdo de duas instâncias.
     */
    public static DocumentDiff calculateDiff(SInstance original, SInstance newer) {
        DiffInfo info = calculateDiff(null, original, newer);
        return new DocumentDiff(info, false);
    }

    //TODO Implementar o Diff de String usando a biblioteca / exemplo abaixo
    //https://bitbucket.org/cowwoc/google-diff-match-patch/wiki/Home
    //https://neil.fraser.name/software/diff_match_patch/svn/trunk/demos/demo_diff.html
    //https://bitbucket.org/cowwoc/google-diff-match-patch/wiki/API
    //Se for comparar HTML:
    //https://github.com/DaisyDiff/DaisyDiff

    /**
     * Retorna (nunca null) o diff das duas instâncias informadas, já fazendo a chamada recursiva para o sub nós de
     * cada
     * instância. Ou seja, retorna o DiffInfo preenchido com sub DiffInfo (se for o caso).
     */
    private static DiffInfo calculateDiff(DiffInfo parent, SInstance original, SInstance newer) {
        DiffInfo info = new DiffInfo(original, newer, DiffType.UNKNOWN_STATE);
        CalculatorEntry originalEntry = getRegister().get(original);
        CalculatorEntry newerEntry = getRegister().get(newer);

        if (originalEntry != newerEntry && original != null && newer != null) {
            if (parent == null) {
                calculateDiff(info, original, null);
                calculateDiff(info, null, newer);
                return info;
            } else {
                calculateDiff(parent, original, null);
                return calculateDiff(parent, null, newer);
            }
        }

        CalculatorEntry commonEntry = originalEntry != null ? originalEntry : newerEntry;
        if (commonEntry != null) {
            commonEntry.getCalculator().calculateDiff(info, original, newer);
        }

        if (info.isUnknownState()) {
            throw new SingularFormException("Invalid internal state for " + info);
        }
        if (parent != null) {
            addToParent(parent, info);
        }
        return info;
    }

    /**
     * Calcula o Diff para uma instância do tipo {@link STypeSimple}.
     */
    private static void calculateDiffSimple(DiffInfo info, SInstance original, SInstance newer) {
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
    }

    /**
     * Calcula o Diff para uma instância do tipo {@link STypeComposite}. Faz chamada de diff recursiva para os
     * cada campo da lista.
     */
    private static void calculateDiffComposite(DiffInfo info, SIComposite original, SIComposite newer) {
        Set<String> names = new HashSet<>();
        if (newer != null) {
            for (SType<?> newerTypeField : newer.getType().getFields()) {
                SInstance newerField = newer.getField(newerTypeField);
                SInstance originalField = null;
                if (original != null) {
                    originalField = original.getFieldOpt(newerTypeField.getNameSimple()).orElse(null);
                }
                calculateDiff(info, originalField, newerField);
                names.add(newerTypeField.getNameSimple());
            }
        }
        if (original != null) {
            //noinspection Convert2streamapi
            for (SType<?> originalTypeField : original.getType().getFields()) {
                if (!names.contains(originalTypeField.getNameSimple())) {
                    calculateDiff(info, original.getField(originalTypeField), null);
                }
            }
        }
    }

    /**
     * Calcula o Diff para uma instância do tipo {@link STypeList}. Faz chamada de diff recursiva para os subElementos
     * de cada lista.
     */
    private static void calculateDiffList(@Nonnull DiffInfo info, SIList<?> original, SIList<?> newer) {
        Objects.requireNonNull(info);
        List<? extends SInstance> originals = original == null ? Collections.emptyList() : new ArrayList<>(
                original.getValues());
        List<? extends SInstance> newerList = newer == null ? Collections.emptyList() : new LinkedList<>(
                newer.getValues());
        boolean[] consumed = new boolean[newerList.size()];
        int posNotConsumed = 0;
        for (int posO = 0; posO < originals.size(); posO++) {
            SInstance iO = originals.get(posO);
            int posN = findById(iO.getId(), newerList);
            if (posN != -1) {
                posNotConsumed = calculateDiffNewListElement(info, newerList, posN, consumed, posNotConsumed);
            }
            posNotConsumed = diffLine(info, iO, posO, newerList, consumed, posN, posNotConsumed);
        }

        for (int posN = posNotConsumed; posN < newerList.size(); posN++) {
            if (!consumed[posN]) {
                calculateDiff(info, null, newerList.get(posN)).setNewerIndex(posN);
            }
        }
        if (info.isUnknownState()) {
            info.setType(DiffType.UNCHANGED_EMPTY);
        }
    }

    private static int calculateDiffNewListElement(@Nonnull DiffInfo info, List<? extends SInstance> newerList,
                                                   int posN, boolean[] consumed, int posNotConsumed) {
        int newPosNotConsumed = posNotConsumed;
        for (int posD = posNotConsumed; posD < posN; posD++) {
            if (!consumed[posD]) {
                newPosNotConsumed = diffLine(info, null, -1, newerList, consumed, posD, posNotConsumed);
            }
        }
        return newPosNotConsumed;
    }

    /**
     * Calcula o diff entre dois elementos de lista diferentes, sem fazer chamada recursiva.
     */
    private static int diffLine(DiffInfo parent, SInstance instanceOriginal, int posO,
                                List<? extends SInstance> newerList, boolean[] consumed, int posN, int posNotConsumed) {
        SInstance instanceNewer = posN == -1 ? null : newerList.get(posN);
        DiffInfo info = calculateDiff(parent, instanceOriginal, instanceNewer);
        int returnValue = posNotConsumed;
        info.setOriginalIndex(posO);
        if (posN != -1) {
            info.setNewerIndex(posN);
            consumed[posN] = true;
            if (posN == posNotConsumed + 1) {
                returnValue = posN;
            }
        }
        return returnValue;
    }

    /**
     * Encontra a posição de um elemento dentro da lista com o ID informado. Retorna -1 senão encontrar.
     */
    private static int findById(Integer instanceId, List<? extends SInstance> list) {
        for (int i = 0; i < list.size(); i++) {
            if (instanceId.equals(list.get(i).getId())) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Inclui novo diff em seu pai já alterando o tipo do pai (se necessário) de acordo com o tipo do novo.
     */
    private static void addToParent(DiffInfo parent, DiffInfo info) {
        parent.addChild(info);
        if (parent.isUnknownState() || parent.isUnchangedEmpty()) {
            parent.setType(info.getType());
        } else if (parent.getType() != info.getType() && !info.isUnchangedEmpty()) {
            parent.setType(DiffType.CHANGED_CONTENT);
        }
    }


    /**
     * Calcula o Diff para uma instância do tipo {@link STypeAttachment}. Não faz diff recursivos nos sub campos.
     */
    private static void calculateDiffAttachment(DiffInfo info, SIAttachment original, SIAttachment newer) {
        boolean originalEmpty = original == null || original.getFileId() == null;
        boolean newerEmpty = newer == null || newer.getFileId() == null;

        if (originalEmpty) {
            calculateDiffAttachmentWhenOriginalEmpty(info, newer, newerEmpty);
        } else if (newerEmpty) {
            info.setType(DiffType.CHANGED_DELETED);
        } else {
            calculateDiffAttachmentWhenBothExists(info, original, newer);
        }
    }

    private static void calculateDiffAttachmentWhenBothExists(DiffInfo info, SIAttachment original, SIAttachment newer) {
        if (!Objects.equals(original.getFileName(), newer.getFileName())) {
            if (Objects.equals(original.getFileHashSHA1(), newer.getFileHashSHA1())) {
                info.setDetail("Nome alterado de '" + original.getFileName() + "' para '" + newer.getFileName() +
                        "', mas conteúdo identico (" + original.fileSizeToString() + ')');
            } else {
                info.setDetail("Conteúdo alterado e nome alterado de '" + original.toStringDisplay() + "' para '" +
                        newer.toStringDisplay() + "'");
            }
            info.setType(DiffType.CHANGED_CONTENT);
        } else if (!Objects.equals(original.getFileHashSHA1(), newer.getFileHashSHA1())) {
            info.setDetail("Nome do arquivo o mesmo, mas conteúdo alterado (tamanho anterior " +
                    original.fileSizeToString() + ", novo tamanho " + newer.fileSizeToString() + ')');
            info.setType(DiffType.CHANGED_CONTENT);
        } else {
            info.setType(DiffType.UNCHANGED_WITH_VALUE);
        }
    }

    private static void calculateDiffAttachmentWhenOriginalEmpty(DiffInfo info, SIAttachment newer, boolean newerEmpty) {
        if (newerEmpty) {
            info.setType(DiffType.UNCHANGED_EMPTY);
        } else {
            info.setType(DiffType.CHANGED_NEW);
            info.setDetail(newer.toStringDisplay());
        }
    }

    /**
     * Retorna uma versão compactada do diff informado, onde serão removidas todas as comparações marcada como
     * Unchanged e em alguns casos colapsando um pai e um filho único, quando fizer sentido. Visa apresentar uma
     * informação mais enxuta.
     *
     * @return Null se o Diff e seus subitens não tiver não uma alteração
     */
    static DiffInfo removeUnchangedAndCompact(DiffInfo info) {
        if (info.isUnknownState() || info.isUnchanged()) {
            return null;
        }
        if (!info.hasChildren() || isElementOfListAndDeletedOrNew(info) || isCompositeAndShouldBeResumed(info)) {
            return info.copyWithoutChildren();
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
            return info.copyWithoutChildren();
        } else if (newList.size() > 1) {
            DiffInfo newInfo = info.copyWithoutChildren();
            newList.forEach(newInfo::addChild);
            return newInfo;
        }
        DiffInfo newInfo = newList.get(0);
        newInfo.addPrePath(info);
        return newInfo;
    }

    private static boolean isCompositeAndShouldBeResumed(DiffInfo info) {
        SInstance instance = info.getNewerOrOriginal();
        return (instance instanceof SIComposite) && (instance.asAtrProvider().getProvider() != null);
    }

    private static boolean isElementOfListAndDeletedOrNew(DiffInfo info) {
        return info.isElementOfAList() && (info.isChangedDeleted() || info.isChangedNew());
    }


    /**
     * Retorna o registro de calculadores de diff por tipo.
     */
    private static synchronized TypeDiffRegister getRegister() {
        if (register == null) {
            register = new TypeDiffRegister();
        }
        return register;
    }

    /**
     * Preenche o diff informado de acordo com a diferença de duas instâncias.
     */
    @FunctionalInterface
    private interface TypeDiffCalculator<I extends SInstance> {
        /**
         * Chamado para fazer o cálculo do diff para duas instâncias. Se necessário, o método deve fazer o diff
         * recursivo para as sub informações de cada instância.
         * <p>Uma das instâncias pode ser null, mas nunca ambas. Ambas as instâncias podem ter conteúdo completamente
         * nulls.</p>
         *
         * @param info     Diff a ser preenchido
         * @param original Instância original (pode ser null)
         * @param newer    Nova versão da instância (pode ser null)
         */
        void calculateDiff(@Nonnull DiffInfo info, @Nullable I original, @Nullable I newer);
    }

    /**
     * Registra os diferentes calculadores de diff para cada classe de {@link SType} e permitir procurar o calculador
     * mais pertinente para uma instância. Leva em consideração qual a implementação mas específica para cada caso (por
     * exemplo, um anexo extende composite, mas retorna o específico para anexo).
     * <p>Por em quanto, o registro (mapeamento) de classe e implementação de calculador de diff está hard coded no
     * construtor.</p>
     */
    private static class TypeDiffRegister {
        private final CalculatorEntry root;

        TypeDiffRegister() {
            root = new CalculatorEntry(SType.class, null);
            //Cuidado: A ordem de inclusão faz diferença. Um tipo derivado deve ser adicionado depois do tipo pai
            register(STypeSimple.class, DocumentDiffUtil::calculateDiffSimple);
            register(STypeComposite.class, DocumentDiffUtil::calculateDiffComposite);
            register(STypeList.class, DocumentDiffUtil::calculateDiffList);
            register(STypeAttachment.class, DocumentDiffUtil::calculateDiffAttachment);
        }

        private <I extends SInstance> void register(Class<?> typeClass, TypeDiffCalculator<I> calculator) {
            if (!root.register(new CalculatorEntry(typeClass, calculator))) {
                throw new SingularFormException("O Diff não está preparado para tratar tipos da classe " + typeClass);
            }
        }

        /**
         * Retorna o calculador de diff mais apropriado para a instância ou dispara exception senão encontrar.
         */
        public CalculatorEntry get(SInstance instance) {
            if (instance == null) {
                return null;
            }
            SType<?> type = instance.getType();
            CalculatorEntry entry = root.get(type.getClass());
            if (entry == null || entry == root) {
                throw new SingularFormException(
                        "O Diff não está preparado para tratar tipos da classe " + instance.getType().getClass(),
                        instance);
            }
            return entry;
        }
    }

    /**
     * Representa uma registro (mapeamento) de classe de SType e sua respectiva implementação de diff, bem como,
     * registrar os sub calculadores mais específicos para os tipos derivados da classe de SType.
     * <p>Por exemplo, a entrada para {@link STypeComposite} tera como sub entrada para {@link STypeAttachment}, pois
     * esse último é uma class derivada de {@link STypeComposite}.</p>
     */
    private static class CalculatorEntry {
        private final Class<?> typeClass;
        private final TypeDiffCalculator<?> calculator;
        private List<CalculatorEntry> subEntries;

        private CalculatorEntry(Class<?> typeClass, TypeDiffCalculator<?> calculator) {
            this.typeClass = typeClass;
            this.calculator = calculator;
        }

        /**
         * Retorna a class de {@link SType} para a qual essa entrada se refere.
         */
        public Class<?> getTypeClass() {
            return typeClass;
        }

        /**
         * Retorna a implementação do calculador de diff para a classe atual.
         */
        @SuppressWarnings("unchecked")
        public TypeDiffCalculator<SInstance> getCalculator() {
            return (TypeDiffCalculator<SInstance>) calculator;
        }

        /**
         * Registra a entrada informada com sub entrada da entrada atual se a mesma for de uma classe de {@link SType}
         * derivada da classe da entrada atual.
         *
         * @return True se adicionou como entrada ou False a entrada não pertecer com sub entrada da atual.
         */
        final boolean register(CalculatorEntry newEntry) {
            if (!this.typeClass.isAssignableFrom(newEntry.getTypeClass())) {
                return false;
            }
            if (subEntries == null) {
                subEntries = new ArrayList<>(2);
            } else {
                for (CalculatorEntry e : subEntries) {
                    if (e.register(newEntry)) {
                        return true;
                    }
                }
            }
            subEntries.add(newEntry);
            return true;
        }

        /**
         * Localiza a entrada mais específica possível para a classe de {@link SType} informada ou retorna null senão
         * tiver nenhum match.
         */
        public CalculatorEntry get(Class<?> typeClassTarget) {
            if (!this.typeClass.isAssignableFrom(typeClassTarget)) {
                return null;
            } else if (subEntries != null) {
                CalculatorEntry result;
                for (CalculatorEntry e : subEntries) {
                    result = e.get(typeClassTarget);
                    if (result != null) {
                        return result;
                    }
                }
            }
            return this;
        }
    }
}
