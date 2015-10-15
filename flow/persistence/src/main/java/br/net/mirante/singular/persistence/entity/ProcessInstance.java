package br.net.mirante.singular.persistence.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import br.net.mirante.singular.persistence.util.Constants;

/**
 * The persistent class for the TB_INSTANCIA_PROCESSO database table.
 */
@Entity
@GenericGenerator(name = "generated_demanda_id", strategy = "org.hibernate.id.IdentityGenerator")
@Table(name = "TB_INSTANCIA_PROCESSO", schema = Constants.SCHEMA)
public class ProcessInstance extends ProcessInstanceBase<Actor, Process, TaskInstance, Variable, RoleInstance, ExecutionVariable> {
    private static final long serialVersionUID = 1L;

}
