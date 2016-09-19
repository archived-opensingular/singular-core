package br.net.mirante.singular.commons.pdf;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Classe utilitária para a manipulação de PDF's em sistemas Linux/Unix.
 * Essa versão sobre escreve as chamadas nativas da classe
 * {@link PDFUtil} com as equivalentes para o sistema Linux/Unix.
 */
public class PDFUtilUnix extends PDFUtil {

    /**
     * Instancia um novo objeto do tipo PDFUtilUnix.
     */
    private PDFUtilUnix() {
        /* MÉTODO VAZIO */
    }

    /**
     * Retorna o valor atual do atributo {@link #instance}.
     *
     * @return O valor atual do atributo.
     */
    public static PDFUtil getInstance() {
        if (instance == null) {
            instance = new PDFUtilUnix();
        }

        return instance;
    }

    /**
     * Retorna um arquivo pdf vazio.
     *
     * @return O pdf criado.
     * @throws IOException
     */
    public File createEmptyPdf() throws IOException {
        File tempLock   = File.createTempFile("SINGULAR-", UUID.randomUUID().toString());
        File tempFolder = new File(tempLock.getParentFile(), tempLock.getName().concat("-DIR"));
        if (!tempFolder.mkdir()) {
            getLogger().error("convertHTML2PDF: temp folder not found");
            return null;
        }
        File pdfFile = new File(tempFolder, "temp.pdf");
        return pdfFile;
    }

    /**
     * Dividi as páginas de um PDF ao meio, gerando duas páginas para cada.
     *
     * @param pdf o PDF.
     * @return O arquivo PDF gerado.
     * @throws IOException          Caso ocorra um problema de IO.
     * @throws InterruptedException Caso ocorra um problema de sincronismo.
     */
    @Override
    public File splitPDF(File pdf) throws IOException, InterruptedException {
        if (wkhtml2pdfHome == null) {
            getLogger().error("splitPDF: 'singular.wkhtml2pdf.home' not set");
            return null;
        }

        if (pdf == null || !pdf.exists()) {
            getLogger().error("splitPDF: PDF file not found");
            return null;
        }

        File tempFolder = pdf.getParentFile();
        File libFolder  = new File(wkhtml2pdfHome);
        File exeFile    = new File(libFolder, "runner");
        File pysFile    = new File(libFolder, "pdfsplit");
        File pdfFile    = new File(tempFolder, "splited.pdf");

        List<String> commandAndArgs = new ArrayList<>(0);
        commandAndArgs.add(exeFile.getAbsolutePath());
        commandAndArgs.add(pysFile.getAbsolutePath());
        commandAndArgs.add("-p2x1a4");
        commandAndArgs.add(pdf.getAbsolutePath());
        commandAndArgs.add(pdfFile.getAbsolutePath());

        getLogger().info(commandAndArgs.toString());
        ProcessBuilder pb      = new ProcessBuilder(commandAndArgs);
        Process        process = pb.start();
        process.waitFor();

        BufferedReader outReader = null;
        BufferedReader errReader = null;
        try {
            outReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            errReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String  line    = outReader.readLine();
            boolean success = true;
            while (line != null) {
                if (!line.isEmpty()) {
                    success = false;
                }
                line = outReader.readLine();
            }

            line = errReader.readLine();
            while (line != null) {
                if (!line.isEmpty()) {
                    success = false;
                    getLogger().error(line);
                }
                line = errReader.readLine();
            }

            if (success && pdfFile.exists()) {
                return pdfFile;
            }
        } finally {
            if (outReader != null) {
                outReader.close();
            }

            if (errReader != null) {
                errReader.close();
            }
        }

        return null;
    }

    /**
     * Converte o código HTML em um arquivo PDF com o cabeçalho e rodapé especificados.
     *
     * @param unsafeHtml             o código HTML.
     * @param header           o código HTML do cabeçalho.
     * @param footer           o código HTML do rodapé.
     * @param additionalConfig configurações adicionais.
     * @return O arquivo PDF gerado.
     * @throws IOException          Caso ocorra um problema de IO.
     * @throws InterruptedException Caso ocorra um problema de sincronismo.
     */
    @Override
    public File convertHTML2PDF(String rawHtml, String rawHeader, String rawFooter, List<String> additionalConfig)
            throws IOException, InterruptedException {
        final String html   = safeWrapHtml(rawHtml);
        final String header = safeWrapHtml(rawHeader);
        final String footer = safeWrapHtml(rawFooter);
        if (wkhtml2pdfHome == null) {
            getLogger().error("convertHTML2PDF: 'singular.wkhtml2pdf.home' not set");
            return null;
        }

        File tempLock   = File.createTempFile("SINGULAR-", UUID.randomUUID().toString());
        File tempFolder = new File(tempLock.getParentFile(), tempLock.getName().concat("-DIR"));
        if (!tempFolder.mkdir()) {
            getLogger().error("convertHTML2PDF: temp folder not found");
            return null;
        }

        File libFolder = new File(wkhtml2pdfHome);
        File exeFile   = new File(libFolder, "bin" + File.separator + "wkhtmltopdf");

        File htmlFile = new File(tempFolder, "temp.html");
        File pdfFile  = new File(tempFolder, "temp.pdf");
        File jarFile  = new File(tempFolder, "temp.jar");

        if (htmlFile.exists() && !htmlFile.delete()) {
            getLogger().error("convertHTML2PDF: HTML file not found");
            return null;
        }

        if (htmlFile.createNewFile()) {
            FileWriter fw = null;
            try {
                fw = new FileWriter(htmlFile);
                fw.write(html);
            } finally {
                if (fw != null) {
                    fw.close();
                    fw = null;
                }
            }

            List<String> commandAndArgs = new ArrayList<>(0);
            commandAndArgs.add(exeFile.getAbsolutePath());

            if (additionalConfig != null) {
                commandAndArgs.addAll(additionalConfig);
            } else {
                addDefaultPDFCommandArgs(commandAndArgs);
            }

            if (header != null) {
                try {
                    File headerFile = new File(tempFolder, "header.html");
                    fw = new FileWriter(headerFile);
                    fw.write(header);
                    commandAndArgs.add("--header-html");
                    commandAndArgs.add("file://".concat(headerFile.getAbsolutePath()));
                    addDefaultHeaderCommandArgs(commandAndArgs);
                } finally {
                    if (fw != null) {
                        fw.close();
                        fw = null;
                    }
                }
            }

            if (footer != null) {
                try {
                    File footerFile = new File(tempFolder, "footer.html");
                    fw = new FileWriter(footerFile);
                    fw.write(footer);
                    commandAndArgs.add("--footer-html");
                    commandAndArgs.add("file://".concat(footerFile.getAbsolutePath()));
                    addDefaultFooterCommandArgs(commandAndArgs);
                } finally {
                    if (fw != null) {
                        fw.close();
                    }
                }
            }

            commandAndArgs.add("--cookie-jar");
            commandAndArgs.add(jarFile.getAbsolutePath());
            commandAndArgs.add("file://".concat(htmlFile.getAbsolutePath()));
            commandAndArgs.add(pdfFile.getAbsolutePath());

            ProcessBuilder pb = new ProcessBuilder(commandAndArgs);
            pb.environment().put("LD_LIBRARY_PATH", libFolder.getAbsolutePath());
            Process process = pb.start();
            process.waitFor();
            return generateFile(process, pdfFile);
        }

        return null;
    }

