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
import org.opensingular.form.type.util.STypeLatitudeLongitudeMapper;
import org.opensingular.form.view.FileEventListener;
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

import static org.opensingular.lib.wicket.util.util.Shortcuts.$b;

public class LatitudeLongitudeListMapper extends TableListMapper {

    private WicketBuildContext pointsCtx;

    @Override
    protected void behaviorAfterRemoveButtonClick(AjaxRequestTarget target) {
        target.add(pointsCtx.getParent().getContainer());
    }

    @Override
    public void buildView(WicketBuildContext ctx) {
        confirmationModal = ctx.getExternalContainer().newComponent(ConfirmationModal::new);
        SInstanceFieldModel<SInstance> zoom = new SInstanceFieldModel<>(ctx.getModel(), STypeLatitudeLongitudeMapper.FIELD_ZOOM);
        WicketBuildContext zoomCtx = ctx.createChild(ctx.getContainer().newGrid(), ctx.getExternalContainer(), zoom);
        zoomCtx.build();

        LatLongMarkupIds ids = new LatLongMarkupIds();

        zoomCtx.getContainer().visitChildren((TextField.class), (object, visit) -> {
            String nameSimple = ((SInstanceValueModel<?>) object.getDefaultModel()).getSInstance().getType().getNameSimple();
            if (nameSimple.equals(STypeLatitudeLongitudeMapper.FIELD_ZOOM)) {
                ids.zoomId = object.getMarkupId();
            }
        });

        final MarkableGoogleMapsPanel<SInstance> googleMapsPanel = new MarkableGoogleMapsPanel<>(ids, ctx.getModel(), ctx.getViewSupplier(SViewCurrentLocation.class),
                ctx.getViewMode().isVisualization(), true);
        BSGrid gridGoogleMaps = ctx.getContainer().newGrid();


        IModel<SIList<SInstance>> points = new SInstanceFieldModel<>(ctx.getModel(), STypeLatitudeLongitudeMapper.FIELD_POINTS);
        ctx.setHint(AbstractControlsFieldComponentMapper.NO_DECORATION, Boolean.TRUE);
        pointsCtx = ctx.createChild(gridGoogleMaps, ctx.getExternalContainer(), points);
        ctx.getContainer().newFormGroup().appendDiv(googleMapsPanel);

        SView viewPoints = points.getObject().getType().getSuperType().getView();
        pointsCtx.setView(viewPoints);
        TableListPanel table = buildPanel(pointsCtx, "table");
        ctx.getContainer().newFormGroup().appendDiv(table);


        IModel<SIAttachment> file = new SInstanceFieldModel<>(ctx.getModel(), STypeLatitudeLongitudeMapper.FIELD_FILE);
        WicketBuildContext fileCtx = ctx.createChild(ctx.getContainer().newGrid(), ctx.getExternalContainer(), file);
        fileCtx.build();

        WicketUtils.findFirstChild(fileCtx.getContainer(), FileUploadPanel.class)
                .ifPresent(panel -> panel.registerFileRemovedListener((FileEventListener) attachment -> {
                    points.getObject().clearInstance();
                    table.setVisible(true);
                    googleMapsPanel.includeKmlFile("");
                    AjaxRequestTarget target = RequestCycle.get().find(AjaxRequestTarget.class);
                    target.add(pointsCtx.getParent().getContainer());
                }));

//        WicketUtils.findFirstChild(fileCtx.getContainer(), FileUploadPanel.class)
//                .ifPresent(panel -> panel.registerFileUploadedListener((FileEventListener) attachment -> {
//                    //Problema aparentemente é que é criado um novo contexto.
//                    points.getObject().clearInstance();
//                    table.setVisible(false);
//AjaxRequestTarget target = RequestCycle.get().find(AjaxRequestTarget.class);
    ////                target.add(gridGoogleMaps);
    //                }));

        WicketUtils.findFirstChild(fileCtx.getContainer(), FileUploadPanel.class)
                .ifPresent(panel -> panel.setConsumerAfterLoadImage((target) -> {
                    points.getObject().clearInstance();
                    table.setVisible(false);
                    String urlFile = createTempPublicFile(panel);
                    googleMapsPanel.includeKmlFile(toAbsolutePath() + urlFile);
                    target.add(pointsCtx.getParent().getContainer());            }));


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

    //TODO verificar se essa logica para criação do arquivo temporario deve permanecer nessa classe.
    private String createTempPublicFile(FileUploadPanel panel) {
        String name = panel.getModel().getObject().getFileName();
        String id = panel.getModel().getObject().getFileId();

        String publico = "publico";
        WebApplication.get().mountResource(AttachmentPublicMapperResource.getMountPathPublic(),
                new SharedResourceReference(publico));
        AttachmentPublicMapperResource attachmentResource = new AttachmentPublicMapperResource(publico);
        WebApplication.get().getSharedResources().add(publico, attachmentResource);

        IModel<IAttachmentRef> attachmentRef = new Model<>();
        panel.getModelObject()
                .getDocument()
                .getAttachmentPersistencePermanentHandler()
                .ifPresent(c -> attachmentRef.setObject(c.getAttachment(id)));
        if(attachmentRef.getObject() == null) {
            attachmentRef.setObject(panel.getModelObject()
                    .getDocument().getAttachmentPersistenceTemporaryHandler().getAttachment(id));
        }
        return attachmentResource.addAttachment(name, ContentDisposition.INLINE, attachmentRef.getObject());
    }

    //TODO verificar se existe uma forma mais elegante de fazer isso.
    private static String toAbsolutePath() {
        HttpServletRequest req = (HttpServletRequest) (RequestCycle.get().getRequest()).getContainerRequest();
        return req.getRequestURL().substring(0, req.getRequestURL().toString().length() - req.getRequestURI().length());
    }


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
