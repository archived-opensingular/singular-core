package br.net.mirante.singular.form.mform.builder.selection;

import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.converter.SInstanceConverter;
import br.net.mirante.singular.form.mform.provider.FreemarkerUtil;
import br.net.mirante.singular.form.mform.util.transformer.Value;

import java.util.HashMap;


public class MapSelectionDisplayBuilder extends AbstractBuilder {

    public MapSelectionDisplayBuilder(SType type) {
        super(type);
    }

    public MapProviderBuilder display(SType id) {
        String simpleName = id.getNameSimple();
        type.asAtrProvider().asAtrProvider().displayFunction((map) -> ((HashMap) map).get(simpleName));
        addConverter();
        return new MapProviderBuilder(super.type);
    }

    public MapProviderBuilder display(String freemakerTemplate) {
        type.asAtrProvider().asAtrProvider().displayFunction((map) -> FreemarkerUtil.mergeWithFreemarker(freemakerTemplate, map));
        addConverter();
        return new MapProviderBuilder(super.type);
    }

    private void addConverter(){
        type.asAtrProvider().asAtrProvider().converter(new SInstanceConverter<HashMap, SIComposite>() {
            @Override
            public void fillInstance(SIComposite ins, HashMap obj) {
                Value.hydrate(ins, obj);
            }

            @Override
            public HashMap toObject(SIComposite ins) {
                return (HashMap) Value.dehydrate(ins);
            }
        });
    }


}
