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

import org.opensingular.lib.commons.pdf.PDFUtil;
import org.opensingular.lib.commons.util.TempFileUtils;
import org.opensingular.lib.commons.views.ViewGenerator;
import org.opensingular.lib.commons.views.ViewOutputFormat;
import org.opensingular.lib.commons.views.ViewOutputFormatExportable;
import org.opensingular.lib.commons.views.ViewsUtil;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;

/**
 * @author Daniel C. Bordin on 26/07/2017.
 */
public class ViewOutputFormatPdf extends ViewOutputFormatExportable {

    public ViewOutputFormatPdf() {
        super("PDF", "Pdf", "pdf");
    }

    @Override
    public void generateFile(@Nonnull File destination, @Nonnull ViewGenerator viewGenerator) throws IOException {
        File fileHtml = ViewsUtil.exportToTempFile(viewGenerator, ViewOutputFormat.HTML);
        try {
            PDFUtil.getInstance().convertHTML2PDF(fileHtml, destination);
            TempFileUtils.deleteOrException(fileHtml, this);
        } finally {
            TempFileUtils.deleteAndFailQuietly(fileHtml, this);
        }
    }
}
