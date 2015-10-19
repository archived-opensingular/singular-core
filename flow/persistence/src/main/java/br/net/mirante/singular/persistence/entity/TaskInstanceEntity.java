package br.net.mirante.singular.persistence.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import br.net.mirante.singular.persistence.util.Constants;

/**
 * The persistent class for the TB_INSTANCIA_TAREFA database table.
 */
@Entity
@GenericGenerator(name = AbstractTaskInstanceEntity.PK_GENERATOR_NAME, strategy = "org.hibernate.id.IdentityGenerator")
@Table(name = "TB_INSTANCIA_TAREFA", schema = Constants.SCHEMA)
public class TaskInstanceEntity extends AbstractTaskInstanceEntity<Actor, ProcessInstanceEntity, TaskVersionEntity, TaskTransitionVersionEntity, ExecutionVariableEntity, TaskInstanceHistoryEntity> {
    private static final long serialVersionUID = 1L;

}
