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

package org.opensingular.lib.commons.views.format;

import org.opensingular.lib.commons.views.ViewGenerator;
import org.opensingular.lib.commons.views.ViewOutputFormatExportable;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 *
 * @author Daniel C. Bordin on 26/07/2017.
 */
public class ViewOutputFormatExcel extends ViewOutputFormatExportable {

    public ViewOutputFormatExcel() {
        super("EXCEL", "Excel", "xlsx");
    }

    @Override
    public void generateFile(@Nonnull File destination, @Nonnull ViewGenerator viewGenerator)
            throws IOException {
        ViewOutputExcel viewOutputExcel = new ViewOutputExcel(null);
        viewGenerator.generateView(viewOutputExcel);
        try(FileOutputStream fos = new FileOutputStream(destination)) {
            viewOutputExcel.write(fos);
        }
    }
}
