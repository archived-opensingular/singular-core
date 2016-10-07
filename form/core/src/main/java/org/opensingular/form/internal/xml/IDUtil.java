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

package org.opensingular.form.internal.xml;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Auxilia o processo de montagem e desmontagem de idenficadores de objetos
 * a partir de chaves múltiplas (ex.: criar id string para objeto com chave
 * coposta de dois ints). É especialmente voltada para gerar ID String para
 * objetos de banco de dados.<p/>
 * <p>
 * É interessante usar essa funcionalidade para abstrair camadas superiores
 * acerca do formato da chave de persistência.<p>
 * <p>
 * Exemplo de Uso:<br/>
 * <pre>
 *     // para gerar
 *     String id = IDBuffer.newID().add(cod).add(dt).toString();
 *
 *     //para ler
 *     String codigo = IDBuffer.getString(id, 0);
 *     Date data     = IDBuffer.getDate(id, 1);
 * </pre>
 *
 * @author Daniel C. Bordin
 */
public final class IDUtil {

    /**
     * caracter para delimitar os componentes do ID
     */
    private static final char SEPARADOR = '-';

    /**
     * Classe para auxilio na geração do ID, a medida que os componentes são
     * adicionados, coloca os separadores adequados.
     *
     * @author Daniel C. Bordin
     */
    public static final class IDBuffer {
        /**
         * Espaço temporário para gerar o ID.
         */
        private final StringBuffer buffer_ = new StringBuffer(32);

        /**
         * Permite a construção apenas a partir da IDUtil.
         */
        IDBuffer() {
        }

        /**
         * Adiciona um componente do tipo String ao ID.
         *
         * @param cmp Valor a ser adicionado
         * @return o proprio IDBuffer
         */
        public IDBuffer add(String cmp) {
            if (cmp == null) {
                throw new IllegalArgumentException("componente null");
            }
            if (cmp.length() == 0) {
                throw new IllegalArgumentException("componente tamanho zero");
            }
            if (cmp.indexOf(SEPARADOR) != -1) {
                throw new IllegalArgumentException(
                        "componente não pode conter com caracter separador '" + SEPARADOR + '\'');
            }
            if (buffer_.length() != 0) {
                buffer_.append(SEPARADOR);
            }
            buffer_.append(cmp);
            return this;
        }

        /**
         * Adiciona um componente do tipo int ao ID.
         *
         * @param cmp Valor a ser adicionado
         * @return o proprio IDBuffer
         */
        public IDBuffer add(int cmp) {
            if (cmp < 0) {
                throw new IllegalArgumentException("O id não pode ser negativo");
            }
            if (buffer_.length() != 0) {
                buffer_.append(SEPARADOR);
            }
            buffer_.append(cmp);
            return this;
        }

        /**
         * Adiciona um componente do tipo long ao ID.
         *
         * @param cmp Valor a ser adicionado
         * @return o proprio IDBuffer
         */
        public IDBuffer add(long cmp) {
            if (cmp < 0) {
                throw new IllegalArgumentException("O id não pode ser negativo");
            }
            if (buffer_.length() != 0) {
                buffer_.append(SEPARADOR);
            }
            buffer_.append(cmp);
            return this;
        }

        /**
         * Adiciona um componente do tipo java.util.Date ao ID.
         *
         * @param cmp Valor a ser adicionado
         * @return o proprio IDBuffer
         */
        public IDBuffer add(java.util.Date cmp) {
            if (cmp == null) {
                throw new IllegalArgumentException("componente null");
            }
            add(ConversorDataISO8601.format(cmp));
            return this;
        }

        /**
         * Adiciona um componente do tipo java.sql.Date ao ID.
         *
         * @param cmp Valor a ser adicionado
         * @return o proprio IDBuffer
         */
        public IDBuffer add(java.sql.Date cmp) {
            if (cmp == null) {
                throw new IllegalArgumentException("componente null");
            }
            add(ConversorDataISO8601.format(cmp));
            return this;
        }

        /**
         * Adiciona um componente do tipo Timestamp ao ID.
         *
         * @param cmp Valor a ser adicionado
         * @return o proprio IDBuffer
         */
        public IDBuffer add(Timestamp cmp) {
            if (cmp == null) {
                throw new IllegalArgumentException("componente null");
            }
            add(ConversorDataISO8601.format(cmp));
            return this;
        }

        /**
         * Adiciona um componente do tipo Calendar ao ID.
         *
         * @param cmp Valor a ser adicionado
         * @return o proprio IDBuffer
         */
        public IDBuffer add(Calendar cmp) {
            if (cmp == null) {
                throw new IllegalArgumentException("componente null");
            }
            add(ConversorDataISO8601.format(cmp));
            return this;
        }

        /**
         * Gera o identificador segundo o seu estado atual.
         *
         * @return nunca diferente de null. Se vazio dispara exception.
         */
        public String toString() {
            if (buffer_.length() == 0) {
                throw new IllegalStateException("ID está vazio");
            }
            return buffer_.toString();
        }
    }

    /**
     * Cria um novo gerador de ID vazio. Por exemplo:<p/>
     * <pre>    String id = IDBuffer.newID().add(cod).add(data).toString();</pre>
     *
     * @return Sempre diferente de null.
     */
    public static IDBuffer newID() {
        return new IDBuffer();
    }

