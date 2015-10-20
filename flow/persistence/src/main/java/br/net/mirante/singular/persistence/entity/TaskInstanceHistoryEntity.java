package br.net.mirante.singular.persistence.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import br.net.mirante.singular.persistence.util.HybridIdentityOrSequenceGenerator;
import org.hibernate.annotations.GenericGenerator;

import br.net.mirante.singular.persistence.util.Constants;

/**
 * The persistent class for the TB_HISTORICO_INSTANCIA_TAREFA database table.
 */
@Entity
@GenericGenerator(name = AbstractTaskInstanceHistoryEntity.PK_GENERATOR_NAME, strategy = HybridIdentityOrSequenceGenerator.CLASS_NAME)
@Table(name = "TB_HISTORICO_INSTANCIA_TAREFA", schema = Constants.SCHEMA)
public class TaskInstanceHistoryEntity extends AbstractTaskInstanceHistoryEntity<Actor, TaskInstanceEntity, TaskHistoricTypeEntity> {
    private static final long serialVersionUID = 1L;

}
