package br.net.mirante.singular.persistence.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import br.net.mirante.singular.persistence.util.Constants;

/**
 * The persistent class for the TB_INSTANCIA_PAPEL database table.
 */
@Entity
@GenericGenerator(name = AbstractRoleInstanceEntity.PK_GENERATOR_NAME, strategy = "org.hibernate.id.IdentityGenerator")
@Table(name = "TB_INSTANCIA_PAPEL", schema = Constants.SCHEMA)
public class RoleInstanceEntity extends AbstractRoleInstanceEntity<Actor, ProcessInstanceEntity, RoleDefinitionEntity> {
    private static final long serialVersionUID = 1L;

}
