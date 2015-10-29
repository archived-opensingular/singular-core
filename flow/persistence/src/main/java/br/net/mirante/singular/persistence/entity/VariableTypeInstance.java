package br.net.mirante.singular.persistence.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import br.net.mirante.singular.persistence.util.Constants;

/**
 * The persistent class for the TB_TIPO_VARIAVEL database table.
 */
@Entity
@Table(name = "TB_TIPO_VARIAVEL", schema = Constants.SCHEMA)
@GenericGenerator(name = AbstractVariableTypeEntity.PK_GENERATOR_NAME, strategy = "org.hibernate.id.IdentityGenerator")
public class VariableTypeInstance extends AbstractVariableTypeEntity {
    private static final long serialVersionUID = 1L;

}
