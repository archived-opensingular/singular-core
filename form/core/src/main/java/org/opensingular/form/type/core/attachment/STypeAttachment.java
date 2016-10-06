/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.form.type.core.attachment;

import java.util.Arrays;
import java.util.List;

import org.opensingular.form.AtrRef;
import org.opensingular.form.TypeBuilder;
import org.opensingular.form.type.core.SIString;
import org.opensingular.form.SInfoType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.type.core.SPackageCore;
import org.opensingular.form.type.core.STypeString;

@SInfoType(name = "Attachment", spackage = SPackageCore.class)
public class STypeAttachment extends STypeComposite<SIAttachment> {

    public static final List<String> INLINE_CONTENT_TYPES = Arrays.asList("application/pdf", "image/.*");


    public static final String FIELD_NAME = "name",
            FIELD_FILE_ID                 = "fileId",
            FIELD_SIZE                    = "size",
            FIELD_HASH_SHA1               = "hashSHA1";

    public static final AtrRef<STypeString, SIString, String> ATR_ORIGINAL_ID  = new AtrRef<>(SPackageCore.class, "originalId", STypeString.class, SIString.class, String.class);
    public static final AtrRef<STypeString, SIString, String> ATR_IS_TEMPORARY = new AtrRef<>(SPackageCore.class, "IS_TEMPORARY", STypeString.class, SIString.class, String.class);

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
