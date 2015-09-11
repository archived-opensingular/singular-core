package br.net.mirante.singular.persistence.entity;

import br.net.mirante.singular.flow.core.entity.IEntityTaskHistoricType;

import javax.persistence.*;


/**
 * The persistent class for the TB_TIPO_HISTORICO_TAREFA database table.
 * 
 */
@Entity
@Table(name="TB_TIPO_HISTORICO_TAREFA")
@NamedQuery(name="TipoHistoricoTarefa.findAll", query="SELECT t FROM TipoHistoricoTarefa t")
public class TipoHistoricoTarefa implements IEntityTaskHistoricType {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="CO_TIPO_HISTORICO_TAREFA")
	private Long cod;

	@Column(name="DS_TIPO_HISTORICO_TAREFA")
	private String descricao;

	public TipoHistoricoTarefa() {
	}

	public Long getCod() {
		return this.cod;
	}

	public void setCod(Long cod) {
		this.cod = cod;
	}

	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Override
	public String getDescription() {
		return null;
	}
}