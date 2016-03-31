package br.net.mirante.singular.exemplos.notificacaosimplificada.domain.enums;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

@XmlEnum
public enum SimNao implements EnumId<SimNao, Character> {

    @XmlEnumValue("S")
    SIM('S', "Sim"),

    @XmlEnumValue("N")
    NAO('N', "Não");

    public static final String ENUM_CLASS_NAME = "br.net.mirante.singular.exemplos.notificacaosimplificada.domain.enums.SimNao";

    private Character codigo;
    private String    descricao;

    private SimNao(Character codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    @Override
    public Character getCodigo() {
        return codigo;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    @Override
    public SimNao getEnum() {
        return this;
    }

    public boolean isNao() {
        return NAO.equals(this);
    }

    public static SimNao valueOfEnum(Character codigo) {
        for (SimNao tipo : values()) {
            if (tipo.getCodigo().equals(codigo)) {
                return tipo;
            }
        }

        return null;
    }

}
