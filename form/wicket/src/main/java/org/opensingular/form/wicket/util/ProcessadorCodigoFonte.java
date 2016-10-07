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

package org.opensingular.form.wicket.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProcessadorCodigoFonte {

    private final String fonte;
    private final List<Integer> linhasParaDestacar;
    private final List<String> fonteFinal;
    private final List<String> javadocDeClasse;
    private final static List<String> LIXOS = Collections.singletonList("@formatter");

    public ProcessadorCodigoFonte(String fonte) {
        this.fonte = fonte;
        this.linhasParaDestacar = new ArrayList<>();
        this.fonteFinal = new ArrayList<>();
        this.javadocDeClasse = new ArrayList<>();
        processar();
    }

    private void processar() {
        boolean javadoc = false;
        boolean classeIniciada = false;
        final String[] linhas = fonte.split("\n");

        for (int i = 0; i < linhas.length; i += 1) {
            final String linha = linhas[i];

            if (!classeIniciada) {
                if (javadoc && !linha.contains("*/")) {
                    javadocDeClasse.add(linha.replace(" *", ""));
                }
                if (linha.startsWith("/**")) {
                    javadoc = true;
                }

                if (linha.contains("public class ")) {
                    classeIniciada = true;
                }
            }

            if (javadoc && linha.contains("*/")) {
                javadoc = false;
                continue;
            }

            if(isLixo(linha) || javadoc){
                continue;
            }
            if (isBloco(linha)) {
                while (!isFimBloco(linhas[++i])) {
                    fonteFinal.add(linha);
                    linhasParaDestacar.add(fonteFinal.size());
                }
            } else if (isLinha(linha)) {
                fonteFinal.add(linhas[++i]);
                linhasParaDestacar.add(fonteFinal.size());
            } else {
                fonteFinal.add(linha);
            }
        }
    }

    private boolean isBloco(String candidato) {
        return candidato.contains("//@destacar:bloco") || candidato.contains("// @destacar:bloco");
    }

    private boolean isFimBloco(String candidato) {
        return candidato.contains("//@destacar:fim") || candidato.contains("// @destacar:fim");
    }

    private boolean isLinha(String candidato) {
        return candidato.contains("//@destacar") || candidato.contains("// @destacar");
    }

    private boolean isLixo(String canditato){
        for(String lixo : LIXOS){
            if(canditato.contains(lixo)){
                return true;
            }
        }
        return false;
    }

    public String getFonteProcessado() {
        StringBuilder sb = new StringBuilder();
        fonteFinal.forEach(s -> sb.append(s).append("\n"));
        return sb.toString();
    }

    public String getJavadoc() {
        StringBuilder sb = new StringBuilder();
        javadocDeClasse.forEach(s -> sb.append(s).append("\n"));
        return sb.toString();
    }

    public List<Integer> getLinhasParaDestacar() {
        return linhasParaDestacar;
    }
}
