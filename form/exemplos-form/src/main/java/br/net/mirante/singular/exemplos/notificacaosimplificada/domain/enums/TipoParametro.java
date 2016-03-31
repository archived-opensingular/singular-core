package br.net.mirante.singular.exemplos.notificacaosimplificada.domain.enums;

public enum TipoParametro implements EnumId<TipoParametro, Character> {

	MAIOR_DEZ_PORCENTO('0', "> 10%"),
	
	MENOR_DEZ_PORCENTO('1', "> 1% e < 10%"),
	
	MENOR_UM_PORCENTO('2', "> 0,1% e < 1%"),
	
	MENOR_ZERO_UM_PORCENTO('3', "> 0,01% e < 0,1%"),

	MENOR_ZERO_ZERO_UM_PORCENTO('4', "< 0,01%");

	public static final String ENUM_CLASS_NAME = "br.gov.anvisa.reg.medicamento.domain.enums.TipoParametro";
	
	private TipoParametro(Character codigo, String descricao) {
		this.codigo = codigo;
		this.descricao = descricao;
	}

	private Character codigo;

	private String descricao;

	@Override
	public TipoParametro getEnum() {
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

	public static TipoParametro valueOfEnum(Character id) {
		for (TipoParametro tipo : values()) {
			if (tipo.getCodigo().equals(id)){
				return tipo;
			}
		}
		return null;
	}
}