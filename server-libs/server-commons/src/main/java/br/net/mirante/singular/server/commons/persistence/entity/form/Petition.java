package br.net.mirante.singular.server.commons.persistence.entity.form;

import br.net.mirante.singular.persistence.util.Constants;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(schema = Constants.SCHEMA, name = "TB_PETICAO")
public class Petition extends AbstractPetitionEntity {

}
