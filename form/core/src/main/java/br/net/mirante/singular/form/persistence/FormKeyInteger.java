package br.net.mirante.singular.form.persistence;

import org.apache.commons.lang3.StringUtils;

public class FormKeyInteger extends AbstractFormKey<Integer> {

    public FormKeyInteger(Integer value) {
        super(value);
    }

    public FormKeyInteger(String persistenceString) {
        super(persistenceString);
    }

    @Override
    protected Integer parseValuePersistenceString(String persistenceString) {
        try {
            return Integer.parseInt(persistenceString);
        } catch (Exception e) {
            throw new SingularFormPersistenceException("O valor da chave não é um inteiro válido", e).add("key",
                    persistenceString);
        }
    }
}
