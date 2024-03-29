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
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.util.string.StringValue;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SIList;
import org.opensingular.form.SInstance;
import org.opensingular.form.type.core.attachment.SIAttachment;
import org.opensingular.form.type.util.SILatitudeLongitudeMultipleMarkable;
import org.opensingular.form.type.util.STypeLatitudeLongitude;
import org.opensingular.form.type.util.STypeLatitudeLongitudeMultipleMarkable;
import org.opensingular.form.view.SViewCurrentLocation;
import org.opensingular.form.wicket.IWicketComponentMapper;
import org.opensingular.form.wicket.WicketBuildContext;
import org.opensingular.form.wicket.mapper.attachment.AttachmentPublicMapperResource;
import org.opensingular.form.wicket.mapper.attachment.single.FileUploadPanel;
import org.opensingular.form.wicket.mapper.buttons.AddButton;
import org.opensingular.form.wicket.mapper.tablelist.TableElementsView;
import org.opensingular.form.wicket.model.SInstanceFieldModel;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSContainer;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSGrid;
import org.opensingular.lib.wicket.util.util.WicketUtils;

import javax.servlet.http.HttpServletRequest;

import static org.opensingular.lib.wicket.util.util.Shortcuts.$b;

/**
 * This class is responsible for include a list of markable in the google maps.
 * <p>
 * There is a google maps panel.
 * There is a table with a list of latitude and logitude.
 * There is a upload file for KML.
 * <p>
 * Be careful with KML:
 * Maximum fetched file size (raw KML, raw GeoRSS, or compressed KMZ 3MB;
 * Maximum uncompressed KML file size 10MB.
 * Date: 16/07/2018
 * </p>
 */
public class LatitudeLongitudeListMapper implements IWicketComponentMapper {

    private WicketBuildContext pointsCtx;

    @Override
    public void buildView(WicketBuildContext ctx) {
        LatLongMarkupIds ids = new LatLongMarkupIds();
        SInstanceFieldModel<SInstance> zoomInstance = createZoomField(ctx, ids);

        final MarkableGoogleMapsPanel<SInstance> googleMapsPanel = createMarkableGoogleMapsPanel(ctx, ids);
        ctx.getContainer().newFormGroup().appendDiv(googleMapsPanel);
        createBooleanField(ctx);
        IModel<SIList<SInstance>> points = createPointField(ctx, googleMapsPanel);
        createUploadField(ctx, googleMapsPanel, points);

        AbstractDefaultAjaxBehavior addPoint = createBehaviorAddPoint(points, zoomInstance);
        ctx.getContainer().add(addPoint);
        googleMapsPanel.add($b.onConfigure(c -> googleMapsPanel.enableMultipleMarkers(addPoint.getCallbackUrl().toString(),
                ctx.getContainer().getMarkupId())));

        WicketUtils.findFirstChild(ctx.getContainer(), AddButton.class)
                .ifPresent(button -> button.add(new AjaxEventBehavior("click") {
                    @Override
                    protected void onEvent(AjaxRequestTarget target) {
                        target.add(googleMapsPanel);
                    }
                }));

    }

    /**
     * Method for create and configure the points field.
     * The points field is a <code>STypeList</code> for latitude and longitude.
     *
     * @param ctx             The context.
     * @param googleMapsPanel The google maps panel.
     * @return A list with the points.
     */
    private IModel<SIList<SInstance>> createPointField(WicketBuildContext ctx, MarkableGoogleMapsPanel<SInstance> googleMapsPanel) {
        IModel<SIList<SInstance>> points = new SInstanceFieldModel<>(ctx.getModel(), STypeLatitudeLongitudeMultipleMarkable.FIELD_POINTS);
        pointsCtx = ctx.createChild(ctx.getContainer().newGrid(), ctx.getExternalContainer(), points);
        pointsCtx.build();

        //The behavior when remove a item, and a listener to create the map.
        WicketUtils.findFirstChild(pointsCtx.getContainer(), TableElementsView.class)
                .ifPresent(panel -> panel.getConfirmationModal().registerListener(googleMapsPanel::updateJS));
        return points;
    }

