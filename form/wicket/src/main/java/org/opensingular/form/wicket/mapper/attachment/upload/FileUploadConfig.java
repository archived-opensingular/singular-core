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

package org.opensingular.form.wicket.mapper.attachment.upload;

import org.opensingular.lib.commons.base.SingularProperties;

import javax.annotation.Nonnull;
import java.io.Serializable;

import static org.opensingular.internal.lib.commons.util.ConversionUtils.toLongHumane;
import static org.opensingular.lib.commons.base.SingularProperties.FILEUPLOAD_DEFAULT_MAX_FILE_SIZE;
import static org.opensingular.lib.commons.base.SingularProperties.FILEUPLOAD_DEFAULT_MAX_REQUEST_SIZE;
import static org.opensingular.lib.commons.base.SingularProperties.FILEUPLOAD_GLOBAL_MAX_FILE_SIZE;
import static org.opensingular.lib.commons.base.SingularProperties.FILEUPLOAD_GLOBAL_MAX_REQUEST_SIZE;

public class FileUploadConfig implements Serializable {

    private final long globalMaxFileSize;
    private final long globalMaxRequestSize;
    private final long defaultMaxFileSize;
    private final long defaultMaxRequestSize;

    public FileUploadConfig(@Nonnull SingularProperties sp) {
        this.globalMaxFileSize = readLong(sp, FILEUPLOAD_GLOBAL_MAX_FILE_SIZE);
        this.globalMaxRequestSize = readLong(sp, FILEUPLOAD_GLOBAL_MAX_REQUEST_SIZE);
        this.defaultMaxFileSize = readLong(sp, FILEUPLOAD_DEFAULT_MAX_FILE_SIZE);
        this.defaultMaxRequestSize = readLong(sp, FILEUPLOAD_DEFAULT_MAX_REQUEST_SIZE);
    }

    private long readLong(@Nonnull SingularProperties sp, @Nonnull String key) {
        return toLongHumane(sp.getPropertyOpt(key).orElse(null), Long.MAX_VALUE);
    }

    private long getGlobalMaxFileSize() {
        return globalMaxFileSize;
    }

    private long getGlobalMaxRequestSize() {
        return globalMaxRequestSize;
    }

    private long getDefaultMaxFileSize() {
        return defaultMaxFileSize;
    }

    private long getDefaultMaxRequestSize() {
        return defaultMaxRequestSize;
    }

    private long resolveMax(long specifiedMax, long defaultMax, long globalMax) {
        return Math.min((specifiedMax > 0) ? specifiedMax : defaultMax, globalMax);
    }

    public long resolveMaxPerFile(Long specifiedMax) {
        return resolveMax(specifiedMax, getDefaultMaxFileSize(), getGlobalMaxFileSize());
    }

    long resolveMaxPerRequest(long specifiedMax) {
        return resolveMax(specifiedMax, getDefaultMaxRequestSize(), getGlobalMaxRequestSize());
    }
}
