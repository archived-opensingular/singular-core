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

/**


 */
package org.opensingular.form.exemplos.notificacaosimplificada.domain.enums;

/**
 * Tipos de análise realizadas para diferenciar uma anális realizada pelo Gerente Geral das outras análises relacionadas
 * a um processo
 *
 * @author Lucas Souza
 */
public enum TipoAnalise {

    COMUM(1, "Comum"),
    FINAL(2, "Final"),
    ESPECIALISTA(3, "Especialista"),
    COREC(4, "Corec"),
    ESPECIALISTA_RETRATACAO(5, "Especialista Retratação"),
    COMUM_RETRATACAO_COORDENADOR(6, "Comum Retratação Coordenador"),
    FINAL_RETRATACAO(7, "Final Retratação"),
    DICOL(8, "DICOL"),
    COMUM_RETRATACAO_GERENTE(9, "Comum Retratação Gerente");

    private Integer codigo;
    private String  descricao;

    private TipoAnalise(Integer codigo, String descricao) {
        this.setCodigo(codigo);
        this.setDescricao(descricao);
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public static TipoAnalise valueOfEnum(Integer codigo) {
        TipoAnalise status[] = TipoAnalise.values();

        for (TipoAnalise st : status) {
            if (codigo != null && codigo.equals(st.getCodigo())) {
                return st;
            }
        }
        return null;
    }

}