    /**
     * Create the Zoom Field, this is a hidden field for know how zoom is setting.
     *
     * @param ctx              The context.
     * @param latLongMarkupIds A object that contains the Markup Id of Zoom.
     * @return return a instance of zoom.
     */
    private SInstanceFieldModel<SInstance> createZoomField(WicketBuildContext ctx, LatLongMarkupIds latLongMarkupIds) {
        SInstanceFieldModel<SInstance> zoomInstance = new SInstanceFieldModel<>(ctx.getModel(), STypeLatitudeLongitudeMultipleMarkable.FIELD_ZOOM);
        WicketBuildContext zoomCtx = ctx.createChild(ctx.getContainer().newGrid(), ctx.getExternalContainer(), zoomInstance);
        zoomCtx.build();
        WicketUtils.findFirstChild(zoomCtx.getContainer(), TextField.class)
                .ifPresent(object -> latLongMarkupIds.zoomId = object.getMarkupId());

        return zoomInstance;
    }

    /**
     * Create the boolean type.
     *
     * @param ctx The context.
     */
    private void createBooleanField(WicketBuildContext ctx) {
        createField(ctx, ctx.getContainer().newGrid(), STypeLatitudeLongitudeMultipleMarkable.FILE_UPLOAD_OR_TABLE);
    }

    /**
     * Create the boolean type.
     *
     * @param ctx             The context.
     * @param googleMapsPanel Panel of google maps.
     * @param points          The list of points of latitude and longitude.
     */
    private void createUploadField(WicketBuildContext ctx, MarkableGoogleMapsPanel<SInstance> googleMapsPanel, IModel<SIList<SInstance>> points) {
        WicketBuildContext uploadCtx = createField(ctx, ctx.getContainer().newGrid(), STypeLatitudeLongitudeMultipleMarkable.FIELD_FILE);
        createBehaviorWhenUploadFileIsVisible(googleMapsPanel, uploadCtx.getContainer());
        createConsumersForUploadPanel(googleMapsPanel, points, uploadCtx);
    }

    /**
     * If upload panel is visible the populate date have to use kml file rather than list of points.
     *
     * @param googleMapsPanel The google maps panel to be configured.
     * @param uploadCtx       The context of upload
     */
    private void createBehaviorWhenUploadFileIsVisible(MarkableGoogleMapsPanel<SInstance> googleMapsPanel, BSContainer uploadCtx) {
        uploadCtx.add(new Behavior() {
            @Override
            public void onConfigure(Component component) {
                super.onConfigure(component);
                //Esse método redesenha o google maps, ele não deve ser chamado no caso do target do consumer AfterLoadImage
                googleMapsPanel.populateMetaData(uploadCtx.isVisible());
            }
        });
    }

    /**
     * Method for create the field for the specfic property
     *
     * @param ctx            The context.
     * @param childContainer The childContainer.
     * @param property       The property.
     * @return The field created.
     */
    private WicketBuildContext createField(WicketBuildContext ctx, BSGrid childContainer, String property) {
        SInstanceFieldModel<SInstance> instance = new SInstanceFieldModel<>(ctx.getModel(), property);
        WicketBuildContext buildCtx = ctx.createChild(childContainer, ctx.getExternalContainer(), instance);
        buildCtx.build();

        return buildCtx;
    }


