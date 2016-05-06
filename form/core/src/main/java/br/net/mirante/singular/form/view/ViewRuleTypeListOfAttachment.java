package br.net.mirante.singular.form.view;

import br.net.mirante.singular.form.SIList;
import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.SType;
import br.net.mirante.singular.form.type.core.attachment.STypeAttachment;


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
