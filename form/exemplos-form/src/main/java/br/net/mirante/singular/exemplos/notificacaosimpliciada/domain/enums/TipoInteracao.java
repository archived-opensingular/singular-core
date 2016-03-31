package br.net.mirante.singular.exemplos.notificacaosimpliciada.domain.enums;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

@XmlEnum
public enum TipoInteracao {
	
	@XmlEnumValue("M")
	MEDICAMENTO('M', "Medicamento"),
	
	@XmlEnumValue("N")
	EXAME_NAO_LABORIAL('N', "Exame laboratorial"),

	@XmlEnumValue("D")
	DOENCA('D', "Doença"),

	@XmlEnumValue("A")
	ALIMENTO('A', "Alimento");

	private Character codigo;
	private String descricao;

	private TipoInteracao(Character codigo, String descricao) {
		this.codigo = codigo;
		this.descricao = descricao;
	}

	public Character getCodigo() {
		return codigo;
	}

	public String getDescricao() {
		return descricao;
	}
	
	public static TipoInteracao valueOf(Character codigo) {

		TipoInteracao status[] = TipoInteracao.values();

		for (TipoInteracao st : status) {
			if (codigo != null && st.getCodigo().charValue() == codigo.charValue()){
				return st;
			}
		}
		return null;
	}
}
