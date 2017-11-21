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

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;
import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.form.type.util.STypeLatitudeLongitude;
import org.opensingular.form.wicket.WicketBuildContext;
import org.opensingular.form.wicket.mapper.composite.DefaultCompositeMapper;
import org.opensingular.form.wicket.model.SInstanceValueModel;

public class LatitudeLongitudeMapper extends DefaultCompositeMapper {

    @Override
    public void buildView(WicketBuildContext ctx) {
        super.buildView(ctx);

        LatLongMarkupIds ids = new LatLongMarkupIds();

        ctx.getContainer().visitChildren((TextField.class), new IVisitor<Component, Object>() {
            @Override
            public void component(Component object, IVisit<Object> visit) {
                String nameSimple = ((SInstanceValueModel)object.getDefaultModel()).getSInstance().getType().getNameSimple();
                if (nameSimple.equals(STypeLatitudeLongitude.FIELD_LATITUDE)) {
                    ids.latitudeId = object.getMarkupId();
                }
                if (nameSimple.equals(STypeLatitudeLongitude.FIELD_LONGITUDE)) {
                    ids.longitudeId = object.getMarkupId();
                }
                if (nameSimple.equals(STypeLatitudeLongitude.FIELD_ZOOM)) {
                    ids.zoomId = object.getMarkupId();
                }
            }
        });

        final IModel<? extends SInstance> model = ctx.getModel();

        final MarkableGoogleMapsPanel<SInstance> googleMapsPanel = new MarkableGoogleMapsPanel<>(ids, model);

        googleMapsPanel.setReadOnly(ctx.getViewMode().isVisualization());
        ctx.getContainer().newFormGroup().appendDiv(googleMapsPanel);
    }

}
