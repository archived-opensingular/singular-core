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

package org.opensingular.lib.commons.io;

import org.opensingular.lib.commons.util.Loggable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Keeps a reference of a temp file and delete it as soon as the stream
 * is closed.
 */
public class TempFileInputStream extends FileInputStream implements Loggable{

    private File tempFile;

    public TempFileInputStream(File file) throws FileNotFoundException {
        super(file);
        this.tempFile = file;
    }


    @Override
    public void close() throws IOException {
        super.close();
        String  name               = tempFile.getName();
        boolean deletedWithSuccess = tempFile.delete();
        if(!deletedWithSuccess){
            getLogger().warn("Não foi possivel deletar o arquivo {} corretamente", name);
        }
    }

}