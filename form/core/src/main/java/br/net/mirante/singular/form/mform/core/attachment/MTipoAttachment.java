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

    static final String         FIELD_NAME        = "name";
    static final String         FIELD_FILE_ID     = "fileId";
    static final String         FIELD_SIZE        = "size";
    static final String         FIELD_HASH_SHA1   = "hashSHA1";
    private static final String FIELD_ORIGINAL_ID = "originalId";

    static final AtrRef<MTipoString, MIString, String> REF_ORIGINAL_ID = new AtrRef<>(MPacoteCore.class, FIELD_ORIGINAL_ID,
        MTipoString.class, MIString.class, String.class);

    public MTipoAttachment() {
        super(MIAttachment.class);
    }

    @Override
    protected void onCargaTipo(TipoBuilder tb) {
        super.onCargaTipo(tb);

        // tb.createTipoAtributo(FIELD_ORIGINAL_ID, MTipoString.class);
        tb.createTipoAtributo(REF_ORIGINAL_ID);
        addCampoString(FIELD_FILE_ID);
        // addCampoString(FIELD_ORIGINAL_ID);
        addCampoString(FIELD_NAME);
        addCampoString(FIELD_HASH_SHA1);
        addCampoInteger(FIELD_SIZE);
    }
}
