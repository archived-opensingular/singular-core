package br.net.mirante.singular.persistence.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import br.net.mirante.singular.persistence.util.HybridIdentityOrSequenceGenerator;
import org.hibernate.annotations.GenericGenerator;

import br.net.mirante.singular.persistence.util.Constants;

/**
 * The persistent class for the TB_VARIAVEL database table.
 */
@Entity
@GenericGenerator(name = AbstractVariableInstanceEntity.PK_GENERATOR_NAME, strategy = HybridIdentityOrSequenceGenerator.CLASS_NAME)
@Table(name = "TB_VARIAVEL", schema = Constants.SCHEMA)
public class VariableInstanceEntity extends AbstractVariableInstanceEntity<ProcessInstanceEntity, VariableTypeInstance> {
    private static final long serialVersionUID = 1L;

}
