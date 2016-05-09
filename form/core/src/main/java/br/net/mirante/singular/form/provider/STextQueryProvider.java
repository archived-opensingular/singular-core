package br.net.mirante.singular.form.provider;

import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.STypeComposite;
import br.net.mirante.singular.form.util.transformer.SCompositeListBuilder;
import br.net.mirante.singular.form.util.transformer.Value;

import java.util.ArrayList;
import java.util.List;

public interface STextQueryProvider extends TextQueryProvider<Value.Content, SIComposite> {

    @Override
    default List<Value.Content> load(SIComposite ins, String query) {
        final SCompositeListBuilder builder = new SCompositeListBuilder((STypeComposite<SIComposite>) ins.getType(), ins);
        fill(builder, query);
        final List<Value.Content> listMap = new ArrayList<>();
        builder.getList().forEach(i -> listMap.add(Value.dehydrate(i)));
        return listMap;
    }

    void fill(SCompositeListBuilder builder, String query);

}