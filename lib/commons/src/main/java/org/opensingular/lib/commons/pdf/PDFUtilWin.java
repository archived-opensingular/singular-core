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

import javax.annotation.Nonnull;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Classe utilitária para a manipulação de PDF's no Windows.
 * Essa versão sobre escreve as chamadas nativas da classe
 * {@link PDFUtil} com
 * as equivalentes para o sistema Windows.
 */
final class PDFUtilWin extends PDFUtil {

    @Override
    protected String fixExecutableName(String executable) {
        return executable + ".exe";
    }

    @Override
    protected void writeToFile(File destination, String content) throws SingularPDFException {
        try (FileOutputStream fos = new FileOutputStream(destination);
             Writer fw = new OutputStreamWriter(fos, StandardCharsets.UTF_8.newEncoder())){
            fw.write(content);
        } catch(Exception e) {
            throw new SingularPDFException("Erro escrevendo conteúdo no arquivo" + destination.getAbsolutePath(), e);
        }
    }

    @Override
    protected
    @Nonnull
    File runProcess(@Nonnull List<String> commandAndArgs, @Nonnull File expectedFile) throws SingularPDFException {
        getLogger().info(commandAndArgs.toString());

        ProcessBuilder pb = new ProcessBuilder(commandAndArgs);
        pb.environment().put("LD_LIBRARY_PATH", getWkhtml2pdfHome().getAbsolutePath());
        try {
            Process process = pb.start();

            StreamGobbler outReader = new StreamGobbler(process.getInputStream(), false);
            StreamGobbler errReader = new StreamGobbler(process.getErrorStream(), true);

            outReader.start();
            errReader.start();

            boolean success = (process.waitFor() == 0);

            if (success && expectedFile.exists()) {
                return expectedFile;
            }
            throw new SingularPDFException("Arquivo não foi gerado " + expectedFile.getAbsolutePath());
        } catch (IOException | InterruptedException e) {
            throw new SingularPDFException(e);
        }
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