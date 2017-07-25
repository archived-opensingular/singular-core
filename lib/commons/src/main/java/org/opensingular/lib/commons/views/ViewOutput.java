
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

package org.opensingular.lib.commons.views;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

/**
 * Permite ao gerador da view adicionar ou obter informa��es extras referentes
 * ao conte�do gerado.
 *
 * @author Daniel C. Bordin - 21/09/2005
 */
public abstract class ViewOutput {

    private String pathAnexo_;

    private String urlApp_;

    private PrintWriter pOut_;

    private Map<String, Object> atributos;

    private HtmlCode htmlCode;

    public void copiarConfig(ViewOutput vOut) {
        pathAnexo_ = vOut.pathAnexo_;
        urlApp_ = vOut.urlApp_;
        if (vOut.atributos != null) {
            atributos = new HashMap<>(vOut.atributos);
        }
    }

    public abstract boolean isStaticContent();

    /**
     * Informa qual o caminho onde est�o os arquivos referenciados pelo conteudo
     * gerado.
     */
    public void setPathAnexo(String caminho) {
        pathAnexo_ = caminho;
    }

    public String getPathAnexo() {
        return pathAnexo_;
    }

    public void setUrlApp(String url) {
        urlApp_ = url;
    }

    public String getUrlApp() {
        if (urlApp_ == null) {
            //TODO Resolver como o singular terá acesso a sua URL de aplicação. Fazer um SPI
            throw new RuntimeException("Implementar esse código");
            //urlApp_ = PropriedadesSistema.getUrlAplicacao();
            //if (!urlApp_.endsWith("/")) {
            //    urlApp_ += "/";
            //}
        }
        return urlApp_;
    }

    /**
     * Obtem a sa�da de escrita do conte�do da view.
     *
     * @return Deve ser sempre diferente de null.
     */
    public abstract Writer getWriter();

    public final PrintWriter getPrintWriter() {
        if (pOut_ == null) {
            Writer out = getWriter();
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
            getWriter().flush();
        } catch (IOException e) {
            throw new RuntimeException("Falha ao descarregar conte�do: " + e.getMessage(), e);
        }
    }

    /**
     * Adiciona uma imagem que � utilizada pelo conte�do gerado.
     *
     * @param nome Nome usado para refer�nciar a imagem.
     * @param dados Conte�do bin�rio da imagem.
     */
    public abstract void addImagem(String nome, byte[] dados) throws IOException;

    /*
     * Adiciona uma imagem utilizada pelo conte�do gerado, utiliza um DataSource
     *
     * @param nome Nome usado para referenciar a imagem
     * @param dataSource conte�do da imagem
     *
     * public abstract void addImagem(String nome, DataSourceComTamanho dataSource);
     */

    public void setAtributo(String nome, Object valor) {
        if (atributos == null) {
            if (valor == null) {
                return;
            }
            atributos = new HashMap<>();
        }
        atributos.put(nome, valor);
    }

    public <T> T getAtributo(String nome) {
        return (atributos != null) ? (T) atributos.get(nome) : null;
    }

    public <T> T getAtributo(String nome, T valorDefault) {
        T v = getAtributo(nome);
        return (v != null) ? v : valorDefault;
    }

    public HtmlCode html() {
        if (htmlCode == null) {
            htmlCode = new HtmlCode();
        }
        return htmlCode;
    }

    public abstract ViewOutputFormat getFormat();

    public class HtmlCode {
        public HtmlCode br() {
            getPrintWriter().println("<br style='clear: both;'/>");
            return this;
        }
        
        public HtmlCode tag(String tag, String texto) {
            return tag(tag, texto, "");
        }

        public HtmlCode tag(String tag, String texto, String atributtes) {
            openTag(tag, atributtes);
            getPrintWriter().print(StringUtils.trimToEmpty(texto));
            return closeTag(tag);
        }
        
        public HtmlCode openTag(String tag) {
            return openTag(tag, "");
        }
        
        public HtmlCode openTag(String tag, String atributtes) {
            getPrintWriter().print("<" + tag + " " + atributtes + ">");
            return this;
        }
        
        public HtmlCode closeTag(String lastTag) {
            getPrintWriter().println("</" + lastTag + ">");
            return this;
        }

        public HtmlCode div(String texto, String atributtes) {
            return tag("div", texto, atributtes);
        }

        public HtmlCode div(String texto) {
            return div(texto, "");
        }

        public HtmlCode script(String script) {
            return tag("script", script, "type=\"text/javascript\"");
        }

        public HtmlCode span(String texto, String atributtes) {
            return tag("span", texto, atributtes);
        }

        public HtmlCode span(String texto) {
            return span(texto, "");
        }

        public HtmlCode separadorTituloInternoRelatorio(String titulo) {
            PrintWriter out = getPrintWriter();
            out.println("\n<hr width='100%' style='clear: both;'>");
            out.println("<b>" + titulo + "</b>");
            out.println("<br/>");
            return this;
        }
    }
}