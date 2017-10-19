
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

package org.opensingular.lib.commons.views.format;

import org.apache.commons.lang3.StringUtils;
import org.opensingular.lib.commons.base.SingularException;
import org.opensingular.lib.commons.views.ViewOutput;
import org.opensingular.lib.commons.views.ViewOutputFormat;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

/**
 * Permite ao gerador da view adicionar ou obter informações extras referentes
 * ao conteúdo gerado.
 *
 * @author Daniel C. Bordin - 21/09/2005
 */
public abstract class ViewOutputHtml implements ViewOutput<Writer> {

    private String pathAttachment;

    private String urlApp_;

    private PrintWriter pOut_;

    private Map<String, Object> attributes;

    private HtmlCode htmlCode;

    @Override
    public ViewOutputFormat getFormat() {
        return ViewOutputFormat.HTML;
    }

    public void copyConfig(ViewOutputHtml vOut) {
        pathAttachment = vOut.pathAttachment;
        urlApp_ = vOut.urlApp_;
        if (vOut.attributes != null) {
            attributes = new HashMap<>(vOut.attributes);
        }
    }

    public abstract boolean isStaticContent();

    /**
     * Informa qual o caminho onde esto os arquivos referenciados pelo conteudo
     * gerado.
     */
    public void setPathAttachment(String path) {
        pathAttachment = path;
    }

    public String getPathAttachment() {
        return pathAttachment;
    }

    public void setUrlApp(String url) {
        urlApp_ = url;
    }

    public String getUrlApp() {
        if (urlApp_ == null) {
            //TODO Resolver como o singular terá acesso a sua URL de aplicação. Fazer um SPI
            throw new SingularException("Implementar esse código");
            //urlApp_ = PropriedadesSistema.getUrlAplicacao();
            //if (!urlApp_.endsWith("/")) {
            //    urlApp_ += "/";
            //}
        }
        return urlApp_;
    }


    public final PrintWriter getPrintWriter() {
        if (pOut_ == null) {
            Writer out = getOutput();
            if (out instanceof PrintWriter) {
                pOut_ = (PrintWriter) out;
            } else {
                pOut_ = new PrintWriter(out);
            }
        }
        return pOut_;
    }

    public void flush() {
        try {
            getOutput().flush();
        } catch (IOException e) {
            throw SingularException.rethrow("Falha ao descarregar contedo: " + e.getMessage(), e);
        }
    }

    /**
     * Adiciona uma imagem que  utilizada pelo contedo gerado.
     *
     * @param name Nome usado para refernciar a imagem.
     * @param content Contedo binrio da imagem.
     */
    public abstract void addImage(String name, byte[] content) throws IOException;

    /*
     * Adiciona uma imagem utilizada pelo contedo gerado, utiliza um DataSource
     *
     * @param nome Nome usado para referenciar a imagem
     * @param dataSource contedo da imagem
     *
     * public abstract void addImage(String nome, DataSourceComTamanho dataSource);
     */

    public void setAttribute(String name, Object value) {
        if (attributes == null) {
            if (value == null) {
                return;
            }
            attributes = new HashMap<>();
        }
        attributes.put(name, value);
    }

    public <T> T getAttribute(String name) {
        return (attributes != null) ? (T) attributes.get(name) : null;
    }

    public <T> T getAttribute(String name, T defaultValue) {
        T v = getAttribute(name);
        return (v != null) ? v : defaultValue;
    }

    public HtmlCode html() {
        if (htmlCode == null) {
            htmlCode = new HtmlCode();
        }
        return htmlCode;
    }

    public class HtmlCode {
        public HtmlCode br() {
            getPrintWriter().println("<br style='clear: both;'/>");
            return this;
        }

        public HtmlCode tag(String tag, String text) {
            return tag(tag, text, "");
        }

        public HtmlCode tag(String tag, String text, String attributes) {
            openTag(tag, attributes);
            getPrintWriter().print(StringUtils.trimToEmpty(text));
            return closeTag(tag);
        }

        public HtmlCode openTag(String tag) {
            return openTag(tag, "");
        }

        public HtmlCode openTag(String tag, String attributes) {
            getPrintWriter().print("<" + tag + " " + attributes + ">");
            return this;
        }

        public HtmlCode closeTag(String lastTag) {
            getPrintWriter().println("</" + lastTag + ">");
            return this;
        }

        public HtmlCode div(String text, String attributes) {
            return tag("div", text, attributes);
        }

        public HtmlCode div(String text) {
            return div(text, "");
        }

        public HtmlCode script(String script) {
            return tag("script", script, "type=\"text/javascript\"");
        }

        public HtmlCode span(String text, String attributes) {
            return tag("span", text, attributes);
        }

        public HtmlCode span(String text) {
            return span(text, "");
        }

        public HtmlCode internalTitleReporterSeprator(String title) {
            PrintWriter out = getPrintWriter();
            out.println("\n<hr width='100%' style='clear: both;'>");
            out.println("<b>" + title + "</b>");
            out.println("<br/>");
            return this;
        }
    }
}