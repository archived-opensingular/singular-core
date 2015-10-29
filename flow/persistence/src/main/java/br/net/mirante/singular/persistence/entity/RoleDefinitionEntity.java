package br.net.mirante.singular.persistence.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import br.net.mirante.singular.persistence.util.Constants;

/**
 * The persistent class for the TB_PAPEL database table.
 */
@Entity
@GenericGenerator(name = AbstractRoleDefinitionEntity.PK_GENERATOR_NAME, strategy = "org.hibernate.id.IdentityGenerator")
@Table(name = "TB_DEFINICAO_PAPEL", schema = Constants.SCHEMA)
public class RoleDefinitionEntity extends AbstractRoleDefinitionEntity<ProcessDefinitionEntity> {
    private static final long serialVersionUID = 1L;

}
