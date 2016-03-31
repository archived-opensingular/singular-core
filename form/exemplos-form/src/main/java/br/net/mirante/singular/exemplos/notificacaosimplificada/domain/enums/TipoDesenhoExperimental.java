package br.net.mirante.singular.exemplos.notificacaosimplificada.domain.enums;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

/**
 * @author allysson.cavalcante
 */
@XmlEnum
public enum TipoDesenhoExperimental {

    /**
     * Tratamento.
     */
    @XmlEnumValue("F")
    FATORIAL('F', "Fatorial"),

    /**
     * Prevencao
     */
    @XmlEnumValue("P")
    PARALELO('P', "Paralelo"),

    /**
     * Auxiliar diagnostico
     */
    @XmlEnumValue("C")
    CRUZADO('C', "Cruzado"),

    /**
     * Diagnostico
     */
    @XmlEnumValue("A")
    ADAPTATIVOS('A', "Adaptativos");

    /**
     * Identificador do tipo de unidade de medida.
     */
    private final Character codigo;

    /**
     * Descricao do tipo de unidade de medida.
     */
    private final String descricao;

    private TipoDesenhoExperimental(Character codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public Character getCodigo() {
        return this.codigo;
    }

    public String getDescricao() {
        return this.descricao;
    }

    /**
     * @param id
     * @return
     */
    public static TipoDesenhoExperimental valueOf(Character codigo) {
        TipoDesenhoExperimental tipos[] = TipoDesenhoExperimental.values();

        for (TipoDesenhoExperimental tipo : tipos) {
            if (tipo != null && tipo.getCodigo().charValue() == codigo.charValue()) {
                return tipo;
            }
        }
        return null;
    }
}