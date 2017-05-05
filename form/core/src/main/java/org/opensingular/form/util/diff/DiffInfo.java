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

    private String simpleName;

    private String simpleLabel;

    private static final Logger LOGGER = Logger.getLogger(DiffInfo.class.getName());

    DiffInfo(SInstance original, SInstance newer, DiffType type) {
        this.original = original;
        this.newer = newer;
        this.type = type;

        //Copia dados básicos para o caso do diff ser serializado
        SInstance instance = newer == null ? original : newer;
        this.simpleName = instance.getType().getNameSimple();
        this.simpleLabel = instance.getType().asAtr().getLabel();
    }

    /** Faz uma copia do diff sem copiar os diffs filhos do mesmo para a nova copia. */
    final DiffInfo copyWithoutChildren() {
        DiffInfo newInfo = new DiffInfo(getOriginal(), getNewer(), getType());
        newInfo.setOriginalIndex(getOriginalIndex());
        newInfo.setNewerIndex(getNewerIndex());
        newInfo.setDetail(getDetail());
        newInfo.simpleName = this.simpleName;
        newInfo.simpleLabel = this.simpleLabel;
        return newInfo;
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

    /**
     * Retorna SInstance envolvido na comparação. Retorna a instância nova se não nula ou a instância original se a
     * nova for null.
     *
     * @return Nunca retorna null
     */
    public SInstance getNewerOrOriginal() {
        return newer != null ? newer : original;
    }

    /** Define o tipo resultante da comparação. */
    final void setType(DiffType type) {
        this.type = Objects.requireNonNull(type);
    }

    /** Indica se possui sub itens de comparação (itens comparação para as sub instâncias). */
    public boolean hasChildren() {
        return children != null && !children.isEmpty();
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
            String token = pathReader.getToken();
            for (DiffInfo info : children) {
                if (info.getOriginalOrNewer().getName().equals(token)) {
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
     * original for null ou senão fazia parte de uma lista, então retorna -1. <p> Esse método e {@link
     * #getNewerIndex()} em conjunto permitem verificar de onde para onde um item de uma lista foi movido ou incluido
     * ou removido.</p>
     */
    public int getOriginalIndex() {
        return originalIndex;
    }

    /**
     * Se a instância nova estava dentro de uma lista, indica qual o índice dela dentro dessa lista. Se a instancia
     * nova for null ou senão fazia parte de uma lista, então retorna -1. <p> {@link #getOriginalIndex()} e esse
     * método em conjunto permitem verificar de onde para onde um item de uma lista foi movido ou incluido
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

    /** Retorna um ID único do diff dentro do {@link org.opensingular.form.util.diff.DocumentDiff} a que pertence. */
    public Integer getId() {
        return id;
    }

    final void setId(Integer id) {
        this.id = id;
    }

    /**
     * Retorna o nome simple do SType a que se refere a instância do diff. Esse método pode ser chamado mesmo depois de
     * uma serialização, pois a informação é replicada do SType.
     */
    public String getSimpleName() {
        return simpleName;
    }

    /**
     * Retorna o label do SType a que se refere a instância do diff. Esse método pode ser chamado mesmo depois de
     * uma serialização, pois a informação é replicada do SType.
     * @return Pode ser null
     */
    public String getSimpleLabel() {
        return simpleLabel;
    }

    /**
     * Retorna o caminho do diff em relação ao diff pai do mesmo em um formato mais técnico.
     */
    public String getName() {
        return getPath(false);
    }

    /**
     * Retorna o caminho do diff em relação ao diff pai do mesmo, mas usando label (quando disponível) e representação
     * de linha mais amigáveis para exibição para o usuário.
     */
    public String getLabel() {
        return getPath(true);
    }

    /**
     * Gera o path do diff, incluindo indices de linhas e diff compactados no diff atual se for o caso.
     *
     * @param showLabel Se true, busca usar o label de cada diff em vez do nome do mesmo. Ou seja, busca gerar o path
     *                  mais user frendily
     */
    private String getPath(boolean showLabel) {
        StringBuilder sb = new StringBuilder();
        if (prePath == null) {
            addPathItem(sb, this, false, showLabel);
        } else {
            for (int i = 0; i < prePath.size(); i++) {
                addPathItem(sb, prePath.get(i), i != 0, showLabel);
            }
            addPathItem(sb, this, true, showLabel);
        }
        return sb.toString();
    }

    /** Gera o nome ou índice que representa o caminha do item informado. */
    private static void addPathItem(StringBuilder sb, DiffInfo info, boolean hasPrevious, boolean showLabel) {
        if (info.isElementOfAList()) {
            addPathList(sb, info, hasPrevious, showLabel);
        } else {
            addPathSimple(sb, info, hasPrevious, showLabel);
        }
    }

    private static void addPathList(StringBuilder sb, DiffInfo info, boolean hasPrevious, boolean showLabel) {
        if (showLabel) {
            if (hasPrevious) {
                sb.append(" : ");
            }
            if (info.originalIndex != -1) {
                sb.append("Linha ").append(info.originalIndex + 1);
            } else {
                sb.append("Linha nova");
            }
        } else {
            sb.append('[');
            if (info.newerIndex == info.originalIndex) {
                sb.append(info.originalIndex);
            } else {
                sb.append(info.originalIndex == -1 ? " " : info.originalIndex);
                sb.append('>').append(info.newerIndex == -1 ? " " : info.newerIndex);
            }
            sb.append(']');
        }
    }

    private static void addPathSimple(StringBuilder sb, DiffInfo info, boolean hasPrevious, boolean showLabel) {
        if (hasPrevious) {
            if (showLabel) {
                sb.append(" : ");
            } else {
                sb.append('.');
            }
        }
        if (showLabel && info.simpleLabel != null) {
            sb.append(info.simpleLabel);
        } else {
            sb.append(info.simpleName);
        }
    }

    /**
     * Imprime para o console a árvore de comparação resultante de forma indentada e indicando o resultado da
     * comparação de cada item da estrutura.
     */
    public void debug() {
        debug(System.out, 0, true, false);
    }

    /**
     * Imprime para o console a árvore de comparação resultante de forma indentada e indicando o resultado da
     * comparação de cada item da estrutura.
     *
     * @param showAll   Indica se exibe todos os itens (true) ou somente aqueles que tiveram alteração (false)
     * @param showLabel Se false, usar o nome simples dos tipos. Se true, usa o label das instâncias se existir.
     */
    public void debug(boolean showAll, boolean showLabel) {
        debug(System.out, 0, showAll, showLabel);
    }

    /**
     * Imprime para a saída informa o item atual de forma indentada e depois chama para os demais subitens se
     * existirem.
     */
    private void debug(Appendable appendable, int level, boolean showAll, boolean showLabel) {
        if (!showAll && isUnchanged()) {
            return;
        }
        try {
            pad(appendable, level);
            appendType(appendable);
            appendable.append(getPath(showLabel));
            if (StringUtils.isNotBlank(detail)) {
                appendable.append(" : ").append(detail);
            }
            appendable.append('\n');
            if (children != null) {
                for (DiffInfo info : children) {
                    info.debug(appendable, level + 1, showAll, showLabel);
                }
            }
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    private void appendType(Appendable appendable) throws IOException {
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
    }

    private static void pad(Appendable appendable, int level) throws IOException {
        for (int i = level * 3; i > 0; i--) {
            appendable.append(' ');
        }
    }
}

