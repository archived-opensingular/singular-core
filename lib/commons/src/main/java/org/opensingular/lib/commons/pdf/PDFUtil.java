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

package org.opensingular.lib.commons.pdf;

import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.opensingular.lib.commons.util.Loggable;

import java.io.*;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * Classe utilitária para a manipulação de PDF's.
 */
@SuppressWarnings("UnusedDeclaration")
public abstract class PDFUtil implements Loggable {

    /**
     * A constante BEGIN_COMMAND.
     */
    private static final String BEGIN_COMMAND = "/";

    /**
     * A constante SET_FONT.
     */
    private static final String SET_FONT = "Tf\n";

    /**
     * A constante SPACE.
     */
    private static final int SPACE = 32;

    /**
     * A constante "username".
     */
    protected static String username = null;

    /**
     * A constante "password".
     */
    protected static String password = null;

    /**
     * A constante "proxy".
     */
    protected static String proxy = null;

    /**
     * A constante instance.
     */
    protected static volatile PDFUtil instance = null;

    /**
     * O caminho no sistema de arquivos para o local onde se encontram as bibliotecas nativas utilizadas
     * por este utilitário de manipulação de PDF's.
     */
    protected static String wkhtml2pdfHome = System.getProperty("singular.wkhtml2pdf.home", "native");

    /**
     * O tamanho da página. O valor padrão é {@link PageSize#PAGE_A4}.
     */
    private PageSize pageSize = null;

    /**
     * A orientação da página. O valor padrão é {@link PageOrientation#PAGE_PORTRAIT}.
     */
    private PageOrientation pageOrientation = null;

    /**
     * Indica quando necessário esperar por execução javascript na página.
     */
    private int javascriptDelay = 0;

