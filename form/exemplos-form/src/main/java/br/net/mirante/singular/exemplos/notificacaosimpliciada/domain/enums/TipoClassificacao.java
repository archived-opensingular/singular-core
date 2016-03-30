package br.net.mirante.singular.exemplos.notificacaosimpliciada.domain.enums;

public enum TipoClassificacao implements EnumId<TipoClassificacao, Character> {

	PRESCRICAO('P', "Prescrição"),
	
	DESTINACAO('D', "Destinação"),
	
	USO('U', "Uso");

	public static final String ENUM_CLASS_NAME = "br.gov.anvisa.reg.medicamento.domain.enums.TipoClassificacao";
	
	private TipoClassificacao(Character codigo, String descricao) {
		this.codigo = codigo;
		this.descricao = descricao;
	}

	private Character codigo;

	private String descricao;

	@Override
	public TipoClassificacao getEnum() {
		return this;
	}

	@Override
	public Character getCodigo() {
		return codigo;
	}

	@Override
	public String getDescricao() {
		return descricao;
	}

	public static TipoClassificacao valueOfEnum(Character id) {
		for (TipoClassificacao tipo : values()) {
			if (tipo.getCodigo().equals(id)){
				return tipo;
			}
		}
		return null;
	}
}