    //------------------------------------------------------------
    //  Métodos para leitura dos IDs
    //------------------------------------------------------------

    /**
     * Obtem um componente do ID do tipo String no índice solicitado.
     *
     * @param id onde será buscado o componente.
     * @param index índice do componente desejado (o primeiro é 0).
     * @return String não vazia desejada (exception se não existir).
     */
    public static String getString(String id, int index) {
        int posi = getPosInicio(id, index);
        int posf = getPosFim(id, posi);
        if (posf == -1) {
            return id.substring(posi);
        }
        return id.substring(posi, posf);
    }

    /**
     * Obtem um componente do ID do tipo int no índice solicitado.
     *
     * @param id onde será buscado o componente.
     * @param index índice do componente desejado (o primeiro é 0).
     * @return String não vazia desejada (exception se não existir ou inválido).
     */
    public static int getInt(String id, int index) {
        int posi = getPosInicio(id, index);
        int posf = getPosFim(id, posi);
        //É feito manualmente para evitar gera um string desnecessariamente
        int valor = 0;
        char digito;
        for (int i = posi; i < posf; i++) {
            digito = id.charAt(i);
            if ((digito < '0') || (digito > '9')) {
                throw new NumberFormatException(
                        "a posição " + index + " de '" + id + "' não é numero");
            }
            valor = valor * 10 + (digito - '0');
        }
        return valor;
    }

    /**
     * Obtem um componente do ID do tipo int no índice solicitado.
     *
     * @param id onde será buscado o componente.
     * @param index índice do componente desejado (o primeiro é 0).
     * @return String não vazia desejada (exception se não existir ou inválido).
     */
    public static long getLong(String id, int index) {
        int posi = getPosInicio(id, index);
        int posf = getPosFim(id, posi);
        //É feito manualmente para evitar gera um string desnecessariamente
        long valor = 0;
        char digito;
        for (int i = posi; i < posf; i++) {
            digito = id.charAt(i);
            if ((digito < '0') || (digito > '9')) {
                throw new NumberFormatException(
                        "a posição " + index + " de '" + id + "' não é numero");
            }
            valor = valor * 10 + (digito - '0');
        }
        return valor;
    }

    /**
     * Obtem um componente do ID do tipo java.util.Date no índice solicitado.
     *
     * @param id onde será buscado o componente.
     * @param index índice do componente desejado (o primeiro é 0).
     * @return Date não null (exception se não existir).
     */
    public static java.util.Date getDate(String id, int index) {
        return ConversorDataISO8601.getDate(getString(id, index));
    }

    /**
     * Obtem um componente do ID do tipo java.sql.Date no índice solicitado.
     *
     * @param id onde será buscado o componente.
     * @param index índice do componente desejado (o primeiro é 0).
     * @return Date não null (exception se não existir).
     */
    public static java.sql.Date getDateSQL(String id, int index) {
        return ConversorDataISO8601.getDateSQL(getString(id, index));
    }

    /**
     * Obtem um componente do ID do tipo Timestamp no índice solicitado.
     *
     * @param id onde será buscado o componente.
     * @param index índice do componente desejado (o primeiro é 0).
     * @return Timestamp não null (exception se não existir).
     */
    public static Timestamp getTimestamp(String id, int index) {
        return ConversorDataISO8601.getTimestamp(getString(id, index));
    }

    /**
     * Obtem um componente do ID do tipo Calendar no índice solicitado.
     *
     * @param id onde será buscado o componente.
     * @param index índice do componente desejado (o primeiro é 0).
     * @return Calendar não null (exception se não existir).
     */
    public static GregorianCalendar getCalendar(String id, int index) {
        return ConversorDataISO8601.getCalendar(getString(id, index));
    }

    /**
     * Localiza a posição onde começa no ID o componente de índice x.
     *
     * @param id String onde será localizado o componente do ID
     * @param index índice do componente desejado (o primeiro é zero).
     * @return a posição na string do início do componente ou dispara Exception.
     */
    private static int getPosInicio(String id, int index) {
        if (id == null) {
            throw new IllegalArgumentException("ID null");
        }
        if (id.charAt(0) == SEPARADOR) {
            throw new IllegalArgumentException("O ID não pode comerçar com '" + SEPARADOR + "'");
        }
        if (index < 0) {
            throw new IndexOutOfBoundsException("Não existe posição " + index + " do ID");
        }
        int posinicial = 0;
        for (int i = 0; i < index; i++) {
            posinicial = id.indexOf(SEPARADOR, posinicial + 1);
            if (posinicial == -1) {
                throw new IndexOutOfBoundsException("Não existe posição " + index + " do ID");
            }
        }
        posinicial++;
        if (posinicial == id.length()) {
            throw new IllegalArgumentException("O ID não pode terminar com '" + SEPARADOR + "'");
        }
        return posinicial;
    }

    /**
     * Localiza a posição no ID onde termina componente de índice x somando de 1.
     *
     * @param id String onde será localizado o fim do componente
     * @param posi posição no ID onde começa o componente a ser buscado o fim
     * @return (posição do fim do componente + 1) ou (-1) se vai até o final.
     */
    private static int getPosFim(String id, int posi) {
        //Não faz nenhuma verificação, pois o getPosInicio já fez
        if (posi + 1 == id.length()) {
            return -1;
        }
        return id.indexOf(SEPARADOR, posi + 1);
    }

}
