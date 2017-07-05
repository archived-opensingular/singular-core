package org.opensingular.form.validation;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.opensingular.form.type.core.SIString;

@RunWith(MockitoJUnitRunner.class)
public class InstanceValidatorExampleTest {

    private InstanceValidator<SIString> identificadorValidator;

    @Mock
    private InstanceValidatable<SIString> instanceValidatable;

    @Mock
    private SIString siString;

    @Before
    public void setUp() throws Exception {
        identificadorValidator = (iv) -> {
            if (!iv.getInstance().getValue().matches("^[a-z][\\dA-Za-z]+$")) {
                instanceValidatable.error("Não é um identificador valido");
            }
        };
    }

    @Test
    public void verificarSeNaoExisteErros1() throws Exception {
        Mockito.when(instanceValidatable.getInstance()).thenReturn(siString);
        Mockito.when(siString.getValue()).thenReturn("nomeProprio123");
        identificadorValidator.validate(instanceValidatable);
        Mockito.verify(instanceValidatable).getInstance();
        Mockito.verifyNoMoreInteractions(instanceValidatable);
    }

    @Test
    public void verificarSeNaoExisteErros2() throws Exception {
        Mockito.when(instanceValidatable.getInstance()).thenReturn(siString);
        Mockito.when(siString.getValue()).thenReturn("nomeProprio");
        identificadorValidator.validate(instanceValidatable);
        Mockito.verify(instanceValidatable).getInstance();
        Mockito.verifyNoMoreInteractions(instanceValidatable);
    }

    @Test
    public void verificarSeExisteErros1() throws Exception {
        Mockito.when(instanceValidatable.getInstance()).thenReturn(siString);
        Mockito.when(siString.getValue()).thenReturn("123nomeProprio");
        identificadorValidator.validate(instanceValidatable);
        Mockito.verify(instanceValidatable).getInstance();
        Mockito.verify(instanceValidatable).error(Mockito.anyString());
    }

    @Test
    public void verificarSeExisteErros2() throws Exception {
        Mockito.when(instanceValidatable.getInstance()).thenReturn(siString);
        Mockito.when(siString.getValue()).thenReturn("#nomeProprio");
        identificadorValidator.validate(instanceValidatable);
        Mockito.verify(instanceValidatable).getInstance();
        Mockito.verify(instanceValidatable).error(Mockito.anyString());
    }

}