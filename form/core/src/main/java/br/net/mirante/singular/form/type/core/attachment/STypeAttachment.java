/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.type.core.attachment;

import java.util.Arrays;
import java.util.List;

import br.net.mirante.singular.form.AtrRef;
import br.net.mirante.singular.form.SInfoType;
import br.net.mirante.singular.form.STypeComposite;
import br.net.mirante.singular.form.TypeBuilder;
import br.net.mirante.singular.form.type.core.SIString;
import br.net.mirante.singular.form.type.core.SPackageCore;
import br.net.mirante.singular.form.type.core.STypeString;

@SInfoType(name = "Attachment", spackage = SPackageCore.class)
public class STypeAttachment extends STypeComposite<SIAttachment> {

    public static final List<String>                          INLINE_CONTENT_TYPES = Arrays.asList("application/pdf", "image/.*");

    public static final String                                FIELD_NAME           = "name";
    public static final String                                FIELD_FILE_ID        = "fileId";
    public static final String                                FIELD_SIZE           = "size";
    public static final String                                FIELD_HASH_SHA1      = "hashSHA1";

    public static final AtrRef<STypeString, SIString, String> ATR_ORIGINAL_ID      = new AtrRef<>(SPackageCore.class, "originalId", STypeString.class, SIString.class, String.class);
    public static final AtrRef<STypeString, SIString, String> ATR_IS_TEMPORARY     = new AtrRef<>(SPackageCore.class, "IS_TEMPORARY", STypeString.class, SIString.class, String.class);

    public STypeAttachment() {
        super(SIAttachment.class);
    }

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);

        addFieldString(FIELD_FILE_ID);
        addFieldString(FIELD_NAME);
        addFieldString(FIELD_HASH_SHA1);
        addFieldInteger(FIELD_SIZE);
    }

}
