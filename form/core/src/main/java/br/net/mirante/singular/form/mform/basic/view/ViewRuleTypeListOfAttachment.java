package br.net.mirante.singular.form.mform.basic.view;

import br.net.mirante.singular.form.mform.SIList;
import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.core.attachment.STypeAttachment;


class ViewRuleTypeListOfAttachment extends ViewRule {

    @Override
    public SView apply(SInstance instance) {
        if (instance instanceof SIList) {
            SIList<?> listType    = (SIList<?>) instance;
            SType<?>  elementType = listType.getElementsType();
            if (elementType instanceof STypeAttachment) {
                return new SViewAttachmentList();
            }
        }
        return null;
    }

}
