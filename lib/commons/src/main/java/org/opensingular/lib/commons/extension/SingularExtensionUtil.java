/*
 *
 *  * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.opensingular.lib.commons.extension;

import org.opensingular.lib.commons.context.SingularContext;
import org.opensingular.lib.commons.context.SingularSingletonStrategy;
import org.opensingular.lib.commons.util.Loggable;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

public class SingularExtensionUtil implements Loggable {
    public static SingularExtensionUtil get() {
        return ((SingularSingletonStrategy) SingularContext.get())
                .singletonize(SingularExtensionUtil.class, SingularExtensionUtil::new);
    }

    public <T extends SingularExtension> List<T> findExtensionsByClass(Class<T> extensionClass) {
        List<T> list = new ArrayList<>();
        for (T extension : ServiceLoader.load(extensionClass)) {
            list.add(extension);
        }
        return list;
    }

    public <T> T findExtensionByClass(Class<T> extensionClass) {
        return ServiceLoader.load(extensionClass).iterator().next();
    }

}