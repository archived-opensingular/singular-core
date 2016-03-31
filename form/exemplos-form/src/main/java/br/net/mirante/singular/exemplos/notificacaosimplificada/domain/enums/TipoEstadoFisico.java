package br.net.mirante.singular.exemplos.notificacaosimplificada.domain.enums;

public enum TipoEstadoFisico implements EnumId<TipoEstadoFisico, Character> {

	NAO_INFORMADO('4', ""),
	
	SOLIDO('0', "Sólido"),
	
	SEMISOLIDO('1', "Semi-sólido"),
	
	LIQUIDO('2', "Líquido"),

	GASOSO('3', "Gasoso");

	public static final String ENUM_CLASS_NAME = "br.gov.anvisa.reg.medicamento.domain.enums.TipoEstadoFisico";
	
	private TipoEstadoFisico(Character codigo, String descricao) {
		this.codigo = codigo;
		this.descricao = descricao;
	}

	private Character codigo;

	private String descricao;

	@Override
	public TipoEstadoFisico getEnum() {
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

	public static TipoEstadoFisico valueOfEnum(Character id) {
		for (TipoEstadoFisico tipo : values()) {
			if (tipo.getCodigo().equals(id)){
				return tipo;
			}
		}
		return null;
	}
}