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

import org.opensingular.form.SIList;
import org.opensingular.form.type.core.attachment.SIAttachment;
import org.opensingular.form.view.FileEventListener;

import static org.opensingular.form.type.util.STypeLatitudeLongitudeMapper.FIELD_POINTS;

public class FileLatLongUploadListener implements FileEventListener {

    private LatlongStrategyFactory factory;

    public FileLatLongUploadListener(LatlongStrategyFactory factory) {
        this.factory = factory;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void accept(SIAttachment attachment) {
        LatlongStrategy strategy = factory.createStrategy(attachment);
        SILatitudeLongitudeMapper latLong = (SILatitudeLongitudeMapper) attachment.getParent();
        SIList<SILatitudeLongitude> pointsList = latLong.getField(FIELD_POINTS, SIList.class);
        pointsList.clearInstance();
        attachment.getContentAsInputStream()
                .ifPresent(is -> strategy.parseFile(is, pointsList));
    }
}
