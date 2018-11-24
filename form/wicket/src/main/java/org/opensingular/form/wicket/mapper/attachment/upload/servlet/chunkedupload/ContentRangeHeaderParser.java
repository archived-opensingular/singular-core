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

package org.opensingular.form.wicket.mapper.attachment.upload.servlet.chunkedupload;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;

public class ContentRangeHeaderParser implements Serializable {

    private long    byteArrayStartIndex;
    private long    byteArrayEndIndex;
    private long    totalBytes;
    private boolean exists;

    public ContentRangeHeaderParser(HttpServletRequest req) {
        String contentRange = req.getHeader("Content-Range");
        if (contentRange != null) {
            exists = true;
            contentRange = contentRange.replaceAll("bytes", "").trim();
            String[] parts = contentRange.split("/");
            String[] range = parts[0].split("-");
            byteArrayStartIndex = Long.parseLong(range[0]);
            byteArrayEndIndex = Long.parseLong(range[1]);
            totalBytes = Long.parseLong(parts[1]);
        }
    }

    public boolean exists() {
        return exists;
    }

    public boolean isLastChunk() {
        return byteArrayEndIndex == totalBytes - 1;
    }
}
