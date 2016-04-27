package br.net.mirante.singular.exemplos.util;

import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.converter.SInstanceConverter;
import br.net.mirante.singular.form.mform.util.transformer.Value;
import org.apache.commons.lang3.tuple.Pair;


public class PairConverter implements SInstanceConverter<Pair> {

    private final String left;
    private final String right;

    public PairConverter(SType left, SType right) {
        this.left = left.getName();
        this.right = right.getName();
    }

    @Override
    public void fillInstance(SInstance ins, Pair obj) {
        ((SIComposite) ins).setValue(left, obj.getLeft());
        ((SIComposite) ins).setValue(right, obj.getRight());
    }

    @Override
    public Pair toObject(SInstance ins) {
        return Pair.of(
                Value.of(ins, left),
                Value.of(ins, right)
        );
    }

}
