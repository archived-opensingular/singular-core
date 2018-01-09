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

package org.opensingular.form.persistence.relational;

import org.apache.commons.io.IOUtils;
import org.opensingular.form.SInstance;
import org.opensingular.form.io.HashUtil;
import org.opensingular.form.persistence.SingularFormPersistenceException;
import org.opensingular.form.type.core.SIString;
import org.opensingular.form.type.core.attachment.SIAttachment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.sql.Clob;
import java.util.List;
import java.util.Optional;

/**
 * Converter implementation for transforming SIAttachment instances from/to a
 * CLOB column in Relational DBMS.
 *
 * @author Vinicius Nunes
 */
public class CLOBConverter implements RelationalColumnConverter {

    public Object toRelationalColumn(SInstance fromInstance) {
        Object value = fromInstance.getValue();
        if (value == null) {
            return null;
        }
        if (fromInstance instanceof SIAttachment) {
            return ((SIAttachment) fromInstance).getAttachmentRef();
        } else if (fromInstance instanceof SIString) {
            return fromInstance.getValue();
        }
        SingularFormPersistenceException exception = new SingularFormPersistenceException(this.getClass().getName() + " is not compatible with " + Optional.ofNullable(fromInstance).map(Object::getClass).map(Class::getName).orElse(null));
        exception.add(Optional.ofNullable(fromInstance).map(SInstance::getName).orElse(null));
        throw exception;
    }

    public void fromRelationalColumn(Object dbData, SInstance toInstance) {
        if (dbData == null) {
            toInstance.clearInstance();
            return;
        }
        try {
            Clob clob = (Clob) dbData;
            if (toInstance instanceof SIAttachment) {
                File tempFile = File.createTempFile("clob_" + toInstance.getName(), null);
                tempFile.deleteOnExit();
                try (Reader input = clob.getCharacterStream(); FileOutputStream output = new FileOutputStream(tempFile)) {
                    IOUtils.copy(input, output, Charset.forName("UTF-8"));
                }
                ((SIAttachment) toInstance).setContent(tempFile.getName(), tempFile, clob.length(),
                        HashUtil.toSHA1Base16(tempFile));
            } else if (toInstance instanceof SIString) {
                List<String>  lines = IOUtils.readLines(clob.getCharacterStream());
                StringBuilder sb    = new StringBuilder();
                lines.forEach(sb::append);
                toInstance.setValue(sb.toString());
            }
        } catch (Exception e) {
            SingularFormPersistenceException exception = new SingularFormPersistenceException("Error on converting CLOB data to " + Optional.ofNullable(toInstance).map(Object::getClass).map(Class::getName).orElse(null), e);
            exception.add(Optional.ofNullable(toInstance).map(SInstance::getName).orElse(null));
            throw exception;

        }
    }
}
