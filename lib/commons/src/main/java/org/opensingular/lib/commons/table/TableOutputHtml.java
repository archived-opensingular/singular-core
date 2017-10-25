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
import org.opensingular.lib.commons.net.WebRef;
import org.opensingular.lib.commons.views.format.ViewOutputHtml;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.PrintWriter;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author Daniel C. Bordin on 16/04/2017.
 */
public class TableOutputHtml extends TableOutput {

    private final ViewOutputHtml vOut;
    private final String tableid = UUID.randomUUID().toString();

    public TableOutputHtml(ViewOutputHtml vOut) {
        this.vOut = vOut;
    }

    @Override
    public String getUrlApp() {
        return vOut.getUrlApp();
    }

    @Override
    public boolean isStaticContent() {
        return vOut.isStaticContent();
    }

    public ViewOutputHtml getVOut() {
        return vOut;
    }

    final PrintWriter getOut() {
        return vOut.getPrintWriter();
    }

    final TableOutputHtml print(char c) {
        getOut().print(c);
        return this;
    }

    final TableOutputHtml print(Object o) {
        getOut().print(o);
        return this;
    }

    final TableOutputHtml print(String s) {
        getOut().print(s);
        return this;
    }

    final TableOutputHtml println(String s) {
        getOut().println(s);
        return this;
    }

    final TableOutputHtml println() {
        getOut().println();
        return this;
    }

    final TableOutputHtml printAttribute(String attributeName, int value) {
        return printAttribute(attributeName, Integer.toString(value));
    }

    final TableOutputHtml printAttribute(String attributeName, String value) {
        if (value != null) {
            PrintWriter out = getOut();
            out.print(' ');
            out.print(attributeName);
            out.print("=\"");
            out.print(value);
            out.print('"');
        }
        return this;
    }

    @Override
    public void generateTableStart(OutputTableContext ctx, TableTool tableTool) {
        println();
        print("<table id='" + tableid + "' cellpadding='0' cellspacing='0'");
        if (tableTool.isStrippedLines()) {
            printAttribute("class", "T_t table table-bordered table-condensed table-hover table-striped");
        } else {
            printAttribute("class", "T_t table table-bordered table-condensed table-hover");
        }
        printAttribute("width", tableTool.getWidth());
        printAttribute("align", tableTool.getAlign());
        printAttribute("id", tableTool.getId());
        decorate(tableTool.getDecorator());
        println(">");
    }

    @Override
    public void generateTableEnd(OutputTableContext ctx, TableTool tableTool) {
        println("</table>");
        println();
        println("<script type='text/javascript'>");
        println("(function(){\n" +
                "if(typeof $ !== 'undefined'){\n" +
                "    $(document).ready(function(){\n" +
                "        var table = $('#" + tableid + "');\n" +
                "        if(table.DataTable){\n" +
                "            table.DataTable(" + datatablesOptions() + ");\n" +
                "        }\n" +
                "    });}\n" +
                "}());");
        println("</script>");
        println();
    }

    @Override
    public void generateBodyBlockStart(@Nonnull OutputTableContext ctx) {
        if (ctx.getTableTool().isSimpleTable()) {
            if (ctx.getTableTool().isStrippedLines()) {
                println("<tbody class=\"T_content_simple T_striped\">");
            } else {
                println("<tbody class=\"T_content_simple\">");
            }
        } else {
            println("<tbody class=\"T_content_tree\">");
        }
    }

    @Override
    public void generateBodyBlockEnd(@Nonnull OutputTableContext ctx) {
        println("</tbody>");
    }

    @Override
    public void generateLineSimpleStart(OutputTableContext ctx, LineInfo line, int lineAlternation) {
        if (lineAlternation != -1) {
            line.getDecorator().setCssClass(lineAlternation == 0 ? "T_ls0" : "T_ls1");
        }
        print("  <tr");
        decorate(line.getDecorator());
        println(">");
    }

    @Override
    public void generateLineSimpleEnd(OutputTableContext ctx) {
        println("  </tr>");
    }

    @Override
    public void generateLineTreeStart(OutputTableContext ctx, LineInfo line, int level) {
        if (ctx.getDecorator().getCssClass() == null) {
            ctx.getDecorator().setCssClass(level <= 4 ? "T_R_" + level : "T_R_N");
        }

        print("  <tr");
        decorate(line.getDecorator());
        println(">");
    }

    @Override
    public void generateLineTreeEnd(OutputTableContext ctx) {
        println("  </tr>");
    }

    @Override
    public void generateTitleBlockStart(OutputTableContext ctx) {
        getOut().println("<thead class=\"T_thead\">");
    }

    @Override
    public void generateTitleBlockEnd(OutputTableContext ctx) {
        getOut().println("</thead>");
    }

