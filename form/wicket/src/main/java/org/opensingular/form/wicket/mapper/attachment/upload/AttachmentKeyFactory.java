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

import org.apache.commons.lang3.StringUtils;
import org.opensingular.form.wicket.mapper.attachment.upload.servlet.strategy.AttachmentKeyStrategy;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.UUID;

import static org.apache.commons.lang3.StringUtils.defaultString;
import static org.apache.commons.lang3.StringUtils.substringAfterLast;

public class AttachmentKeyFactory implements Serializable {

    public AttachmentKey make() {
        return new AttachmentKey(UUID.randomUUID().toString());
    }

    public AttachmentKey makeFromRequestPathOrNull(HttpServletRequest req) {
        if (isRawKeyPresent(req)) {
            return new AttachmentKey(retrieveRawKeyFromRequest(req));
        }
        return null;
    }

    /**
     * Utility method to check if the raw key is present in the request.
     *
     * @param req servlet request.
     * @return <code>true</code> if present.
     */
    public boolean isRawKeyPresent(HttpServletRequest req) {
        return !StringUtils.isBlank(retrieveRawKeyFromRequest(req));
    }

    /**
     * Retrieves the raw key from {@link HttpServletRequest}.
     *
     * @param req servlet request.
     * @return the raw key or blank if not present.
     */
    protected String retrieveRawKeyFromRequest(HttpServletRequest req) {
        return substringAfterLast(defaultString(req.getRequestURL().toString()), AttachmentKeyStrategy.UPLOAD_URL + "/");
    }

}