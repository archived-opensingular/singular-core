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

package org.opensingular.form.exemplos.notificacaosimplificada.domain.enums;

public enum TipoConformidade {

    SIM("S", "Conforme"),
    NAO("N", "Não conforme"),
    NAO_SE_APLICA("X", "Não se aplica"),
    NAO_INFORMADO("I", "Não analisado");

    public static final String ENUM_CLASS_NAME = "org.opensingular.form.exemplos.notificacaosimplificada.domain.enums.TipoConformidade";

    private String codigo;
    private String descricao;

    private TipoConformidade(String codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    /**
     * @return codigo
     */
    public String getCodigo() {
        return codigo;
    }

    /**
     * @param codigo codigo a ser atribuído
     */
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    /**
     * @return descricao
     */
    public String getDescricao() {
        return descricao;
    }

    /**
     * @param descricao descricao a ser atribuído
     */
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public static TipoConformidade valueOfEnum(String codigo) {
        TipoConformidade status[] = TipoConformidade.values();

        for (TipoConformidade st : status) {
            if (codigo != null && codigo.equals(st.getCodigo())) {
                return st;
            }
        }
        return null;
    }

}
