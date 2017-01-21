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

import org.opensingular.lib.commons.util.TempFileUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Classe utilitária para a manipulação de PDF's no Windows.
 * Essa versão sobre escreve as chamadas nativas da classe
 * {@link PDFUtil} com
 * as equivalentes para o sistema Windows.
 */
public class PDFUtilWin extends PDFUtil {

    /**
     * Instancia um novo objeto do tipo PDFUtilWin.
     */
    private PDFUtilWin() {
    }

    /**
     * Retorna o valor atual do atributo {@link #instance}.
     *
     * @return O valor atual do atributo.
     */
    public static PDFUtil getInstance() {
        if (instance == null) {
            instance = new PDFUtilWin();
        }
        return instance;
    }

    /**
     * Dividi as páginas de um PDF ao meio, gerando duas páginas para cada.
     *
     * @param pdf o PDF.
     * @return O arquivo PDF gerado.
     */
    @Override
    public File splitPDF(File pdf) throws IOException, InterruptedException {
        if (getWkhtml2pdfHomeOpt() == null) {
            return null;
        } else if (pdf == null || !pdf.exists()) {
            getLogger().error("splitPDF: PDF file not found");
            return null;
        }

        File tempFolder = pdf.getParentFile();
        File pdfFile    = new File(tempFolder, "splited.pdf");

        List<String> commandAndArgs = new ArrayList<>(0);
        commandAndArgs.add(getExecFile(null, "runner.exe"));
        commandAndArgs.add(getExecFile(null, "pdfsplit"));
        commandAndArgs.add("-p2x1a4");
        commandAndArgs.add(pdf.getAbsolutePath());
        commandAndArgs.add(pdfFile.getAbsolutePath());

        getLogger().info(commandAndArgs.toString());

        ProcessBuilder pb      = new ProcessBuilder(commandAndArgs);
        Process        process = pb.start();
        boolean success = runProcess(process);
        if (success && pdfFile.exists()) {
            return pdfFile;
        }
        return null;
    }

    private boolean runProcess(Process process) throws InterruptedException {
        StreamGobbler outReader = new StreamGobbler(process.getInputStream(), false);
        StreamGobbler errReader = new StreamGobbler(process.getErrorStream(), true);

        outReader.start();
        errReader.start();

        return process.waitFor() == 0;
    }

    /**
     * Converte o código HTML em um arquivo PDF com o cabeçalho e rodapé especificados.
     *
     * @param rawHtml       o código HTML.
     * @param rawHeader     o código HTML do cabeçalho.
     * @param rawFooter     o código HTML do rodapé.
     * @param additionalConfig configurações adicionais.
     * @return O arquivo PDF gerado.
     * @throws IOException          Caso ocorra um problema de IO.
     * @throws InterruptedException Caso ocorra um problema de sincronismo.
     */
    @Override
    public File convertHTML2PDF(String rawHtml, String rawHeader, String rawFooter, List<String> additionalConfig)
            throws IOException, InterruptedException {
        if (getWkhtml2pdfHomeOpt() == null) {
            return null;
        }

        final String html   = safeWrapHtml(rawHtml);
        final String header = safeWrapHtml(rawHeader);
        final String footer = safeWrapHtml(rawFooter);

        File tempLock   = File.createTempFile("SINGULAR-", UUID.randomUUID().toString());
        File tempFolder = new File(tempLock.getParentFile(), tempLock.getName().concat("-DIR"));
        if (!tempFolder.mkdir()) {
            getLogger().error("convertHTML2PDF: temp folder not found");
            return null;
        }

        File htmlFile = new File(tempFolder, "temp.html");
        File pdfFile  = new File(tempFolder, "temp.pdf");
        File jarFile  = new File(tempFolder, "temp.jar");

        TempFileUtils.deleteOrException(htmlFile, getClass());

        if (! htmlFile.createNewFile()) {
            return null;
        }
        writeToFile(htmlFile, html);

        List<String> commandAndArgs = new ArrayList<>(0);
        commandAndArgs.add(getExecFile("bin", "wkhtmltopdf.exe"));

        if (additionalConfig != null) {
            commandAndArgs.addAll(additionalConfig);
        } else {
            addDefaultPDFCommandArgs(commandAndArgs);
        }

        if (header != null) {
            File headerFile = new File(tempFolder, "header.html");
            writeToFile(headerFile, header);
            commandAndArgs.add("--header-html");
            commandAndArgs.add(headerFile.getAbsolutePath());
            addDefaultHeaderCommandArgs(commandAndArgs);
        }

        if (footer != null) {
            File footerFile = new File(tempFolder, "footer.html");
            writeToFile(footerFile, footer);
            commandAndArgs.add("--footer-html");
            commandAndArgs.add(footerFile.getAbsolutePath());
            addDefaultFooterCommandArgs(commandAndArgs);
        }

        commandAndArgs.add("--cookie-jar");
        commandAndArgs.add(jarFile.getAbsolutePath());
        commandAndArgs.add(htmlFile.getAbsolutePath());
        commandAndArgs.add(pdfFile.getAbsolutePath());

        ProcessBuilder pb = new ProcessBuilder(commandAndArgs);
        pb.environment().put("LD_LIBRARY_PATH", getWkhtml2pdfHome().getAbsolutePath());
        Process process = pb.start();

        boolean success = runProcess(process);
        if (success && pdfFile.exists()) {
            return pdfFile;
        }
        return null;
    }

