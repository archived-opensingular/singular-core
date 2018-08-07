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

import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.form.internal.PathReader;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Representa o resultado total de uma comparação na forma de uma árvore de {@link DiffInfo}. A arvoré contêm a
 * informação de comparação para todas as instâncias da versão original e da versão nova, mesmo que a resultado seja o
 * de indicação de não alteração para cada nó. <p>A informação para uma instância específica pode ser obtida usando o
 * método {@link #getByNewer(SInstance)} ou {@link #getByOriginal(SInstance)}.</p>
 * <p>O Diff é serializável, mas perde o apontamento para as instâncias original e a nova.</p>
 *
 * @author Daniel C. Bordin on 24/12/2016.
 */
public class DocumentDiff implements Serializable {

    private final DiffInfo diffRoot;

    private final HashMap<Integer, DiffInfo> infoForOriginal = new HashMap<>();
    private final HashMap<Integer, DiffInfo> infoForNewer = new HashMap<>();
    private final HashMap<Integer, DiffInfo> byId = new HashMap<>();
    private Integer lastId = 0;

    DocumentDiff(DiffInfo diffRoot, boolean compacting) {
        this.diffRoot = diffRoot;
        addToMap(diffRoot, compacting);
    }

    /**
     * Indexa as informações de diff pelos IDs das instâncias.
     *
     * @param info       info a ser adicionado no documento
     * @param compacting indica se esse é um diff compactado
     */
    private void addToMap(DiffInfo info, boolean compacting) {
        if (!compacting) {
            if (info.getOriginal() != null) {
                infoForOriginal.put(info.getOriginal().getId(), info);
            }
            if (info.getNewer() != null) {
                infoForNewer.put(info.getNewer().getId(), info);
            }
        }

        info.setId(lastId++);
        byId.put(info.getId(), info);

        if (info.hasChildren()) {
            info.getChildren().forEach(c -> addToMap(c, compacting));
        }
    }

    /** Retorna a informação de comparação para as instâncias raizes. */
    public DiffInfo getDiffRoot() {
        return diffRoot;
    }

    /**
     * Conta a quantidade de alterações encontradas. São contada apenas as alterações em itens ou sub-itens que não
     * possuam sub itens adicionais (ou seja, conta apenas as alterações nas folhas).
     */
    public int getQtdChanges() {
        return diffRoot == null ? 0 : diffRoot.getQtdChanges();
    }

    /** Indica se foi detectada alguma alteração como resultado da comparação. */
    public boolean hasChange() {
        return diffRoot != null && !diffRoot.isUnchanged();
    }

    /**
     * Varre a árvore de comparações em profundidade, retornando, se houver, o primeiro diff que atende ao predicado
     * informado.
     */
    public Optional<DiffInfo> findFirst(Predicate<DiffInfo> predicate) {
        return diffRoot == null ? Optional.empty() : diffRoot.findFirst(predicate);
    }

    /** Veja {@link DiffInfo#get(PathReader)}. */
    final DiffInfo get(String path) {
        return diffRoot.get(new PathReader(path));
    }

    /** Retorna o primeiro diff que encotrar na árvore que é do tipo informado ou de sub tipo do tipo informado. */
    final Optional<DiffInfo> findFirst(SType<?> field) {
        return findFirst(diff -> (diff.getOriginal() != null && diff.getOriginal().getType().isTypeOf(field)) ||
                (diff.getNewer() != null && diff.getNewer().getType().isTypeOf(field)));
    }

    /** Retorna a informação de diff para a instância informada, que deve ser da instância original. */
    public DiffInfo getByOriginal(SInstance instance) {
        return getByOriginal(instance.getId());
    }

    /** Retorna a informação de diff para o ID da instância informada, que deve ser da instância original. */
    public DiffInfo getByOriginal(Integer instanceId) {
        return infoForOriginal.get(instanceId);
    }

    /** Retorna a informação de diff para a instância informada, que deve ser da instância nova. */
    public DiffInfo getByNewer(SInstance instance) {
        return getByNewer(instance.getId());
    }

    /** Retorna a informação de diff para o ID da instância informada, que deve ser da instância nova. */
    public DiffInfo getByNewer(Integer instanceId) {
        return infoForNewer.get(instanceId);
    }

    public DiffInfo getById(Integer id) {
        return byId.get(id);
    }

    /**
     * Retorna uma versão compactada do resultado da comparação, onde serão removidas todas as comparações marcada como
     * Unchanged e em alguns casos colapsando um pai e um filho único, quando fizer sentido. Visa apresenta uma
     * informação mais enxuta.
     *
     * @return Pode retornar objeto com conteúdo vazio senão houver nenhum alteração detectada
     */
    public DocumentDiff removeUnchangedAndCompact() {
        DiffInfo compactRoot;
        if (diffRoot.isUnknownState() || diffRoot.isUnchanged()) {
            compactRoot = diffRoot.copyWithoutChildren();
        } else {
            compactRoot = DocumentDiffUtil.removeUnchangedAndCompact(diffRoot);
        }
        return new DocumentDiff(compactRoot, true);
    }

    /**
     * Imprime para o console a árvore de comparação resultante de forma indentada e indicando o resultado da
     * comparação de cada item da estrutura.
     */
    public void debug() {
        if (diffRoot != null) {
            diffRoot.debug();
        }
    }

    /**
     * Imprime para o console a árvore de comparação resultante de forma indentada e indicando o resultado da
     * comparação de cada item da estrutura.
     *
     * @param showAll   Indica se exibe todos os itens (true) ou somente aqueles que tiveram alteração (false)
     * @param showLabel Se false, usar o nome simples dos tipos. Se true, usa o label das instâncias se existir.
     */
    public void debug(boolean showAll, boolean showLabel) {
        if (diffRoot != null) {
            diffRoot.debug(showAll, showLabel);
        }
    }
}
