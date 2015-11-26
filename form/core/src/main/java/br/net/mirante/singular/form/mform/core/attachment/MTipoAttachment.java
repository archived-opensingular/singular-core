package br.net.mirante.singular.form.mform.core.attachment;

import br.net.mirante.singular.form.mform.MInfoTipo;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.MTipoSimples;
import br.net.mirante.singular.form.mform.TipoBuilder;
import br.net.mirante.singular.form.mform.core.MPacoteCore;

@MInfoTipo(nome = "Attachment", pacote = MPacoteCore.class)
public class MTipoAttachment extends MTipoComposto<MIAttachment> {

    static final String FIELD_NAME = "name";
    static final String FIELD_FILE_ID = "fileId";
    static final String FIELD_SIZE = "size";
    static final String FIELD_HASH_SHA1 = "hashSHA1";
    static final String FIELD_ORIGINAL_ID = "originalId";

    public MTipoAttachment() {
        super(MIAttachment.class);
    }

    @Override
    protected void onCargaTipo(TipoBuilder tb) {
        super.onCargaTipo(tb);
        
        tb.createTipoAtributo(FIELD_ORIGINAL_ID, MTipoSimples.class);
        addCampoString(FIELD_FILE_ID);
//        addCampoString(FIELD_ORIGINAL_ID);
        addCampoString(FIELD_NAME, true);
        addCampoString(FIELD_HASH_SHA1);
        addCampoInteger(FIELD_SIZE);
    }
    
    
}
