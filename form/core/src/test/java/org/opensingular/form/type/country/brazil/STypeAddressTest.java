package org.opensingular.form.type.country.brazil;

import org.junit.Assert;
import org.junit.Test;
import org.opensingular.form.SDictionary;
import org.opensingular.form.SIComposite;

public class STypeAddressTest {

    @Test
    public void testName() throws Exception {
        STypeAddress address = SDictionary.create().getType(STypeAddress.class);
        SIComposite siAddress1 = address.newInstance();
        SIComposite siAddress2 = address.newInstance();

        siAddress1.setValue(address.bairro, "noroeste");
        siAddress1.setValue(address.cep, "70386345");
        siAddress1.setValue(address.cidade, "brasilia");
        address.estado.fillDF(siAddress1.getField(address.estado));
        siAddress1.setValue(address.complemento, "nada");
        siAddress1.setValue(address.logradouro, "muito louco");
        siAddress1.setValue(address.pais, "brasil");

        siAddress2.setValue(siAddress1);

        Assert.assertEquals(siAddress1.getValue(address.bairro), siAddress2.getValue(address.bairro));
        Assert.assertEquals(siAddress1.getValue(address.cep), siAddress2.getValue(address.cep));
        Assert.assertEquals(siAddress1.getValue(address.complemento), siAddress2.getValue(address.complemento));
        System.out.println(siAddress2.getValue(address.bairro));
        System.out.println(siAddress2.getField(address.estado).toStringDisplay());

    }
}
