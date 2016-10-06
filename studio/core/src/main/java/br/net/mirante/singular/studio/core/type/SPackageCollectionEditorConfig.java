package br.net.mirante.singular.studio.core.type;

import org.opensingular.singular.form.AtrRef;
import org.opensingular.singular.form.PackageBuilder;
import org.opensingular.singular.form.SInfoPackage;
import org.opensingular.singular.form.SPackage;
import org.opensingular.singular.form.STypeSimple;
import org.opensingular.singular.form.type.core.SIBoolean;
import org.opensingular.singular.form.type.core.STypeBoolean;

@SInfoPackage(name= "singular.studio.core")
public class SPackageCollectionEditorConfig extends SPackage {

    public static final AtrRef<STypeBoolean, SIBoolean, Boolean> ATR_DEFAULT_SEARCH_CRITERIA = new AtrRef<>(SPackageCollectionEditorConfig.class, "defaultSearchCriteria", STypeBoolean.class, SIBoolean.class, Boolean.class);

    @Override
    protected void onLoadPackage(PackageBuilder pb) {
        pb.createAttributeIntoType(STypeSimple.class, ATR_DEFAULT_SEARCH_CRITERIA);
    }
}
