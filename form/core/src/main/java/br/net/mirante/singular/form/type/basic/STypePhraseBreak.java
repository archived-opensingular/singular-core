package br.net.mirante.singular.form.type.basic;

import br.net.mirante.singular.form.SInfoType;
import br.net.mirante.singular.form.STypeSimple;
import br.net.mirante.singular.form.enums.PhraseBreak;

@SInfoType(name = "STypePhraseBreak", spackage = SPackageBasic.class)
public class STypePhraseBreak extends STypeSimple<SIPhraseBreak, PhraseBreak> {

    public STypePhraseBreak() {
        super(SIPhraseBreak.class, PhraseBreak.class);
    }

}