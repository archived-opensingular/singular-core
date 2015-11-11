package br.net.mirante.singular.persistence.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import br.net.mirante.singular.persistence.util.Constants;


/**
 * The persistent class for the TB_GRUPO_PROCESSO database table.
 */
@Entity
@Table(name = "TB_GRUPO_PROCESSO", schema = Constants.SCHEMA)
public class ProcessGroupEntity extends AbstractProcessGroupEntity {

    private static final long serialVersionUID = 1L;

}