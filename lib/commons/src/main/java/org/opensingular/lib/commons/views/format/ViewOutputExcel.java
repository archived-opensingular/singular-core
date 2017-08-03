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

package org.opensingular.lib.commons.views.format;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.opensingular.lib.commons.util.Loggable;
import org.opensingular.lib.commons.views.ViewOutput;
import org.opensingular.lib.commons.views.ViewOutputFormat;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.OutputStream;

public class ViewOutputExcel implements ViewOutput<XSSFSheet>, Loggable {

    private final XSSFWorkbook workbook;
    private final XSSFSheet sheet;

    public ViewOutputExcel(@Nullable String worksheetName) {
        this.workbook = new XSSFWorkbook();
        this.sheet = worksheetName == null ? workbook.createSheet() : workbook.createSheet(worksheetName);
    }

    @Override
    public XSSFSheet getOutput() {
        return sheet;
    }

    @Override
    public ViewOutputFormat getFormat() {
        return ViewOutputFormat.EXCEL;
    }

    public void write(OutputStream os) throws IOException {
        workbook.write(os);
    }
}