    @Override
    public void generateTitleLineStart(OutputTableContext ctx, boolean superTitleLine) {
        getOut().println("  <tr>");
    }

    @Override
    public void generateTitleLineEnd(OutputTableContext ctx, boolean superTitleLine) {
        getOut().println("  </tr>");
    }

    @Override
    public void generateTitleCell(OutputTableContext ctx, Column column, int rowSpan, boolean asSubTitle,
                                  boolean columnWithSeparator) {
        PrintWriter out = getOut();
        out.print("   <th");
        if (column.getWidth() != null) {
            printAttribute("width", column.getWidth());
        }
        if (rowSpan > 1) {
            printAttribute("rowspan", Integer.toString(rowSpan));
        }
        generateTitleCellClassAttribute(column, asSubTitle, columnWithSeparator, out);
        decorate(column.getDecoratorTitle());

        out.print(">");
        if (column.getTitle() == null) {
            out.print("&nbsp;");
        } else {
            if (column.isSmall()) {
                out.print("<small>");
            }
            if (column.isStrong()) {
                out.print("<strong>");
            }
            out.print(column.getTitle());
            if (column.isStrong()) {
                out.print("</strong>");
            }
            if (column.isSmall()) {
                out.print("</small>");
            }
        }
        out.println("</th>");
    }

    private void generateTitleCellClassAttribute(Column column, boolean asSubTitle, boolean columnWithSeparator,
                                                 PrintWriter out) {
        if (asSubTitle) {
            switch (column.getAlignment()) {
                case CENTER:
                    printClass(out, "T_subtit_cen", columnWithSeparator);
                    break;
                case RIGHT:
                    printClass(out, "T_subtit_dir", columnWithSeparator);
                    break;
                default:
                    printClass(out, "T_subtit", columnWithSeparator);
                    break;
            }
        } else {
            switch (column.getAlignment()) {
                case CENTER:
                    printClass(out, "T_tit_cen", columnWithSeparator);
                    break;
                case RIGHT:
                    printClass(out, "T_tit_dir", columnWithSeparator);
                    break;
                default:
                    printClass(out, null, columnWithSeparator);
                    break;
            }
        }
    }

    private void printClass(PrintWriter out, String style, boolean columnWithSeparator) {
        if (style != null || columnWithSeparator) {
            out.print(" class=\"");
            if (style != null) {
                out.print(style);
                if (columnWithSeparator) {
                    out.print(' ');
                    out.print("T_sep");
                }
            } else {
                out.print("T_sep");
            }
            out.print('"');
        }
    }

    @Override
    public void generateTitleCellSuper(OutputTableContext ctx, Column column, int colSpan, boolean cColumnWithSeparator) {
        PrintWriter out = getOut();
        out.print("   <th");
        if (colSpan > 1) {
            printAttribute("colspan", colSpan);
        }
        printClass(out, "T_tit_super", cColumnWithSeparator);
        out.print(">");
        out.print(column.getSuperTitle());
        out.println("</th>");
    }

    @Override
    public void generateTotalBlockStart(@Nonnull OutputTableContext ctx) {
        println("<tfoot class=\"T_tfoot\">");
    }

    @Override
    public void generateTotalBlockEnd(@Nonnull OutputTableContext ctx) {
        println("</tfoot>");
    }

    @Override
    public void generateTotalLineStart(@Nonnull OutputTableContext ctx, @Nonnull LineInfo totalLine,
                                       @Nonnull Decorator tempDecorator, int level) {
        if (level != -1) {
            if (level <= 2) {
                tempDecorator.setCssClass("RA_TR_" + level);
            } else {
                tempDecorator.setCssClass("RA_TR_N");
            }
        } else {
            tempDecorator.setCssClass("T_l_tot");
        }

        PrintWriter out = getOut();
        out.print("  <tr");
        decorate(tempDecorator);
        out.println('>');
    }

    @Override
    public void generateTotalLineEnd(@Nonnull OutputTableContext ctx) {
        getOut().println("  </tr>");
    }

    @Override
    public void generateTotalCellSkip(@Nonnull OutputTableContext ctx, @Nonnull Column column,
                                      boolean columnWithSeparator) {
        if (columnWithSeparator) {
            getOut().println("   <td class=\"T_sep\">&nbsp;</td>");
        } else {
            getOut().println("   <td>&nbsp;</td>");
        }
    }

    @Override
    public void generateTotalLabel(@Nonnull OutputTableContext ctx, @Nonnull Column column, @Nonnull String label,
                                   @Nonnull DecoratorCell tempDecorator, int level) {
        tempDecorator.setCssClass("T_tot_label");

        PrintWriter out = getOut();
        out.print("   <td");
        decorate(tempDecorator);
        out.print('>');
        out.print(label);
        out.println("</td>");

    }

