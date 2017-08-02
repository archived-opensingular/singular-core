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
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.util.template.PackageTextTemplate;
import org.opensingular.form.SInstance;
import org.opensingular.form.type.util.STypeLatitudeLongitude;
import org.opensingular.form.wicket.model.SInstanceFieldModel;
import org.opensingular.form.wicket.model.SInstanceValueModel;
import org.opensingular.lib.commons.base.SingularProperties;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSContainer;
import org.opensingular.lib.wicket.util.bootstrap.layout.TemplatePanel;
import org.opensingular.lib.wicket.util.util.WicketUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.opensingular.lib.wicket.util.util.Shortcuts.$m;

public class MarkableGoogleMapsPanel<T> extends BSContainer {

    private static final Logger LOGGER = LoggerFactory.getLogger(MarkableGoogleMapsPanel.class);
    private static final String PANEL_SCRIPT = "MarkableGoogleMapsPanel.js";
    private static final String METADATA_JSON = "MarkableGoogleMapsPanelMetadata.json";
    private static final Integer DEFAULT_ZOOM = 4;
    private static final String SINGULAR_GOOGLEMAPS_KEY = "singular.googlemaps.key";
    public static final String MAP_ID = "map";
    public static final String MAP_STATIC_ID = "mapStatic";

    private final IModel<String> metadadosModel = new Model<>();
    private final IModel<String> zoomModel = Model.of(DEFAULT_ZOOM.toString());
    private final IModel<Boolean> readOnly = Model.of(Boolean.FALSE);

    private IModel<SInstance> latitudeModel;
    private IModel<SInstance> longitudeModel;

    private final ImgMap mapStatic;

    private final WebMarkupContainer map = new WebMarkupContainer(MAP_ID);
    private final HiddenField<String> metadados = new HiddenField<>("metadados", metadadosModel);
    private final HiddenField<String> zoomField = new HiddenField<>("zoomField", zoomModel);

    private final String lat;
    private final String lng;
    private final Button cleanButton;
    private final ExternalLink verNoMaps;

    @Override
    public void renderHead(IHeaderResponse response) {
        String property = SingularProperties.get().getProperty(SINGULAR_GOOGLEMAPS_KEY);
        if(property == null)
            property = "AIzaSyALU10ekJ7BQ8jBbMyiCfBK4Yw3giSRmqk";

        final PackageResourceReference customJS = new PackageResourceReference(getClass(), PANEL_SCRIPT);

        response.render(JavaScriptReferenceHeaderItem.forReference(customJS));
        response.render(OnDomReadyHeaderItem.forScript("createSingularMap(" + stringfyId(metadados) + ", '" + property + "');"));

        super.renderHead(response);
    }

    public MarkableGoogleMapsPanel(IModel<? extends SInstance> model, String lat, String lng) {
        super(model.getObject().getName());
        this.lat = lat;
        this.lng = lng;
        this.cleanButton = new Button("cleanButton", $m.ofValue("Limpar"));

        latitudeModel = new SInstanceValueModel<>(new SInstanceFieldModel<>(model, STypeLatitudeLongitude.FIELD_LATITUDE));
        longitudeModel= new SInstanceValueModel<>(new SInstanceFieldModel<>(model, STypeLatitudeLongitude.FIELD_LONGITUDE));

        LoadableDetachableModel<String> googleMapsLinkModel = $m.loadable(()->{
            if(latitudeModel.getObject() != null && longitudeModel.getObject() != null){
                return "https://www.google.com.br/maps/search/"+latitudeModel.getObject()+","+longitudeModel.getObject();
            }else {
                return "https://www.google.com.br/maps/dir/@-15.7481632,-47.8872134,15z";
            }
        });
        verNoMaps = new ExternalLink("verNoMaps", googleMapsLinkModel, $m.ofValue("Visualizar no Google Maps")){
            @Override
            protected void onComponentTag(ComponentTag tag) {
                super.onComponentTag(tag);
                tag.put("target", "_blank");
            }
        };

        cleanButton.setDefaultFormProcessing(false);
        zoomModel.setObject(DEFAULT_ZOOM.toString());

        LoadableDetachableModel<String> mapStaticModel = $m.loadable(() -> {
            String latLng = "-15.7922, -47.4609";
            if(latitudeModel.getObject() != null && longitudeModel.getObject() != null)
                latLng =  latitudeModel.getObject() + "," + longitudeModel.getObject();

            String marker = "&markers="+latLng;
            if(latLng.equals("-15.7922, -47.4609")){
                marker = "";
            }

            String parameters = "key=AIzaSyDda6eqjAVOfU4HeV1j9ET-FRxZkagjnRQ"
                    + "&size=1000x" + getHeight()
                    + "&zoom="+zoomModel.getObject()
                    + "&center=" + latLng
                    + marker;

            return "https://maps.googleapis.com/maps/api/staticmap?" + parameters;
        });
        mapStatic = new ImgMap(MAP_STATIC_ID, mapStaticModel);
    }

