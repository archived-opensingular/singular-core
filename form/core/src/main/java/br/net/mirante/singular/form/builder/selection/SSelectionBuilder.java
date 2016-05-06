package br.net.mirante.singular.form.builder.selection;

import br.net.mirante.singular.commons.lambda.IFunction;
import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.SType;
import br.net.mirante.singular.form.STypeList;
import br.net.mirante.singular.form.util.transformer.Value;

import static br.net.mirante.singular.form.util.transformer.Value.Content;

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
