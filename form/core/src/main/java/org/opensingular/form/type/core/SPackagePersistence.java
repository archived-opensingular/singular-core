package org.opensingular.form.type.core;

import org.opensingular.form.AtrRef;
import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SIPredicate;
import org.opensingular.form.SInstance;
import org.opensingular.form.SPackage;
import org.opensingular.form.SType;
import org.opensingular.form.STypePredicate;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

@SuppressWarnings("unchecked")
public class SPackagePersistence extends SPackage {

    public static final AtrRef<STypeBoolean, SIBoolean, Boolean>                  ATR_PERSISTENT = new AtrRef<>(SPackagePersistence.class, "persistent", STypeBoolean.class, SIBoolean.class, Boolean.class);
    public static final AtrRef<STypeString, SIString, String>                     ATR_ALIAS      = new AtrRef<>(SPackagePersistence.class, "alias", STypeString.class, SIString.class, String.class);
    public static final AtrRef<STypePredicate, SIPredicate, Predicate<SInstance>> ATR_XML        = new AtrRef(SPackagePersistence.class, "xmlOpts", STypePredicate.class, SIPredicate.class, Predicate.class);

    @Override
    protected void onLoadPackage(@Nonnull PackageBuilder pb) {
        pb.createAttributeIntoType(SType.class, ATR_PERSISTENT);
        pb.createAttributeIntoType(SType.class, ATR_XML);
        pb.createAttributeIntoType(SType.class, ATR_ALIAS);
    }
}
