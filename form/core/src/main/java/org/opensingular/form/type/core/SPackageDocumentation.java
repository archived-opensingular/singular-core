package org.opensingular.form.type.core;

import org.opensingular.form.*;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

@SuppressWarnings("unchecked")
public class SPackageDocumentation extends SPackage {

    public static final AtrRef<STypePredicate, SIPredicate, Boolean> ATR_DOC_HIDDEN        = new AtrRef(SPackageDocumentation.class, "hiddenInDocs", STypePredicate.class, SIPredicate.class, Boolean.class);

    @Override
    protected void onLoadPackage(@Nonnull PackageBuilder pb) {
        pb.createAttributeIntoType(SType.class, ATR_DOC_HIDDEN);
    }
}
