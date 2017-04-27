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

import java.io.IOException;
import java.io.InputStream;

/**
 * Reconhece tokens de uma InputStream.
 * <p>
 * Atualmente s� s�o reconhidos os tokens NOME_ESTILO e DEF_ESTILO.
 * Ignora coment�rios de umaLinha e multiLinha
 *
 * @author Bruno Pedroso
 */
final class SimpleCSSLex {

    private InputStream is_;

    public static final int TOKEN_NOME_ESTILO = 1;
    public static final int TOKEN_DEF_ESTILO = 2;

    public SimpleCSSLex(InputStream is) {
        is_ = is;
    }

    /**
     * @return null quando encontrado EOF
     *
     * @throws IOException
     */
    public SimpleCSSToken getProximoToken() throws IOException {

        StringBuffer buf = new StringBuffer();

        int proximo = pulaEspacosEEnters(is_);
        if (proximo != -1) {

            // se encontrar '/', tem que ser comentario
            while (proximo == '/') {
                proximo = is_.read();

                if (proximo == '/') {
                    skipComentarioUmaLinha();
                } else if (proximo == '*') {
                    skipComentarioMultiLinha();
                } else {
                    throw new RuntimeException("token \"/\" inesperado.");
                }

                proximo = pulaEspacosEEnters(is_);
            }

            // se encontrar '{', tem que ser definicao de estilo
            if (proximo == '{') {
                return reconhecerDefEstilo(buf);
            }

            buf.append((char) proximo);

            // se n�o come�ar nem com
            return reconhecerNomeEstilo(buf);

        } else {
            return null; // EOF
        }

    }

    /**
     * Dispara RuntimeException se n�o conseguir reconhecer
     *
     * @return
     */
    private void skipComentarioUmaLinha() throws IOException {
        int car = -1;
        do {
            car = is_.read();
            // le at� o proximo enter ou at� o EOF
        } while (car != -1 && car != '\n');

    }

    /**
     * Dispara RuntimeException se n�o conseguir reconhecer
     *
     * @return
     */
    private void skipComentarioMultiLinha() throws IOException {
        while (true) {
            int car = is_.read();

            if (car == -1) {
                throw new RuntimeException("EOF encontrado antes do fim do comentario");
            }

            if (car == '*') {
                car = is_.read();
                if (car == '/') {
                    return;
                }
            }

        }
    }

    /**
     * Dispara RuntimeException se n�o conseguir reconhecer
     *
     * @return
     */
    private SimpleCSSToken reconhecerNomeEstilo(StringBuffer jaLido) throws IOException {

        while (true) {
            // le tudo at� encontrar um espa�o ou EOF

            int car = is_.read();
            if (car == -1 || car == ' ') {
                return new SimpleCSSToken(TOKEN_NOME_ESTILO, jaLido.toString());

            } else if (car == '/') { // possivel comentario colado no nome

                car = is_.read();
                if (car == '/') {
                    skipComentarioUmaLinha();

                } else if (car == '*') {
                    skipComentarioMultiLinha();

                } else {
                    throw new RuntimeException("token \"/\" inesperado.");

                }

                return new SimpleCSSToken(TOKEN_NOME_ESTILO, jaLido.toString());

            } else {
                jaLido.append((char) car);

            }

        }

    }

    /**
     * Dispara RuntimeException se n�o conseguir reconhecer
     *
     * @return
     */
    private SimpleCSSToken reconhecerDefEstilo(StringBuffer jaLido) throws IOException {
        while (true) {
            // le tudo at� encontrar um '}'

            int car = is_.read();
            if (car == '}') {
                //jaLido.append((char)car);
                return new SimpleCSSToken(TOKEN_DEF_ESTILO, jaLido.toString());

            } else if (car == '/') { // possivel comentario no meio da definicao

                car = is_.read();
                if (car == '/') {
                    skipComentarioUmaLinha();

                } else if (car == '*') {
                    skipComentarioMultiLinha();

                } else {
                    throw new RuntimeException("token \"/\" inesperado.");

                }

            } else {
                jaLido.append((char) car);

            }

        }
    }

    /**
     * pula espacos, enters e tabs. retorna o proximo caractere diferente desses.
     *
     * @param is
     * @return
     *
     * @throws IOException
     */
    private static int pulaEspacosEEnters(InputStream is) throws IOException {
        int car = is.read();
        while (car == ' ' || car == '\t' || car == '\n' || car == '\r') {
            car = is.read();
        }
        return car;
    }

    /**
     * para testes
     *
     * @param args
     */
    public static void main(String[] args) {

//        try {
//            FileInputStream fis = new FileInputStream("teste.css");
//            SimpleCSSLex lex = new SimpleCSSLex(fis);
//            
//            SimpleCSSToken tok = lex.getProximoToken();
//            
//            while (tok != null) {
//                System.out.println(tok);
//                tok = lex.getProximoToken();
//            }
//            
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

    }
}
