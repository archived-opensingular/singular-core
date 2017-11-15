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

package org.opensingular.form.servlet;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MimeTypesTest {

    @Test
    public void getMimeTypeForExtension(){
        assertThat(MimeTypes.getMimeTypeForExtension("tei")).isEqualTo("application/tei+xml");
        assertThat(MimeTypes.getMimeTypeForExtension("mp4a")).isEqualTo("audio/mp4");
        assertThat(MimeTypes.getMimeTypeForExtension("html")).isEqualTo("text/html");
        assertThat(MimeTypes.getMimeTypeForExtension("pdf")).isEqualTo("application/pdf");
    }

    @Test
    public void getExtensionForMimeType(){
        assertThat(MimeTypes.getExtensionsForMimeType("application/tei+xml")).contains("tei");
        assertThat(MimeTypes.getExtensionsForMimeType("audio/mp4")).contains("mp4a");
        assertThat(MimeTypes.getExtensionsForMimeType("text/html")).contains("html");
        assertThat(MimeTypes.getExtensionsForMimeType("application/pdf")).contains("pdf");
    }
}
