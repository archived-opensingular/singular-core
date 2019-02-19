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

package org.opensingular.lib.commons.util;

import org.opensingular.lib.commons.base.SingularException;

import javax.annotation.Nonnull;

public class SingularIntegrationException extends SingularException {

    protected SingularIntegrationException(String serviceName, Throwable e) {
        super(serviceName, e);
    }

    protected SingularIntegrationException(@Nonnull String msg) {
        super(msg);
    }

    protected SingularIntegrationException(Throwable cause) {
        super(cause);
    }

    public static SingularIntegrationException rethrow(Throwable e) {
        return rethrow(null, e);
    }

    public static SingularIntegrationException rethrow(String message) {
        return rethrow(message, null);
    }

    public static SingularIntegrationException rethrow(String message, Throwable e) {
        if (e instanceof SingularIntegrationException) {
            return (SingularIntegrationException) e;
        } else {
            return new SingularIntegrationException(message, e);
        }
    }


}