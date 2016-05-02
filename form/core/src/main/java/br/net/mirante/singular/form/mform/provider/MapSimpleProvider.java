package br.net.mirante.singular.form.mform.provider;

import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.util.transformer.SCompositeListBuilder;
import br.net.mirante.singular.form.mform.util.transformer.Value;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface MapSimpleProvider extends SimpleProvider<HashMap<String, Object>, SIComposite> {

    @Override
    default public List<HashMap<String, Object>> load(SIComposite ins) {
        final SCompositeListBuilder builder = new SCompositeListBuilder((STypeComposite<SIComposite>) ins.getType());
        fill(builder);
        final List<HashMap<String, Object>> listMap = new ArrayList<>();
        builder.getList().forEach(i -> listMap.add((HashMap<String, Object>) Value.dehydrate(i)));
        return listMap;
    }

    void fill(SCompositeListBuilder builder);

}