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

package org.opensingular.ws.wkhtmltopdf.controller;

import org.junit.Assert;
import org.junit.Test;

public class WkHtmlToPdfRestControllerTest {

//    @Test
//    public void testConvertHtmlToPdf(){
//        Assert.assertNotNull(new WkHtmlToPdfRestController().convertHtmlToPdf(getHtmlToPdfDTO()));
//    }

//    private HtmlToPdfDTO getHtmlToPdfDTO(){
//        HtmlToPdfDTO dto = new HtmlToPdfDTO();
//        dto.setHeader("<header>" +
//                "<title>Mock html</title>" +
//                "</header>");
//        dto.setBody("<body><div>Qualquer coisa com um valor<div></body>");
//        dto.setFooter("<footer></footer>");
//
//        return dto;
//    }

    @Test
    public void testConvertHtmlToPdfWithNullValue(){
        Assert.assertNull(new WkHtmlToPdfRestController().convertHtmlToPdf(null));
    }

}
