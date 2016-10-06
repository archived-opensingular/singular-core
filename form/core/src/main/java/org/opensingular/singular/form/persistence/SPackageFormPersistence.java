package org.opensingular.singular.form.persistence;

import br.net.mirante.singular.form.*;
import org.opensingular.singular.form.AtrRef;
import org.opensingular.singular.form.PackageBuilder;
import org.opensingular.singular.form.SDictionary;
import org.opensingular.singular.form.SISimple;
import org.opensingular.singular.form.SInfoPackage;
import org.opensingular.singular.form.SPackage;
import org.opensingular.singular.form.STypeComposite;

/**
 * Pacote com atributos e tipos para apoio na persistência de Collections de instâncias.
 *
 * @author Daniel C. Bordin
 */
@SInfoPackage(name = SDictionary.SINGULAR_PACKAGES_PREFIX + "persitence")
public class SPackageFormPersistence extends SPackage {

    public static final AtrRef<STypeFormKey, SISimple, FormKey> ATR_FORM_KEY = new AtrRef<>(SPackageFormPersistence.class,
            "formKey", STypeFormKey.class, SISimple.class, FormKey.class);

    protected void onLoadPackage(PackageBuilder pb) {
        pb.createType(STypeFormKey.class);
        pb.createAttributeIntoType(STypeComposite.class, ATR_FORM_KEY);
        pb.createType(STypePersistentComposite.class);
    }
}
