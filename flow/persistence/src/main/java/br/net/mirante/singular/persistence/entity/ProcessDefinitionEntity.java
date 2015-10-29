package br.net.mirante.singular.persistence.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import br.net.mirante.singular.persistence.util.Constants;

/**
 * The persistent class for the TB_DEFINICAO_PROCESSO database table.
 */
@Entity
@GenericGenerator(name = AbstractProcessDefinitionEntity.PK_GENERATOR_NAME, strategy = HybridIdentityOrSequenceGenerator.CLASS_NAME)
@Table(name = "TB_DEFINICAO_PROCESSO", schema = Constants.SCHEMA)
public class ProcessDefinitionEntity extends AbstractProcessDefinitionEntity<CategoryEntity, TaskDefinitionEntity, RoleDefinitionEntity, ProcessVersionEntity> {

    private static final long serialVersionUID = 1L;
}
