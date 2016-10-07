package org.opensingular.form.exemplos.notificacaosimplificada.domain.enums;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

@XmlEnum
public enum TipoFase {

    @XmlEnumValue("1")
    FASE_I('1', "Fase I"),

    @XmlEnumValue("2")
    FASE_II('2', "Fase II"),

    @XmlEnumValue("3")
    FASE_III('3', "Fase III"),

    @XmlEnumValue("4")
    FASE_IV('4', "Fase IV");

    private Character codigo;
    private String    descricao;

    private TipoFase(Character codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public Character getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public static TipoFase valueOfEnum(Character codigo) {

        TipoFase status[] = TipoFase.values();

        for (TipoFase st : status) {
            if (codigo != null && st.getCodigo().charValue() == codigo.charValue()) {
                return st;
            }
        }
        return null;
    }
}
