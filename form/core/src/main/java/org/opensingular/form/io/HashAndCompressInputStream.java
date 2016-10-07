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
import java.security.DigestInputStream;
import java.util.zip.Deflater;
import java.util.zip.DeflaterInputStream;

/**
 * Input stream para comprimir e calcular o hash ao mesmo tempo
 * O hash, após a leitura do input stream fica disponível através do método
 * {@link HashAndCompressInputStream#getHashSHA1()}
 *
 * O algoritmo de hash utilizado é o SHA1 e o nível de compressão é o máximo.
 */
public class HashAndCompressInputStream extends DeflaterInputStream {




    public HashAndCompressInputStream(InputStream in) {
        super(HashUtil.toSHA1InputStream(IOUtil.newBuffredInputStream(in)), new Deflater(Deflater.BEST_COMPRESSION));
    }


    public String getHashSHA1() {
        return HashUtil.bytesToBase16(((DigestInputStream) this.in).getMessageDigest().digest());
    }

}
