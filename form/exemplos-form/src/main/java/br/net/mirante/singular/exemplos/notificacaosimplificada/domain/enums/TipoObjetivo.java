package br.net.mirante.singular.exemplos.notificacaosimplificada.domain.enums;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

@XmlEnum
public enum TipoObjetivo {

	@XmlEnumValue("T")
	TREINAMENTO('T', "Tratamento"),
	
	@XmlEnumValue("P")
	PREVENCAO('P', "Prevencao"),
	
	@XmlEnumValue("A")
	AUXILIAR_DIAGNOSTICO('A', "Auxiliar Diagnostico"),
	
	@XmlEnumValue("D")
	DIAGNOSTICO('D',"Diagnostico");
	
	private Character codigo;
	private String descricao;

	private TipoObjetivo(Character codigo, String descricao) {
		this.codigo = codigo;
		this.descricao = descricao;
	}

	public Character getCodigo() {
		return codigo;
	}

	public String getDescricao() {
		return descricao;
	}

	public static TipoObjetivo valueOf(Character codigo) {
		
		TipoObjetivo status[] = TipoObjetivo.values();
		
		for (TipoObjetivo st : status) {    
			if (codigo != null && st.getCodigo().charValue() == codigo.charValue()){
				return st;
			}
		}
		return null;
	}
}
