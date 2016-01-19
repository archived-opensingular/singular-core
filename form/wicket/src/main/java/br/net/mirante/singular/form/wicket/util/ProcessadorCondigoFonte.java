package br.net.mirante.singular.form.wicket.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProcessadorCondigoFonte {

    private final String fonte;
    private final List<Integer> linhasParaDestacar;
    private final List<String> fonteFinal;
    private final static List<String> LIXOS = Collections.singletonList("@formatter");

    public ProcessadorCondigoFonte(String fonte) {
        this.fonte = fonte;
        this.linhasParaDestacar = new ArrayList<>();
        this.fonteFinal = new ArrayList<>();
        processar();
    }

    private void processar() {
        final String[] linhas = fonte.split("\n");

        for (int i = 0; i < linhas.length; i += 1) {
            if(isLixo(linhas[i])){
                continue;
            }
            if (isBloco(linhas[i])) {
                while (!isFimBloco(linhas[++i])) {
                    fonteFinal.add(linhas[i]);
                    linhasParaDestacar.add(fonteFinal.size());
                }
            } else if (isLinha(linhas[i])) {
                fonteFinal.add(linhas[++i]);
                linhasParaDestacar.add(fonteFinal.size());
            } else {
                fonteFinal.add(linhas[i]);
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

    public List<Integer> getLinhasParaDestacar() {
        return linhasParaDestacar;
    }
}