    @Override
    public void generateTotalCell(@Nonnull OutputCellContext ctx, @Nullable Number value) {

        ctx.getTempDecorator().setCssClass(resolveCellCss(ctx.getColumn(), -1, ctx.isColumnWithSeparator()));

        PrintWriter out = getOut();
        out.print("   <td");
        decorate(ctx.getTempDecorator());
        out.print(">");

        if (value != null && !ctx.isActionCell()) {
            String s = ctx.generateFormatDisplayString(value);
            if (s != null) {
                out.print(s);
            }
        }
        out.println("</td>");
    }

    @Override
    public void generateCell(@Nonnull OutputCellContext ctx) {

        InfoCell cell = ctx.getCell();
        Column column = ctx.getColumn();
        DecoratorCell tempDecorator = ctx.getTempDecorator();

        PrintWriter out = getOut();
        if (ctx.getLevel() != -1) {
            tempDecorator.addStyle("padding-left", ctx.getLevel() * 16 + "px");
        }
        if (tempDecorator.getCssClass() == null) {
            tempDecorator.setCssClass(resolveCellCss(column, ctx.getLevel(), ctx.isColumnWithSeparator()));
        } else if (ctx.isColumnWithSeparator()) {
            tempDecorator.setCssClass(tempDecorator.getCssClass() + " T_sep");
        }
        if (ctx.isActionCell()) {
            tempDecorator.setNoWrap();
        }

        out.print("   <td");
        decorateCell(tempDecorator);
        out.print(">");
        if (ctx.isNullContent()) {
            out.print("&nbsp;");
            out.println("</td>");
            return;
        }
        cellTagsOpen(ctx, cell, column, out);
        if (ctx.isActionCell()) {
            out.print(generateActions(getVOut(), ctx.getCell()));
        } else {
            String s = ctx.generateFormatDisplayString();
            if (s != null) {
                out.print(escapeHTML(s, ctx.getColumn().getType().getProcessor()));
            }
        }
        cellTagsClose(ctx, cell, column, out);
        out.println("</td>");
    }

    private String escapeHTML(String s, ColumnTypeProcessor type) {
        if (type instanceof ColumnTypeProcessor.ColumnTypeProcessorTypeRaw) {
            return s;
        }
        return AlocproToolkit.plainTextToHtml(s, false);
    }

    private void cellTagsOpen(@Nonnull OutputCellContext ctx, InfoCell cell, Column column, PrintWriter out) {
        if (column.isSmall()) {
            out.print("<small>");
        }
        if (column.isStrong()) {
            out.print("<strong>");
        }
        if (!ctx.getColumnProcessor().shouldBePrinted()) {
            out.print("<span class=\"naoImprime\">");
        }
        if (cell.getLink() != null) {
            generateLink(cell, out);
        }
    }

    private void cellTagsClose(@Nonnull OutputCellContext ctx, InfoCell cell, Column column, PrintWriter out) {
        if (cell.getLink() != null) {
            out.print("</a>");
        }
        if (!ctx.getColumnProcessor().shouldBePrinted()) {
            out.print("</span>");
        }
        if (column.isStrong()) {
            out.print("</strong>");
        }
        if (column.isSmall()) {
            out.print("</small>");
        }
    }

    private void generateLink(InfoCell cell, PrintWriter out) {
        WebRef link = cell.getLink();
        out.print("<a href=\"");
        if (link.isJs()) {
            out.print("#");
            //out.print(Optional.ofNullable(link.getAtributos()).map(m -> m.get("id")).orElse(""));
            out.print("\" onclick=\"" + link.getJs() + "return false;");
        } else {
            String url = link.getPath().getUrl(getUrlApp());
            if (!url.startsWith("http") && !url.startsWith(getUrlApp()) && !url.contains("://")) {
                out.print(getUrlApp());
                if (getUrlApp().endsWith("/") && url.startsWith("/")) {
                    out.print(url.substring(1));
                } else {
                    out.print(url);
                }
            } else {
                out.print(url);
            }
        }
        out.print('"');
        if (cell.getLinkTarget() != null) {
            printAttribute("target", cell.getLinkTarget());
        }
        if (cell.getLinkTitle() != null) {
            printAttribute("title", cell.getLinkTitle());
        }
        out.print('>');
    }

    private String resolveCellCss(Column column, int level, boolean columnWithSeparator) {
        if (level != -1) {
            if (level <= 4) {
                return "T_DL" + level + "0";
            } else {
                return "T_DLN";
            }
        }
        switch (column.getAlignment()) {
            case CENTER:
                return columnWithSeparator ? "T_cen T_sep" : "T_cen";
            case RIGHT:
                return columnWithSeparator ? "T_dir T_sep" : "T_dir";
            default:
                return columnWithSeparator ? "T_sep" : null;
        }
    }

