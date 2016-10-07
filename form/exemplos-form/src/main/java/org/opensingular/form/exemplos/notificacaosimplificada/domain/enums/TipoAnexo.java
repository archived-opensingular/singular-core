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
 * @author Denis Coutinho
 * @since 16/12/2011
 */
public enum TipoAnexo {

    OFICIO("O", "Oficio"),
    PARECER("P", "Parecer"),
    OUTROS_ARQUIVOS("X", "Outros Arquivos"),
    DESPACHO("D", "Despacho"),
    ATA("A", "Ata");

    private String codigo;

    private String descricao;

    private TipoAnexo(String codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    /**
     * @return the codigo
     */
    public String getCodigo() {
        return codigo;
    }

    /**
     * @param codigo the codigo to set
     */
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    /**
     * @return the descricao
     */
    public String getDescricao() {
        return descricao;
    }

    /**
     * @param descricao the descricao to set
     */
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public static TipoAnexo valueOfEnum(String codigo) {
        TipoAnexo status[] = TipoAnexo.values();

        for (TipoAnexo st : status) {
            if (codigo != null && codigo.equals(st.getCodigo())) {
                return st;
            }
        }
        return null;
    }

}
