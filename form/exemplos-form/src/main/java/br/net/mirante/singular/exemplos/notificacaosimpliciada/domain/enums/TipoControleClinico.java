package br.net.mirante.singular.exemplos.notificacaosimpliciada.domain.enums;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

@XmlEnum
public enum TipoControleClinico {
	
	@XmlEnumValue("A")
	ATIVO('A', "Ativo"),

	@XmlEnumValue("P")
	PLACEBO('P', "Placebo");

	private Character codigo;
	private String descricao;

	private TipoControleClinico(Character codigo, String descricao) {
		this.codigo = codigo;
		this.descricao = descricao;
	}

	public Character getCodigo() {
		return codigo;
	}

	public String getDescricao() {
		return descricao;
	}
	
	public static TipoControleClinico valueOf(Character codigo) {

		TipoControleClinico status[] = TipoControleClinico.values();

		for (TipoControleClinico st : status) {
			if (codigo != null && st.getCodigo().charValue() == codigo.charValue()){
				return st;
			}
		}
		return null;
	}
}
