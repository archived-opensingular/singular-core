package br.net.mirante.singular.server.commons.persistence.entity.form.attachment;

import br.net.mirante.singular.form.type.core.attachment.IAttachmentRef;
import br.net.mirante.singular.persistence.util.Constants;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(schema = Constants.SCHEMA, name = "TB_ARQUIVO_PETICAO")
public class Attachment extends AbstractAttachmentEntity implements IAttachmentRef {


}
