package br.net.mirante.singular.exemplos.util;

import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.converter.SInstanceConverter;
import br.net.mirante.singular.form.mform.util.transformer.Value;
import org.apache.commons.lang3.tuple.Triple;


public class TripleConverter implements SInstanceConverter<Triple> {

    private final String left;
    private final String middle;
    private final String right;

    public TripleConverter(SType left, SType middle, SType right) {
        this.left = left.getName();
        this.middle = middle.getName();
        this.right = right.getName();
    }

    @Override
    public void fillInstance(SInstance ins, Triple obj) {
        ((SIComposite) ins).setValue(left, obj.getLeft());
        ((SIComposite) ins).setValue(middle, obj.getMiddle());
        ((SIComposite) ins).setValue(right, obj.getRight());
    }

    @Override
    public Triple toObject(SInstance ins) {
        return Triple.of(
                Value.of(ins, left),
                Value.of(ins, middle),
                Value.of(ins, right)
        );
    }

}