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

import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.resource.ContentDisposition;
import org.apache.wicket.request.resource.SharedResourceReference;
import org.apache.wicket.util.string.StringValue;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SIList;
import org.opensingular.form.SInstance;
import org.opensingular.form.type.core.attachment.IAttachmentRef;
import org.opensingular.form.type.core.attachment.SIAttachment;
import org.opensingular.form.type.util.STypeLatitudeLongitude;
import org.opensingular.form.type.util.STypeLatitudeLongitudeMultipleMarkable;
import org.opensingular.form.view.SView;
import org.opensingular.form.view.SViewCurrentLocation;
import org.opensingular.form.wicket.WicketBuildContext;
import org.opensingular.form.wicket.mapper.AbstractControlsFieldComponentMapper;
import org.opensingular.form.wicket.mapper.TableListMapper;
import org.opensingular.form.wicket.mapper.attachment.AttachmentPublicMapperResource;
import org.opensingular.form.wicket.mapper.attachment.single.FileUploadPanel;
import org.opensingular.form.wicket.mapper.buttons.AddButton;
import org.opensingular.form.wicket.mapper.components.ConfirmationModal;
import org.opensingular.form.wicket.mapper.tablelist.TableListPanel;
import org.opensingular.form.wicket.model.SInstanceFieldModel;
import org.opensingular.form.wicket.model.SInstanceValueModel;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSContainer;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSGrid;
import org.opensingular.lib.wicket.util.util.WicketUtils;

import static org.opensingular.form.wicket.mapper.attachment.AttachmentPublicMapperResource.sessionKey;
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
        SInstanceFieldModel<SInstance> zoom = new SInstanceFieldModel<>(ctx.getModel(), STypeLatitudeLongitudeMultipleMarkable.FIELD_ZOOM);
        WicketBuildContext zoomCtx = ctx.createChild(ctx.getContainer().newGrid(), ctx.getExternalContainer(), zoom);
        zoomCtx.build();

        LatLongMarkupIds ids = new LatLongMarkupIds();

        zoomCtx.getContainer().visitChildren((TextField.class), (object, visit) -> {
            String nameSimple = ((SInstanceValueModel<?>) object.getDefaultModel()).getSInstance().getType().getNameSimple();
            if (nameSimple.equals(STypeLatitudeLongitudeMultipleMarkable.FIELD_ZOOM)) {
                ids.zoomId = object.getMarkupId();
            }
        });

        final MarkableGoogleMapsPanel<SInstance> googleMapsPanel = createMarkableGoogleMapsPanel(ctx, ids);
        BSGrid gridGoogleMaps = ctx.getContainer().newGrid();


        IModel<SIList<SInstance>> points = new SInstanceFieldModel<>(ctx.getModel(), STypeLatitudeLongitudeMultipleMarkable.FIELD_POINTS);
        ctx.setHint(AbstractControlsFieldComponentMapper.NO_DECORATION, Boolean.TRUE);
        pointsCtx = ctx.createChild(gridGoogleMaps, ctx.getExternalContainer(), points);
        ctx.getContainer().newFormGroup().appendDiv(googleMapsPanel);

        configureTablePointsView(points);
        TableListPanel table = buildPanel(pointsCtx, "table");
        ctx.getContainer().newFormGroup().appendDiv(table);


        IModel<SIAttachment> file = new SInstanceFieldModel<>(ctx.getModel(), STypeLatitudeLongitudeMultipleMarkable.FIELD_FILE);
        WicketBuildContext fileCtx = ctx.createChild(ctx.getContainer().newGrid(), ctx.getExternalContainer(), file);
        fileCtx.build();

        createConsumersForUploadPanel(googleMapsPanel, points, table, fileCtx);

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
     * This method is responsible for creating the consumers for the upload panel.
     * Created a consumer after upload the file, and other after removed the file.
     *
     * @param googleMapsPanel The google maps panel.
     * @param points          The points of the table.
     * @param table           The table.
     * @param fileCtx         The file context that will be included the consumers.
     */
    private void createConsumersForUploadPanel(MarkableGoogleMapsPanel<SInstance> googleMapsPanel, IModel<SIList<SInstance>> points,
            TableListPanel table, WicketBuildContext fileCtx) {
        WicketUtils.findFirstChild(fileCtx.getContainer(), FileUploadPanel.class)
                .ifPresent(panel -> {
                    panel.setConsumerAfterLoadImage(target -> {
                        points.getObject().clearInstance();
                        table.setVisible(false);
                        String urlFile = createTempPublicFile(panel);
                        googleMapsPanel.includeKmlFile(toAbsolutePath() + urlFile);
                        target.add(pointsCtx.getParent().getContainer());
                    });

                    panel.setConsumerAfterRemoveImage(target -> {
                        table.setVisible(true);
                        googleMapsPanel.includeKmlFile("");
                        target.add(pointsCtx.getParent().getContainer());
                    });
                });
    }

    /**
     * Method for configure the view for the ctx of the table.
     *
     * @param points The points whose contains the view.
     */
    private void configureTablePointsView(IModel<SIList<SInstance>> points) {
        SView viewPoints = points.getObject().getType().getSuperType().getView();
        pointsCtx.setView(viewPoints);
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
     * Create a public file that will be used by google Maps API for render the KML File.
     *
     * @param panel The fileUpload.
     * @return return a public url for the file.
     */
    private String createTempPublicFile(FileUploadPanel panel) {
        //TODO verificar se essa logica para criação do arquivo temporario deve permanecer nessa classe.
        String name = panel.getModel().getObject().getFileName();
        String id = panel.getModel().getObject().getFileId();

        AttachmentPublicMapperResource attachmentResource;
        if (WebApplication.get().getSharedResources().get(sessionKey) == null) {
            WebApplication.get().mountResource(AttachmentPublicMapperResource.getMountPathPublic(),
                    new SharedResourceReference(sessionKey));
            attachmentResource = new AttachmentPublicMapperResource();
            WebApplication.get().getSharedResources().add(sessionKey, attachmentResource);
        } else {
            attachmentResource = (AttachmentPublicMapperResource) WebApplication.get().getSharedResources().get(sessionKey).getResource();
        }

        IModel<IAttachmentRef> attachmentRef = getAttachmentModel(panel, id);
        return attachmentResource.addAttachment(name, ContentDisposition.INLINE, attachmentRef.getObject());
    }

    /**
     * Method responsible for retrieve the file uploaded in the form.
     *
     * @param panel The file upload panel.
     * @param id    The id of the file uploaded.
     * @return The model of the Attachment file.
     */
    private IModel<IAttachmentRef> getAttachmentModel(FileUploadPanel panel, String id) {
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
        //TODO verificar se existe uma forma mais elegante de fazer isso.
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
