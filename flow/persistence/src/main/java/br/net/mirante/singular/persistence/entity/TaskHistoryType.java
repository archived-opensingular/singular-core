package br.net.mirante.singular.persistence.entity;

import br.net.mirante.singular.flow.core.entity.IEntityTaskHistoricType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the TB_TIPO_HISTORICO_TAREFA database table.
 */
@Entity
@Table(name = "TB_TIPO_HISTORICO_TAREFA")
@NamedQuery(name = "TipoHistoricoTarefa.findAll", query = "SELECT t FROM TipoHistoricoTarefa t")
public class TaskHistoryType implements IEntityTaskHistoricType {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CO_TIPO_HISTORICO_TAREFA")
    private Long cod;

    @Column(name = "DS_TIPO_HISTORICO_TAREFA")
    private String description;

    public TaskHistoryType() {
    }

    public Long getCod() {
        return this.cod;
    }

    public void setCod(Long cod) {
        this.cod = cod;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getDescription() {
        return null;
    }
}