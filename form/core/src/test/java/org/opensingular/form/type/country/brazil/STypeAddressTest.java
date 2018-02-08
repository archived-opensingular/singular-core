/*
 *
 *  * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.opensingular.form.type.country.brazil;

import org.junit.Assert;
import org.junit.Test;
import org.opensingular.form.SDictionary;
import org.opensingular.form.SIComposite;

public class STypeAddressTest {

    @Test
    public void testCopyValues() throws Exception {
        STypeAddress address = SDictionary.create().getType(STypeAddress.class);
        SIComposite siAddress1 = address.newInstance();
        SIComposite siAddress2 = address.newInstance();

        siAddress1.setValue(address.bairro, "noroeste");
        siAddress1.setValue(address.cep, "70386345");
        siAddress1.setValue(address.cidade, "brasilia");
        address.estado.fillDF(siAddress1.getField(address.estado));
        siAddress1.setValue(address.complemento, "nada");
        siAddress1.setValue(address.logradouro, "muito louco");

        siAddress2.setValue(siAddress1);

        Assert.assertEquals(siAddress1.getValue(address.bairro), siAddress2.getValue(address.bairro));
        Assert.assertEquals(siAddress1.getValue(address.cep), siAddress2.getValue(address.cep));
        Assert.assertEquals(siAddress1.getValue(address.complemento), siAddress2.getValue(address.complemento));
        System.out.println(siAddress2.getValue(address.bairro));
        System.out.println(siAddress2.getField(address.estado).toStringDisplay());

    }
}
