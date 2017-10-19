/*
 *
 *  * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.opensingular.form.view;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.opensingular.form.util.HtmlSanitizer;

import static org.junit.Assert.assertEquals;

public class TestSanitizeHTML {

    @Test
    @Ignore
    public void testIfKeepHtmlTag(){
        String htmlWithEntity = "<html></html>";
        assertEquals(htmlWithEntity, HtmlSanitizer.sanitize(htmlWithEntity));
    }

    @Test
    @Ignore
    public void testIfKeepHtmlEntities(){
        String htmlWithEntity = "<i>Of&iacute;cio n&#x00ba;</i>";
        assertEquals(htmlWithEntity, HtmlSanitizer.sanitize(htmlWithEntity));
    }

    @Test
    public void addStyleTag() {
        StringBuilder sb = new StringBuilder(getBaseHtml());
        sb.append("<style type=\"text/css\">\n");
        sb.append("    body {\n");
        sb.append("        font-family: sans-serif;\n");
        sb.append("    }\n");
        sb.append("    .bordered-table {\n");
        sb.append("        border-collapse: collapse;\n");
        sb.append("        width: 100%;\n");
        sb.append("    }\n");
        sb.append("</style>\n");

        String htmlInput = sb.toString();
        Assert.assertTrue(htmlInput.contains("bordered-table"));
        String safeHtml = HtmlSanitizer.sanitize(htmlInput);
        Assert.assertTrue(safeHtml.contains("bordered-table"));
    }

    @Test
    public void removeJavaScriptEvent() {
        StringBuilder sb = new StringBuilder(getBaseHtml());
        sb.append("<img src=\"\" onmouseover=\"alert('XSS')\" />");

        String htmlInput = sb.toString();
        String safeHtml = HtmlSanitizer.sanitize(htmlInput);
        Assert.assertFalse(safeHtml.contains("onmouseover"));

        Assert.assertTrue(safeHtml.contains("<img src=\"\" />"));
    }

    @Test
    public void removeScriptTag() {
        StringBuilder sb = new StringBuilder(getBaseHtml());
        sb.append("<script language='javascript'>alert('XSS')</script>;");

        String htmlInput = sb.toString();
        String safeHtml = HtmlSanitizer.sanitize(htmlInput);
        Assert.assertFalse(safeHtml.contains("<script laguage='javascript'>"));
    }

    @Test
    public void removeExternalLinks() {
        StringBuilder sb = new StringBuilder(getBaseHtml());
        sb.append("<a href='#'>ancora</a>");
        sb.append("<a href='http://www.google.com'>link externo</a>");

        String htmlInput = sb.toString();
        String safeHtml = HtmlSanitizer.sanitize(htmlInput);
        Assert.assertFalse(safeHtml.contains("<a href='http://www.google.com'>"));
        Assert.assertTrue(safeHtml.contains("link externo"));
        Assert.assertTrue(safeHtml.contains("<a href=\"#\" rel=\"nofollow\">ancora</a>"));
    }

    private String getBaseHtml() {
        StringBuffer sb = new StringBuffer("");
        sb.append("<table border=\"0\" cellSpacing=\"3\" cellPadding=\"0\" width=\"760\" align=\"center\" style=\"page-break-inside: avoid;\">\n");
        sb.append("    <tbody>\n");
        sb.append("    <tr>\n");
        sb.append("        <td align=\"center\" style=\"text-decoration: underline;\"><b>TESTE</b></td>\n");
        sb.append("    </tr>\n");
        sb.append("    <tr>\n");
        sb.append("        <td>\n");
        sb.append("            <table style=\"width: 100%;\" class=\"semborda\" border=\"0\" cellSpacing=\"2\" cellPadding=\"5\" align=\"center\">\n");
        sb.append("                <tbody>\n");
        sb.append("                <tr>\n");
        sb.append("                    <td class=\"formulario\" style=\"font-weight: bold;\" width=\"30%\">CEL1</td>\n");
        sb.append("                    <td class=\"formulario\" colSpan=\"3\">VAL1</td>\n");
        sb.append("                </tr>\n");
        sb.append("                </tbody>\n");
        sb.append("            </table>\n");
        sb.append("        </td>\n");
        sb.append("    </tr>\n");
        sb.append("    <tr>\n");
        sb.append("        <td></td>\n");
        sb.append("    </tr>\n");
        sb.append("    <tr>\n");
        sb.append("        <td>\n");
        sb.append("            <table style=\"width: 100%;\" border=\"0\" cellSpacing=\"2\" cellPadding=\"5\" align=\"center\">\n");
        sb.append("                <tbody>\n");
        sb.append("                <tr>\n");
        sb.append("                    <td style=\"text-align: justify\" class=\"Corpo_Exigencia\">\n");
        sb.append("                        <label style=\"font-weight: bold;\">Titulo</label> <br/> <br/> Prezados Senhores,<br/> <br/> Segue notifica&ccedil;&atilde;o\n");
        sb.append("                        de exig&ecirc;ncia para o processo identificado acima.<br/> <br/>\n");
        sb.append("                        <ol style=\"margin-left: 30px;\">\n");
        sb.append("                        </ol>\n");
        sb.append("                        <hr SIZE=\"1\"/>\n");
        sb.append("                        Informamos que de acordo com o Art. 15 do Decreto...\n");
        sb.append("                        <br/><br/>\n");
        sb.append("                        Bras&iacute;lia, #DATA_GERACAO <br/> <br/> <br/> <br/>\n");
        sb.append("                        <center></center>\n");
        sb.append("                    </td>\n");
        sb.append("                </tr>\n");
        sb.append("                </tbody>\n");
        sb.append("            </table>\n");
        sb.append("        </td>\n");
        sb.append("    </tr>\n");
        sb.append("    </tbody>\n");
        sb.append("</table>\n");
        return sb.toString();
    }
}
