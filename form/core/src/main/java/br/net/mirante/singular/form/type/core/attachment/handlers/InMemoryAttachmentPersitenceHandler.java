/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.type.core.attachment.handlers;

import br.net.mirante.singular.commons.base.SingularException;
import br.net.mirante.singular.form.type.core.attachment.IAttachmentRef;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * Implementação manipulador de anexo que guarda tudo em arquivos temporários
 * </p>>
 *
 * @author Daniel C. Bordin
 */
@SuppressWarnings("serial")
public class InMemoryAttachmentPersitenceHandler extends FileSystemAttachmentHandler {

    private Map<String, IAttachmentRef> attachments = new HashMap<>();

    public InMemoryAttachmentPersitenceHandler() {
        super(StringUtils.isEmpty(System.getProperty("java.io.tmpdir")) ? "./tmp" : System.getProperty("java.io.tmpdir"));
    }

    @Override
    public IAttachmentRef copy(IAttachmentRef toBeCopied) {
        IAttachmentRef ref = super.copy(toBeCopied);
        attachments.put(ref.getId(), ref);
        return ref;
    }

    @Override
    public IAttachmentRef addAttachment(File file, long length) {
        IAttachmentRef ref = super.addAttachment(file, length);
        attachments.put(ref.getId(), ref);
        return ref;
    }

    @Override
    public void deleteAttachment(String fileId) {
        super.deleteAttachment(fileId);
        attachments.remove(fileId);
    }

    @Override
    public IAttachmentRef getAttachment(String fileId) {
        return attachments.get(fileId);
    }

    @Override
    public Collection<? extends IAttachmentRef> getAttachments() {
        return Collections.unmodifiableCollection(attachments.values());
    }

    @Override
    protected File findFileFromId(String fileId) {
        try {
            File f = File.createTempFile("tmp_handler", fileId);
            f.deleteOnExit();
            return f;
        } catch (Exception e) {
            throw new SingularException(e);
        }
    }
}