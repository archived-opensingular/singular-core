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
import info.mirante.develox.view.CSSUtil;
import info.mirante.develox.view.ViewOutput;
import org.jetbrains.annotations.NotNull;
import org.opensingular.lib.commons.net.WebRef;

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

    private boolean innerTable = true;

    public TableOutputHtml(ViewOutput vOut) {this.vOut = vOut;}

    public void withoutInnerTable() {
        innerTable = false;
    }

    final CSSUtil getCSS() {
        return vOut.getCss();
    }

    @Override
    public String getUrlApp() {
        return vOut.getUrlApp();
    }

    @Override
    public boolean isStaticConent() {
        return vOut.isEmail();
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
        print("<table cellpadding='0' cellspacing='0' ");
        print(getCSS().tbl("RA_TABELA_EXTERNA"));
        printAtributo("width", tableTool.getWidth());
        printAtributo("align", tableTool.getAlign());
        printAtributo("id", tableTool.getId());

        decorate(tableTool.getDecorator());

        if (innerTable) {
            println("><tr><td>");
            print("<table cellpadding='2' cellspacing='1' ");
            if (tableTool.getId() != null) {
                printAtributo("id", tableTool.getId() + "_internal");
            }
            print(getCSS().tbl("RA_TABELA_INTERNA"));
            if (tableTool.getWidth() != null) {
                printAtributo("width", "100%");
            }
        }
        println(">");
    }

    @Override
    public void generateTableEnd(OutputTableContext ctx, TableTool tableTool) {
        println("</table>");
        if (innerTable) {
            println("</td></tr></table>");
        }
        println();
    }

    @Override
    public void generateLineSimpleStart(OutputTableContext ctx, InfoLinha line, int lineAlternation) {
        ctx.getDecorador().setCssClass((lineAlternation == -1 || lineAlternation == 0) ? "RA_LIN_0" : "RA_LIN_1");
        print("  <tr");
        decorate(line.getDecorador());
        println(">");
    }

    @Override
    public void generateLineSimpleEnd(OutputTableContext ctx) {
        println("  </tr>");
    }

    @Override
    public void generateColumnSeparator(OutputTableContext ctx, int rowSpan) {
        print("   <td ").print(getCSS().tbl("RA_TD_SEP"));
        if (rowSpan > 1) {
            printAtributo("rowspan", rowSpan);
        }
        println(">&nbsp;</td> ");
    }

    @Override
    public void generateLineTreeStart(OutputTableContext ctx, InfoLinha line, int nivel) {
        if (ctx.getDecorador().getCssClass() == null) {
            ctx.getDecorador().setCssClass(nivel <= 2 ? "RA_TR_" + nivel : "RA_TR_N");
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
        getOut().println("  <thead>");
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
    public void generateTiltleCell(OutputTableContext ctx, Column column, int rowSpan, boolean asSubTitle) {
        PrintWriter out = getOut();
        out.print("   <td");
        if (column.getWidth() != null) {
            printAtributo("width", column.getWidth());
        }
        if (rowSpan > 1) {
            printAtributo("rowspan", Integer.toString(rowSpan));
        }
        out.print(' ');
        if (asSubTitle) {
            switch (column.getAlinhamento()) {
                case tpCentro:
                    out.print(getCSS().tbl("RA_SUBTITULO_CEN"));
                    break;
                case tpDireita:
                    out.print(getCSS().tbl("RA_SUBTITULO_DIR"));
                    break;
                default:
                    out.print(getCSS().tbl("RA_SUBTITULO"));
                    break;
            }
        } else {
            switch (column.getAlinhamento()) {
                case tpCentro:
                    out.print(getCSS().tbl("RA_TITULO_CEN"));
                    break;
                case tpDireita:
                    out.print(getCSS().tbl("RA_TITULO_DIR"));
                    break;
                default:
                    out.print(getCSS().tbl("RA_TITULO"));
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
        out.println("</td>");
    }

    @Override
    public void generateTitleCellSuper(OutputTableContext ctx, Column column, int colSpan) {
        PrintWriter out = getOut();
        out.print("   <td");
        if (colSpan > 1) {
            printAtributo("colspan", colSpan);
        }
        out.print(' ');
        out.print(getCSS().tbl("RA_TITULO_CEN"));
        out.print("><b>");
        out.print(column.getSuperTitle());
        out.println("</b></td>");
    }

    @Override
    public void generateTotalLineStart(@Nonnull OutputTableContext ctx, @Nonnull InfoLinha totalLine,
            @Nonnull Decorator tempDecorator, int lineAlternation, int level) {
        if (lineAlternation != -1) {
            if (lineAlternation == 0) {
                tempDecorator.setCssClass("RA_LIN_0");
            } else {
                tempDecorator.setCssClass("RA_LIN_1");
            }
        } else if (level != -1) {
            if (level <= 2) {
                tempDecorator.setCssClass("RA_TR_" + level);
            } else {
                tempDecorator.setCssClass("RA_TR_N");
            }
        } else {
            tempDecorator.setCssClass("RA_LIN_1");
        }

        PrintWriter out = getOut();
        out.print("  <tr");
        decorate(tempDecorator);
        out.println(">");
    }

    @Override
    public void generateTotalLineEnd(@Nonnull OutputTableContext ctx) {
        getOut().print("  </tr>");
    }

    @Override
    public void generateTotalCellSkip(@Nonnull OutputTableContext ctx, @Nonnull Column column) {
        getOut().print("   <td>&nbsp;</td>");
    }

    @Override
    public void generateTotalLabel(@Nonnull OutputTableContext ctx, @Nonnull Column column, @Nonnull String label,
            @Nonnull DecoratorCell tempDecorator, int level) {
        tempDecorator.setCssClass(resolveCellCss(column, level));

        PrintWriter out = getOut();
        out.print("   <td");
        decorate(tempDecorator);
        out.print("><strong>");
        out.print(label);
        out.print("</strong></td>");

    }

    @Override
    public void generateTotalCell(@Nonnull OutputCellContext ctx, @Nullable Number value) {

        ctx.getTempDecorator().setCssClass(resolveCellCss(ctx.getColumn(), -1));

        PrintWriter out = getOut();
        out.print("   <td");
        decorate(ctx.getTempDecorator());
        out.print(">");

        if (value != null && !ctx.isActionCell()) {
            String s = ctx.generateFormatDisplayString(value);
            if (s != null) {
                out.print("<strong>");
                out.print(s);
                out.print("</strong>");
            }
        }
        out.print("</td>");
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
            tempDecorator.setCssClass(resolveCellCss(column, ctx.getLevel()));
        }
        if (ctx.isActionCell()) {
            tempDecorator.setNoWrap();
        }

        out.print("   <td ");
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

    @NotNull
    private String resolveCellCss(Column column, int level) {
        if (level != -1) {
            if (level <= 2) {
                return "RA_TD" + level + "0";
            } else {
                return "RA_TDN0";
            }
        }
        switch (column.getAlinhamento()) {
            case tpCentro:
                return "RA_TDN_CEN";
            case tpDireita:
                return "RA_TDN_DIR";
            default:
                return "RA_TDN_ESQ";
        }
    }

    private static String gerarAcoes(ViewOutput out, InfoCelula cell) {
        return cell.getAcoes().stream().filter(Predicates.notNull()).filter(WebRef::isSeAplicaAoContexto).map(
                webActionEnabled -> webActionEnabled.gerarHtml(out.getUrlApp())).filter(Predicates.notNull()).collect(
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
        if (decorator.getCssClass() != null) {
            builder.append(' ').append(getCSS().tbl(decorator.getCssClass()));
        }
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