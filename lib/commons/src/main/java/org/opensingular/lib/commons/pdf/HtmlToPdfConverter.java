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

package org.opensingular.lib.commons.pdf;

import java.io.File;
import java.io.InputStream;
import java.util.Optional;

import org.opensingular.lib.commons.dto.HtmlToPdfDTO;
import org.opensingular.lib.commons.util.Loggable;
/**
 * Conversor de Html para PDF <br>
 * 
 */
public interface HtmlToPdfConverter extends Loggable {

   
    /**
     * Converte o {@link HtmlToPdfDTO} em pdf e retorna um stream para recuperar o arquivo pdf.
     * 
     * @param htmlToPdfDTO
     * @return
     */
    Optional<File> convert(HtmlToPdfDTO htmlToPdfDTO);

    /**
     * Converte o {@link HtmlToPdfDTO} em pdf e retorna um stream para recuperar o arquivo pdf.
     * 
     * @param htmlToPdfDTO
     * @return
     */
    InputStream convertStream(HtmlToPdfDTO htmlToPdfDTO);

}