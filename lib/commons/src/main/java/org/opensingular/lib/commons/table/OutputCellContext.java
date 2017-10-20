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

package org.opensingular.lib.commons.table;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Representa as informações pre-processadas para a geração de uma célula na saída final.
 *
 * @author Daniel C. Bordin on 23/04/2017.
 */
public class OutputCellContext {

    private final OutputTableContext outputTableContext;

    private final InfoCell cell;

    private Object value;

    private DecoratorCell tempDecorator;

    private int level;

    private ColumnTypeProcessor columnProcessor;

    private boolean columnWithSeparator;

    OutputCellContext(@Nonnull OutputTableContext outputTableContext, @Nonnull InfoCell cell,
            @Nonnull DecoratorCell tempDecorator) {
        this.outputTableContext = outputTableContext;
        this.cell = cell;
        this.value = cell.getValue();
        this.columnProcessor = cell.getColumn().getProcessor();
        this.tempDecorator = tempDecorator;
    }

    @Nonnull
    public OutputTableContext getOutputTableContext() {
        return outputTableContext;
    }

    @Nonnull
    public InfoCell getCell() {
        return cell;
    }

    @Nullable
    public Object getValue() {
        return value;
    }

    void setValue(Object value) {
        this.value = value;
    }

    /**
     * Retorna a configuração de decoração da celula, a qual o método pode alterar se quiser sem alterar a célula
     * original. Será descartado depois da chamada de uso do contexto.
     */
    @Nonnull
    public DecoratorCell getTempDecorator() {
        return tempDecorator;
    }

    /**
     * Indica se a célula deve ter indentação interna no valor. Se -1, então não deve considerar indentação.
     */
    public int getLevel() {
        return level;
    }

    OutputCellContext setLevel(int level) {
        this.level = level;
        return this;
    }

    @Nonnull
    public ColumnTypeProcessor getColumnProcessor() {
        return columnProcessor;
    }

    void setColumnProcessor(@Nonnull ColumnTypeProcessor columnProcessor) {
        this.columnProcessor = columnProcessor;
    }

    /**
     * Verifica se a celula atual possui algum valor para ser exibido de acordo com as definições do procesador em uso.
     */
    public boolean isNullContent() {
        return columnProcessor.isNullContent(cell);
    }

    /** Verifica se a célula é apenas um conjunto de ações (link ou icones de ações). */
    public boolean isActionCell() {
        return columnProcessor == ColumnTypeProcessor.ACTION;
    }

    /** Retorna a coluna de celula atual. */
    @Nonnull
    public Column getColumn() {
        return cell.getColumn();
    }

    public String generateFormatDisplayString() {
        return generateFormatDisplayString(this.value);
    }

    public String generateFormatDisplayString(Object targetValue) {
        ColumnTypeProcessor.PrintResult result = columnProcessor.generatePrintValue(getColumn(), targetValue);
        if (result.isDefined()) {
            return resolveMaxLength(result.getContent(), tempDecorator);
        } else if (targetValue == null) {
            return null;
        }
        String s = resolveMaxLength(targetValue.toString(), tempDecorator);
        return AlocproToolkit.plainTextToHtml(s, false);
    }

    private String resolveMaxLength(String content, DecoratorCell tempDecorator) {
        if (content != null && tempDecorator.getMaxTextLength() != null &&
                content.length() > tempDecorator.getMaxTextLength()) {
            return content.substring(0, tempDecorator.getMaxTextLength());
        }
        return content;
    }

    public void setColumnWithSeparator(boolean columnWithSeparator) {
        this.columnWithSeparator = columnWithSeparator;
    }

    public boolean isColumnWithSeparator() {
        return columnWithSeparator;
    }
}
