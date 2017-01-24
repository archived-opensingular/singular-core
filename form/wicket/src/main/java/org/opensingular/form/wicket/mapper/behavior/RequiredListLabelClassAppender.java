package org.opensingular.form.wicket.mapper.behavior;

import org.apache.wicket.ClassAttributeModifier;
import org.apache.wicket.model.IModel;
import org.opensingular.form.SInstance;
import org.opensingular.form.type.basic.SPackageBasic;

import java.util.Set;

public class RequiredListLabelClassAppender extends ClassAttributeModifier {

    private final IModel<? extends SInstance> model;

    public RequiredListLabelClassAppender(IModel<? extends SInstance> model) {
        this.model = model;
    }

    @Override
    protected Set<String> update(Set<String> oldClasses) {
        final Boolean required    = model.getObject().getAttributeValue(SPackageBasic.ATR_REQUIRED);
        final Integer minimumSize = model.getObject().getAttributeValue(SPackageBasic.ATR_MINIMUM_SIZE);
        if ((required != null && required) || (minimumSize != null && minimumSize > 0)) {
            oldClasses.add("singular-form-required");
        } else {
            oldClasses.remove("singular-form-required");
        }
        return oldClasses;
    }

}
