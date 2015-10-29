package br.net.mirante.singular.persistence.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import br.net.mirante.singular.flow.core.TaskType;
import br.net.mirante.singular.persistence.util.Constants;

/**
 * The persistent class for the TB_VERSAO_TAREFA database table.
 */
@Entity
@GenericGenerator(name = AbstractEntityTaskVersion.PK_GENERATOR_NAME, strategy = "org.hibernate.id.IdentityGenerator")
@Table(name = "TB_VERSAO_TAREFA", schema = Constants.SCHEMA)
public class TaskVersionEntity extends AbstractEntityTaskVersion<ProcessVersionEntity, TaskDefinitionEntity, TaskTransitionVersionEntity, TaskType> {
    private static final long serialVersionUID = 1L;

}
