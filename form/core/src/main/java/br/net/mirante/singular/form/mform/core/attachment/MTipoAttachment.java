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
    private static final String FIELD_ORIGINAL_ID = "originalId";

    static final AtrRef<MTipoString, MIString, String> REF_ORIGINAL_ID =
            new AtrRef<>(MPacoteCore.class, FIELD_ORIGINAL_ID,
                            MTipoString.class, MIString.class, String.class);
    static final AtrRef<MTipoString, MIString, String> IS_TEMPORARY =
            new AtrRef<>(MPacoteCore.class, "IS_TEMPORARY",
                MTipoString.class, MIString.class, String.class);

    public MTipoAttachment() {
        super(MIAttachment.class);
    }

    @Override
    protected void onCargaTipo(TipoBuilder tb) {
        super.onCargaTipo(tb);

        tb.createTipoAtributo(REF_ORIGINAL_ID);
        tb.createTipoAtributo(IS_TEMPORARY);
        addCampoString(FIELD_FILE_ID);
        addCampoString(FIELD_NAME);
        addCampoString(FIELD_HASH_SHA1);
        addCampoInteger(FIELD_SIZE);
    }

}
