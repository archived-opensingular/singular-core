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
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.util.string.StringValue;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SIList;
import org.opensingular.form.SInstance;
import org.opensingular.form.type.util.STypeLatitudeLongitude;
import org.opensingular.form.type.util.STypeListLatitudeLongitude;
import org.opensingular.form.wicket.WicketBuildContext;
import org.opensingular.form.wicket.mapper.TableListMapper;
import org.opensingular.form.wicket.model.SInstanceFieldModel;
import org.opensingular.form.wicket.model.SInstanceValueModel;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSContainer;

public class ListLatitudeLongitudeMapper extends TableListMapper {

    @Override
    public void buildView(WicketBuildContext ctx) {
        SInstanceFieldModel<SInstance> points = new SInstanceFieldModel<>(ctx.getModel(), STypeListLatitudeLongitude.FIELD_POINTS);
        WicketBuildContext pointsCtx = ctx.createChild(ctx.getContainer().newGrid(), ctx.getExternalContainer(), points, ctx.getConfirmationModal());
        pointsCtx.build();
        SInstanceFieldModel<SInstance> zoom = new SInstanceFieldModel<>(ctx.getModel(), STypeListLatitudeLongitude.FIELD_ZOOM);
        WicketBuildContext zoomCtx = ctx.createChild(ctx.getContainer().newGrid(), ctx.getExternalContainer(), zoom, ctx.getConfirmationModal());
        zoomCtx.build();

        LatLongMarkupIds ids = new LatLongMarkupIds();

        zoomCtx.getContainer().visitChildren((TextField.class), new IVisitor<Component, Object>() {
            @Override
            public void component(Component object, IVisit<Object> visit) {
                String nameSimple = ((SInstanceValueModel)object.getDefaultModel()).getSInstance().getType().getNameSimple();
                if (nameSimple.equals(STypeLatitudeLongitude.FIELD_ZOOM)) {
                    ids.zoomId = object.getMarkupId();
                }
            }
        });

        AbstractDefaultAjaxBehavior addPoint = createBehaviorAddPoint(points, pointsCtx.getContainer());
        ctx.getContainer().add(addPoint);

        final IModel<? extends SInstance> model = ctx.getModel();

        final MarkableGoogleMapsPanel<SInstance> googleMapsPanel = new MarkableGoogleMapsPanel<>(ids, model, ctx.getView(), ctx.getViewMode().isVisualization());
        googleMapsPanel.enableMultipleMarkers(addPoint.getCallbackUrl().toString(), pointsCtx.getContainer().getMarkupId());
        ctx.getContainer().newGrid().newFormGroup().appendDiv(googleMapsPanel);
    }

    private AbstractDefaultAjaxBehavior createBehaviorAddPoint(final SInstanceFieldModel<SInstance> points, BSContainer<?> container) {
        return new AbstractDefaultAjaxBehavior() {
            @Override
            protected void respond(AjaxRequestTarget target) {
                SIList list = (SIList) points.getObject();
                SIComposite sInstance = (SIComposite) list.addNew();
                StringValue lat = RequestCycle.get().getRequest().getRequestParameters().getParameterValue("lat");
                StringValue lng = RequestCycle.get().getRequest().getRequestParameters().getParameterValue("lng");
                sInstance.setValue(STypeLatitudeLongitude.FIELD_LATITUDE, lat.toString("").replaceAll("\\.", ","));
                sInstance.setValue(STypeLatitudeLongitude.FIELD_LONGITUDE, lng.toString("").replaceAll("\\.", ","));

                target.add(container);
            }
        };
    }

}
