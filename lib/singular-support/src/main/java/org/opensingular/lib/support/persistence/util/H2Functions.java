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

package org.opensingular.lib.support.persistence.util;

import java.util.Date;

public class H2Functions {

    public static Double dateDiffInDays(Date d1, Date d2) {
        long d1l = d1 == null ? 0 : d1.getTime();
        long d2l = d2 == null ? 0 : d2.getTime();
        return (d1l - d2l) / ((double) 1000 * 60 * 60 * 24);
    }

}