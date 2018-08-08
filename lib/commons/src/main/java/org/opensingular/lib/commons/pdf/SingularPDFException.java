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

package org.opensingular.lib.commons.pdf;

import org.opensingular.lib.commons.base.SingularException;

/**
 * Exception associadas a geração e manipulação de PDF.
 *
 * @author Daniel C. Bordin on 21/01/2017.
 */
public class SingularPDFException extends SingularException {

    /**
     * Constructs a new <code>SingularException</code> without specified
     * detail message.
     */
    protected SingularPDFException() {
        super();
    }

    /**
     * Constructs a new <code>SingularException</code> with specified
     * detail message.
     *
     * @param msg the error message
     */
    protected SingularPDFException(String msg) {
        super(msg);
    }

    /**
     * Constructs a new <code>SingularException</code> with specified
     * cause <code>Throwable</code>.
     *
     * @param cause the exception or error that caused this exception to be
     *              thrown
     */
    protected SingularPDFException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new <code>SingularException</code> with specified
     * detail message and cause <code>Throwable</code>.
     *
     * @param msg   the error message
     * @param cause the exception or error that caused this exception to be
     *              thrown
     */
    protected SingularPDFException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

