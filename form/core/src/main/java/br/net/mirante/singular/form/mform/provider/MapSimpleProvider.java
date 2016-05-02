package br.net.mirante.singular.form.mform.provider;

import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.util.transformer.SCompositeListBuilder;
import br.net.mirante.singular.form.mform.util.transformer.Value;
import br.net.mirante.singular.form.mform.util.transformer.Value.Content;

import java.util.ArrayList;
import java.util.List;

public interface MapSimpleProvider extends SimpleProvider<Content, SIComposite> {

    @Override
    default public List<Content> load(SIComposite ins) {
        final SCompositeListBuilder builder = new SCompositeListBuilder((STypeComposite<SIComposite>) ins.getType());
        fill(builder);
        final List<Content> listMap = new ArrayList<>();
        builder.getList().forEach(i -> listMap.add(Value.dehydrate(i)));
        return listMap;
    }

    void fill(SCompositeListBuilder builder);

}