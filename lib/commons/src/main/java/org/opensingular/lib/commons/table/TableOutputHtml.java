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
import org.opensingular.lib.commons.views.ViewOutput;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.PrintWriter;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Daniel C. Bordin on 16/04/2017.
 */
public class TableOutputHtml extends TableOutput {

    private final ViewOutput vOut;

    public TableOutputHtml(ViewOutput vOut) {this.vOut = vOut;}

    @Override
    public String getUrlApp() {
        return vOut.getUrlApp();
    }

    @Override
    public boolean isStaticContent() {
        return vOut.isStaticContent();
    }

    public ViewOutput getVOut() {
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

    final TableOutputHtml printAtributo(String nomeAtributo, int value) {
        return printAtributo(nomeAtributo, Integer.toString(value));
    }

    final TableOutputHtml printAtributo(String nomeAtributo, String valor) {
        if (valor != null) {
            PrintWriter out = getOut();
            out.print(' ');
            out.print(nomeAtributo);
            out.print("=\"");
            out.print(valor);
            out.print('"');
        }
        return this;
    }

    @Override
    public void generateTableStart(OutputTableContext ctx, TableTool tableTool) {
        println();
        print("<table cellpadding='0' cellspacing='0'");
        if (tableTool.isCorLinhaAlternada()) {
            printAtributo("class", "T_t table table-bordered table-condensed table-hover table-striped");
        } else {
            printAtributo("class", "T_t table table-bordered table-condensed table-hover");
        }
        printAtributo("width", tableTool.getWidth());
        printAtributo("align", tableTool.getAlign());
        printAtributo("id", tableTool.getId());
        decorate(tableTool.getDecorator());
        println(">");
    }

    @Override
    public void generateTableEnd(OutputTableContext ctx, TableTool tableTool) {
        println("</table>");
        println();
    }

    @Override
    public void generateBodyBlockStart(@Nonnull OutputTableContext ctx) {
        if (ctx.getTableTool().isSimpleTable()) {
            if (ctx.getTableTool().isCorLinhaAlternada()) {
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
        if (! ctx.getTableTool().isTotalizar()) {
            println("</tbody>");
        }
    }

    @Override
    public void generateLineSimpleStart(OutputTableContext ctx, InfoLinha line, int lineAlternation) {
        if (lineAlternation != -1 ) {
            line.getDecorador().setCssClass(lineAlternation == 0 ? "T_ls0" : "T_ls1");
        }
        print("  <tr");
        decorate(line.getDecorador());
        println(">");
    }

    @Override
    public void generateLineSimpleEnd(OutputTableContext ctx) {
        println("  </tr>");
    }

    @Override
    public void generateLineTreeStart(OutputTableContext ctx, InfoLinha line, int nivel) {
        if (ctx.getDecorador().getCssClass() == null) {
            ctx.getDecorador().setCssClass(nivel <= 4 ? "T_R_" + nivel : "T_R_N");
        }

        print("  <tr");
        decorate(line.getDecorador());
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
    public void generateTiltleCell(OutputTableContext ctx, Column column, int rowSpan, boolean asSubTitle,
            boolean columnWithSeparator) {
        PrintWriter out = getOut();
        out.print("   <th");
        if (column.getWidth() != null) {
            printAtributo("width", column.getWidth());
        }
        if (rowSpan > 1) {
            printAtributo("rowspan", Integer.toString(rowSpan));
        }
        if (asSubTitle) {
            switch (column.getAlinhamento()) {
                case tpCentro:
                    printClass(out, "T_subtit_cen", columnWithSeparator);
                    break;
                case tpDireita:
                    printClass(out, "T_subtit_dir", columnWithSeparator);
                    break;
                default:
                    printClass(out, "T_subtit", columnWithSeparator);
                    break;
            }
        } else {
            switch (column.getAlinhamento()) {
                case tpCentro:
                    printClass(out, "T_tit_cen", columnWithSeparator);
                    break;
                case tpDireita:
                    printClass(out, "T_tit_dir", columnWithSeparator);
                    break;
                default:
                    printClass(out, null, columnWithSeparator);
                    break;
            }
        }
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
            printAtributo("colspan", colSpan);
        }
        printClass(out, "T_tit_super", cColumnWithSeparator);
        out.print(">");
        out.print(column.getSuperTitle());
        out.println("</th>");
    }

    @Override
    public void generateTotalLineStart(@Nonnull OutputTableContext ctx, @Nonnull InfoLinha totalLine,
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
        getOut().println("</tbody>");
    }

    @Override
    public void generateTotalCellSkip(@Nonnull OutputTableContext ctx, @Nonnull Column column,
            boolean columnWithSeparator) {
        if (columnWithSeparator) {
            getOut().print("   <td class=\"T_sep\">&nbsp;</td>");
        } else {
            getOut().print("   <td>&nbsp;</td>");
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

        InfoCelula cell = ctx.getCell();
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
        if (ctx.isActionCell()) {
            out.print(gerarAcoes(getVOut(), ctx.getCell()));
        } else {
            String s = ctx.generateFormatDisplayString();
            if (s != null) {
                out.print(s);
            }
        }
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
        out.println("</td>");
    }

    private void generateLink(InfoCelula cell, PrintWriter out) {
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
            printAtributo("target", cell.getLinkTarget());
        }
        if (cell.getLinkTitle() != null) {
            printAtributo("title", cell.getLinkTitle());
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
        switch (column.getAlinhamento()) {
            case tpCentro:
                return columnWithSeparator ? "T_cen T_sep" : "T_cen";
            case tpDireita:
                return columnWithSeparator ? "T_dir T_sep" : "T_dir";
            default:
                return columnWithSeparator ? "T_sep" : null;
        }
    }

    private static String gerarAcoes(ViewOutput out, InfoCelula cell) {
        return cell.getAcoes().stream().filter(Predicates.notNull()).filter(WebRef::appliesToContext).map(
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
            printAtributo("colspan", decorator.getColSpan());
        }
        if (decorator.getRowSpan() > 1) {
            printAtributo("rowspan", decorator.getRowSpan());
        }
        decorate(decorator);
    }

    /**
     * Gera na tag HTML atual os atributos HTML determinadas pelo decorador informado, se o decorador possuir
     * configuração específica.
     */
    final void decorate(@Nonnull Decorator decorator) {
        StringBuilder builder = new StringBuilder();
        printAtributo("class", decorator.getCssClass());
        if (!decorator.getStyles().isEmpty()) {
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
        if (!decorator.getAttributes().isEmpty()) {
            for (Map.Entry<String, String> att : decorator.getAttributes().entrySet()) {
                if (att.getKey().equalsIgnoreCase("class")) {
                    int indexClass = builder.indexOf("class=\"");
                    if (indexClass > -1) {
                        indexClass = builder.substring(indexClass + 7).indexOf('"') + indexClass + 7;
                        builder.insert(indexClass, " ".concat(stringValue(att)));
                        continue;
                    }
                } else if (att.getKey().equalsIgnoreCase("style")) {
                    int indexStyle = builder.indexOf("style=\"");
                    if (indexStyle > -1) {
                        builder.insert(indexStyle + 7, " ".concat(stringValue(att)).concat(";"));
                    }
                }
                builder.append(' ').append(att.getKey()).append("=\"").append(att.getValue()).append('"');
            }
        }
        if (builder.length() != 0) {
            print(builder.toString());
        }
    }

    private static String stringValue(Map.Entry<String, String> entry) {
        return entry.getValue() == null ? "null" : entry.getValue();
    }
}