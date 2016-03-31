package br.net.mirante.singular.exemplos.notificacaosimplificada.domain.enums;

public enum TipoTarja implements EnumId<TipoTarja, Character>  {

	SEM_TARJA('1', "Sem tarja"),
	VERMELHA('2', "Vermelha"),
	VERMELHA_COM_RETENCAO('3', "Vermelha com retenção"),
	PRETA('4', "Preta");
	
	public static final String ENUM_CLASS_NAME = "br.gov.anvisa.reg.medicamento.domain.enums.TipoTarja";
	
	private Character codigo;
	private String descricao;
	
	private TipoTarja(Character codigo, String descricao){
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
	public TipoTarja getEnum() {
		return this;
	}
	
	public static TipoTarja valueOfEnum(Character codigo) {
		for (TipoTarja tipo : values()) {
			if (tipo.getCodigo().equals(codigo)){
				return tipo;
			}
		}

        return null;
    }
	
}
