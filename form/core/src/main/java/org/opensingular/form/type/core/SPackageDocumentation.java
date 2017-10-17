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
