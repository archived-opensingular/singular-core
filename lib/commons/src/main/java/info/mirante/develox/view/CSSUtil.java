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

import java.io.InputStream;
import java.util.Map.Entry;

/**
 * Da apoio na substitui�� d�mica do uso de css por camando direto na gera��o de
 * HTML que podem n�o enxergar o arquico css (por exemplo e-mail). Essa classe �
 * particularmente util para uso junto a templates velocity.
 *
 * @author Daniel C. Bordin - 16/02/2005
 */
public class CSSUtil {

    /**
     * Indica que a gera��o deve dar prefer�ncia ao uso da tag class.
     */
    private static final int GERAR_TAG_CLASS = 1;

    /**
     * Indica o uso da tag style (com as propriedades) em vez de class.
     */
    private static final int GERAR_TAG_STYLE = 2;

    /**
     * Indica que a gera��o deve dar prefer�ncia pela expans�o do estilo em tags
     * especificas de cada propriedade.
     */
    private static final int GERAR_TAGS_EXPANDIDAS = 3;

    /**
     * Lista de defini��es de estilo.
     */
    private SimpleCSS css_;

    /**
     * Determina o tipo de gera�ao preferencial segundo as defini��es acima.
     */
    private int tipoGeracao_ = GERAR_TAG_CLASS;

    public CSSUtil() {
    }

    public CSSUtil setUsoTagClass() {
        tipoGeracao_ = GERAR_TAG_CLASS;
        return this;
    }

    public CSSUtil setUsoTagSytle() {
        tipoGeracao_ = GERAR_TAG_STYLE;
        return this;
    }

    public CSSUtil setUsoTagsExpandidas() {
        tipoGeracao_ = GERAR_TAGS_EXPANDIDAS;
        return this;
    }

    public void setExpandirTags(boolean expandirTagsCSS) {
        if (expandirTagsCSS) {
            setUsoTagsExpandidas();
        } else {
            setUsoTagClass();
        }
    }

    public void carregarCSS(InputStream in) {
        if (css_ == null) {
            css_ = new SimpleCSS();
        }
        css_.addCSS(in);
    }

    /**
     * Retorna as tags para o estilo solicitado segundo a configura��o de
     * gera��o.
     *
     * @param nomeEstilo Nome do estilo a ser utilizado. Caso esteja configurado
     * para expandir as tags ou usar a tag style e n�o encontre a
     * defini��o do estilo, ent�o usar� a tag class assim mesmo.
     * @return Sempre diferente null, mas pode ser string vazia.
     */
    public String tbl(String nomeEstilo) {
        return tratarTag(nomeEstilo);
    }

    public String tratarTag(String nomeEstilo) {
        if (tipoGeracao_ != GERAR_TAG_CLASS) {
            EstiloCSS estilo = getEstilo(nomeEstilo);
            if (estilo != null) {
                switch (tipoGeracao_) {
                    case GERAR_TAG_STYLE:
                        return "style=\"" + estilo.propertiesToString() + '"';
                    case GERAR_TAGS_EXPANDIDAS:
                        return expandirTagsTabela(estilo);
                }
            }
        }
        return "class=\"" + nomeEstilo + '"';
    }

    private String expandirTagsTabela(EstiloCSS estilo) {
        StringBuilder bufTbl = new StringBuilder();

        for (Entry<String, String> entry : estilo) {
            String nome = entry.getKey();
            String valor = entry.getValue();
            if (nome.equals("width") || nome.equals("height")) {
                addAtt(bufTbl, nome, valor);
            } else if ("background".equals(nome)) {
                addAtt(bufTbl, "bgcolor", valor);
            } else if ("text-align".equals(nome)) {
                addAtt(bufTbl, "align", valor);
            } else if ("vertical-align".equals(nome)) {
                int pos = valor.indexOf('-');
                if ((pos != -1) && (pos != valor.length() - 1)) {
                    valor = valor.substring(pos + 1);
                }
                addAtt(bufTbl, "valign", valor);
            }
        }
        bufTbl.append(" style=\"");
        estilo.propertiesToString(bufTbl);
        bufTbl.append('"');
        return bufTbl.toString();
    }

    private static void addAtt(StringBuilder buf, String nome, String valor) {
        buf.append(' ').append(nome).append("=\"").append(valor).append('"');
    }

    private EstiloCSS getEstilo(String nome) {
        if (css_ != null) {
            return css_.getEstiloSemPonto(nome);
        }
        return null;
    }

}