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
