/*
 *
 *  * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.opensingular.form.type.core;

import org.opensingular.form.AtrRef;
import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SIPredicate;
import org.opensingular.form.SPackage;
import org.opensingular.form.SType;
import org.opensingular.form.STypePredicate;
import org.opensingular.form.STypes;
import org.opensingular.form.type.basic.AtrDOC;
import org.opensingular.form.type.core.attachment.STypeAttachment;
import org.opensingular.form.type.country.brazil.STypeUF;

import javax.annotation.Nonnull;

@SuppressWarnings("unchecked")
public class SPackageDocumentation extends SPackage {

    public static final AtrRef<STypePredicate, SIPredicate, Boolean> ATR_DOC_HIDDEN = new AtrRef(SPackageDocumentation.class, "hiddenInDocs", STypePredicate.class, SIPredicate.class, Boolean.class);

    @Override
    protected void onLoadPackage(@Nonnull PackageBuilder pb) {
        pb.createAttributeIntoType(SType.class, ATR_DOC_HIDDEN);


        /* Hide all subtypes from documentation engine*/
        STypes.streamDescendants(pb.getType(STypeAttachment.class), false).forEach(s -> s.as(AtrDOC::new).hiddenForDocumentation());
        STypes.streamDescendants(pb.getType(STypeUF.class), false).forEach(s -> s.as(AtrDOC::new).hiddenForDocumentation());
        /* Hide element subtype from documentation engine*/
//        pb.getType(STypeAttachmentList.class).getElementsType().as(AtrDOC::new).hiddenForDocumentation();
    }
}
