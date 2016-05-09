package br.net.mirante.singular.form.provider;

import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.SIList;
import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.STypeComposite;
import br.net.mirante.singular.form.util.transformer.SCompositeListBuilder;
import br.net.mirante.singular.form.util.transformer.Value;
import br.net.mirante.singular.form.util.transformer.Value.Content;

import java.util.ArrayList;
import java.util.List;

public interface SSimpleProvider extends SimpleProvider<Content, SInstance> {

    @Override
    default List<Content> load(SInstance ins) {

        STypeComposite typeComposite = null;

        if (ins instanceof SIList) {
            typeComposite = (STypeComposite) ((SIList) ins).getElementsType();
        } else if (ins instanceof SIComposite) {
            typeComposite = (STypeComposite) ins.getType();
        }

        if (typeComposite == null) {
            throw new RuntimeException("Não foi possivel obter o tipo da instancia");
        }

        final SCompositeListBuilder builder = new SCompositeListBuilder(typeComposite, ins);
        final List<Content>         listMap = new ArrayList<>();

        fill(builder);
        builder.getList().forEach(i -> listMap.add(Value.dehydrate(i)));

        return listMap;
    }

    void fill(SCompositeListBuilder builder);

}