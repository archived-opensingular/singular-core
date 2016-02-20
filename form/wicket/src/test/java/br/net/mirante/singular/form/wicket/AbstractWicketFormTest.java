package br.net.mirante.singular.form.wicket;

import org.junit.Before;

import br.net.mirante.singular.form.mform.SDictionary;
import br.net.mirante.singular.form.mform.RefSDictionary;

public abstract class AbstractWicketFormTest {

    protected SDictionary dicionario;

    @Before
    public void setUpDicionario() {
        dicionario = SDictionary.create();
        dicionario.setSerializableDictionarySelfReference(new RefSDictionary() {
            @Override
            public SDictionary retrieve() {
                throw new RuntimeException("Não deveria ter chamado. Era apenas para cumprir tabela.");
            }
        });
    }

}