    private void popularMetadados() {

        final Map<String, Object> properties = new HashMap<>();
        try (final PackageTextTemplate metadataJSON = new PackageTextTemplate(getClass(), METADATA_JSON)){
            properties.put("idButton", cleanButton.getMarkupId(true));
            properties.put("idMap", map.getMarkupId(true));
            properties.put("idLat", lat);
            properties.put("idLng", lng);
            properties.put("idZoom", zoomField.getMarkupId(true));
            properties.put("readOnly", isReadOnly());
            metadataJSON.interpolate(properties);
            metadadosModel.setObject(metadataJSON.getString());
            metadataJSON.close();
        } catch (IOException e) {
            LOGGER.error("Erro ao fechar stream", e);
        }
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        popularMetadados();

        TemplatePanel templatePanel = newTemplateTag(tt -> {
            final StringBuilder templateBuilder = new StringBuilder();
            templateBuilder.append(" <div class=\"form-group\"> ");
            templateBuilder.append("    <input type=\"button\" class=\"btn btn-default\" wicket:id=\"cleanButton\"> ");
            templateBuilder.append("    <a class=\"btn btn-default\" wicket:id=\"verNoMaps\"></a> ");
            templateBuilder.append(" </div>");
            templateBuilder.append(" <div wicket:id=\"map\" style=\"height: 100%;\"> </div> ");
            templateBuilder.append(" <input type=\"hidden\" wicket:id=\"metadados\"> ");
            templateBuilder.append(" <input type=\"hidden\" wicket:id=\"zoomField\"> ");
            templateBuilder.append("<div class=\"form-group\">");
            templateBuilder.append("    <img wicket:id=\"mapStatic\" > ");
            templateBuilder.append(" </div>");
            return templateBuilder.toString();
        });
        templatePanel.add(verNoMaps, cleanButton, map, metadados, mapStatic, zoomField);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();

        visitChildren(FormComponent.class, (comp, visit) ->comp.setEnabled( !isReadOnly()));
        this.add(WicketUtils.$b.attrAppender("style", "height: " + getHeight() + "px;", ""));

        map.setVisible(!isReadOnly());
        cleanButton.setVisible(!isReadOnly());

        mapStatic.setVisible(isReadOnly());
        verNoMaps.setVisible(isReadOnly());
    }

    protected Integer getHeight() {
        return 500;
    }

    private String stringfyId(Component c) {
        return "'" + c.getMarkupId(true) + "'";
    }

    public MarkableGoogleMapsPanel<T> setReadOnly(boolean readOnly){
        this.readOnly.setObject(readOnly);
        return this;
    }

    protected boolean isReadOnly(){
        return readOnly.getObject();
    }

    private class ImgMap extends WebComponent {
        public ImgMap(String id, IModel<?> model) {
            super(id, model);
        }

        @Override
        protected void onComponentTag(ComponentTag tag) {
            super.onComponentTag(tag);
            checkComponentTag(tag, "img");
            tag.put("src", getDefaultModelObjectAsString().replaceAll("amp;", ""));
        }
    }
}
