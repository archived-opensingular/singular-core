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

package org.opensingular.form.wicket.mapper.maps;

import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.opensingular.form.SInstance;
import org.opensingular.form.type.util.STypeLatitudeLongitude;
import org.opensingular.form.wicket.WicketBuildContext;
import org.opensingular.form.wicket.mapper.composite.DefaultCompositeMapper;

import java.util.ArrayList;
import java.util.List;

public class LatitudeLongitudeMapper extends DefaultCompositeMapper {

    @Override
    public void buildView(WicketBuildContext ctx) {
        super.buildView(ctx);

        List<String> markups = new ArrayList<>();
        ctx.getContainer().visitChildren((TextField.class), (component, iVisit) -> markups.add(component.getMarkupId(true)));

        String latitudeId = null;
        String longitudeId = null;
        String zoomId = null;
        for(String t : markups){
            if(t.contains(STypeLatitudeLongitude.FIELD_LATITUDE))
                latitudeId = t;
            if(t.contains(STypeLatitudeLongitude.FIELD_LONGITUDE))
                longitudeId = t;
            if(t.contains(STypeLatitudeLongitude.FIELD_ZOOM))
                zoomId = t;
        }

        final IModel<? extends SInstance> model = ctx.getModel();

        final MarkableGoogleMapsPanel<SInstance> googleMapsPanel =
                new MarkableGoogleMapsPanel<>(model, latitudeId, longitudeId, zoomId);

        googleMapsPanel.setReadOnly(ctx.getViewMode().isVisualization());
        ctx.getContainer().newFormGroup().appendDiv(googleMapsPanel);
    }
}
