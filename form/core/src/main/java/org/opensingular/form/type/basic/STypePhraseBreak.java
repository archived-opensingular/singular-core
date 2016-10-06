package org.opensingular.form.type.basic;

import org.opensingular.form.STypeSimple;
import org.opensingular.form.enums.PhraseBreak;
import org.opensingular.form.SInfoType;

@SInfoType(name = "STypePhraseBreak", spackage = SPackageBasic.class)
public class STypePhraseBreak extends STypeSimple<SIPhraseBreak, PhraseBreak> {

    public STypePhraseBreak() {
        super(SIPhraseBreak.class, PhraseBreak.class);
    }

}