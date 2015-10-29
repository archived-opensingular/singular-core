package br.net.mirante.singular.persistence.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import br.net.mirante.singular.persistence.util.Constants;

/**
 * The persistent class for the TB_TRANSICAO database table.
 */
@Entity
@GenericGenerator(name = AbstractTaskTransitionVersionEntity.PK_GENERATOR_NAME, strategy = "org.hibernate.id.IdentityGenerator")
@Table(name = "TB_VERSAO_TRANSICAO", schema = Constants.SCHEMA)
public class TaskTransitionVersionEntity extends AbstractTaskTransitionVersionEntity<TaskVersionEntity> {
    private static final long serialVersionUID = 1L;
}
