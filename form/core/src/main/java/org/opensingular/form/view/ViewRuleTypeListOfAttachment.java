package org.opensingular.form.view;

import org.opensingular.form.SIList;
import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.form.type.core.attachment.STypeAttachment;


class ViewRuleTypeListOfAttachment extends ViewRule {

    @Override
    public SView apply(SInstance instance) {
        if (instance instanceof SIList) {
            SIList<?> listType    = (SIList<?>) instance;
            SType<?> elementType = listType.getElementsType();
            if (elementType instanceof STypeAttachment) {
                return new SViewAttachmentList();
            }
        }
        return null;
    }

}
