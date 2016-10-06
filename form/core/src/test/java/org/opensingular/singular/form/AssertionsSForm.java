package org.opensingular.singular.form;

import org.opensingular.form.SInstance;
import org.opensingular.form.SType;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Classe de apoio a a escrita de asserções referente ao Singular Form.
 *
 * @author Daniel C. Bordin
 */
public class AssertionsSForm {

    private AssertionsSForm() {
    }

    /** Cria assertivas para um {@link SType}. */
    public static AssertionsSType assertType(SType<?> type) {
        return new AssertionsSType(type);
    }

    /** Cria assertivas para um {@link SInstance}. */
    public static AssertionsSInstance assertInstance(SInstance instance) {
        return new AssertionsSInstance(instance);
    }

}
