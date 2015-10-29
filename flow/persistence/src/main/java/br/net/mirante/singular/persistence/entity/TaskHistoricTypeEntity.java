package br.net.mirante.singular.persistence.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import br.net.mirante.singular.persistence.util.Constants;

/**
 * The persistent class for the TB_TIPO_HISTORICO_TAREFA database table.
 */
@Entity
@GenericGenerator(name = AbstractTaskHistoricTypeEntity.PK_GENERATOR_NAME, strategy = "org.hibernate.id.IdentityGenerator")
@Table(name = "TB_TIPO_HISTORICO_TAREFA", schema = Constants.SCHEMA)
public class TaskHistoricTypeEntity extends AbstractTaskHistoricTypeEntity {
    private static final long serialVersionUID = 1L;

}
