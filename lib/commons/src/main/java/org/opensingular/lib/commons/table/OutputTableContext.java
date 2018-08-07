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

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author Daniel C. Bordin on 16/04/2017.
 */
public final class OutputTableContext {

    private final TableTool tableTool;

    private final TableOutput tableOutput;

    private int indexCurrentColumn;

    private int indexCurrentLine;

    private LineInfo line;
    private List<Column> visibleColumns;

    private LineReadContext lineReadContext;

    OutputTableContext(TableTool tableTool, TableOutput tableOutput) {
        this.tableTool = tableTool;
        this.tableOutput = tableOutput;
    }

    public TableTool getTableTool() {
        return tableTool;
    }

    public TableOutput getOutput() {
        return tableOutput;
    }

    public void clean(int numLevel) {
        line = null;
        setLevel(numLevel);
    }

    @Nullable
    final LineInfo getLineIfExists() {
        return line;
    }

    private LineInfo getLine() {
        if (line == null) {
            line = tableTool.newBlankLine();
        }
        return line;
    }

    final void setLine(LineInfo lineInfo) {
        line = lineInfo;
    }

    //TODO Verificar se ao final esse método fica
    public String getUrlApp() {
        return tableOutput.getUrlApp();
    }

    public Decorator getDecorator() {
        return getLine().getDecorator();
    }

    final int getVisibleColumnsSize() {
        return visibleColumns.size();
    }

    final void setIndexCurrentColumn(int i) {
        indexCurrentColumn = i;
    }

    final int getIndexCurrentColumn() {
        return indexCurrentColumn;
    }

    public final int getIndexCurrentLine() {
        return indexCurrentLine;
    }

    final void incIndexCurrentLine() {
        indexCurrentLine++;
    }

    public final void setLevel(int level) {
        getLine().setLevel(level);
    }

    public int getLevel() {
        return getLine().getLevel();
    }

    final boolean isShowLine() {
        return line == null ? true : getLine().isShowLine();
    }

    public void setShowLine(boolean showLine) {
        if (line != null || !showLine) {
            getLine().setShowLine(showLine);
        }
    }

    /**
     * Indica que o conteúdo sendo gerado será estático, ou seja, sera consultado fora do contexto do servidor. Por
     * exemplo, HTML enviado por e-mail, Excel, PDF, etc.
     */
    public boolean isStaticContent() {
        return tableOutput.isStaticContent();
    }

    /** Retorna a lista de coluna a serem exibidas (efetivamente geradas). */
    public List<Column> getVisibleColumns() {
        return visibleColumns;
    }

    /** Define as colunas a serem exibidas no resultado final. */
    final void setVisibleColumns(List<Column> visibleColumns) {
        this.visibleColumns = visibleColumns;
    }

    public LineReadContext getLineReadContext() {
        if (lineReadContext == null) {
            lineReadContext = new LineReadContext(tableTool);
        }
        return lineReadContext;
    }
}
