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

package info.mirante.develox.view;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Parser para arquivos css.
 * <p>
 * Atualmente, apenas identifica os nomes de seletor/classe e os associa com a
 * String que a define, sem interpretar o conteudo.
 * <p>
 * (veja http://www.htmlhelp.com/reference/css/structure.html sobre a sintaxe
 * CSS)
 *
 * @author Bruno Pedroso
 */
public class SimpleCSS {

    private final List<EstiloCSS> listaEstilos_ = new ArrayList<>();

    private final Map<String, EstiloCSS> tabelaEstilos_ = new HashMap<>();

    private final Map<String, EstiloCSS> tabelaEstilosSemPonto_ = new HashMap<>();

    public SimpleCSS() {
    }

    public void addCSS(InputStream is) {
        try {

            SimpleCSSLex lex = new SimpleCSSLex(is);

            SimpleCSSToken nome = lex.getProximoToken();

            while (nome != null) {

                if (nome.getTipo() == SimpleCSSLex.TOKEN_NOME_ESTILO) {
                    SimpleCSSToken def = lex.getProximoToken();
                    if (def != null && def.getTipo() == SimpleCSSLex.TOKEN_DEF_ESTILO) {
                        EstiloCSS e = tabelaEstilos_.computeIfAbsent(nome.getTexto(), EstiloCSS::new);
                        e.addDef(def.getTexto());
                        if(!listaEstilos_.contains(e)){
                            listaEstilos_.add(e);
                        }
                        tabelaEstilosSemPonto_.putIfAbsent(e.getNomeSemPonto(), e);

                    } else {
                        throw new RuntimeException("Definicao de estilo esperada, mas encontrado: "+ def);
                    }
                } else {
                    throw new RuntimeException("Nome de estilo esperado, mas encontrado: " + nome);
                }

                // le o proximo
                nome = lex.getProximoToken();
            }
        } catch (IOException e) {
            throw new RuntimeException("Erro lendo css", e);
        }
    }

    public static SimpleCSS parse(InputStream in) throws IOException {
        SimpleCSS c = new SimpleCSS();
        c.addCSS(in);
        return c;
    }

    /**
     * Devolve a lista de estilos do css na ordem em que foram adicionados.
     *
     * @return semrpe diferente de null.
     */
    public EstiloCSS[] getEstilos() {
        return (EstiloCSS[]) listaEstilos_.toArray(new EstiloCSS[listaEstilos_.size()]);

    }

    /**
     * Retorna o estilo com o nome solicitado, o qual deve ser exatamente com
     * est� escrito no arquivo CSS.
     *
     * @param nome -
     * @return Null se n�o existir.
     */
    public EstiloCSS getEstilo(String nome) {
        return (EstiloCSS) tabelaEstilos_.get(nome);
    }

    /**
     * Retorna o estilo com o nome solicitado, a pesquisa desconsidera o ponto
     * na frente dos nomes do estilo no arquivo para efeito de localiza��o.
     *
     * @param nome -
     * @return Null se n�o existir.
     */
    public EstiloCSS getEstiloSemPonto(String nome) {
        return (EstiloCSS) tabelaEstilosSemPonto_.get(nome);
    }

    public void gerarArquivo(OutputStream out) {
        gerarArquivo(new PrintWriter(out));
    }

    public void gerarArquivo(Writer wt) {
        PrintWriter out;
        if (wt instanceof PrintWriter) {
            out = (PrintWriter) wt;
        } else {
            out = new PrintWriter(wt);
        }
        EstiloCSS[] estilos = getEstilos();
        for (int i = 0; i < estilos.length; i++) {
            EstiloCSS e = estilos[i];
            out.println(e.toString());
        }
        out.flush();
    }

    /**
     * para testes
     *
     * @param args
     */
    public static void main(String[] args) {
        try {
            SimpleCSS css = SimpleCSS.parse(new FileInputStream("teste.css"));
            css.gerarArquivo(System.out);
        } catch (Exception e) {
            Logger.getLogger(SimpleCSS.class.getName()).log(Level.WARNING, e.getMessage(), e);
        }
    }
}