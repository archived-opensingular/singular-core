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

package org.opensingular.lib.commons.table;

import org.opensingular.lib.commons.views.ViewGenerator;
import org.opensingular.lib.commons.views.ViewGeneratorProvider;
import org.opensingular.lib.commons.views.ViewOutput;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ServiceLoader;

/**
 * @author Daniel C. Bordin on 24/07/2017.
 */
final class TableToolUtil {

    private static Collection<ViewGeneratorProvider<ViewGenerator, ? extends ViewOutput<?>>> generators;

    public static Collection<ViewGeneratorProvider<ViewGenerator, ? extends ViewOutput<?>>> getGenerators() {
        if (generators == null) {
            List<ViewGeneratorProvider<ViewGenerator, ? extends ViewOutput<?>>> list = new ArrayList<>();
            for(ViewGeneratorProvider g : ServiceLoader.load(ViewGeneratorForTableTool.class)) {
                list.add(g);
            }
            generators = list;
        }
        return generators;
    }
}
