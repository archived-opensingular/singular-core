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

import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.util.string.StringValue;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SIList;
import org.opensingular.form.SInstance;
import org.opensingular.form.type.util.STypeLatitudeLongitude;
import org.opensingular.form.type.util.STypeLatitudeLongitudeList;
import org.opensingular.form.view.FileEventListener;
import org.opensingular.form.wicket.WicketBuildContext;
import org.opensingular.form.wicket.mapper.TableListMapper;
import org.opensingular.form.wicket.mapper.attachment.single.FileUploadPanel;
import org.opensingular.form.wicket.mapper.components.ConfirmationModal;
import org.opensingular.form.wicket.model.SInstanceFieldModel;
import org.opensingular.form.wicket.model.SInstanceValueModel;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSContainer;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSGrid;
import org.opensingular.lib.wicket.util.util.WicketUtils;

import static org.opensingular.lib.wicket.util.util.Shortcuts.$b;

public class LatitudeLongitudeListMapper extends TableListMapper {

    @Override
    public void buildView(WicketBuildContext ctx) {

        SInstanceFieldModel<SInstance> zoom = new SInstanceFieldModel<>(ctx.getModel(), STypeLatitudeLongitudeList.FIELD_ZOOM);
        WicketBuildContext zoomCtx = ctx.createChild(ctx.getContainer().newGrid(), ctx.getExternalContainer(), zoom);
        zoomCtx.build();

        LatLongMarkupIds ids = new LatLongMarkupIds();

        zoomCtx.getContainer().visitChildren((TextField.class), (object, visit) -> {
            String nameSimple = ((SInstanceValueModel) object.getDefaultModel()).getSInstance().getType().getNameSimple();
            if (nameSimple.equals(STypeLatitudeLongitudeList.FIELD_ZOOM)) {
                ids.zoomId = object.getMarkupId();
            }
        });

        final MarkableGoogleMapsPanel<SInstance> googleMapsPanel = new MarkableGoogleMapsPanel<>(ids, ctx.getModel(), ctx.getView(),
                ctx.getViewMode().isVisualization(), true);
        BSGrid gridGoogleMaps = ctx.getContainer().newGrid();
        gridGoogleMaps.newFormGroup().appendDiv(googleMapsPanel);

        SInstanceFieldModel<SInstance> points = new SInstanceFieldModel<>(ctx.getModel(), STypeLatitudeLongitudeList.FIELD_POINTS);
        WicketBuildContext pointsCtx = ctx.createChild(gridGoogleMaps, ctx.getExternalContainer(), points);
        pointsCtx.build();

        SInstanceFieldModel<SInstance> file = new SInstanceFieldModel<>(ctx.getModel(), STypeLatitudeLongitudeList.FIELD_FILE);
        WicketBuildContext fileCtx = ctx.createChild(ctx.getContainer().newGrid(), ctx.getExternalContainer(), file);
        fileCtx.build();

        WicketUtils.findFirstChild(fileCtx.getContainer(), FileUploadPanel.class)
                .ifPresent(panel -> panel.registerFileRemovedListener((FileEventListener) attachment -> {
                    points.getObject().clearInstance();
                    AjaxRequestTarget target = RequestCycle.get().find(AjaxRequestTarget.class);
                    target.add(gridGoogleMaps);
                }));


        AbstractDefaultAjaxBehavior addPoint = createBehaviorAddPoint(points, pointsCtx.getContainer());
        ctx.getContainer().add(addPoint);
        googleMapsPanel.add($b.onConfigure(c -> googleMapsPanel.enableMultipleMarkers(addPoint.getCallbackUrl().toString(), pointsCtx.getContainer().getMarkupId())));

        WicketUtils.findFirstChild(pointsCtx.getContainer(), AddButton.class)
                .ifPresent(button -> button.add(new AjaxEventBehavior("click") {
                    @Override
                    protected void onEvent(AjaxRequestTarget target) {
                        target.add(googleMapsPanel);
                    }
                }));

        ConfirmationModal confirmationModal = ctx.getExternalContainer().newComponent(ConfirmationModal::new);
        confirmationModal.registerListener(googleMapsPanel::updateJS);


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
