package br.net.mirante.singular.studio.core.type;

import br.net.mirante.singular.form.*;
import br.net.mirante.singular.form.type.core.SIBoolean;
import br.net.mirante.singular.form.type.core.STypeBoolean;

@SInfoPackage(name= "singular.studio.core")
public class SPackageCollectionEditorConfig extends SPackage {

    public static final AtrRef<STypeBoolean, SIBoolean, Boolean> ATR_DEFAULT_SEARCH_CRITERIA = new AtrRef<>(SPackageCollectionEditorConfig.class, "defaultSearchCriteria", STypeBoolean.class, SIBoolean.class, Boolean.class);

    @Override
    protected void onLoadPackage(PackageBuilder pb) {
        pb.createAttributeIntoType(STypeSimple.class, ATR_DEFAULT_SEARCH_CRITERIA);
    }
}
