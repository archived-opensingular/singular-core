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

package org.opensingular.lib.commons.table;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Daniel C. Bordin on 16/04/2017.
 */
public abstract class TableOutput {

    public abstract String getUrlApp();

    /**
     * Indica que o conteúdo sendo gerado será estático, ou seja, sera consultado fora do contexto do servidor. Por
     * exemplo, HTML enviado por e-mail, Excel, PDF, etc.
     */
    public abstract boolean isStaticContent();

    /** Inicializa a geração da tabela de resultado. Chamado antes do pedido de criação dos títulos. */
    public abstract void generateTableStart(@Nonnull OutputTableContext ctx, @Nonnull TableTool tableTool);

    /** Finaliza a geração da tabela de resultado. Chamado após todas as chamdas de geração de conteúdo. */
    public abstract void generateTableEnd(@Nonnull OutputTableContext ctx, @Nonnull TableTool tableTool);

    /** Gera o início de um conjunto de linhas que representa as linhas de dados. */
    public abstract void generateBodyBlockStart(@Nonnull OutputTableContext ctx);

    /** Gera o fim de um conjunto de linhas que representa as linhas de dados. */
    public abstract void generateBodyBlockEnd(@Nonnull OutputTableContext ctx);

    /**
     * Gera o início de uma nova linha.
     *
     * @param lineAlternation Se -1, indica que as linhas não devem ter cores alternada. Caso contrário, esse valor
     *                        alternará entre 0 e 1
     */
    public abstract void generateLineSimpleStart(@Nonnull OutputTableContext ctx, @Nonnull LineInfo line,
            int lineAlternation);

    /** Fecha a geração da linha simples. */
    public abstract void generateLineSimpleEnd(@Nonnull OutputTableContext ctx);

    /**
     * Gera o início de uma nova linha para uma tabela de dados em árvore.
     *
     * @param level Indica o nível da linha dentro da tabela. O indicado começa em zero.
     */
    public abstract void generateLineTreeStart(@Nonnull OutputTableContext ctx, @Nonnull LineInfo line, int level);

    /** Fecha a geração da linha de dados em árvore. */
    public abstract void generateLineTreeEnd(@Nonnull OutputTableContext ctx);

    /**
     * Gera o conteúdo de uma celula de valor.
     */
    public abstract void generateCell(@Nonnull OutputCellContext ctx);

    /** Gera o incio de um conjunto de linhas que representa o título dos dados. */
    public abstract void generateTitleBlockStart(@Nonnull OutputTableContext ctx);

    /** Gera o fim de um conjunto de linhas que representa o título dos dados. */
    public abstract void generateTitleBlockEnd(@Nonnull OutputTableContext ctx);

    /**
     * Gera o início de uma nova linha de títulos da tabela.
     *
     * @param superTitleLine Indica se a linha é um linha de títulos comuns ou uma linha de super título (título comum
     *                       de um conjunto de coluna).
     */
    public abstract void generateTitleLineStart(@Nonnull OutputTableContext ctx, boolean superTitleLine);

    /**
     * Gera o fim de uma linha de títulos da tabela.
     *
     * @param superTitleLine Indica se a linha é um linha de títulos comuns ou uma linha de super título (título comum
     *                       de um conjunto de coluna).
     */
    public abstract void generateTitleLineEnd(@Nonnull OutputTableContext ctx, boolean superTitleLine);

    /**
     * Gera um celula que representa o título da coluna.
     *
     * @param column     Columna para o qual será gerado o super título
     * @param rowSpan    Quantas linhas deve ocupar o título
     * @param asSubTitle Indica se está sendo gerado título de baixo de um super título
     */
    public abstract void generateTitleCell(@Nonnull OutputTableContext ctx, @Nonnull Column column, int rowSpan,
            boolean asSubTitle, boolean columnWithSeparator);

    /**
     * Gera um celula que representa o super título de um conjunto de colunas.
     *
     * @param column  Primeira coluna do conjunto de coluna abarcados pelo super título
     * @param colSpan Quantas colunas são ocupadas pelo super título.
     */
    public abstract void generateTitleCellSuper(@Nonnull OutputTableContext ctx, @Nonnull Column column, int colSpan,
            boolean columnWithSeparator);

    /** Gera o incio de um conjunto de linhas que representa o rodapé dos dados. */
    public abstract void generateTotalBlockStart(@Nonnull OutputTableContext ctx);

    /** Gera o fim de um conjunto de linhas que representa o rodapé dos dados. */
    public abstract void generateTotalBlockEnd(@Nonnull OutputTableContext ctx);

    /**
     * Gera o início de uma nova linha de totalização da tabela.
     *
     * @param totalLine       linha a ser gerada a totalização
     * @param tempDecorator   configuração de decoração da celula, a qual o método pode alterar se quiser. Será
     *                        descartado depois da chamada desse método.
     * @param level           Indica o nível da linha dentro da tabela. O indicado começa em zero. Se -1, indica que a
     *                        linha não têm relação com nível.
     */
    public abstract void generateTotalLineStart(@Nonnull OutputTableContext ctx, @Nonnull LineInfo totalLine,
            @Nonnull Decorator tempDecorator, int level);

    /** Fecha a geração de uma linha de totalização da tabela. */
    public abstract void generateTotalLineEnd(@Nonnull OutputTableContext ctx);

    /** Gera uma celula que não tera totalização, pois não foi marcada para tanto. */
    public abstract void generateTotalCellSkip(@Nonnull OutputTableContext ctx, @Nonnull Column column,
            boolean columnWithSeparator);

    /**
     * Gera uma celula que é apenas o label da linha de totalização.
     *
     * @param tempDecorator configuração de decoração da celula, a qual o método pode alterar se quiser. Será
     *                      descartado depois da chamada desse método.
     */
    public abstract void generateTotalLabel(@Nonnull OutputTableContext ctx, @Nonnull Column column,
            @Nonnull String label, @Nonnull DecoratorCell tempDecorator, int level);

    /**
     * Gera uma celula da linha de totalização.
     */
    public abstract void generateTotalCell(@Nonnull OutputCellContext ctx, @Nullable Number value);
}
