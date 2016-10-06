package org.opensingular.lib.support.persistence.enums;

import org.opensingular.lib.support.persistence.util.EnumId;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

@XmlEnum
public enum SimNao implements EnumId<SimNao, String> {

    @XmlEnumValue("S")
    SIM("S", "Sim"),

    @XmlEnumValue("N")
    NAO("N", "Não");

    public static final String ENUM_CLASS_NAME = "org.opensingular.singular.support.persistence.enums.SimNao";

    private String codigo;
    private String descricao;

    SimNao(String codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    @Override
    public SimNao valueOfEnum(String codigo) {
        for (SimNao tipo : values()) {
            if (tipo.getCodigo().equals(codigo)) {
                return tipo;
            }
        }

        return null;
    }

    @Override
    public String getCodigo() {
        return codigo;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

}
