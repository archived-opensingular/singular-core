/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
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

package org.opensingular.form.wicket.mapper.attachment.upload;

import org.opensingular.lib.commons.base.SingularProperties;

import java.io.Serializable;

import static org.opensingular.lib.commons.base.SingularProperties.*;
import static org.opensingular.lib.commons.util.ConversionUtils.toLongHumane;

public class FileUploadConfig implements Serializable {

    private final long globalMaxFileAge;
    private final long globalMaxFileCount;
    private final long globalMaxFileSize;
    private final long globalMaxRequestSize;
    private final long defaultMaxFileSize;
    private final long defaultMaxRequestSize;

    public FileUploadConfig(SingularProperties sp) {
        //@formatter:off
        this.globalMaxFileAge = toLongHumane(sp.getProperty(FILEUPLOAD_GLOBAL_MAX_FILE_AGE), Long.MAX_VALUE);
        this.globalMaxFileCount = toLongHumane(sp.getProperty(FILEUPLOAD_GLOBAL_MAX_FILE_COUNT), Long.MAX_VALUE);
        this.globalMaxFileSize = toLongHumane(sp.getProperty(FILEUPLOAD_GLOBAL_MAX_FILE_SIZE), Long.MAX_VALUE);
        this.globalMaxRequestSize = toLongHumane(sp.getProperty(FILEUPLOAD_GLOBAL_MAX_REQUEST_SIZE), Long.MAX_VALUE);

        this.defaultMaxFileSize = toLongHumane(sp.getProperty(FILEUPLOAD_DEFAULT_MAX_FILE_SIZE), Long.MAX_VALUE);
        this.defaultMaxRequestSize = toLongHumane(sp.getProperty(FILEUPLOAD_DEFAULT_MAX_REQUEST_SIZE), Long.MAX_VALUE);
        //@formatter:on
    }

    public long getGlobalMaxFileAge() {
        return globalMaxFileAge;
    }

    public long getGlobalMaxFileCount() {
        return globalMaxFileCount;
    }

    public long getGlobalMaxFileSize() {
        return globalMaxFileSize;
    }

    public long getGlobalMaxRequestSize() {
        return globalMaxRequestSize;
    }

    public long getDefaultMaxFileSize() {
        return defaultMaxFileSize;
    }

    public long getDefaultMaxRequestSize() {
        return defaultMaxRequestSize;
    }
}
