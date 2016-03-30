package br.net.mirante.singular.exemplos.notificacaosimpliciada.domain.enums;


public enum TipoResultadoAnalise {

	DEFERIDO('S', "Deferido"),
	INDEFERIDO('N', "Indeferido");
	
	public static final String ENUM_CLASS_NAME = "br.gov.anvisa.reg.medicamento.domain.enums.TipoResultadoAnalise";
	
	private Character codigo;
	private String descricao;
	
	private TipoResultadoAnalise(Character codigo, String descricao){
		this.codigo = codigo;
		this.descricao = descricao;
	}

	public Character getCodigo() {
		return codigo;
	}

	public String getDescricao() {
		return descricao;
	}
	
	public static TipoResultadoAnalise valueOfEnum(Character codigo) {

		TipoResultadoAnalise tipos[] = TipoResultadoAnalise.values();

		for (TipoResultadoAnalise tipo : tipos) {
			if (codigo != null && tipo.getCodigo().equals( codigo) ){
				return tipo;
			}
		}
		return null;
	}
	
}
