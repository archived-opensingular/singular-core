/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.persistence.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import br.net.mirante.singular.form.type.core.attachment.IAttachmentRef;
import br.net.mirante.singular.support.persistence.util.Constants;

@Entity
@Table(schema = Constants.SCHEMA, name = "TB_ARQUIVO_PETICAO")
public class Attachment extends AbstractAttachmentEntity implements IAttachmentRef {


}
