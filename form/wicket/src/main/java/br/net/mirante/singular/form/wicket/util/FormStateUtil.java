package br.net.mirante.singular.form.wicket.util;

import br.net.mirante.singular.form.mform.SInstance;

import java.io.Serializable;

import static br.net.mirante.singular.form.mform.util.transformer.Value.*;

public class FormStateUtil {

    public static FormState keepState(SInstance instance) {
        return new FormState(dehydrate(instance));
    }

    public static void restoreState(final SInstance instance, final FormState state) {
        instance.clearInstance();
        hydrate(instance, state.value);
    }

    public static class FormState implements Serializable {
        final Content value;

        FormState(Content value) {
            this.value = value;
        }
    }
}
