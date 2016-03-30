package br.net.mirante.singular.exemplos.notificacaosimpliciada.domain.enums;

public enum TipoSolventeResidual implements EnumId<TipoSolventeResidual, Character> {

	CLASSE_1('1', "Classe 1 - Solventes que devem ser evitados"),
	CLASSE_2('2', "Classe 2 - Solventes que devem ser limitados"),
	CLASSE_3('3', "Classe 3 - Solventes com baixo potencial tóxico"),
	CLASSE_4('4', "Classe 4 - Solventes sem avaliação do ICH");
	
	public static final String ENUM_CLASS_NAME = "br.gov.anvisa.reg.medicamento.domain.enums.TipoSolventeResidual";
	
	private Character codigo;
	private String descricao;
	
	
	private TipoSolventeResidual(Character codigo, String descricao) {
		this.codigo = codigo;
		this.descricao = descricao;
	}


	public String getDescricao() {
		return descricao;
	}


	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}


	public Character getCodigo() {
		return codigo;
	}


	public void setCodigo(Character codigo) {
		this.codigo = codigo;
	}
	
	public static TipoSolventeResidual valueOfEnum(Character codigo) {
		TipoSolventeResidual status[] = TipoSolventeResidual.values();

		for (TipoSolventeResidual st : status) {
			if (codigo != null && st.getCodigo().charValue() == codigo.charValue()){
				return st;
			}
		}
		return null;
	}


	@Override
	public TipoSolventeResidual getEnum() {
		return this;
	}
	
}