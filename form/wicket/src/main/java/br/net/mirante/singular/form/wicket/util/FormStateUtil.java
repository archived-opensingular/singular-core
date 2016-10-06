package br.net.mirante.singular.form.wicket.util;

import org.opensingular.singular.form.SInstance;
import org.opensingular.singular.form.util.transformer.Value;

import java.io.Serializable;

import static org.opensingular.singular.form.util.transformer.Value.dehydrate;
import static org.opensingular.singular.form.util.transformer.Value.hydrate;

public class FormStateUtil {

    public static FormState keepState(SInstance instance) {
        return new FormState(dehydrate(instance));
    }

    public static void restoreState(final SInstance instance, final FormState state) {
        instance.clearInstance();
        hydrate(instance, state.value);
    }

    public static class FormState implements Serializable {
        final Value.Content value;

        FormState(Value.Content value) {
            this.value = value;
        }
    }
}
