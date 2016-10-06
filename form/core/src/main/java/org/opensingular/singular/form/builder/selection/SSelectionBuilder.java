package org.opensingular.singular.form.builder.selection;

import org.opensingular.singular.commons.lambda.IFunction;
import org.opensingular.singular.form.SIComposite;
import org.opensingular.singular.form.SInstance;
import org.opensingular.singular.form.SType;
import org.opensingular.singular.form.STypeList;
import org.opensingular.singular.form.util.transformer.Value;

import static org.opensingular.singular.form.util.transformer.Value.Content;

public class SSelectionBuilder extends AbstractBuilder {

    public SSelectionBuilder(SType type) {
        super(type);
    }

    public SSelectionDisplayBuilder selfId() {
        return id(type);
    }

    public SProviderBuilder selfIdAndDisplay() {
        return selfId().selfDisplay();
    }

    public SSelectionDisplayBuilder id(SType id) {
        type.asAtrProvider().asAtrProvider().idFunction(new IFunction<Value.Content, String>() {
            @Override
            public String apply(Content content) {
                final SType elementsType;
                if (type instanceof STypeList) {
                    elementsType = ((STypeList) type).getElementsType();
                } else {
                    elementsType = type;
                }
                final SInstance ins = elementsType.newInstance();
                Value.hydrate(ins, content);
                if (ins instanceof SIComposite) {
                    return String.valueOf(((SIComposite) ins).getValue(id));
                }
                return String.valueOf(ins.getValue());
            }
        });
        return new SSelectionDisplayBuilder(super.type);
    }

}
