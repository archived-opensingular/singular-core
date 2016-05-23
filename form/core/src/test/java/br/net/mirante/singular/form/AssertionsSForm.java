package br.net.mirante.singular.form;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Classe de apoio a a escrita de asserções referente ao Singular Form.
 *
 * @author Daniel C. Bordin
 */
public class AssertionsSForm {

    private AssertionsSForm() {
    }

    public static AssertionsSType assertType(SType<?> type) {
        return new AssertionsSType(type);
    }

}
