package br.net.mirante.singular.exemplos.notificacaosimplificada.domain.enums;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlEnum
@XmlType(namespace="http://www.anvisa.gov.br/reg-med/schema/domains")
public enum TipoObrigatoriedade {
	
	@XmlEnumValue("0")
	NAO_APLICAVEL('1', "Não aplicável"),

	@XmlEnumValue("1")
	OBRIGATORIO('1', "Obrigatório"),
	
	@XmlEnumValue("2")
	NAO_OBRIGATORIO('2', "Não obrigatório");

	public static final String ENUM_CLASS_NAME = "br.net.mirante.singular.exemplos.notificacaosimplificada.domain.enums.TipoObrigatoriedade";
	
	private Character codigo;
	private String descricao;

	private TipoObrigatoriedade(Character codigo, String descricao) {
		this.codigo = codigo;
		this.descricao = descricao;
	}

	public Character getCodigo() {
		return codigo;
	}

	public String getDescricao() {
		return descricao;
	}
	
	public static TipoObrigatoriedade valueOfEnum(Character codigo) {

		TipoObrigatoriedade status[] = TipoObrigatoriedade.values();

		for (TipoObrigatoriedade st : status) {
			if (st.getCodigo().equals(codigo)){
				return st;
			}
		}
		return null;
	}
}
