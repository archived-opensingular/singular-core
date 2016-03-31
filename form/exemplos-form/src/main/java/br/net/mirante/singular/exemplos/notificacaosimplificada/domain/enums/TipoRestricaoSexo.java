package br.net.mirante.singular.exemplos.notificacaosimplificada.domain.enums;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlEnum
@XmlType(namespace="http://www.anvisa.gov.br/reg-med/schema/domains")
public enum TipoRestricaoSexo {

	@XmlEnumValue("M")
	MASCULINO('M', "Masculino"),
	
	@XmlEnumValue("F")
	FEMININO('F', "Feminino"),
	
	@XmlEnumValue("A")
	AMBOS('A', "Ambos");

	public static final String ENUM_CLASS_NAME = "br.net.mirante.singular.exemplos.notificacaosimplificada.domain.enums.TipoRestricaoSexo";
	
	private Character codigo;
	private String descricao;

	private TipoRestricaoSexo(Character codigo, String descricao) {
		this.codigo = codigo;
		this.descricao = descricao;
	}

	public Character getCodigo() {
		return codigo;
	}

	public String getDescricao() {
		return descricao;
	}
	
	public static TipoRestricaoSexo valueOfEnum(Character codigo) {

		TipoRestricaoSexo status[] = TipoRestricaoSexo.values();

		for (TipoRestricaoSexo st : status) {
			if (codigo != null && st.getCodigo().charValue() == codigo.charValue()){
				return st;
			}
		}
		return null;
	}
}
