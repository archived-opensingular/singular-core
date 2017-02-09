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

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlEnum
@XmlType(namespace = "http://www.anvisa.gov.br/reg-med/schema/domains")
public enum TipoCategoriaEnsaio {

    @XmlEnumValue("1")
    CATEGORIA_I('1', "I"),

    @XmlEnumValue("2")
    CATEGORIA_II('2', "II"),

    @XmlEnumValue("3")
    CATEGORIA_III('3', "III"),

    @XmlEnumValue("4")
    CATEGORIA_IV('4', "IV"),

    @XmlEnumValue("N")
    CATEGORIA_NA('N', "N/A");

    private Character codigo;
    private String    descricao;

    private TipoCategoriaEnsaio(Character codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public Character getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public static TipoCategoriaEnsaio valueOf(Character codigo) {

        TipoCategoriaEnsaio status[] = TipoCategoriaEnsaio.values();

        for (TipoCategoriaEnsaio st : status) {
            if (codigo != null && st.getCodigo().charValue() == codigo.charValue()) {
                return st;
            }
        }
        return null;
    }
}
