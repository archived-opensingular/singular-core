package br.net.mirante.singular.persistence.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import br.net.mirante.singular.persistence.util.HybridIdentityOrSequenceGenerator;
import org.hibernate.annotations.GenericGenerator;

import br.net.mirante.singular.persistence.util.Constants;

/**
 * The persistent class for the TB_INSTANCIA_PAPEL database table.
 */
@Entity
@GenericGenerator(name = AbstractRoleInstance.PK_GENERATOR_NAME, strategy = HybridIdentityOrSequenceGenerator.CLASS_NAME)
@Table(name = "TB_INSTANCIA_PAPEL", schema = Constants.SCHEMA)
public class RoleInstanceEntity extends AbstractRoleInstanceEntity<Actor, ProcessInstanceEntity, RoleDefinitionEntity> {
    private static final long serialVersionUID = 1L;

}
