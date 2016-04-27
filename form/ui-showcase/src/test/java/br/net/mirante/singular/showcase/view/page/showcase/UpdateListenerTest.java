package br.net.mirante.singular.showcase.view.page.showcase;

import static org.junit.Assert.assertEquals;

import java.util.Optional;

import org.apache.wicket.markup.html.form.FormComponent;
import org.junit.Test;

import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.core.SIString;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.wicket.helpers.SingularFormBaseTest;

public class UpdateListenerTest extends SingularFormBaseTest {

    private STypeString logradouro;
    private STypeString cep;

    @Override
    protected void buildBaseType(STypeComposite<?> baseType) {

        baseType.asAtrBasic().label("Endereço");
        cep = baseType.addFieldString("cep");
        cep.asAtrBasic().tamanhoMaximo(8).label("CEP (Use os valores 70863520 ou 70070120)");
        logradouro = baseType.addFieldString("logradouro");
        logradouro
                .asAtrBasic().enabled(false)
                .label("Logradouro")
                .dependsOn(cep);

        logradouro.withUpdateListener(i -> {
            final Optional<SIString> cepField = i.findNearest(cep);
            cepField.ifPresent(c -> {
                if (c.getValue().equalsIgnoreCase("70863520")) {
                    i.setValue("CLN 211 Bloco 'B' Subsolo");
                } else if (c.getValue().equalsIgnoreCase("70070120")) {
                    i.setValue("SBS - Qd. 02 - Bl. Q - Centro Empresarial João Carlos Saad 12° andar");
                } else {
                    i.setValue("Não encontrado");
                }
            });
        });

    }

    @Test
    public void testarUpdateListenerCEPValido() {
        final FormComponent logradouro = findFirstFormComponentsByType(page.getForm(), this.logradouro);
        final FormComponent cep = findFirstFormComponentsByType(page.getForm(), this.cep);

        form.setValue(cep, "70863520");
        tester.executeAjaxEvent(cep, "change");
        assertEquals("Logradouro incorreto",
                logradouro.getDefaultModelObjectAsString(), "CLN 211 Bloco 'B' Subsolo");

        form.setValue(cep, "70070120");
        tester.executeAjaxEvent(cep, "change");
        assertEquals("Logradouro incorreto",
                logradouro.getDefaultModelObjectAsString(), "SBS - Qd. 02 - Bl. Q - Centro Empresarial João Carlos Saad 12° andar");
    }


    @Test
    public void testarUpdateListenerCEPInvalido() {
        final FormComponent logradouro = findFirstFormComponentsByType(page.getForm(), this.logradouro);
        final FormComponent cep = findFirstFormComponentsByType(page.getForm(), this.cep);

        form.setValue(cep, "12345678");
        tester.executeAjaxEvent(cep, "change");
        assertEquals("Logradouro incorreto",
                logradouro.getDefaultModelObjectAsString(), "Não encontrado");
    }

}
