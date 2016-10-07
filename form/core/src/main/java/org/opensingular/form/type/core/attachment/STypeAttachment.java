/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
