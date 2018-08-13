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


import java.io.IOException;
import java.io.InputStream;

/**
 * Common interface to a file upload item.
 */
public interface FileUploadItem<T> {

    /**
     * Gets the input stream of the file.
     *
     * @return an instance of {@link InputStream}.
     * @throws IOException
     */
    InputStream getInputStream() throws IOException;

    /**
     * Checks if the file item is a form field.
     *
     * @return <code>true</code> if the file is a form field.
     */
    boolean isFormField();

    /**
     * Gets the content type of a file.
     *
     * @return file content type.
     */
    String getContentType();

    /**
     * Gets the size of the file.
     *
     * @return file size.
     */
    long getSize();

    /**
     * Gets the file name.
     *
     * @return the file name.
     */
    String getName();

    /**
     * Gets the file item that implements this interface.
     *
     * @return file upload item wrapped.
     */
    T getWrapped();
}
