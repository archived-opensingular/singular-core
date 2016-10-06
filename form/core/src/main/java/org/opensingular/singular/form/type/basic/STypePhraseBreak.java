package org.opensingular.singular.form.type.basic;

import org.opensingular.singular.form.SInfoType;
import org.opensingular.singular.form.STypeSimple;
import org.opensingular.singular.form.enums.PhraseBreak;

@SInfoType(name = "STypePhraseBreak", spackage = SPackageBasic.class)
public class STypePhraseBreak extends STypeSimple<SIPhraseBreak, PhraseBreak> {

    public STypePhraseBreak() {
        super(SIPhraseBreak.class, PhraseBreak.class);
    }

}