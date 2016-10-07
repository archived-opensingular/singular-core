/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.form.io;

import java.io.InputStream;
import java.util.zip.Deflater;
import java.util.zip.DeflaterInputStream;
import java.util.zip.InflaterInputStream;

/**
 * Funções de apoio a compressão e descompressão de dados para uso interno
 * apenas.
 *
 * @author Daniel C. Bordin
 */
public final class CompressionUtil {

    public static InputStream inflateToInputStream(InputStream source) {
        return new InflaterInputStream(source);
    }


    public static InputStream toDeflateInputStream(InputStream source) {
        return new DeflaterInputStream(source, new Deflater(Deflater.BEST_COMPRESSION));
    }

}
