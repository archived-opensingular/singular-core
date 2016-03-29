package br.net.mirante.singular.form.wicket.util;

import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.io.FormSerializationUtil;
import br.net.mirante.singular.form.mform.io.FormSerialized;
import org.apache.wicket.Page;

import java.io.Serializable;

public class FormStateUtil {

    public static FormState keepState(SInstance instance, Page curentPage) {
        final FormSerialized formSerialized = FormSerializationUtil.toSerializedObject(instance);
        return new FormState(formSerialized, curentPage);
    }

    public static SInstance restoreState(FormState state) {
        return FormSerializationUtil.toInstance(state.formSerialized);
    }

    public static class FormState implements Serializable {

        final FormSerialized formSerialized;
        final Page           page;

        FormState(FormSerialized formSerialized, Page page) {
            this.formSerialized = formSerialized;
            this.page = page;
        }

        public FormSerialized getFormSerialized() {
            return formSerialized;
        }
    }
}
