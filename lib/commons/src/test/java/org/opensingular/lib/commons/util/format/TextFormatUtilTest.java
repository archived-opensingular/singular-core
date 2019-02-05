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

package org.opensingular.lib.commons.util.format;

import org.junit.Assert;
import org.junit.Test;

public class TextFormatUtilTest {

    @Test
    public void removeNonNumberCharactersTest() {
        Assert.assertEquals("123456789", TextFormatUtil.removeNonNumberCharacters("Texto12 %345)(*&$#@6789"));
    }

    @Test
    public void formatCpfTest() {
        Assert.assertEquals("123.456.789-10", TextFormatUtil.formatCpf("12345678910"));
    }

    @Test
    public void formatCnpjTest() {
        Assert.assertEquals("08.581.483/0001-77", TextFormatUtil.formatCnpj("08581483000177"));
    }

    @Test
    public void formatCpfCnpjTest() {
       String cpf= "12345678910";
       String cpnj= "08581483000177";
       Assert.assertEquals("123.456.789-10", TextFormatUtil.formatCpfCnpj(cpf));
       Assert.assertEquals("08.581.483/0001-77", TextFormatUtil.formatCpfCnpj(cpnj));
    }

    @Test
    public void formatTelefoneTest() {
        String telefone = "6133345678";
        Assert.assertEquals("(61) 3334-5678", TextFormatUtil.formatTelefone(telefone));
    }

    @Test
    public void formatNumeroUnicoProcessoTest() {
        String nup = "99970000004201815";
        Assert.assertEquals("99970.000004/2018-15", TextFormatUtil.formatNumeroUnicoProcesso(nup));
    }

    @Test
    public void formatCepTest() {
        String cep = "72130000";
        Assert.assertEquals("72.130-000", TextFormatUtil.formatCep(cep));
    }

}
