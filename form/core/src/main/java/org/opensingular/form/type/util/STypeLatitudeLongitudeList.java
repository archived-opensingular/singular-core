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

package org.opensingular.form.type.util;

import org.opensingular.form.SInfoType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.STypeList;
import org.opensingular.form.TypeBuilder;
import org.opensingular.form.type.core.STypeHiddenString;
import org.opensingular.form.type.core.attachment.STypeAttachment;
import org.opensingular.form.view.SViewAttachment;
import org.opensingular.form.view.list.SViewListByTable;

@SInfoType(name = "LatitudeLongitudeList", spackage = SPackageUtil.class)
public class STypeLatitudeLongitudeList extends STypeComposite<SILatitudeLongitudeList> {

    private static final Integer DEFAULT_ZOOM = 4;

    public static final String FIELD_POINTS = "points";
    public static final String FIELD_ZOOM = "zoom";
    public static final String FIELD_FILE = "file";

    public STypeList<STypeLatitudeLongitude, SILatitudeLongitude> points;
    public STypeHiddenString zoom;
    public STypeAttachment file;

    public STypeLatitudeLongitudeList() {
        super(SILatitudeLongitudeList.class);
    }

    @Override
    protected void onLoadType(TypeBuilder tb) {
        points = addFieldListOf(FIELD_POINTS, STypeLatitudeLongitude.class);
        file = addFieldAttachment(FIELD_FILE);
        zoom = addField(FIELD_ZOOM, STypeHiddenString.class);

        points.withView(new SViewListByTable().setNewEnabled(list -> {
            SILatitudeLongitudeList latLongList = (SILatitudeLongitudeList) list.getParent();
            return !latLongList.hasFile();
        }).configureDeleteButtonPerRow(instance -> {
            SILatitudeLongitudeList latLongList = (SILatitudeLongitudeList) instance.getParent().getParent();
            return !latLongList.hasFile();
        }))
            .asAtr().dependsOn(file);

        LatlongStrategyFactory factory = new LatlongStrategyFactory();
        file.withView(new SViewAttachment().withFileUploadedListener(new FileLatLongUploadListener(factory)));

        zoom.withInitListener(ins -> {
            if(ins.isEmptyOfData())
                ins.setValue(DEFAULT_ZOOM);
        });
    }
}
