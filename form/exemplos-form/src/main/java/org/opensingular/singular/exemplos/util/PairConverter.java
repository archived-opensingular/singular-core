package org.opensingular.singular.exemplos.util;

import org.opensingular.singular.form.SIComposite;
import org.opensingular.singular.form.SType;
import org.opensingular.singular.form.converter.SInstanceConverter;
import org.opensingular.singular.form.util.transformer.Value;
import org.apache.commons.lang3.tuple.Pair;


public class PairConverter implements SInstanceConverter<Pair, SIComposite> {

    private final String left;
    private final String right;

    public PairConverter(SType left, SType right) {
        this.left = left.getNameSimple();
        this.right = right.getNameSimple();
    }

    @Override
    public void fillInstance(SIComposite ins, Pair obj) {
        ins.setValue(left, obj.getLeft());
        ins.setValue(right, obj.getRight());
    }

    @Override
    public Pair toObject(SIComposite ins) {
        return Pair.of(
                Value.of(ins, left),
                Value.of(ins, right)
        );
    }

}
