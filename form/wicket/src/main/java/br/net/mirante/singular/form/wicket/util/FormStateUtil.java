package br.net.mirante.singular.form.wicket.util;

import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.util.transformer.Value;

import java.io.Serializable;

public class FormStateUtil {

    public static FormState keepState(SInstance instance) {
        return new FormState(Value.dehydrate(instance));
    }

    public static void restoreState(final SInstance instance, final FormState state) {
        instance.clearInstance();
        Value.hydrate(instance, state.value);
    }

    public static class FormState implements Serializable {
        final Object value;
        FormState(Object value) {
            this.value = value;
        }
    }
}
