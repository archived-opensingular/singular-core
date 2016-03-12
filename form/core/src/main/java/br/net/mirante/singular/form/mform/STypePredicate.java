package br.net.mirante.singular.form.mform;

import java.util.function.Predicate;

import br.net.mirante.singular.form.mform.core.SPackageCore;

@SInfoType(name = "MTipoPredicate", spackage = SPackageCore.class)
public class STypePredicate extends STypeCode<SIPredicate, Predicate<SInstance>> {

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public STypePredicate() {
        super((Class) SIPredicate.class, (Class) Predicate.class);
    }
}