    /**
     * Converte o código HTML em um arquivo PNG.
     *
     * @param html             o código HTML.
     * @param additionalConfig configurações adicionais.
     * @return O arquivo PNG gerado.
     * @throws IOException          Caso ocorra um problema de IO.
     * @throws InterruptedException Caso ocorra um problema de sincronismo.
     */
    @Override
    public File convertHTML2PNG(String html, List<String> additionalConfig) throws IOException, InterruptedException {
        if (wkhtml2pdfHome == null) {
            getLogger().error("convertHTML2PNG: 'singular.wkhtml2pdf.home' not set");
            return null;
        }

        File tempLock   = File.createTempFile("SINGULAR-", UUID.randomUUID().toString());
        File tempFolder = new File(tempLock.getParentFile(), tempLock.getName().concat("-DIR"));
        if (!tempFolder.mkdir()) {
            getLogger().error("convertHTML2PNG: temp folder not found");
            return null;
        }

        File libFolder = new File(wkhtml2pdfHome);
        File exeFile   = new File(libFolder, "bin" + File.separator + "wkhtmltoimage");

        File htmlFile = new File(tempFolder, "temp.html");
        File pngFile  = new File(tempFolder, "temp.png");
        File jarFile  = new File(tempFolder, "temp.jar");

        if (htmlFile.exists() && !htmlFile.delete()) {
            getLogger().error("convertHTML2PNG: HTML file not found");
            return null;
        }

        if (htmlFile.createNewFile()) {
            FileWriter fw = null;
            try {
                fw = new FileWriter(htmlFile);
                fw.write(html);
            } finally {
                if (fw != null) {
                    fw.close();
                }
            }

            List<String> commandAndArgs = new ArrayList<>(0);
            commandAndArgs.add(exeFile.getAbsolutePath());

            if (additionalConfig != null) {
                commandAndArgs.addAll(additionalConfig);
            } else {
                addDefaultPNGCommandArgs(commandAndArgs);
            }

            commandAndArgs.add("--cookie-jar");
            commandAndArgs.add(jarFile.getAbsolutePath());
            commandAndArgs.add("file://".concat(htmlFile.getAbsolutePath()));
            commandAndArgs.add(pngFile.getAbsolutePath());

            ProcessBuilder pb = new ProcessBuilder(commandAndArgs);
            pb.environment().put("LD_LIBRARY_PATH", libFolder.getAbsolutePath());
            Process process = pb.start();
            process.waitFor();
            return generateFile(process, pngFile);
        }

        return null;
    }

    /**
     * Gera o arquivo através do processo especificado.
     *
     * @param process o processo especificado.
     * @param file    o arquivo a ser criado.
     * @return o arquivo criado.
     * @throws IOException
     */
    private File generateFile(Process process, File file) throws IOException {
        BufferedReader outReader = null;
        BufferedReader errReader = null;

        try {
            outReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            errReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String line = outReader.readLine();
            while (line != null) {
                getLogger().info(line);
                line = outReader.readLine();
            }
            line = errReader.readLine();
            boolean done    = false;
            boolean success = true;
            while (line != null) {
                if (line.startsWith("Done")) {
                    done = true;
                } else if (line.startsWith("Warning:")) {
                    getLogger().warn(line);
                } else if (line.startsWith("Error:")) {
                    success = false;
                    getLogger().error(line);
                } else {
                    getLogger().info(line);
                }
                line = errReader.readLine();
            }

            if (done && success && file.exists()) {
                return file;
            } else {
                getLogger().error(String.format("done:%b success:%b file.exists():%b", done, success, file.exists()));
            }
        } finally {
            if (outReader != null) {
                outReader.close();
            }

            if (errReader != null) {
                errReader.close();
            }
        }

        return null;
    }
}