package br.net.mirante.singular.persistence.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import br.net.mirante.singular.persistence.util.Constants;
import br.net.mirante.singular.persistence.util.HybridIdentityOrSequenceGenerator;
import org.hibernate.annotations.GenericGenerator;

/**
 * The persistent class for the TB_VARIAVEL_EXECUCAO_TRANSICAO database table.
 */
@Entity
@GenericGenerator(name = AbstractExecutionVariableEntity.PK_GENERATOR_NAME, strategy = HybridIdentityOrSequenceGenerator.CLASS_NAME)
@Table(name = "TB_VARIAVEL_EXECUCAO_TRANSICAO", schema = Constants.SCHEMA)
public class ExecutionVariableEntity extends AbstractExecutionVariableEntity<ProcessInstanceEntity, TaskInstanceEntity, VariableInstanceEntity, VariableTypeInstance> {
    private static final long serialVersionUID = 1L;

}