    /**
     * Método Fabric.
     *
     * @return Um PDFUtil.
     */
    private static PDFUtil fabric() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            return PDFUtilWin.getInstance();
        } else {
            return PDFUtilUnix.getInstance();
        }
    }

    /**
     * Retorna o valor atual do atributo {@link #instance}.
     *
     * @return O valor atual do atributo.
     */
    public static PDFUtil getInstance() {
        if (instance == null) {
            return fabric();
        }

        return instance;
    }

    /**
     * Retorna um arquivo pdf vazio.
     *
     * @return O pdf criado.
     * @throws IOException
     */
    public abstract File createEmptyPdf() throws IOException;

    /**
     * Dividi as páginas de um PDF ao meio, gerando duas páginas para cada.
     *
     * @param pdf o PDF.
     * @return O arquivo PDF gerado.
     * @throws IOException          Caso ocorra um problema de IO.
     * @throws InterruptedException Caso ocorra um problema de sincronismo.
     */
    public abstract File splitPDF(File pdf) throws IOException, InterruptedException;

    /**
     * Converte o código HTML em um arquivo PDF com o cabeçalho e rodapé especificados.
     *
     * @param html   o código HTML.
     * @param header o código HTML do cabeçalho.
     * @param footer o código HTML do rodapé.
     * @return O arquivo PDF gerado.
     * @throws IOException          Caso ocorra um problema de IO.
     * @throws InterruptedException Caso ocorra um problema de sincronismo.
     */
    public File convertHTML2PDF(String html, String header, String footer)
            throws IOException, InterruptedException {
        return convertHTML2PDF(html, header, footer, null);
    }

    /**
     * Converte o código HTML em um arquivo PDF com o cabeçalho e rodapé especificados.
     *
     * @param html             o código HTML.
     * @param header           o código HTML do cabeçalho.
     * @param footer           o código HTML do rodapé.
     * @param additionalConfig configurações adicionais.
     * @return O arquivo PDF gerado.
     * @throws IOException          Caso ocorra um problema de IO.
     * @throws InterruptedException Caso ocorra um problema de sincronismo.
     */
    public abstract File convertHTML2PDF(String html, String header, String footer, List<String> additionalConfig)
            throws IOException, InterruptedException;

    /**
     * Converte o código HTML em um arquivo PNG.
     *
     * @param html             o código HTML.
     * @param additionalConfig configurações adicionais.
     * @return O arquivo PNG gerado.
     * @throws IOException          Caso ocorra um problema de IO.
     * @throws InterruptedException Caso ocorra um problema de sincronismo.
     */
    public abstract File convertHTML2PNG(String html, List<String> additionalConfig)
            throws IOException, InterruptedException;

    /**
     * Converte o código HTML em um arquivo PDF.
     *
     * @param html o código HTML.
     * @return O arquivo PDF gerado.
     * @throws IOException          Caso ocorra um problema de IO.
     * @throws InterruptedException Caso ocorra um problema de sincronismo.
     */
    public File convertHTML2PDF(String html) throws IOException, InterruptedException {
        return convertHTML2PDF(html, null);
    }

    /**
     * Converte o código HTML em um arquivo PNG.
     *
     * @param html o código HTML.
     * @return O arquivo PNG gerado.
     * @throws IOException          Caso ocorra um problema de IO.
     * @throws InterruptedException Caso ocorra um problema de sincronismo.
     */
    public File convertHTML2PNG(String html) throws IOException, InterruptedException {
        return convertHTML2PNG(html, null);
    }

    /**
     * Converte o código HTML em um arquivo PDF com o cabeçalho especificado.
     *
     * @param html   o código HTML.
     * @param header o código HTML do cabeçalho.
     * @return O arquivo PDF gerado.
     * @throws IOException          Caso ocorra um problema de IO.
     * @throws InterruptedException Caso ocorra um problema de sincronismo.
     */
    public File convertHTML2PDF(String html, String header) throws IOException, InterruptedException {
        return convertHTML2PDF(html, header, null);
    }

    /**
     * Adiciona os argumentos padrões para a geração do PDF.
     *
     * @param commandArgs o vetor com os argumentos.
     */
    protected void addDefaultPDFCommandArgs(List<String> commandArgs) {
        commandArgs.add("--print-media-type");
        commandArgs.add("--load-error-handling");
        commandArgs.add("ignore");

        if (username != null) {
            commandArgs.add("--username");
            commandArgs.add(username);
        }
        if (password != null) {
            commandArgs.add("--password");
            commandArgs.add(password);
        }
        if (proxy != null) {
            commandArgs.add("--proxy");
            commandArgs.add(proxy);
        }

        if (pageSize != null) {
            commandArgs.add("--page-size");
            commandArgs.add(pageSize.getValue());
        }
        if (pageOrientation != null) {
            commandArgs.add("--orientation");
            commandArgs.add(pageOrientation.getValue());
        }
        if (javascriptDelay > 0) {
            commandArgs.add("--javascript-delay");
            commandArgs.add(String.valueOf(javascriptDelay));
        }

        addSmartBreakScript(commandArgs);

    }

    /**
     * adiciona um script minificado de break de texto com mais de 1000 caracteres de forma automatica,
     * segue em comentario a versão original
     * <p>
     * (function () {
     * function preventBreakWrap(value) {
     * return '<span style=\'page-break-inside: avoid\'>' + value + '</span>';
     * }
     * function breakInBlocks(value, size) {
     * if (value.length > size) {
     * return preventBreakWrap(value.substr(0, size)) + breakInBlocks(value.substr(size, value.length), size);
     * }
     * return value;
     * }
     * function visitLeafs(root, visitor) {
     * if (root.children.length == 0) {
     * visitor(root);
     * } else {
     * for (var i = 0; i < root.children.length; i += 1) {
     * visitLeafs(root.children[i], visitor);
     * }
     * }
     * }
     * visitLeafs(document.getElementsByTagName('body')[0], function(e) {
     * e.innerHTML = breakInBlocks(e.innerHTML, 1000);
     * });
     * })();
     *
     * @param commandArgs os argumentos
     */
    private void addSmartBreakScript(List<String> commandArgs) {
        final String minificado = "\"!function(){function a(a){return'<span style=\\\'page-break-inside: avoid\\\'>'+" +
                "a+'</span>'}function b(c,d){return c.length>d?a(c.substr(0,d))+b(c.substr(d,c.length),d):c}function " +
                "c(a,b){if(0==a.children.length)b(a);else for(var d=0;d<a.children.length;d+=1)c(a.children[d],b)}c(d" +
                "ocument.getElementsByTagName('body')[0],function(a){a.innerHTML=b(a.innerHTML,600)})}();\"";
        commandArgs.add("--run-script");
        commandArgs.add(minificado);
    }

    /**
     * Adiciona os argumentos padrões para a geração do PNG.
     *
     * @param commandArgs o vetor com os argumentos.
     */
    protected void addDefaultPNGCommandArgs(List<String> commandArgs) {
        commandArgs.add("--format");
        commandArgs.add("png");
        commandArgs.add("--load-error-handling");
        commandArgs.add("ignore");

        if (username != null) {
            commandArgs.add("--username");
            commandArgs.add(username);
        }
        if (password != null) {
            commandArgs.add("--password");
            commandArgs.add(password);
        }
        if (proxy != null) {
            commandArgs.add("--proxy");
            commandArgs.add(proxy);
        }
        if (javascriptDelay > 0) {
            commandArgs.add("--javascript-delay");
            commandArgs.add(String.valueOf(javascriptDelay));
        }
    }

    /**
     * Adiciona os argumentos padrões para a geração do cabeçalho.
     *
     * @param commandArgs o vetor com os argumentos.
     */
    protected void addDefaultHeaderCommandArgs(List<String> commandArgs) {
        commandArgs.add("--header-spacing");
        commandArgs.add("5");
    }

    /**
     * Adiciona os argumentos padrões para a geração do rodapé.
     *
     * @param commandArgs o vetor com os argumentos.
     */
    protected void addDefaultFooterCommandArgs(List<String> commandArgs) {
        commandArgs.add("--footer-spacing");
        commandArgs.add("5");
    }

    /**
     * Altera o valor do atributo {@link #pageSize}.
     *
     * @param pageSize o novo valor a ser utilizado para "page size".
     */
    public void setPageSize(PageSize pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * Altera o valor do atributo {@link #pageOrientation}.
     *
     * @param pageOrientation o novo valor a ser utilizado para "page orientation".
     */
    public void setPageOrientation(PageOrientation pageOrientation) {
        this.pageOrientation = pageOrientation;
    }

    /**
     * Altera o valor do atributo {@link #javascriptDelay}.
     *
     * @param javascriptDelay o novo valor a ser utilizado para "javascript delay".
     */
    public void setJavascriptDelay(int javascriptDelay) {
        this.javascriptDelay = javascriptDelay;
    }

    /**
     * O enumerador para o tamanho da página.
     */
    public enum PageSize {
        PAGE_A3("A3"),
        PAGE_A4("A4"),
        PAGE_LETTER("Letter");

        private String value;

        PageSize(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    /**
     * O enumerador para a orientação da página.
     */
    public enum PageOrientation {
        PAGE_PORTRAIT("Portrait"),
        PAGE_LANDSCAPE("Landscape");

        private String value;

        PageOrientation(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public File merge(List<InputStream> pdfs) {

        final PDFMergerUtility pdfMergerUtility = new PDFMergerUtility();

        try {
            pdfs.forEach(pdfMergerUtility::addSource);
            File tempMergedFile = File.createTempFile("merged-" + UUID.randomUUID().toString(), ".pdf");
            try (FileOutputStream output = new FileOutputStream(tempMergedFile)) {
                pdfMergerUtility.setDestinationStream(output);
                pdfMergerUtility.mergeDocuments(MemoryUsageSetting.setupTempFileOnly());
                return tempMergedFile;
            }
        } catch (IOException e) {
            getLogger().error(e.getMessage(), e);
        }

        return null;
    }

    protected String safeWrapHtml(String html) {
        if (html == null || html.startsWith(("<!DOCTYPE"))) {
            return html;
        }
        String  wraped   = html;
        boolean needHTML = !html.startsWith("<html>");
        boolean needBody = needHTML && !html.startsWith("<body>");
        if (needBody) {
            wraped = "<body>" + wraped + "<body>";
        }
        if (needHTML) {
            wraped = "<!DOCTYPE HTML><html>" + wraped + "</html>";
        }
        return wraped;
    }

}