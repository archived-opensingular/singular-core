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

import org.opensingular.form.type.basic.AtrDOC;
import org.opensingular.form.type.core.SPackageCore;
import org.opensingular.form.type.core.attachment.SIAttachment;
import org.opensingular.form.type.core.attachment.STypeAttachment;

@SInfoType(name = "STypeAttachmentList", spackage = SPackageCore.class)
public class STypeAttachmentList extends STypeList<STypeAttachment, SIAttachment> {

    void setElementsTypeFieldName(String fieldName) {
        setElementsType(fieldName, STypeAttachment.class);
        this.getElementsType().as(AtrDOC::new).hiddenForDocumentation();
    }

    @Override
    protected void onLoadType(TypeBuilder tb) {
        asAtr().displayString(context -> {
            final StringBuilder displayString = new StringBuilder();
            SInstance instance = context.instance();
            if (instance instanceof SIList) {
                ((SIList<?>) instance)
                        .stream()
                        .map(i -> (SIAttachment) i)
                        .map(SIAttachment::toStringDisplayDefault)
                        .forEach(name -> {
                            if (displayString.length() != 0) {
                                displayString.append(", ");
                            }
                            displayString.append(name);
                        });
            }
            return displayString.toString();
        });
    }
}