    private void writeToFile(File destination, String content) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(destination);
             Writer fw = new OutputStreamWriter(fos, Charset.forName("UTF-8").newEncoder())){
            fw.write(content);
        }
    }

    /**
     * Converte o código HTML em um arquivo PNG.
     *
     * @param html             o código HTML.
     * @param additionalConfig configurações adicionais.
     * @return O arquivo PDF gerado.
     * @throws IOException          Caso ocorra um problema de IO.
     * @throws InterruptedException Caso ocorra um problema de sincronismo.
     */
    @Override
    public File convertHTML2PNG(String html, List<String> additionalConfig) throws IOException, InterruptedException {
        if (getWkhtml2pdfHomeOpt() == null) {
            return null;
        }

        File tempLock   = File.createTempFile("SINGULAR-", UUID.randomUUID().toString());
        File tempFolder = new File(tempLock.getParentFile(), tempLock.getName().concat("-DIR"));
        if (!tempFolder.mkdir()) {
            getLogger().error("convertHTML2PNG: temp folder not found");
            return null;
        }

        File htmlFile = new File(tempFolder, "temp.html");
        File pngFile  = new File(tempFolder, "temp.png");
        File jarFile  = new File(tempFolder, "temp.jar");

        TempFileUtils.deleteOrException(htmlFile, getClass());

        if (htmlFile.createNewFile()) {
            writeToFile(htmlFile, html);

            List<String> commandAndArgs = new ArrayList<>(0);
            commandAndArgs.add(getExecFile("bin","wkhtmltoimage.exe"));

            if (additionalConfig != null) {
                commandAndArgs.addAll(additionalConfig);
            } else {
                addDefaultPNGCommandArgs(commandAndArgs);
            }

            commandAndArgs.add("--cookie-jar");
            commandAndArgs.add(jarFile.getAbsolutePath());
            commandAndArgs.add(htmlFile.getAbsolutePath());
            commandAndArgs.add(pngFile.getAbsolutePath());

            ProcessBuilder pb = new ProcessBuilder(commandAndArgs);
            pb.environment().put("LD_LIBRARY_PATH", getWkhtml2pdfHome().getAbsolutePath());
            Process process = pb.start();

            boolean success = runProcess(process);
            if (success && pngFile.exists()) {
                return pngFile;
            }
        }

        return null;
    }

    /**
     * O tipo StreamGobbler.
     */
    private class StreamGobbler extends Thread {
        /**
         * O inputstream.
         */
        private InputStream is;
        /**
         * Indicador de erro.
         */
        private boolean     error;

        /**
         * Instancia um novo objeto do tipo StreamGobbler.
         *
         * @param is    o inputstream.
         * @param error indica quando se trata de uma "stream" de erro.
         */
        public StreamGobbler(InputStream is, boolean error) {
            this.is = is;
            this.error = error;
        }

        /**
         * Método Run.
         */
        @Override
        public void run() {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(is));
                String line = reader.readLine();
                while (line != null) {
                    if (error) {
                        getLogger().error(line);
                    } else {
                        getLogger().info(line);
                    }
                    line = reader.readLine();
                }
            } catch (IOException e) {
                getLogger().error(e.getMessage(), e);
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        getLogger().error(e.getMessage(), e);
                    }
                }
            }
        }
    }
}