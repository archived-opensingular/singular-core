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

package org.opensingular.form.internal;

import org.opensingular.form.SFormUtil;
import org.opensingular.form.SInstance;
import org.opensingular.form.SScope;
import org.opensingular.form.SingularFormException;

/**
 * Faz o parse de string de paths e aponta para a posição atual dentro path, permitindo a leitura do caminho de forma
 * incremental mediante o uso do método {@link #next()}.
 * <p>
 * Exemplos de path válidos tratados: "endereco.rua", "pedido.itens[3].qtd", "[4]", "partes[1][3]".
 * <p>
 * ATENÇÃO: ESSA CLASSE INTERNA É APENAS PARA USO PELO SINGULAR. EM FUTURAS VERSÕES, PODE SER REMOVIDA OU ALTERADA
 * SEM MANTER RETRO COMPATIBILIDADE.
 *
 * @author Daniel C. Bordin
 */
public final class PathReader {

    private final String path;
    private final String token;
    private final int end;
    private final boolean aListIndex;

    /** Cria um novo leitor de path para string informada. */
    public PathReader(String path) {
        this(path, 0);
    }

    /** Cria um leitor de path para o resto do caminho a partir da posição informada. */
    private PathReader(String path, int inicio) {
        int inicioCopy = inicio;
        if (path == null) {
            throw new SingularFormException("O path do campo não pode ser nulo.");
        }
        this.path = path;
        if (inicioCopy >= path.length()) {
            aListIndex = false;
            end = inicioCopy;
            token = null;
        } else if(path.charAt(inicioCopy) == '[') {
            aListIndex = true;
            end = findListIndexTokenEndOrException(path, inicioCopy);
            token = path.substring(inicioCopy + 1, end - 1);
        } else {
            aListIndex = false;
            if (path.charAt(inicioCopy) == '.') {
                if (inicioCopy == 0) {
                    throw newInvalidPathInPosition(path, inicioCopy, null);
                } else {
                    inicioCopy++;
                }
            } else if (inicioCopy != 0) {
                throw newInvalidPathInPosition(path, inicioCopy, null);
            }

            end = findTokenEndOrException(path, inicioCopy);
            token = path.substring(inicioCopy, end);
            if (SFormUtil.isNotValidSimpleName(token)) {
                throw newInvalidPathInPosition(path, inicioCopy, "Não é um nome de campo válido");
            }
        }
    }

    private int findListIndexTokenEndOrException(String path, int inicio) {
        int posEnd = path.indexOf(']', inicio + 1) + 1;
        if (posEnd == 0 || inicio + 2 == posEnd) {
            throw newInvalidPathInPosition(path, inicio, null);
        }
        for (int i = inicio + 1; i < posEnd - 1; i++) {
            if (!Character.isDigit(path.charAt(i))) {
                throw newInvalidPathInPosition(path, i, "caracter inválido");
            }
        }
        return posEnd;
    }

    private SingularFormException newInvalidPathInPosition(String path, int pos, String complement) {
        return new SingularFormException(
                "Path '" + path + "': inválido na posição " + pos + (complement != null ? " : " + complement : ""));
    }

    private int findTokenEndOrException(String s, int inicio) {
        int pos = inicio;
        while (pos < s.length() && s.charAt(pos) != '.' && s.charAt(pos) != '[') {
            pos++;
        }
        if (inicio == pos) {
            throw newInvalidPathInPosition(s, inicio, null);
        }
        return pos;
    }

    /** Retorna o trecho atual do path sendo lido. Será o nome do campo ou número do índice sendo lido. */
    public String getToken() {
        if (token == null) {
            throw new SingularFormException("Leitura já está no fim");
        }
        return token;
    }

    /** Retorna o próximo trecho do path a ser processado. */
    public PathReader next() {
        if (token == null) {
            throw new SingularFormException("Leitura já está no fim");
        }
        return new PathReader(path, end);
    }

    /** True se a leitura ja tiver sido concluida. */
    public boolean isEmpty() {
        return token == null;
    }

    /** Indica se é o último elemento do path. */
    public boolean isLast() {
        if (token == null) {
            throw new SingularFormException("Leitura já está no fim");
        }
        return end == path.length();
    }

    /** Indica se o trecho atual é um indice para uma lista. */
    public boolean isIndex() {
        if (token == null) {
            throw new SingularFormException("Leitura já está no fim");
        }
        return aListIndex;
    }

    /** Retorna o índice informado pelo trecho atual (faz parseInt do trecho). */
    public int getIndex() {
        return Integer.parseInt(token);
    }

    /** Monta uma mensagem de erro referente ao path atual e a instância informada. */
    public String getErrorMsg(SInstance instanciaContexto, String msg) {
        return getErrorMsg("Na instancia '" + instanciaContexto.getPathFull() + "' do tipo '" +
                instanciaContexto.getType().getName() + "'", msg);
    }

    /** Monta uma mensagem de erro referente ao path atual e ao escopo informado. */
    public String getErrorMsg(SScope escopo, String msg) {
        return getErrorMsg("No tipo '" + escopo.getName() + "'", msg);
    }

    /** Monta uma mensagem de erro referente ao path atual e ao escopo informado. */
    public String getErrorMsg(String scope, String msg) {
        if (path.length() == token.length()) {
            return scope + " para o path '" + path + "': " + msg;
        }
        return scope + " ao tentar resolver o trecho '" + token + "' do path '" + path + "': " + msg;
    }
}