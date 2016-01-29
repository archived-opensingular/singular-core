package br.net.mirante.singular.form.mform.core.attachment;

import br.net.mirante.singular.form.mform.AtrRef;
import br.net.mirante.singular.form.mform.MInfoTipo;
import br.net.mirante.singular.form.mform.STypeComposto;
import br.net.mirante.singular.form.mform.TipoBuilder;
import br.net.mirante.singular.form.mform.core.SIString;
import br.net.mirante.singular.form.mform.core.SPackageCore;
import br.net.mirante.singular.form.mform.core.STypeString;

@MInfoTipo(nome = "Attachment", pacote = SPackageCore.class)
public class STypeAttachment extends STypeComposto<SIAttachment> {

    public static final String          FIELD_NAME        = "name",
                                        FIELD_FILE_ID     = "fileId",
                                        FIELD_SIZE        = "size",
                                        FIELD_HASH_SHA1   = "hashSHA1";

    public static final AtrRef<STypeString, SIString, String>    ATR_ORIGINAL_ID     = new AtrRef<>(SPackageCore.class, "originalId", STypeString.class, SIString.class, String.class);
    public static final AtrRef<STypeString, SIString, String>    ATR_IS_TEMPORARY    = new AtrRef<>(SPackageCore.class, "IS_TEMPORARY", STypeString.class, SIString.class, String.class);
    
    public STypeAttachment() {
        super(SIAttachment.class);
    }

    @Override
    protected void onLoadType(TipoBuilder tb) {
        super.onLoadType(tb);

        addCampoString(FIELD_FILE_ID);
        addCampoString(FIELD_NAME);
        addCampoString(FIELD_HASH_SHA1);
        addCampoInteger(FIELD_SIZE);
    }

}
