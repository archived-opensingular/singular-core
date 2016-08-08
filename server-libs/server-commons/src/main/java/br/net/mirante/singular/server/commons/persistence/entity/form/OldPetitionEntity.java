package br.net.mirante.singular.server.commons.persistence.entity.form;

import javax.persistence.Entity;
import javax.persistence.Table;

import br.net.mirante.singular.support.persistence.util.Constants;

@Deprecated
@Entity
@Table(schema = Constants.SCHEMA, name = "TB_PETICAO_OLD")
public class OldPetitionEntity extends AbstractOldPetitionEntity {

}
