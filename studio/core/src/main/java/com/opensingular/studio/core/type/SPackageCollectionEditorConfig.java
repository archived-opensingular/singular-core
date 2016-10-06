package com.opensingular.studio.core.type;

import org.opensingular.form.AtrRef;
import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SInfoPackage;
import org.opensingular.form.SPackage;
import org.opensingular.form.STypeSimple;
import org.opensingular.form.type.core.SIBoolean;
import org.opensingular.form.type.core.STypeBoolean;

@SInfoPackage(name= "singular.studio.core")
public class SPackageCollectionEditorConfig extends SPackage {

    public static final AtrRef<STypeBoolean, SIBoolean, Boolean> ATR_DEFAULT_SEARCH_CRITERIA = new AtrRef<>(SPackageCollectionEditorConfig.class, "defaultSearchCriteria", STypeBoolean.class, SIBoolean.class, Boolean.class);

    @Override
    protected void onLoadPackage(PackageBuilder pb) {
        pb.createAttributeIntoType(STypeSimple.class, ATR_DEFAULT_SEARCH_CRITERIA);
    }
}
