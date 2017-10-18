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

package org.opensingular.lib.commons.lambda;

import java.io.Serializable;

/**
 * It's a reimplementation o java Function that lets Exception being thrown.
 *
 * @author Daniel C. Bordin on 2017-09-23.
 */
@FunctionalInterface
public interface IFunctionEx<T, R, EX extends Exception> extends Serializable {
    R apply(T t) throws EX;
}
