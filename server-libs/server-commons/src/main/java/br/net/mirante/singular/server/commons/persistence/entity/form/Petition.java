package br.net.mirante.singular.server.commons.persistence.entity.form;

import javax.persistence.Entity;
import javax.persistence.Table;

import br.net.mirante.singular.support.persistence.util.Constants;

@Entity
@Table(schema = Constants.SCHEMA, name = "TB_PETICAO")
public class Petition extends AbstractPetitionEntity {

}