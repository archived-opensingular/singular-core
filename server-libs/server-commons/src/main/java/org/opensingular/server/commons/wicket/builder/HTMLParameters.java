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

package org.opensingular.server.commons.wicket.builder;

import java.util.HashMap;
import java.util.Map;

public class HTMLParameters {

    final Map<String, String> params = new HashMap<>();

    public HTMLParameters styleClass(String value) {
        return this.add("class", value);
    }

    public HTMLParameters add(String key, String value) {
        params.put(key, value);
        return this;
    }

    public Map<String, String> getParametersMap() {
        return params;
    }

}