    private static String generateActions(ViewOutputHtml out, InfoCell cell) {
        return cell.getActions().stream().filter(Predicates.notNull()).filter(WebRef::appliesToContext).map(
                webActionEnabled -> webActionEnabled.generateHtml(out.getUrlApp())).filter(Predicates.notNull())
                .collect(
                        Collectors.joining());
    }

    /**
     * Gera na tag HTML atual os atributos HTML determinadas pelo decorador informado, se o decorador possuir
     * configuração específica.
     */
    final void decorateCell(@Nonnull DecoratorCell decorator) {
        if (decorator.getColSpan() > 1) {
            printAttribute("colspan", decorator.getColSpan());
        }
        if (decorator.getRowSpan() > 1) {
            printAttribute("rowspan", decorator.getRowSpan());
        }
        decorate(decorator);
    }

    /**
     * Gera na tag HTML atual os atributos HTML determinadas pelo decorador informado, se o decorador possuir
     * configuração específica.
     */
    final void decorate(@Nonnull Decorator decorator) {
        StringBuilder builder = new StringBuilder();
        printAttribute("class", decorator.getCssClass());
        if (!decorator.getStyles().isEmpty()) {
            addDecoratorStyles(decorator, builder);
        }
        if (!decorator.getAttributes().isEmpty()) {
            addDecoratorAttributes(decorator, builder);
        }
        if (builder.length() != 0) {
            print(builder.toString());
        }
    }

    private void addDecoratorStyles(@Nonnull Decorator decorator, StringBuilder builder) {
        int indexStyle = builder.indexOf("style=\"");
        if (indexStyle == -1) {
            builder.append(" style=\"");
            for (Map.Entry<String, String> style : decorator.getStyles().entrySet()) {
                builder.append(style.getKey()).append(':').append(style.getValue()).append(';');
            }
            builder.append('"');
        } else {
            for (Map.Entry<String, String> style : decorator.getStyles().entrySet()) {
                builder.insert(indexStyle + 7, style.getKey().concat(":").concat(stringValue(style)).concat(";"));
            }
        }
    }

    private void addDecoratorAttributes(@Nonnull Decorator decorator, StringBuilder builder) {
        for (Map.Entry<String, String> att : decorator.getAttributes().entrySet()) {
            if ("class".equalsIgnoreCase(att.getKey())) {
                int indexClass = builder.indexOf("class=\"");
                if (indexClass > -1) {
                    indexClass = builder.substring(indexClass + 7).indexOf('"') + indexClass + 7;
                    builder.insert(indexClass, " ".concat(stringValue(att)));
                    continue;
                }
            } else if ("style".equalsIgnoreCase(att.getKey())) {
                int indexStyle = builder.indexOf("style=\"");
                if (indexStyle > -1) {
                    builder.insert(indexStyle + 7, " ".concat(stringValue(att)).concat(";"));
                    continue;
                }
            }
            builder.append(' ').append(att.getKey()).append("=\"").append(att.getValue()).append('"');
        }
    }

    private static String stringValue(Map.Entry<String, String> entry) {
        return entry.getValue() == null ? "null" : entry.getValue();
    }

    private String datatablesOptions() {
        return "{'oLanguage' : {\n" +
                "    'sEmptyTable': 'Nenhum registro encontrado',\n" +
                "    'sInfo': 'Mostrando de _START_ até _END_ de _TOTAL_ registros',\n" +
                "    'sInfoEmpty': 'Mostrando 0 até 0 de 0 registros',\n" +
                "    'sInfoFiltered': '(Filtrados de _MAX_ registros)',\n" +
                "    'sInfoPostFix': '',\n" +
                "    'sInfoThousands': '.',\n" +
                "    'sLengthMenu': '_MENU_ resultados por página',\n" +
                "    'sLoadingRecords': 'Carregando...',\n" +
                "    'sProcessing': 'Processando...',\n" +
                "    'sZeroRecords': 'Nenhum registro encontrado',\n" +
                "    'sSearch': 'Pesquisar',\n" +
                "    'oPaginate': {\n" +
                "        'sNext': 'Próximo',\n" +
                "        'sPrevious': 'Anterior',\n" +
                "        'sFirst': 'Primeiro',\n" +
                "        'sLast': 'Último'\n" +
                "    },\n" +
                "    'oAria': {\n" +
                "        'sSortAscending': ': Ordenar colunas de forma ascendente',\n" +
                "        'sSortDescending': ': Ordenar colunas de forma descendente'\n" +
                "    }\n" +
                "}}";
    }
}