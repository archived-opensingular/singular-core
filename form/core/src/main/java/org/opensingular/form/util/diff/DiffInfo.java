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

import org.apache.commons.lang3.StringUtils;
import org.opensingular.form.SInstance;
import org.opensingular.form.SingularFormException;
import org.opensingular.form.internal.PathReader;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Representa a informação de compração entre duas instâncias. <p> Pode ser que uma das instâncias seja null quando for
 * uma alteração de criação ou exclusão. Também pode contem uma lista de informações de comparações para os sub itens
 * da instância (ver {@link #getChildren()}). </p>
 * <p>O Diff é serializável, mas perde o apontamento para as instâncias original e a nova.</p>
 *
 * @author Daniel C. Bordin on 24/12/2016.
 */
public final class DiffInfo implements Serializable {

    private Integer id;

    private transient final SInstance original;
    private transient final SInstance newer;

    private int originalIndex = -1;
    private int newerIndex = -1;

    private DiffType type = DiffType.UNKNOWN_STATE;

    private List<DiffInfo> prePath;

    private List<DiffInfo> children;

    private String detail;

    private static final Logger LOGGER = Logger.getLogger(DiffInfo.class.getName());

    DiffInfo(SInstance original, SInstance newer, DiffType type) {
        this.original = original;
        this.newer = newer;
        this.type = type;
    }

    /** Tipo resultante da comparação entre as instâncias. Nunca null. */
    public DiffType getType() {
        return type;
    }

    /** Indica que foi alterado ou por ter sido apagado da lista ou pelo conteudo ter sido alterado tudo para null. */
    public boolean isChangedNew() {
        return type == DiffType.CHANGED_NEW;
    }

    /**
     * Indica que o conteúdo foi alterado, sendo que tanto antes quando depois as instâncias tinham conteudo diferente
     * de null.
     */
    public boolean isChangedContent() {
        return type == DiffType.CHANGED_CONTENT;
    }

    /** Foi alterado ou por ter sido apagado da lista ou pelo conteudo ter sido alterado tudo para null. */
    public boolean isChangedDeleted() {
        return type == DiffType.CHANGED_DELETED;
    }

    /**
     * AS instância não tiverem o conteúdo alterado. Se true , então {@link #isUnchangedEmpty()} ou {@link
     * #isUnchangedWithValue()} será true.
     */
    public boolean isUnchanged() {
        return type == DiffType.UNCHANGED_EMPTY || type == DiffType.UNCHANGED_WITH_VALUE;
    }

    /** As instâncias não foram alterandas e ambas apresentem o mesmo valor não nulo atribuido. */
    public boolean isUnchangedWithValue() {
        return type == DiffType.UNCHANGED_WITH_VALUE;
    }

    /** As instâncias não foram alterandas e apresentem ambas conteúdo null */
    public boolean isUnchangedEmpty() {
        return type == DiffType.UNCHANGED_EMPTY;
    }

    /** Resultado da comparação indefinido */
    public boolean isUnknownState() {
        return type == DiffType.UNKNOWN_STATE;
    }

    /**
     * Representa a instância original sobre a qual foi feita a comparação. Pode se null se for uma alteração de
     * criação.
     */
    public SInstance getOriginal() {
        return original;
    }

    /**
     * Representa a instância nova sobre a qual foi feita a comparação. Pode se null se for uma alteração de remoção .
     */
    public SInstance getNewer() {
        return newer;
    }

    /**
     * Retorna SInstance envolvido na comparação. Retorna a instância original se não nula ou a instância nova se a
     * original for null.
     *
     * @return Nunca retorna null
     */
    public SInstance getOriginalOrNewer() {
        return original != null ? original : newer;
    }

    /** Define o tipo resultante da comparação. */
    final void setType(DiffType type) {
        this.type = Objects.requireNonNull(type);
    }

    /** Indica se possui sub itens de comparação (itens comparação para as sub instâncias). */
    public boolean hasChildren() {
        return children == null ? false : !children.isEmpty();
    }

    /**
     * retorna os sub itens de comparação (itens de comparação para as sub instâncias). Nunca null, mas pode ser uma
     * lista vazia.
     */
    public List<DiffInfo> getChildren() {
        return children == null ? Collections.emptyList() : children;
    }

    /**
     * Conta a quantidade de alterações encontradas. São contada apenas as alterações em itens ou sub-itens que não
     * possuam sub itens adicionais (ou seja, conta apenas as alterações nas folhas).
     */
    public int getQtdChanges() {
        int s = 0;
        if (children == null || children.isEmpty()) {
            if (!isUnchanged()) {
                return 1;
            }
        } else {
            for (DiffInfo info : children) {
                s += info.getQtdChanges();
            }
        }
        return s;
    }

    /**
     * Adiciona um caminho de path para chegar no item a ser comparado. O item sendo adicionado foi suprimido da
     * estrutura a fim de comapctá-la.
     */
    final void addPrePath(DiffInfo info) {
        if (prePath == null) {
            prePath = new LinkedList<>();
        }
        prePath.add(0, info);
    }

    /**
     * Adiciona uma sub comparação a comparação atual, representando um comparação para uma sub instância das instância
     * sendo comparada no diff atual.
     */
    final void addChild(DiffInfo subDiff) {
        if (children == null) {
            children = new ArrayList<>();
        }
        children.add(subDiff);
    }

    /**
     * Varre a árvore de comparações em profundidade, retornando, se houver, o primeiro diff que atende ao predicado
     * informado.
     */
    final Optional<DiffInfo> findFirst(Predicate<DiffInfo> predicate) {
        if (predicate.test(this)) {
            return Optional.of(this);
        } else if (children != null) {
            for (DiffInfo info : children) {
                Optional<DiffInfo> result = info.findFirst(predicate);
                if (result.isPresent()) {
                    return result;
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Retorna o DiffInfo referente ao path informado. Usa mesma anotação para path de instâncias. Para consulta
     * indexadas (por exemplo, "nomes[3]", o índice considerado não é o da lista da SInstance, mas da lista da sub
     * lista de comparação.
     *
     * @return Nunca retorna null.
     */
    final DiffInfo get(PathReader pathReader) {
        if (pathReader.isEmpty()) {
            return this;
        } else if (pathReader.isIndex()) {
            int index = pathReader.getIndex();
            if (children == null || children.size() <= index) {
                throw new SingularFormException(getErrorMsg(pathReader, "Índice inválido"), getOriginalOrNewer());
            }
            return children.get(index).get(pathReader.next());
        } else if (children != null) {
            String trecho = pathReader.getToken();
            for (DiffInfo info : children) {
                if (info.getOriginalOrNewer().getName().equals(trecho)) {
                    return info.get(pathReader.next());
                }
            }
        }
        throw new SingularFormException(getErrorMsg(pathReader, "Não encontrado"), getOriginalOrNewer());
    }

    /** Retorna o sub diff na posição indicada. */
    final DiffInfo get(int childIndex) {
        return getChildren().get(childIndex);
    }

    /** Monta uma mensagem de erro referente ao processo do path informado. */
    private String getErrorMsg(PathReader pathReader, String msg) {
        return pathReader.getErrorMsg("Para o diff da instância " + getOriginalOrNewer().getPathFull(), msg);
    }

    /** Se a instância noa estava dentro de um tipo lista, informa a posição dentro da lista. */
    final void setNewerIndex(int newerIndex) {
        this.newerIndex = newerIndex;
    }

    /** Se a instância original estava dentro de um tipo lista, informa a posição dentro da lista. */
    final void setOriginalIndex(int originalIndex) {
        this.originalIndex = originalIndex;
    }

    /**
     * Se a instância original estava dentro de uma lista, indica qual o índice dela dentro dessa lista. Se a instancia
     * original for null ou senão fazia parte de uma lista, então retorna -1. <p> {@link #getOriginalIndex()} e {@link
     * #getNewerIndex()} em conjunto permitem verificar de onde para onde um item de uma lista foi movido ou incluido
     * ou removido.</p>
     */
    public int getOriginalIndex() {
        return originalIndex;
    }

    /**
     * Se a instância nova estava dentro de uma lista, indica qual o índice dela dentro dessa lista. Se a instancia
     * nova for null ou senão fazia parte de uma lista, então retorna -1. <p> {@link #getOriginalIndex()} e {@link
     * #getNewerIndex()} em conjunto permitem verificar de onde para onde um item de uma lista foi movido ou incluido
     * ou removido.</p>
     */
    public int getNewerIndex() {
        return newerIndex;
    }

    /** Indica se o diff se refere uma instancia que elemento de uma lista. */
    final boolean isElementOfAList() {
        return newerIndex != -1 || originalIndex != -1;
    }

    /** Define informação complemetar para a alteração detectada. */
    final void setDetail(String detail) {
        this.detail = detail;
    }

    /** Retorna informação complementar para a alteração detectada. Pode ser null. */
    public String getDetail() {
        return detail;
    }

    /**
     * Imprime para o console a árvore de comparação resultante de forma indentada e indicando o resultado da
     * comparação de cada item da estrutura.
     */
    public void debug() {
        debug(System.out, 0, true);
    }

    /**
     * Imprime para o console a árvore de comparação resultante de forma indentada e indicando o resultado da
     * comparação de cada item da estrutura.
     *
     * @param showAll Indica se exibe todos os itens (true) ou somente aqueles que tiveram alteração (false)
     */
    public void debug(boolean showAll) {
        debug(System.out, 0, showAll);
    }

    /**
     * Imprime para a saída informa o item atual de forma indentada e depois chama para os demais subitens se
     * existirem.
     */
    private void debug(Appendable appendable, int level, boolean showAll) {
        if (!showAll && isUnchanged()) {
            return;
        }
        try {
            pad(appendable, level);
            switch (type) {
                case UNCHANGED_WITH_VALUE:
                    appendable.append('1');
                    break;
                case UNCHANGED_EMPTY:
                    appendable.append('0');
                    break;
                case CHANGED_NEW:
                    appendable.append('+');
                    break;
                case CHANGED_DELETED:
                    appendable.append('-');
                    break;
                case CHANGED_CONTENT:
                    appendable.append('~');
                    break;
                default:
                    appendable.append('?');
            }
            if (prePath == null) {
                printPathItem(appendable, this, false);
            } else {
                for (int i = 0; i < prePath.size(); i++) {
                    printPathItem(appendable, prePath.get(i), i != 0);
                }
                printPathItem(appendable, this, true);
            }
            if (StringUtils.isNotBlank(detail)) {
                appendable.append(" : ").append(detail);
            }
            appendable.append('\n');
            if (children != null) {
                for (DiffInfo info : children) {
                    info.debug(appendable, level + 1, showAll);
                }
            }
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    /** Imprime o nome ou índice que representa o caminha do item informado. */
    private static void printPathItem(Appendable appendable, DiffInfo info, boolean hasPrevious) throws IOException {
        if (info.newerIndex != -1 || info.originalIndex != -1) {
            appendable.append('[');
            if (info.newerIndex == info.originalIndex) {
                appendable.append(Integer.toString(info.originalIndex));
            } else {
                appendable.append(info.originalIndex == -1 ? " " : Integer.toString(info.originalIndex));
                appendable.append('>').append(info.newerIndex == -1 ? " " : Integer.toString(info.newerIndex));
            }
            appendable.append(']');
        } else {
            if (hasPrevious) {
                appendable.append('.');
            }
            appendable.append(info.getOriginalOrNewer().getName());
        }
    }


    private static Appendable pad(Appendable appendable, int level) throws IOException {
        for (int i = level * 3; i > 0; i--) {
            appendable.append(' ');
        }
        return appendable;
    }

    public Integer getId() {
        return id;
    }

    final void setId(Integer id) {
        this.id = id;
    }
}

