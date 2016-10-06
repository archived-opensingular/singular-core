package org.opensingular.singular.form.view;

import org.opensingular.singular.form.SIList;
import org.opensingular.singular.form.SInstance;
import org.opensingular.singular.form.SType;
import org.opensingular.singular.form.type.core.attachment.STypeAttachment;


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
