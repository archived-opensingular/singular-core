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

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Simula uma tabela HTML para facilitar a verificação do correto resultado gerado.
 *
 * @author Daniel C. Bordin on 18/04/2017.
 */
public class TableSimulator {

    private int currentLine = -1;
    private int currentColumn = -1;

    private int columnsSize = 0;

    private List<List<TableCellRef>> table = new ArrayList<>();

    /** Adiciona uma linha na tabela. */
    public TableSimulator addLine() {
        currentLine++;
        currentColumn = -1;
        ensureCells(currentLine, -1);
        return this;
    }

    /** Adiciona uma nova celula na linha com rowSpan e colSpan iguais a zero. */
    public TableSimulator add(Object value) {
        return add(1, 1, value);
    }

    /** Adiciona uma nova celula na linha com rowSpan e colSpan espcecíficos. */
    public TableSimulator add(int rowSpan, int colSpan, Object value) {
        addCellIntern(rowSpan, colSpan).setValue(value).setColSpan(colSpan);
        return this;
    }

    private TableCellRef addCellIntern(int rowSpan, int colSpan) {
        if (currentLine == -1) {
            throw new RuntimeException("currentLine == -1");
        } else if (rowSpan < 1 ) {
            throw new RuntimeException("rowSpan < 1");
        } else if (colSpan < 1) {
            throw new RuntimeException("colSpan < 1");
        }
        while (table.get(currentLine).size() > currentColumn + 1 && table.get(currentLine).get(currentColumn + 1) !=
                null) {
            currentColumn++;
        }
        TableCellRef cell = new TableCellRef();
        for (int i = 0; i < rowSpan; i++) {
            for (int j = 0; j < colSpan; j++) {
                ensureCells(currentLine + i, currentColumn + j + 1);
                createNewCell(currentLine + i, currentColumn + j + 1, cell);
            }
        }
        currentColumn += colSpan;
        return cell;
    }

    private void createNewCell(int lineIndex, int columnIndex, TableCellRef cell) {
        List<TableCellRef> line = table.get(lineIndex);
        if (line.get(columnIndex) != null) {
            throw new RuntimeException("Cell span colision");
        }
        line.set(columnIndex, cell);
    }

    private void ensureCells(int lineIndex, int columnIndex) {
        if (table.size() < lineIndex + 1) {
            table.add(new ArrayList<>());
        }
        List<TableCellRef> line = table.get(lineIndex);
        while (line.size() < columnIndex + 1) {
            line.add(null);
            columnsSize = Math.max(columnsSize, line.size());
        }
    }

    public TableCellRef get() {
        return get(currentLine, currentColumn);
    }

    public TableCellRef get(int lineIndex, int columnIndex) {
        return table.get(lineIndex).get(columnIndex);
    }

    /** Verifica se existem a quantidade de linhas esperadas ou dispara uma {@link java.lang.AssertionError}. */
    public void assertLinesSize(int expectedSize) {
        Assert.assertEquals(expectedSize, currentLine + 1);
    }

    /**
     * Verifica se existem a linha indica possui os valores esperados em suas coluna. Dispara uma {@link
     * java.lang.AssertionError} se a quantidade de colunas não bater ou senão bates os valores esperados para cada
     * celula.
     */
    public TableSimulator assertLine(int lineIndex, Object... expectedValues) {
        Assert.assertEquals(expectedValues.length, table.get(lineIndex).size());
        for (int i = 0; i < expectedValues.length; i++) {
            TableCellRef cell = table.get(lineIndex).get(i);
            Object value = cell == null ? null : cell.getValue();
            Object expected = expectedValues[i];
            if ((value instanceof String) && expected !=null && ! (expected instanceof String)) {
                expected = expected.toString();
            }
            if (!Objects.equals(expected, value)) {
                debug();
                throw new AssertionError(
                        "Erro valor não esperado na possição (" + lineIndex + "," + i + "): expected='" +
                                expectedValues[i] + "' currentValue='" + value + "'");
            }
        }
        return this;
    }

    /** Imprime o conteúdo da tablema para o consile de forma tabulada. */
    public void debug() {
        int[] widths = new int[columnsSize];
        for(List<TableCellRef> line : table) {
            TableCellRef last = null;
            for(int i = 0; i < line.size(); i++) {
                TableCellRef cell = line.get(i);
                if (cell == last) {
                    continue;
                }
                if (cell.getColSpan() == 1) {
                    widths[i] = Math.max(widths[i], cellSize(cell));
                } else {
                    distributeWidth(widths,i, cell.getColSpan(),cellSize(cell));
                }
                last = cell;
            }
        }
        PrintStream out = System.out;
        debugBeginEndTable(out, widths);
        for(List<TableCellRef> line : table) {
            out.print('|');
            TableCellRef last = null;
            for(int i = 0; i < widths.length; i++) {
                TableCellRef cell = i < line.size() ? line.get(i) : null;
                if (cell == last) {
                    continue;
                }
                int size = widths[i];
                for(int j = 1; j < cell.getColSpan(); j++) {
                    size += widths[i+j] + 1;
                }
                String value = cell == null ? null : cell.getValueAsString();
                value = value == null ? "" : value;
                out.print(StringUtils.rightPad(value, size));
                out.print("|");
                last = cell;
            }
            out.println();
        }
        debugBeginEndTable(out, widths);
    }

    private void debugBeginEndTable(PrintStream out, int[] widths) {
        out.print('+');
        for(int i = 0; i < widths.length; i++) {
            out.print(StringUtils.rightPad("", widths[i],'-'));
            out.print('+');
        }
        out.println();
    }

    private void distributeWidth(int[] widths, int pos, int colSpan, int width) {
        int r = width - colSpan + 1;
        int d = r / colSpan;
        r = r - (d * colSpan);
        for(int i = 0; i< colSpan; i++) {
            if (r > 0) {
                widths[pos+i] = Math.max(widths[pos+i], d + 1);
                r--;
            } else {
                widths[pos+i] = Math.max(widths[pos+i], d);
            }
        }
    }

    private int cellSize(TableCellRef cell) {
        String value = cell == null ? null : cell.getValueAsString();
        return value == null ? 0 : value.length();
    }

    public TableSimulator assertLevel(int lineIndex, int expectedLevel) {
        Assert.assertEquals(expectedLevel, get(lineIndex, 0).getLevel());
        return this;
    }

    /**
     * Representa um celula única de informação, a qual pode ocupar mais de uma posição (linha e coluna) da tabela, caso
     * tenha sido usado rowSpan ou colSpan.
     */
    public static class TableCellRef {

        private int colSpan = 1;

        private int level = 0;

        private Object value;

        public TableCellRef setValue(Object value) {
            this.value = value;
            return this;
        }

        public Object getValue() {
            return value;
        }

        public String getValueAsString() {
            return value == null ? null : StringUtils.rightPad("", level) + value.toString();
        }

        public int getColSpan() {
            return colSpan;
        }

        public TableCellRef setColSpan(int colSpan) {
            this.colSpan = colSpan;
            return this;
        }

        public TableCellRef setLevel(int level) {
            this.level = level;
            return this;
        }

        public int getLevel() {
            return level;
        }
    }
}
