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

package org.opensingular.ws.wkhtmltopdf.client.mock;

import org.junit.Assert;
import org.junit.Test;
import org.opensingular.lib.commons.dto.HtmlToPdfDTO;
import org.opensingular.ws.wkhtmltopdf.client.MockHtmlToPdfConverter;


public class MockHtmlToPdfConverterTest {
    @Test
    public void convert() throws Exception {
        MockHtmlToPdfConverter mockHtmlToPdfConverter = new MockHtmlToPdfConverter();
        HtmlToPdfDTO htmlToPdfDTO = new HtmlToPdfDTO("","", "");
        Assert.assertTrue(mockHtmlToPdfConverter.convert(htmlToPdfDTO).isPresent());
    }

}