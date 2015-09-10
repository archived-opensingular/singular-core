package br.net.mirante.singular.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the TB_TRANSICAO database table.
 * 
 */
@Entity
@Table(name="TB_TRANSICAO")
@NamedQuery(name="Transicao.findAll", query="SELECT t FROM Transicao t")
public class Transicao  {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="CO_TRANSICAO")
	private Long cod;

	@Column(name="NO_TRANSICAO")
	private String nome;

	@Column(name="SG_TRANSICAO")
	private String sigla;

	@Column(name="TP_TRANSICAO")
	private String tipoTransicao;

	//uni-directional many-to-one association to Tarefa
	@ManyToOne
	@JoinColumn(name="CO_TAREFA_DESTINO")
	private Tarefa tarefaDestino;

	//uni-directional many-to-one association to Tarefa
	@ManyToOne
	@JoinColumn(name="CO_TAREFA_ORIGEM")
	private Tarefa tarefaOrigem;

	public Transicao() {
	}

	public Long getCod() {
		return this.cod;
	}

	public void setCod(Long cod) {
		this.cod = cod;
	}

	public String getNome() {
		return this.nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getSigla() {
		return this.sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public String getTipoTransicao() {
		return this.tipoTransicao;
	}

	public void setTipoTransicao(String tipoTransicao) {
		this.tipoTransicao = tipoTransicao;
	}

	public Tarefa getTarefaDestino() {
		return this.tarefaDestino;
	}

	public void setTarefaDestino(Tarefa tarefaDestino) {
		this.tarefaDestino = tarefaDestino;
	}

	public Tarefa getTarefaOrigem() {
		return this.tarefaOrigem;
	}

	public void setTarefaOrigem(Tarefa tarefaOrigem) {
		this.tarefaOrigem = tarefaOrigem;
	}

}