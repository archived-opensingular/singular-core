package br.net.mirante.singular.persistence.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import br.net.mirante.singular.persistence.util.Constants;
import br.net.mirante.singular.persistence.util.HybridIdentityOrSequenceGenerator;
import org.hibernate.annotations.GenericGenerator;

/**
 * The persistent class for the TB_PAPEL database table.
 */
@Entity
@GenericGenerator(name = AbstractRoleDefinitionEntity.PK_GENERATOR_NAME, strategy = HybridIdentityOrSequenceGenerator.CLASS_NAME)
@Table(name = "TB_DEFINICAO_PAPEL", schema = Constants.SCHEMA)
public class RoleDefinitionEntity extends AbstractRoleDefinitionEntity<ProcessDefinitionEntity> {
    private static final long serialVersionUID = 1L;

}
