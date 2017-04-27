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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

/**
 * Representa as defini��es de um �nico estilo. Basicamente nome e propriedades.
 * <p>
 * Para saber todas as propriedade poss�veis, veja org.w3c.dom.css.CSS2Properties
 *
 * @author daniel - Feb 15, 2005
 */
public final class EstiloCSS implements Iterable<Entry<String, String>>{

    /**
     * Nome do estilo.
     */
    private final String nome_;


    /**
     * Contem os pares de definicao de estilo.
     */
    private final Map<String, String> definicoes_ = new LinkedHashMap<>();

    /**
     * Cria um novo estilo fazendo um parse da defini��es informada.
     *
     * @param nome Nome do estilo
     * @param def Deve ser diferente de null.
     */
    EstiloCSS(String nome) {
        nome_ = nome;
    }

    void addDef(String def) {
        StringTokenizer tok1 = new StringTokenizer(def, ";", false);
        while (tok1.hasMoreTokens()) {
            String st = tok1.nextToken().trim();
            if (!st.equals("")) {
                StringTokenizer tok2 = new StringTokenizer(st, ":", false);

                // se tiver qtd tokens diferente de 2 (erro sintaxe), ignora
                if (tok2.countTokens() == 2) {
                    String nm = tok2.nextToken().trim();
                    String vlr = tok2.nextToken().trim();
                    
                    definicoes_.put(nm, vlr);
                }
            }
        }
    }

    /**
     * Retorn o nome do estilo.
     *
     * @return -
     */
    public String getNome() {
        return nome_;
    }

    /**
     * Retorna o nome do estilo removendo o ponto na frente do nome, se esse
     * existir.
     *
     * @return -
     */
    public String getNomeSemPonto() {
        if (nome_.charAt(0) == '.') {
            return nome_.substring(1);
        }
        return nome_;
    }

    /**
     * Devolve o valro da propriedade do estilo se existir.
     *
     * @param nome Nome da defini��o procurada.
     * @return null se n�o existir a defini��o.
     */
    public String getValor(String nome) {
        return (String) definicoes_.get(nome);
    }

    /**
     * Retorna uma lista das defini��es onde cada posi��o do array possui mais
     * duas posi��es (nome e valor).
     *
     * @return sempre diferente de null.
     */
    public String[][] getDefinicoes() {
        return definicoes_.entrySet().stream().map(entry -> new String[] {entry.getKey(), entry.getValue()}).collect(Collectors.toCollection(ArrayList::new)).toArray(new String[definicoes_.size()][]);
    }

    public String propertiesToString() {
        StringBuilder buf = new StringBuilder();
        propertiesToString(buf);
        return buf.toString();
    }

    public void propertiesToString(StringBuilder buf) {
        buf.append(definicoes_.entrySet().stream().map(entry -> entry.getKey() + ": "+entry.getValue()).collect(Collectors.joining(";")));
    }

    public void toString(StringBuilder buf) {
        buf.append(nome_).append(" {");
        propertiesToString(buf);
        buf.append(" }");
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        toString(buf);
        return buf.toString();
    }
    
    @Override
    public Iterator<Entry<String, String>> iterator() {
        return definicoes_.entrySet().iterator();
    }

}