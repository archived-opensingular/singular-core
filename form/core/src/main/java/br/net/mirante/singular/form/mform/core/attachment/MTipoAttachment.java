package br.net.mirante.singular.form.mform.core.attachment;

import br.net.mirante.singular.form.mform.AtrRef;
import br.net.mirante.singular.form.mform.MInfoTipo;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.TipoBuilder;
import br.net.mirante.singular.form.mform.core.MIString;
import br.net.mirante.singular.form.mform.core.MPacoteCore;
import br.net.mirante.singular.form.mform.core.MTipoString;

@MInfoTipo(nome = "Attachment", pacote = MPacoteCore.class)
public class MTipoAttachment extends MTipoComposto<MIAttachment> {

    public static final String          FIELD_NAME        = "name",
                                        FIELD_FILE_ID     = "fileId",
                                        FIELD_SIZE        = "size",
                                        FIELD_HASH_SHA1   = "hashSHA1";

    public static final AtrRef<MTipoString, MIString, String>    ATR_ORIGINAL_ID     = new AtrRef<>(MPacoteCore.class, "originalId", MTipoString.class, MIString.class, String.class);
    public static final AtrRef<MTipoString, MIString, String>    ATR_IS_TEMPORARY    = new AtrRef<>(MPacoteCore.class, "IS_TEMPORARY", MTipoString.class, MIString.class, String.class);
    
    public MTipoAttachment() {
        super(MIAttachment.class);
    }

    @Override
    protected void onCargaTipo(TipoBuilder tb) {
        super.onCargaTipo(tb);

        addCampoString(FIELD_FILE_ID);
        addCampoString(FIELD_NAME);
        addCampoString(FIELD_HASH_SHA1);
        addCampoInteger(FIELD_SIZE);
    }

}
