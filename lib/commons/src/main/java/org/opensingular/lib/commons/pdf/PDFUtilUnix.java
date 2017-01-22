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
import java.util.List;

/**
 * Classe utilitária para a manipulação de PDF's em sistemas Linux/Unix.
 * Essa versão sobre escreve as chamadas nativas da classe
 * {@link PDFUtil} com as equivalentes para o sistema Linux/Unix.
 */
final class PDFUtilUnix extends PDFUtil {

    @Override
    protected @Nonnull String fixPathArg(@Nonnull File arq) {
        return "file://".concat(arq.getAbsolutePath());
    }

    @Override
    protected void writeToFile(File destination, String content) throws SingularPDFException {
        try(FileWriter fw = new FileWriter(destination)) {
            fw.write(content);
        } catch (IOException e) {
            throw new SingularPDFException("Erro escrevendo conteúdo para o arquivo " + destination.getAbsolutePath());
        }
    }

    @Override
    protected
    @Nonnull
    File runProcess(@Nonnull List<String> commandAndArgs, @Nonnull File expectedFile) throws SingularPDFException {
        getLogger().info(commandAndArgs.toString());

        try {
            ProcessBuilder pb = new ProcessBuilder(commandAndArgs);
            pb.environment().put("LD_LIBRARY_PATH", getWkhtml2pdfHome().getAbsolutePath());
            Process process = pb.start();
            process.waitFor();

            return generateFile(process, expectedFile);
        } catch (IOException | InterruptedException e) {
            throw new SingularPDFException(e);
        }

    }
    /**
     * Gera o arquivo através do processo especificado.
     *
     * @param process o processo especificado.
     * @param expectedFile    o arquivo a ser criado.
     * @return o arquivo criado.
     */
    private File generateFile(Process process, File expectedFile) throws SingularPDFException {

        boolean done    = false;
        boolean success = true;
        try(BufferedReader outReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader errReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
            String line = outReader.readLine();
            while (line != null) {
                getLogger().info(line);
                line = outReader.readLine();
            }
            line = errReader.readLine();
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
        } catch (IOException e) {
            throw new SingularPDFException(e);
        }
        if (done && success && expectedFile.exists()) {
            return expectedFile;
        }

        throw new SingularPDFException(
                String.format("O arquivo não foi gerado:  done:%b success:%b file.exists():%b", done, success,
                        expectedFile.exists()));
    }
}