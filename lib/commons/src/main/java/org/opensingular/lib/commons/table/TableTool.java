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

import com.google.common.base.Predicates;
import org.opensingular.internal.lib.commons.xml.ConversorToolkit;
import org.opensingular.lib.commons.base.SingularException;
import org.opensingular.lib.commons.views.ViewGenerator;
import org.opensingular.lib.commons.views.ViewGeneratorProvider;
import org.opensingular.lib.commons.views.ViewMultiGenerator;
import org.opensingular.lib.commons.views.ViewOutput;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class TableTool implements ViewMultiGenerator, Serializable {

    private static final long serialVersionUID = 1L;

    private final List<Column> columns = new ArrayList<>();

    private TreeLineReader reader;

    private boolean tableByLevel;

    private boolean simpleTable;

    private boolean hasSuperTitles = false;

    private int levelLimit;

    private String align;

    private String width;

    private boolean showTitles = true;

    private int initialLevel;

    private int indentedColumn;

    private boolean strippedLines = true;

    private List<GenerationModifier> modifiers = new ArrayList<>();

    /**
     * configurao a respeito de gerar ou no uma linha com o total das colunas
     * no final da tabela
     */
    private boolean showTotalLine;

    /**
     * Indica qual nivel deve ser utilizado para totalizar
     */
    private Integer totalLevel = 0;

    private transient LineInfo totalLine;

    private String id;

    private Decorator decorator = new Decorator();

    public TableTool() {

    }

    public TableTool(String id) {
        this.id = id;
    }

    /**
     * Adiciona uma nova coluna no relatrio segundo o ttulo e type informado
     * (opcional). Considera a primeira coluna como sendo a responsvel pela
     * identao.
     *
     * @param type  Tipo da classe a ser condiserada ao formatar a coluna.
     * @param title Texto para aparecer na primeira linha.
     * @return Coluna criada, para eventual modificao de formatao.
     */
    @Nonnull
    public Column addColumn(@Nonnull ColumnType type, @Nullable String title) {
        Column column = new Column(type);
        column.setTitle(title);
        column.setIndex(columns.size());
        columns.add(column);
        return column;
    }

    @Nonnull
    public Column addColumn(@Nonnull ColumnType type) {
        return addColumn(type, null);
    }

    /**
     * Adiciona um linha superior ao ttulo normais. Com isso  possvel ter
     * duas um ttulo superior sobre vrias colunas.
     *
     * @param startColumn Primeira coluna a qual se aplica o super ttulo
     * @param endColumn    ltima coluna a qual se aplica o super ttulo
     * @param superTitle  Texto para fica centralizado sobre todas as colunas
     */
    public void addSuperTitle(int startColumn, int endColumn, String superTitle) {
        addSuperTitle(startColumn, endColumn, superTitle, true);
    }

    public void addSuperTitle(int startColumn, int endColumn, String superTitle, boolean separator) {
        hasSuperTitles = true;
        for (int i = startColumn; i <= endColumn; i++) {
            Column c = columns.get(i);
            c.setSuperTitle(superTitle);
            if (i == startColumn) {
                c.setHasSeparator(separator);
            }
        }
    }

    public TableTool addGroupBy(String columnTitle) {
        addOrderBy(columnTitle);
        Column c = getColumn(columnTitle);
        addModifier(new GenerationModifierAgrupar(this, c));
        return this;
    }

    public TableTool addOrderBy(String columnTitle) {
        return addOrderBy(columnTitle, false);
    }

    public TableTool addOrderBy(String columnTitle, boolean descending) {
        return addOrderBy(getColumn(columnTitle), descending);
    }

    public TableTool addOrderBy(int columnIndex) {
        return addOrderBy(columnIndex, false);
    }

    public TableTool addOrderBy(int columnIndex, boolean descending) {
        return addOrderBy(getColumn(columnIndex), descending);
    }

    public TableTool addOrderBy(Column column, boolean descending) {
        Optional<GenerationModifier> order = modifiers.stream().filter(
                Predicates.instanceOf(GenerationModifierOrder.class)).findFirst();
        if (order.isPresent()) {
            ((GenerationModifierOrder) order.get()).addColumn(column);
        } else {
            addModifier(new GenerationModifierOrder(this, column, descending));
        }
        return this;
    }

    public TableTool addAggregation(Column column, ColumnAggregationType columnAggregationType) {
        findOrAddModifier(GenerationModifierAggregate.class, () -> new GenerationModifierAggregate(this)).addColumn(
                column, columnAggregationType);
        return this;
    }

    public TableTool addAggregation(Column column, Object value) {
        findOrAddModifier(GenerationModifierAggregate.class, () -> new GenerationModifierAggregate(this))
                .setColumnExternalResult(column, value);
        return this;
    }

    public TableTool addFilter(Column column, Predicate<InfoCell> filter) {
        findOrAddModifier(GenerationModifierFilter.class, () -> new GenerationModifierFilter(this)).addColumn(
                column, filter);
        return this;
    }

    public TableTool configAggregation(Map<Column, ColumnAggregationType> aggregationConfig) {
        findOrAddModifier(GenerationModifierGroupingWithAggregation.class,
                () -> new GenerationModifierGroupingWithAggregation(this, aggregationConfig));
        return this;
    }

    public TableTool addAGroupingWothAggregation(Column column) {
        findOrAddModifier(GenerationModifierGroupingWithAggregation.class,
                () -> new GenerationModifierGroupingWithAggregation(this)).addColumn(column);
        return this;
    }

    public Decorator getDecorator() {
        return decorator;
    }

    public <T extends GenerationModifier> T findOrAddModifier(Class<T> modifier, Supplier<T> newModifier) {
        @SuppressWarnings("unchecked") Optional<T> find = (Optional<T>) modifiers.stream().filter(
                Predicates.instanceOf(modifier)).findFirst();
        return find.orElseGet(() -> addModifier(newModifier.get()));
    }

    public <T extends GenerationModifier> T addModifier(T m) {
        if (!modifiers.isEmpty()) {
            modifiers.get(0).addFimCadeia(m);
        }
        modifiers.add(m);
        return m;
    }

    public void setLevelLimit(int levelLimit) {
        this.levelLimit = levelLimit;
    }

    public String getAlign() {
        return align;
    }

    public void setAlign(String align) {
        this.align = align;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public boolean isShowTitles() {
        return showTitles;
    }

    boolean isSimpleTable() {
        return simpleTable;
    }

    public void setShowTitles(boolean showTitles) {
        this.showTitles = showTitles;
    }

    /**
     * Indica se deve ser gerada uma linha com a soma total de cada coluna no
     * final da tabela
     */
    public void setShowTotalLine(boolean showTotalLine) {
        this.showTotalLine = showTotalLine;
    }

    public boolean isShowTotalLine() {
        return showTotalLine;
    }

    /**
     * Indica qual nivel deve ser utilizado para totalizar
     */
    public void setTotalLevel(Integer totalLevel) {
        this.totalLevel = totalLevel;
    }

    public boolean isStrippedLines() {
        return strippedLines;
    }

    public void setStrippedLines(boolean strippedLines) {
        this.strippedLines = strippedLines;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public Column getColumn(int index) {
        return columns.get(index);
    }

    public Column getColumn(String titleOrId) {
        return columns.stream().filter(c -> titleOrId.equalsIgnoreCase(c.getTitle())).findFirst().orElseGet(
                () -> columns.stream().filter(c -> titleOrId.equalsIgnoreCase(c.getId())).findFirst().orElse(null));
    }

    public void setInitialLevel(int initialLevel) {
        this.initialLevel = initialLevel;
    }

    private List<Column> calculateVisibleColumns(OutputTableContext ctx) {
        List<Column> visible = new ArrayList<>(columns.size());
        String superTitle = null;
        boolean addedSuperTitleGroupSeparator = false;
        for (Column c : columns) {
            if (!shouldShowColumn(ctx, c)) {
                continue;
            }
            if (!Objects.equals(superTitle, c.getSuperTitle())) {
                if (c.hasSeparator()) {
                    addColumnSeparator(visible);
                    superTitle = c.getSuperTitle();
                    addedSuperTitleGroupSeparator = c.getSuperTitle() != null;
                } else if (c.getSuperTitle() == null && addedSuperTitleGroupSeparator) {
                    addColumnSeparator(visible);
                    superTitle = null;
                    addedSuperTitleGroupSeparator = false;
                } else {
                    superTitle = null;
                    addedSuperTitleGroupSeparator = false;
                }
            } else if (c.hasSeparator()) {
                addColumnSeparator(visible);
            }
            visible.add(c);
        }
        return visible;
    }

    private boolean shouldShowColumn(OutputTableContext ctx, Column c) {
        return c.isVisible() && (c.getProcessor().shouldBeGeneretedOnStaticContent() || !ctx.isStaticContent());
    }

    private void addColumnSeparator(List<Column> visible) {
        if (! visible.isEmpty()) {
            visible.add(null);
        }
    }

    public TablePopulator createSimpleTablePopulator() {
        TablePopulator populator = new TablePopulator(this);
        reader = populator.asTreeLineReader();
        simpleTable = true;
        return populator;
    }

    @SuppressWarnings("unchecked")
    public <T> void setReaderByLine(Iterable<? extends T> list, LineReader<T> reader) {
        this.reader = GeneratorUtil.toTreeLineReader(list, (LineReader<Object>) reader);
        simpleTable = true;
    }

    public void setReaderByTree(TreeLineReader reader) {
        this.reader = reader;
        simpleTable = false;
    }

    public void setReaderByLevel(TreeLineReader reader) {
        this.reader = reader;
        tableByLevel = true;
    }

    public void setIndentedColumn(int indentedColumn) {
        this.indentedColumn = indentedColumn;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void generate(@Nonnull TableOutput tableOutput) {
        if (getColumns().isEmpty()) {
            return;
        }
        totalLine = newBlankLine();

        DataReader dataReader = new DataReaderByIterator(reader, reader.getRoots(), 0);
        if (!isShowTitles() && dataReader.isEmpty()) {
            return;
        }

        OutputTableContext ctx = new OutputTableContext(this, tableOutput);
        ctx.setVisibleColumns(calculateVisibleColumns(ctx));

        ctx.getOutput().generateTableStart(ctx, this);

        if (!modifiers.isEmpty()) {
            ctx.setVisibleColumns(modifiers.get(0).adjustTitles(ctx.getVisibleColumns()));
            generateTitles(ctx);
            dataReader = modifiers.get(0).apply(dataReader);
        } else {
            generateTitles(ctx);
        }
        generateChildren(dataReader, ctx, initialLevel);
        if (showTotalLine) {
            generateTotalLine(ctx);
        }

        ctx.getOutput().generateTableEnd(ctx, this);
    }

    private void generateTitles(OutputTableContext ctx) {
        if (isShowTitles() || hasSuperTitles) {
            ctx.getOutput().generateTitleBlockStart(ctx);
            if (hasSuperTitles) {
                generateSuperTitles(ctx);
            }
            if (isShowTitles()) {
                generateNormalTitles(ctx);
            }
            ctx.getOutput().generateTitleBlockEnd(ctx);
        }
    }

    private void generateNormalTitles(OutputTableContext ctx) {
        List<Column> visible = ctx.getVisibleColumns();
        ctx.getOutput().generateTitleLineStart(ctx, false);
        boolean nextColumnWithSeparator = false;
        for (Column column : visible) {
            if (column == null) {
                nextColumnWithSeparator = true;
            } else if ((column.getSuperTitle() != null) || !hasSuperTitles) {
                ctx.getOutput().generateTitleCell(ctx, column, 1, hasSuperTitles, nextColumnWithSeparator);
                nextColumnWithSeparator = false;
            }
        }
        ctx.getOutput().generateTitleLineEnd(ctx, false);
    }

    private void generateSuperTitles(OutputTableContext ctx) {
        List<Column> visible = ctx.getVisibleColumns();
        ctx.getOutput().generateTitleLineStart(ctx, true);
        boolean nextColumnWithSeparator = false;
        for (int i = 0; i < visible.size(); i++) {
            Column c = visible.get(i);
            if (c == null) {
                nextColumnWithSeparator = true;
            } else if (c.getSuperTitle() == null) {
                ctx.getOutput().generateTitleCell(ctx, c, 2, false, nextColumnWithSeparator);
                nextColumnWithSeparator = false;
            } else {
                int ult = i;
                while ((ult + 1 < visible.size()) && (visible.get(ult + 1) != null) && (Objects.equals(
                        visible.get(ult + 1).getSuperTitle(), c.getSuperTitle()))) {
                    ult++;
                }
                ctx.getOutput().generateTitleCellSuper(ctx, c, ult - i + 1, nextColumnWithSeparator);
                nextColumnWithSeparator = false;
                i = ult;
            }
        }
        ctx.getOutput().generateTitleLineEnd(ctx, true);
    }

    final LineInfo newBlankLine() {
        return new LineInfo(this);
    }

    private void generateChildren(DataReader children, OutputTableContext ctx, int level) {
        if (children == null || children.isEmpty()) {
            return;
        }
        ctx.getOutput().generateBodyBlockStart(ctx);
        if (tableByLevel || columns.stream().anyMatch(c -> c.getDataLevel() > 0)) {
            int qtdLevel = columns.stream().mapToInt(c -> c.getDataLevel()).max().getAsInt() + 1;
            int[] lineCount = new int[qtdLevel];
            for (LineData child : children) {
                for (LineData[] line : child.normalizeLevels(qtdLevel)) {
                    generateTableByLevel(line, ctx, lineCount);
                }
            }
        } else {
            for (LineData lineData : children) {
                if (simpleTable) {
                    generateSimpleTable(lineData, ctx);
                } else {
                    generateTreeLine(lineData, ctx, level);
                }
            }
        }
        ctx.getOutput().generateBodyBlockEnd(ctx);
    }

    private void generateTableByLevel(LineData[] lineData, OutputTableContext ctx, int[] lineCounter) {
        int lineLevel = -1;
        int lineColor = 0;
        LineInfo lineInfo = null;
        for (int i = 0; i < lineData.length; i++) {
            if (lineData[i] == null) {
                continue;
            }
            if (lineLevel == -1) {
                lineLevel = i;
                lineCounter[i]++;
                for (int j = i + 1; j < lineCounter.length; j++) {
                    lineCounter[j] = lineCounter[i];
                }
                lineColor = lineCounter[i];
            }
            ctx.getLineReadContext().setLevel(lineLevel);
            lineInfo = lineData[i].retrieveValues(ctx.getLineReadContext(), i, lineLevel == i, false);
        }
        if (lineInfo == null) {
            throw new SingularException("Invalid State");
        }
        if (ctx.isShowLine()) {
            generateTableByLevelLine(lineData, ctx, lineInfo, lineLevel, lineColor);
        }
    }

    private void generateTableByLevelLine(LineData[] lineData, OutputTableContext ctx, LineInfo line, int lineLevel,
            int lineColor) {
        int lineAlternation = isStrippedLines() ? lineColor : -1;
        ctx.getOutput().generateLineSimpleStart(ctx, line, lineAlternation);

        int columnIndex = 0;
        boolean nextColumnWithSeparator = false;
        while (columnIndex < ctx.getVisibleColumns().size()) {
            Column c = ctx.getVisibleColumns().get(columnIndex);
            ctx.setIndexCurrentColumn(columnIndex);
            int qtdSpan = 1;
            if (c == null) {
                if (lineData[0] != null) {
                    nextColumnWithSeparator = true;
                }
            } else if (c.getDataLevel() >= lineLevel) {
                int level = c.getDataLevel();
                InfoCell cell = line.get(c);
                OutputCellContext ctxCell = createCellContext(ctx, cell, nextColumnWithSeparator);
                nextColumnWithSeparator = false;
                if (lineData[level] != null) {
                    int rowSpan = lineData[level].getLines();
                    ctxCell.getTempDecorator().setRowSpan(rowSpan);
                }
                if (ctxCell.getTempDecorator().getRowSpan() != 0) {
                    ctxCell.setLevel(indentedColumn == columnIndex ? level : -1);
                    ctx.getOutput().generateCell(ctxCell);
                }

                addToTotal(c, cell, level);
                qtdSpan = ctxCell.getTempDecorator().getColSpan();
            }
            columnIndex += qtdSpan;
        }
        ctx.getOutput().generateLineSimpleEnd(ctx);

        ctx.incIndexCurrentLine();
    }

    private Decorator resolverDecorator(OutputTableContext ctx, LineInfo line) {
        return line.createTempDecorator();
    }

    private void generateTreeLine(LineData lineData, OutputTableContext ctx, int level) {
        if ((levelLimit > 0) && (level + 1 > levelLimit)) {
            return;
        }
        ctx.getLineReadContext().setLevel(level);
        LineInfo line = lineData.retrieveValues(ctx.getLineReadContext(), level, true, false);

        if (!ctx.isShowLine()) {
            generateChildren(lineData.getChildrenReader(), ctx, level);
            return;
        }
        //nivel = ctx.getLevel();
        ctx.getOutput().generateLineTreeStart(ctx, line, level);

        int columnIndex = 0;
        boolean nextColumnWithSeparator = false;
        while (columnIndex < ctx.getVisibleColumns().size()) {
            Column c = ctx.getVisibleColumns().get(columnIndex);
            ctx.setIndexCurrentColumn(columnIndex);
            if (c == null) {
                nextColumnWithSeparator = true;
                columnIndex++;
            } else {
                InfoCell cell = line.get(c);
                if ((level == 0) && c.isShowAsPercentageOfParent()) {
                    c.setValueForPercentageCalculation(cell.getValueAsNumberOrNull());
                }
                OutputCellContext ctxCell = createCellContext(ctx, cell, nextColumnWithSeparator);
                nextColumnWithSeparator = false;
                ctxCell.setLevel(indentedColumn == columnIndex ? level : -1);
                if (ctxCell.getTempDecorator().getRowSpan() != 0) {
                    ctx.getOutput().generateCell(ctxCell);
                }

                addToTotal(c, cell, level);
                columnIndex += ctxCell.getTempDecorator().getColSpan();
            }
        }
        ctx.getOutput().generateLineTreeEnd(ctx);

        ctx.incIndexCurrentLine();

        generateChildren(lineData.getChildrenReader(), ctx, level + 1);
    }

    private void generateSimpleTable(LineData lineData, OutputTableContext ctx) {

        LineInfo line = lineData.retrieveValues(ctx.getLineReadContext(), 0, true, false);
        if (! ctx.isShowLine()) {
            return;
        }

        int lineAlternation = isStrippedLines() ? ctx.getIndexCurrentLine() % 2 : -1;
        ctx.getOutput().generateLineSimpleStart(ctx, line, lineAlternation);

        int columnIndex = 0;
        boolean nextColumnWithSeparator = false;
        while (columnIndex < ctx.getVisibleColumns().size()) {
            Column c = ctx.getVisibleColumns().get(columnIndex);
            ctx.setIndexCurrentColumn(columnIndex);
            if (c == null) {
                nextColumnWithSeparator = true;
                columnIndex++;
            } else {
                InfoCell cell = line.get(c);
                if (c.isShowAsPercentageOfParent()) {
                    c.setValueForPercentageCalculation(cell.getValueAsNumberOrNull());
                }
                OutputCellContext ctxCell = createCellContext(ctx, cell, nextColumnWithSeparator);
                nextColumnWithSeparator = false;
                ctxCell.setLevel(-1);
                if (ctxCell.getTempDecorator().getRowSpan() != 0) {
                    ctx.getOutput().generateCell(ctxCell);
                }

                addToTotal(c, cell, 0);
                columnIndex += ctxCell.getTempDecorator().getColSpan();
            }
        }
        ctx.getOutput().generateLineSimpleEnd(ctx);

        ctx.incIndexCurrentLine();
    }

    @Nonnull
    private OutputCellContext createCellContext(OutputTableContext ctx, InfoCell cell, boolean columnWithSeparator) {
        DecoratorCell decorator = cell.createTempDecorator();
        if (decorator.isColSpanAll()) {
            decorator.setColSpan(ctx.getVisibleColumnsSize() - ctx.getIndexCurrentColumn());
        }
        OutputCellContext ctxCell = new OutputCellContext(ctx, cell, decorator);

        // Trata a exibição do valor como percentual do valor pai
        if (ctxCell.getColumn().isShowAsPercentageOfParent()) {
            ctxCell.setColumnProcessor(ColumnType.PERCENT.getProcessor());
            if (ctxCell.getValue() instanceof Number) {
                Number value = (Number) ctxCell.getValue();
                value = ConversorToolkit.divide(value, ctxCell.getColumn().getValueForPercentageCalculation());
                ctxCell.setValue(value);
            }
        }
        ctxCell.setColumnWithSeparator(columnWithSeparator);
        return ctxCell;
    }

    private void addToTotal(Column c, InfoCell cell, int level) {
        if (showTotalLine && c.isTotalize() && (totalLevel == null || level == totalLevel)) {
            InfoCell total = totalLine.get(c);
            Number value = null;
            if (cell.getValue() instanceof Number) {
                value = (Number) cell.getValue();
            } else if (cell.getValueReal() instanceof Number) {
                value = (Number) cell.getValueReal();
            }
            total.setValue(ConversorToolkit.add(total.getValueAsNumberOrNull(), value));
        }
    }


    private void generateTotalLine(OutputTableContext ctx) {
        Decorator tmpDecorator = resolverDecorator(ctx, totalLine);

        ctx.getOutput().generateTotalBlockStart(ctx);
        if (ctx.getTableTool().isSimpleTable()) {
            ctx.getOutput().generateTotalLineStart(ctx, totalLine, tmpDecorator, -1);
        } else {
            ctx.getOutput().generateTotalLineStart(ctx, totalLine, tmpDecorator, initialLevel);
        }

        int columnIndex = 0;
        boolean nextColumnWithSeparator = false;
        for (Column c : ctx.getVisibleColumns()) {
            if (c == null) {
                nextColumnWithSeparator = true;
            } else if (!c.isTotalize()) {
                ctx.getOutput().generateTotalCellSkip(ctx, c, nextColumnWithSeparator);
                nextColumnWithSeparator = false;
            } else if (columnIndex == 0) {
                DecoratorCell tmpDecoratorCell = new DecoratorCell(c.getDecoratorValues());
                ctx.getOutput().generateTotalLabel(ctx, c, "Total", tmpDecoratorCell, initialLevel);
            } else {
                InfoCell cell = totalLine.get(c);
                OutputCellContext ctxCell = new OutputCellContext(ctx, cell, cell.createTempDecorator()).setLevel(-1);
                ctxCell.setColumnWithSeparator(nextColumnWithSeparator);
                Number value = cell.getValueAsNumberOrNull();
                ctx.getOutput().generateTotalCell(ctxCell, value);
                nextColumnWithSeparator = false;
            }
            columnIndex++;
        }

        ctx.getOutput().generateTotalLineEnd(ctx);
        ctx.getOutput().generateTotalBlockEnd(ctx);
    }


    @Nonnull
    @Override
    public Collection<ViewGeneratorProvider<ViewGenerator, ? extends ViewOutput<?>>> getGenerators() {
        return TableToolUtil.getGenerators();
    }
}
