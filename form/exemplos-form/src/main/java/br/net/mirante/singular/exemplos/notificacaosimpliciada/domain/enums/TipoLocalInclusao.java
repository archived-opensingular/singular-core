package br.net.mirante.singular.exemplos.notificacaosimpliciada.domain.enums;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

@XmlEnum
public enum TipoLocalInclusao implements EnumId<TipoLocalInclusao, Integer> {

	@XmlEnumValue("1")
	BULA_PACIENTE(1, "BULA DO PACIENTE"),

	@XmlEnumValue("2")
	BULA_PROFISSIONAL_SAUDE(2, "BULA DO PROFISSIONAL DE SAÚDE"),

	@XmlEnumValue("3")
	ROTULO_EMBALAGEM_PRIMARIA(3, "RÓTULO DA EMBALAGEM PRIMÁRIA"),

	@XmlEnumValue("4")
	ROTULO_EMBALAGEM_SECUNDARIO(4, "RÓTULO DA EMBALAGEM SECUNDÁRIO"),

	@XmlEnumValue("5")
	ROTULO_EMBALAGEM_INTERMEDIARIA(5, "RÓTULO DE EMBALAGEM INTERMEDIÁRIA"),

	@XmlEnumValue("6")
	ROTULO_EMBALAGEM_TRANSPORTE(6, "RÓTULO DE EMBALAGEM DE TRANSPORTE"),
	
	@XmlEnumValue("7")
	ROTULO_DESSECANTE(7, "RÓTULO DO DESSECANTE");
	
	public static final String ENUM_CLASS_NAME = "br.gov.anvisa.reg.medicamento.domain.enums.TipoLocalInclusao";
	
	/**
	 * Identificador do tipo de unidade de medida.
	 */
	private final Integer codigo;

	/**
	 * Descrição do tipo de unidade de medida.
	 */
	private final String descricao;

	private TipoLocalInclusao(Integer codigo, String descricao) {
		this.codigo = codigo;
		this.descricao = descricao;
	}

	@Override
	public Integer getCodigo() {
		return this.codigo;
	}

	@Override
	public String getDescricao() {
		return this.descricao;
	}

	@Override
	public TipoLocalInclusao getEnum() {
		return this;
	}

	/**
	 * @param id
	 * @return
	 */
	public static TipoLocalInclusao valueOfEnum(Integer id) {
		for (TipoLocalInclusao tipo : values()) {
			if (tipo.getCodigo().equals(id)){
				return tipo;
			}
		}
		return null;
	}
}