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

package org.opensingular.form;

import org.opensingular.form.type.core.SPackageCore;
import org.opensingular.form.type.core.attachment.SIAttachment;
import org.opensingular.form.type.core.attachment.STypeAttachment;

@SInfoType(name = "STypeAttachmentList", spackage = SPackageCore.class)
public class STypeAttachmentList extends STypeList<STypeAttachment, SIAttachment> {

    void setElementsTypeFieldName(String fieldName) {
        setElementsType(fieldName, STypeAttachment.class);
    }

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);
        asAtr().displayString(context -> {
            final StringBuilder displayString = new StringBuilder();
            if (context.instance() instanceof SIList) {
                ((SIList<?>) context.instance()).getChildren()
                        .stream()
                        .map(i -> (SIAttachment) i)
                        .map(SIAttachment::toStringDisplayDefault)
                        .forEach(name -> {
                            if (!displayString.toString().isEmpty()) {
                                displayString.append(", ");
                            }
                            displayString.append(name);
                        });
            }
            return displayString.toString();
        });
    }
}
