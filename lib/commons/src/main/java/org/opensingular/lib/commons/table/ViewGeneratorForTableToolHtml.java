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

import org.opensingular.lib.commons.views.ViewOutputFormat;
import org.opensingular.lib.commons.views.format.ViewOutputHtml;

import javax.annotation.Nonnull;

/**
 * @author Daniel on 24/07/2017.
 */
public class ViewGeneratorForTableToolHtml extends ViewGeneratorForTableTool<ViewOutputHtml> {

    @Override
    public ViewOutputFormat getOutputFormat() {
        return ViewOutputFormat.HTML;
    }

    @Override
    public void generate(@Nonnull TableTool tableTool, @Nonnull ViewOutputHtml viewOutputHtml) {
        TableOutputHtml out = new TableOutputHtml(viewOutputHtml);
        tableTool.generate(out);
    }
}
