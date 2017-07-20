package org.opensingular.form.validation;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.opensingular.form.SDictionary;
import org.opensingular.form.type.core.SIString;
import org.opensingular.form.type.core.STypeString;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class InstanceValidatorExampleTest {

    private SIString sistring;
    private InstanceValidator<SIString> identificadorValidator;

    @Before
    public void setUp() throws Exception {
        sistring = SDictionary.create().newInstance(STypeString.class);
        identificadorValidator = (stringInstanceValidatable) -> {
            if (!stringInstanceValidatable.getInstance().matches("^[a-z][\\dA-Za-z]+$")) {
                stringInstanceValidatable.error("Não é um identificador valido");
            }
        };
    }

    @Test
    public void verificarSeNaoExisteErros1() throws Exception {
        verify(preencherValorEValidar("nomeProprio123"), never()).error(anyString());
    }

    @Test
    public void verificarSeNaoExisteErros2() throws Exception {
        verify(preencherValorEValidar("nomeProprio"), never()).error(anyString());
    }

    @Test
    public void verificarSeExisteErros1() throws Exception {
        verify(preencherValorEValidar("123nomeProprio")).error(anyString());
    }

    @Test
    public void verificarSeExisteErros2() throws Exception {
        verify(preencherValorEValidar("#nomeProprio")).error(anyString());
    }

    private InstanceValidatable<SIString> preencherValorEValidar(String nomeProprio123) {
        InstanceValidatable<SIString> instanceValidatable = Mockito.mock(InstanceValidatable.class);
        Mockito.when(instanceValidatable.getInstance()).thenReturn(sistring);
        sistring.setValue(nomeProprio123);
        identificadorValidator.validate(instanceValidatable);
        return instanceValidatable;
    }

}