    /**
     * This method is responsible for creating the consumers for the upload panel.
     * Created a consumer after upload the file, and other after removed the file.
     *
     * @param googleMapsPanel The google maps panel.
     * @param points          The points of the table.
     * @param fileCtx         The file context that will be included the consumers.
     */
    private void createConsumersForUploadPanel(MarkableGoogleMapsPanel<SInstance> googleMapsPanel, IModel<SIList<SInstance>> points,
                                               WicketBuildContext fileCtx) {
        WicketUtils.findFirstChild(fileCtx.getContainer(), FileUploadPanel.class)
                .ifPresent(panel -> {
                    panel.setConsumerAfterLoadImage(target -> {
                        points.getObject().clearInstance();
                        googleMapsPanel.setKmlUrl(createTempFile(panel.getModel().getObject()));
                        target.add(pointsCtx.getParent().getContainer());
                    });

                    panel.setConsumerAfterRemoveImage(target -> {
                        googleMapsPanel.setKmlUrl("");
                        target.add(pointsCtx.getParent().getContainer());
                    });
                });
    }

    /**
     * Create a temp file for the Google API use.
     *
     * @param instanceAttachment instance of Attachment.
     * @return Url file for download.
     */
    private String createTempFile(SIAttachment instanceAttachment) {
        String urlFile = AttachmentPublicMapperResource.createTempPublicMapFile(instanceAttachment.getFileName(), instanceAttachment.getAttachmentRef());
        return toAbsolutePath() + urlFile;
    }

    /**
     * Method responsible for crate the Markable Google Maps panel.
     *
     * @param ctx The context.
     * @param ids The ids of Latitude and Longitude.
     * @return <code>MarkableGoogleMapsPanel</code>
     */
    private MarkableGoogleMapsPanel<SInstance> createMarkableGoogleMapsPanel(WicketBuildContext ctx, LatLongMarkupIds ids) {
        return new MarkableGoogleMapsPanel<SInstance>(ids, ctx.getModel(), ctx.getViewSupplier(SViewCurrentLocation.class),
                ctx.getViewMode().isVisualization(), true) {

            @Override
            public String getKmlUrl() {
                String kmlUrl = super.getKmlUrl();
                if (kmlUrl == null) {
                    SILatitudeLongitudeMultipleMarkable currentInstance = ctx.getCurrentInstance();
                    if (currentInstance.hasFile()) {
                        kmlUrl = createTempFile(currentInstance.getFile());
                        setKmlUrl(kmlUrl);
                    }
                }
                return kmlUrl;
            }
        };
    }

    /**
     * Method for return the absolute Path for the application.
     *
     * @return the absolute path for the application.
     */
    private static String toAbsolutePath() {
        HttpServletRequest req = (HttpServletRequest) (RequestCycle.get().getRequest()).getContainerRequest();
        return req.getRequestURL().substring(0, req.getRequestURL().toString().length() - req.getRequestURI().length());
    }


    /**
     * Method responsible for creating latitude and longitude in the map when a markable is adding,
     * this will also create a markable in the map when the latitude and longitude is setting.
     *
     *
     * @param points       The points whose contains the latitude and longitude data.
     * @param zoomInstance
     * @return The ajax behavior for include in the JS callback.
     */
    private AbstractDefaultAjaxBehavior createBehaviorAddPoint(final IModel<SIList<SInstance>> points, SInstanceFieldModel<SInstance> zoomInstance) {
        return new AbstractDefaultAjaxBehavior() {
            @Override
            protected void respond(AjaxRequestTarget target) {
                SIList list = points.getObject();
                SIComposite sInstance = (SIComposite) list.addNew();
                StringValue lat = RequestCycle.get().getRequest().getRequestParameters().getParameterValue("lat");
                StringValue lng = RequestCycle.get().getRequest().getRequestParameters().getParameterValue("lng");
                sInstance.setValue(STypeLatitudeLongitude.FIELD_LATITUDE, lat.toString("").replaceAll("\\.", ","));
                sInstance.setValue(STypeLatitudeLongitude.FIELD_LONGITUDE, lng.toString("").replaceAll("\\.", ","));
                updateZoomInstance();
                target.add(pointsCtx.getContainer());
            }

            /**
             * This method update the zoom instance.
             */
            private void updateZoomInstance() {
                StringValue zoom = RequestCycle.get().getRequest().getRequestParameters().getParameterValue("zoom");
                zoomInstance.getObject().setValue(zoom);
            }
        };
    }

}
