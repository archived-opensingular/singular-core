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
import org.opensingular.form.view.SViewListByTable;

@SInfoType(name = "LatitudeLongitudeMapper", spackage = SPackageUtil.class)
public class STypeLatitudeLongitudeMultipleMarkable extends STypeComposite<SILatitudeLongitudeMapper> {

    private static final Integer DEFAULT_ZOOM = 4;

    public static final String FIELD_POINTS = "points";
    public static final String FIELD_ZOOM = "zoom";
    public static final String FIELD_FILE = "file";

    //TODO fazer um enum que tenha varios tipos de files.
    private static final String KMZ_FILE = "kml";

    public STypeList<STypeLatitudeLongitude, SILatitudeLongitude> points;
    public STypeHiddenString zoom;
    public STypeAttachment file;

    public STypeLatitudeLongitudeMultipleMarkable() {
        super(SILatitudeLongitudeMapper.class);
    }

    @Override
    protected void onLoadType(TypeBuilder tb) {
        points = addFieldListOf(FIELD_POINTS, STypeLatitudeLongitude.class);

        file = addFieldAttachment(FIELD_FILE);
        file.asAtr().allowedFileTypes(KMZ_FILE);
        zoom = addField(FIELD_ZOOM, STypeHiddenString.class);

        points.withView(new SViewListByTable().setNewEnabled(list -> {
            SILatitudeLongitudeMapper latLongList = (SILatitudeLongitudeMapper) list.getParent();
            return latLongList != null && !latLongList.hasFile();
        }).setDeleteEnabled(instance -> {
            SILatitudeLongitudeMapper latLongList = (SILatitudeLongitudeMapper) instance.getParent().getParent();
            return latLongList != null &&  !latLongList.hasFile();
        }))
            .asAtr().dependsOn(file);


        zoom.withInitListener(ins -> {
            if(ins.isEmptyOfData())
                ins.setValue(DEFAULT_ZOOM);
        });
    }
}
