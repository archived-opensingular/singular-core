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
 * É um {@link TableOutput} mock que guarda os resultados gerados em {@link TableSimulator} a fim de permitir
 * aelaboração de teste (JUnits).
 *
 * @author Daniel C. Bordin on 22/04/2017.
 */
public class TableOutputSimulated extends TableOutput {

    private TableSimulator table = new TableSimulator();

    public TableSimulator getResult() {
        return table;
    }

    @Override
    public String getUrlApp() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public boolean isStaticContent() {
        return false;
    }

    @Override
    public void generateTableStart(OutputTableContext ctx, TableTool tableTool) { }

    @Override
    public void generateTableEnd(OutputTableContext ctx, TableTool tableTool) { }

    @Override
    public void generateBodyBlockStart(@Nonnull OutputTableContext ctx) {}

    @Override
    public void generateBodyBlockEnd(@Nonnull OutputTableContext ctx) {}

    @Override
    public void generateLineSimpleStart(OutputTableContext ctx, InfoLinha line, int lineAlternation) {
        table.addLine();
    }

    @Override
    public void generateLineSimpleEnd(OutputTableContext ctx) { }

    @Override
    public void generateLineTreeStart(OutputTableContext ctx, InfoLinha line, int nivel) {
        table.addLine();
    }

    @Override
    public void generateLineTreeEnd(OutputTableContext ctx) {}

    @Override
    public void generateCell(@Nonnull OutputCellContext ctx) {
        if (ctx.isColumnWithSeparator()) {
            table.add("#");
        }
        DecoratorCell tempDecorator = ctx.getTempDecorator();
        String value = ctx.generateFormatDisplayString();

        table.add(tempDecorator.getRowSpan(), tempDecorator.getColSpan(), value);
        if (ctx.getLevel() > 0) {
            table.get().setLevel(ctx.getLevel());
        }
    }

    @Override
    public void generateTitleBlockStart(OutputTableContext ctx) {}

    @Override
    public void generateTitleBlockEnd(OutputTableContext ctx) { }

    @Override
    public void generateTitleLineStart(OutputTableContext ctx, boolean superTitleLine) {
        table.addLine();
    }

    @Override
    public void generateTitleLineEnd(OutputTableContext ctx, boolean superTitleLine) { }

    @Override
    public void generateTiltleCell(OutputTableContext ctx, Column column, int rowSpan, boolean asSubTitle,
            boolean columnWithSeparator) {
        if (columnWithSeparator) {
            table.add("#");
        }
        table.add(rowSpan, 1, column.getTitle());
    }

    @Override
    public void generateTitleCellSuper(OutputTableContext ctx, Column column, int colSpan, boolean columnWithSeparator) {
        if (columnWithSeparator) {
            table.add("#");
        }
        table.add(1, colSpan, column.getSuperTitle());
    }

    @Override
    public void generateTotalLineStart(@Nonnull OutputTableContext ctx, @Nonnull InfoLinha totalLine,
            @Nonnull Decorator tempDecorator, int level) {
        table.addLine();
    }

    @Override
    public void generateTotalLineEnd(@Nonnull OutputTableContext ctx) { }

    @Override
    public void generateTotalCellSkip(@Nonnull OutputTableContext ctx, @Nonnull Column column,
            boolean columnWithSeparator) {
        if (columnWithSeparator) {
            table.add("#");
        }
        table.add(null);
    }

    @Override
    public void generateTotalLabel(@Nonnull OutputTableContext ctx, @Nonnull Column column, @Nonnull String label,
            @Nonnull DecoratorCell tempDecorator, int level) {
        table.add(label);
        if (level > 0) {
            table.get().setLevel(level);
        }
    }

    @Override
    public void generateTotalCell(@Nonnull OutputCellContext ctx, @Nullable Number value) {
        if (ctx.isColumnWithSeparator()) {
            table.add("#");
        }
        table.add(ctx.generateFormatDisplayString(value));
    }
}
