package br.net.mirante.singular.exemplos.notificacaosimplificada.domain.enums;

public enum SituacaoSimNao {
	
	SIM("Sim","S"),
	NAO("Não","N"),
	NAO_SE_APLICA("NÃO SE APLICA" ,"X");
	
	private String descricao;
	private String codigo;
	
	
	private SituacaoSimNao(String descricao, String codigo) {
		this.codigo = codigo;
		this.descricao = descricao;
	}


	public String getDescricao() {
		return descricao;
	}


	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}


	public String getCodigo() {
		return codigo;
	}


	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	
	public static SituacaoSimNao valueOfEnum(String codigo) {
		SituacaoSimNao status[] = SituacaoSimNao.values();

		for (SituacaoSimNao st : status) {
			if (codigo != null && codigo.equals(st.getCodigo())){
				return st;
			}
		}
		return null;
	}
	
	public static SituacaoSimNao fromBoolean(Boolean bool){
		if (bool != null && bool.booleanValue()){
			return SIM;
		} else {
			return NAO;
		}		
	}
	
	public Boolean toBoolean(){
		if (this == SIM){
			return Boolean.TRUE;
		}
		else{ 
			return Boolean.FALSE;
		}
	}

}
