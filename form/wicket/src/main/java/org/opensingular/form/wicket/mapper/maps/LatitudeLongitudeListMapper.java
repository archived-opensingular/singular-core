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

import javax.servlet.http.HttpServletRequest;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.util.string.StringValue;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SIList;
import org.opensingular.form.SInstance;
import org.opensingular.form.type.core.attachment.IAttachmentRef;
import org.opensingular.form.type.util.STypeLatitudeLongitude;
import org.opensingular.form.type.util.STypeLatitudeLongitudeMultipleMarkable;
import org.opensingular.form.view.SViewCurrentLocation;
import org.opensingular.form.wicket.WicketBuildContext;
import org.opensingular.form.wicket.mapper.TableListMapper;
import org.opensingular.form.wicket.mapper.attachment.AttachmentPublicMapperResource;
import org.opensingular.form.wicket.mapper.attachment.AttachmentPublicResource;
import org.opensingular.form.wicket.mapper.attachment.single.FileUploadPanel;
import org.opensingular.form.wicket.mapper.buttons.AddButton;
import org.opensingular.form.wicket.mapper.components.ConfirmationModal;
import org.opensingular.form.wicket.model.SInstanceFieldModel;
import org.opensingular.form.wicket.model.SInstanceValueModel;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSContainer;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSGrid;
import org.opensingular.lib.wicket.util.util.WicketUtils;

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
public class LatitudeLongitudeListMapper extends TableListMapper {

    private WicketBuildContext pointsCtx;

    /**
     * Target before remove element of table.
     *
     * @param target target the pointsCtx.
     */
    @Override
    protected void behaviorAfterRemoveButtonClick(AjaxRequestTarget target) {
        target.add(pointsCtx.getParent().getContainer());
    }

    @Override
    public void buildView(WicketBuildContext ctx) {
        confirmationModal = ctx.getExternalContainer().newComponent(ConfirmationModal::new);

        LatLongMarkupIds ids = new LatLongMarkupIds();
        WicketBuildContext zoomCtx = createZoomField(ctx, ids);
        final MarkableGoogleMapsPanel<SInstance> googleMapsPanel = createMarkableGoogleMapsPanel(ctx, ids);
        ctx.getContainer().newFormGroup().appendDiv(googleMapsPanel);
        createBooleanField(ctx);
        IModel<SIList<SInstance>> points = createPointField(ctx);
        createUploadField(ctx, googleMapsPanel, points);

        AbstractDefaultAjaxBehavior addPoint = createBehaviorAddPoint(points, ctx.getContainer());
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

        confirmationModal.registerListener(googleMapsPanel::updateJS);

    }

    /**
     * Method for create and configure the points field.
     * The points field is a <code>STypeList</code> for latitude and longitude.
     *
     * @param ctx The context.
     * @return A list with the points.
     */
    private IModel<SIList<SInstance>> createPointField(WicketBuildContext ctx) {
        IModel<SIList<SInstance>> points = new SInstanceFieldModel<>(ctx.getModel(), STypeLatitudeLongitudeMultipleMarkable.FIELD_POINTS);
        pointsCtx = ctx.createChild(ctx.getContainer().newGrid(), ctx.getExternalContainer(), points);
        pointsCtx.build();
        return points;
    }

    /**
     * Create the Zoom Field, this is a hidden field for know how zoom is setting.
     *
     * @param ctx              The context.
     * @param latLongMarkupIds A object that contains the Markup Id of Zoom.
     * @return the field created.
     */
    private WicketBuildContext createZoomField(WicketBuildContext ctx, LatLongMarkupIds latLongMarkupIds) {
        WicketBuildContext zoomCtx = createField(ctx, ctx.getContainer().newGrid(), STypeLatitudeLongitudeMultipleMarkable.FIELD_ZOOM);
        zoomCtx.getContainer().visitChildren((TextField.class), (object, visit) -> {
            String nameSimple = ((SInstanceValueModel<?>) object.getDefaultModel()).getSInstance().getType().getNameSimple();
            if (nameSimple.equals(STypeLatitudeLongitudeMultipleMarkable.FIELD_ZOOM)) {
                latLongMarkupIds.zoomId = object.getMarkupId();
            }
        });
        return zoomCtx;
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
        uploadCtx.getContainer().add(new Behavior() {

            @Override
            public void onConfigure(Component component) {
                super.onConfigure(component);
                googleMapsPanel.populateMetaData(!pointsCtx.getContainer().isVisible());
            }
        });
        createConsumersForUploadPanel(googleMapsPanel, points, uploadCtx);
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
                        //Create a temp file for the Google API use.
                        String urlFile = AttachmentPublicResource.createTempPublicFile(panel, getAttachmentModel(panel), AttachmentPublicMapperResource.APPLICATION_MAP_KEY);
                        googleMapsPanel.includeKmlFile(toAbsolutePath() + urlFile);
                        target.add(pointsCtx.getParent().getContainer());
                    });

                    panel.setConsumerAfterRemoveImage(target -> {
                        googleMapsPanel.includeKmlFile("");
                        target.add(pointsCtx.getParent().getContainer());
                    });
                });
    }

    /**
     * Method responsible for crate the Markable Google Maps panel.
     *
     * @param ctx The context.
     * @param ids The ids of Latitude and Longitude.
     * @return <code>MarkableGoogleMapsPanel</code>
     */
    private MarkableGoogleMapsPanel<SInstance> createMarkableGoogleMapsPanel(WicketBuildContext ctx, LatLongMarkupIds ids) {
        return new MarkableGoogleMapsPanel<>(ids, ctx.getModel(), ctx.getViewSupplier(SViewCurrentLocation.class),
                ctx.getViewMode().isVisualization(), true);
    }

    /**
     * Method responsible for retrieve the file uploaded in the form.
     *
     * @param panel The file upload panel.
     * @return The model of the Attachment file.
     */
    private IModel<IAttachmentRef> getAttachmentModel(FileUploadPanel panel) {
        String id = panel.getModel().getObject().getFileId();
        IModel<IAttachmentRef> attachmentRef = new Model<>();
        panel.getModelObject()
                .getDocument()
                .getAttachmentPersistencePermanentHandler()
                .ifPresent(c -> attachmentRef.setObject(c.getAttachment(id)));
        if (attachmentRef.getObject() == null) {
            attachmentRef.setObject(panel.getModelObject()
                    .getDocument().getAttachmentPersistenceTemporaryHandler().getAttachment(id));
        }
        return attachmentRef;
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
     * And create a markable in the map when the latitude and longitude is setting.
     *
     * @param points    The points whose contains the latitude and longitude data.
     * @param container The container whose will be rendered.
     * @return The ajax behavior for include in the JS callback.
     */
    private AbstractDefaultAjaxBehavior createBehaviorAddPoint(final IModel<SIList<SInstance>> points, BSContainer<?> container) {
        return new AbstractDefaultAjaxBehavior() {
            @Override
            protected void respond(AjaxRequestTarget target) {
                SIList list = points.getObject();
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
