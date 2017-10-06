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

package org.opensingular.form.persistence.relational;

import java.io.File;
import java.sql.Blob;
import java.sql.SQLException;

import org.opensingular.form.SInstance;
import org.opensingular.form.io.HashUtil;
import org.opensingular.form.type.core.attachment.SIAttachment;
import org.opensingular.internal.lib.commons.util.TempFileProvider;

/**
 * Converter implementation for transforming SIAttachment instances from/to a
 * BLOB column in Relational DBMS.
 *
 * @author Edmundo Andrade
 */
public class BLOBConverter implements RelationalColumnConverter {
	public Object toRelationalColumn(SInstance fromInstance) {
		Object value = fromInstance.getValue();
		if (value == null) {
			return null;
		}
		return ((SIAttachment) fromInstance).getAttachmentRef();
	}

	public void fromRelationalColumn(Object dbData, SInstance toInstance) {
		if (dbData == null) {
			toInstance.clearInstance();
		} else {
			Blob blob = (Blob) dbData;
			TempFileProvider.create(this, tempFileProvider -> {
				try {
					File tempFile = tempFileProvider.createTempFile(blob.getBinaryStream());
					((SIAttachment) toInstance).setContent(tempFile.getName(), tempFile, blob.length(),
							HashUtil.toSHA1Base16(tempFile));
				} catch (SQLException e) {
					throw new IllegalArgumentException(e);
				}
			});
		}
	}
}
