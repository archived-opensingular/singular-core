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

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.json.JSONObject;
import org.opensingular.form.SInstance;
import org.opensingular.form.type.util.STypeLatitudeLongitude;
import org.opensingular.form.view.SView;
import org.opensingular.form.view.SViewCurrentLocation;
import org.opensingular.form.wicket.model.SInstanceFieldModel;
import org.opensingular.form.wicket.model.SInstanceValueModel;
import org.opensingular.lib.commons.base.SingularProperties;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSContainer;
import org.opensingular.lib.wicket.util.bootstrap.layout.TemplatePanel;
import org.opensingular.lib.wicket.util.util.WicketUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.opensingular.lib.wicket.util.util.Shortcuts.$m;

public class MarkableGoogleMapsPanel<T> extends BSContainer {

    private static final Logger LOGGER        = LoggerFactory.getLogger(MarkableGoogleMapsPanel.class);
    private static final String PANEL_SCRIPT  = "MarkableGoogleMapsPanel.js";

    private static final String SINGULAR_GOOGLEMAPS_JS_KEY     = "singular.googlemaps.js.key";
    private static final String SINGULAR_GOOGLEMAPS_STATIC_KEY = "singular.googlemaps.static.key";
    public static final  String MAP_ID                         = "map";
    public static final  String MAP_STATIC_ID                  = "mapStatic";
    private final LatLongMarkupIds ids;

    private final String singularKeyMaps      = SingularProperties.getOpt(SINGULAR_GOOGLEMAPS_JS_KEY).orElse(null);
    private final String singularKeyMapStatic = SingularProperties.getOpt(SINGULAR_GOOGLEMAPS_STATIC_KEY).orElse(null);

    private final IModel<String> metaDataModel = new Model<>();
    private final boolean visualization;
    private final SView   view;

    private IModel<SInstance> latitudeModel;
    private IModel<SInstance> longitudeModel;
    private IModel<SInstance> zoomModel;
    private boolean           multipleMarkers;
    private String            callbackUrl;
    private String            tableContainerId;

    private final Button       clearButton;
    private final Button       currentLocationButton;
    private final WebMarkupContainer verNoMaps;
    private final ImgMap       mapStatic;
    private final WebMarkupContainer  map      = new WebMarkupContainer(MAP_ID);
    private final HiddenField<String> metaData = new HiddenField<>("metadados", metaDataModel);

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        final PackageResourceReference customJS = new PackageResourceReference(getClass(), PANEL_SCRIPT);
        response.render(JavaScriptReferenceHeaderItem.forReference(customJS));
        if (StringUtils.isNotBlank(singularKeyMapStatic) && StringUtils.isNotBlank(singularKeyMaps)) {
            response.render(OnDomReadyHeaderItem.forScript("Singular.createSingularMap(" + stringfyId(metaData) + ", '" + singularKeyMaps + "');"));
        }
    }

    public void updateJS(AjaxRequestTarget target) {
        if (StringUtils.isNotBlank(singularKeyMapStatic) && StringUtils.isNotBlank(singularKeyMaps)) {
            target.appendJavaScript("window.setTimeout(function () {console.log('teste');Singular.createSingularMap(" + stringfyId(metaData) + ", '" + singularKeyMaps + "');}, 500);");
        }
    }

    public MarkableGoogleMapsPanel(LatLongMarkupIds ids, IModel<? extends SInstance> model, SView view, boolean visualization, boolean multipleMarkers) {
        super(model.getObject().getName());
        this.visualization = visualization;
        this.ids = ids;
        this.view = view;
        this.clearButton = new Button("clearButton", $m.ofValue("Limpar"));
        this.currentLocationButton = new Button("currentLocationButton", $m.ofValue("Marcar Minha Posição"));

        latitudeModel = new SInstanceValueModel<>(new SInstanceFieldModel<>(model, STypeLatitudeLongitude.FIELD_LATITUDE));
        longitudeModel = new SInstanceValueModel<>(new SInstanceFieldModel<>(model, STypeLatitudeLongitude.FIELD_LONGITUDE));
        zoomModel = new SInstanceValueModel<>(new SInstanceFieldModel<>(model, STypeLatitudeLongitude.FIELD_ZOOM));
        if (!multipleMarkers) {

            LoadableDetachableModel<String> googleMapsLinkModel = $m.loadable(() -> {
                if (latitudeModel.getObject() != null && longitudeModel.getObject() != null) {
                    String localization = latitudeModel.getObject() + "," + longitudeModel.getObject() + "/@" + latitudeModel.getObject() + "," + longitudeModel.getObject();
                    return "https://www.google.com.br/maps/place/" + localization + "," + zoomModel.getObject() + "z";
                } else {
                    return "https://www.google.com.br/maps/search/-15.7481632,-47.8872134,15";
                }
            });

            verNoMaps = new ExternalLink("verNoMaps", googleMapsLinkModel, $m.ofValue("Visualizar no Google Maps")) {
                @Override
                protected void onComponentTag(ComponentTag tag) {
                    super.onComponentTag(tag);
                    tag.put("target", "_blank");
                }
            };
        } else {
            verNoMaps = new WebMarkupContainer("verNoMaps");
        }

        clearButton.setDefaultFormProcessing(false);
        currentLocationButton.setDefaultFormProcessing(false);

        mapStatic = new ImgMap(MAP_STATIC_ID, $m.loadable(() -> {
            String latLng = "-15.7922, -47.4609";
            if (latitudeModel.getObject() != null && longitudeModel.getObject() != null)
                latLng = latitudeModel.getObject() + "," + longitudeModel.getObject();

            String marker = "&markers=" + latLng;
            if (("-15.7922, -47.4609").equals(latLng))
                marker = "";

            String parameters = "key=" + singularKeyMapStatic
                    + "&size=1000x" + (getHeight() - 35)
                    + "&zoom=" + zoomModel.getObject()
                    + "&center=" + latLng
                    + marker;

            return "https://maps.googleapis.com/maps/api/staticmap?" + parameters;
        }));
    }

    private void populateMetaData() {
        JSONObject json = new JSONObject();
        json.put("idClearButton", clearButton.getMarkupId(true));
        json.put("idCurrentLocationButton", currentLocationButton.getMarkupId(true));
        json.put("idMap", map.getMarkupId(true));
        json.put("idLat", ids.latitudeId);
        json.put("idLng", ids.longitudeId);
        json.put("idZoom", ids.zoomId);
        json.put("readOnly", isReadOnly());
        json.put("tableContainerId", tableContainerId);
        json.put("callbackUrl", callbackUrl);
        json.put("multipleMarkers", multipleMarkers);
        metaDataModel.setObject(json.toString());
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();


        TemplatePanel panelErrorMsg = newTemplateTag(tt -> {
            final StringBuilder templateBuilder = new StringBuilder();
            templateBuilder.append("<div> <label class=\"text-danger\" wicket:id='errorMapStatic'></label> </div>");
            templateBuilder.append("<div> <label class=\"text-danger\" wicket:id='errorMapJS'></label> </div>");
            return templateBuilder.toString();
        });

        TemplatePanel templatePanel = newTemplateTag(tt -> {
            final StringBuilder templateBuilder = new StringBuilder();
            templateBuilder.append(" <div class=\"form-group\"> ");
            templateBuilder.append("    <input type=\"button\" class=\"btn btn-default\" wicket:id=\"clearButton\"> ");
            templateBuilder.append("    <input type=\"button\" class=\"btn btn-default\" wicket:id=\"currentLocationButton\"> ");
            templateBuilder.append("    <a class=\"btn btn-default\" wicket:id=\"verNoMaps\"></a> ");
            templateBuilder.append(" </div>");
            templateBuilder.append(" <div wicket:id=\"map\" style=\"height: 90%;\"> </div> ");
            templateBuilder.append(" <input type=\"hidden\" wicket:id=\"metadados\"> ");
            templateBuilder.append("<div class=\"form-group\">");
            templateBuilder.append("    <img wicket:id=\"mapStatic\" > ");
            templateBuilder.append(" </div>");
            return templateBuilder.toString();
        });

        Component errorMapStatic = new Label("errorMapStatic", "Não foi encontrada a Key do Google Maps Static no arquivo singular.properties").setVisible(false);
        Component errorMapJS     = new Label("errorMapJS", "Não foi encontrada a Key do Google Maps JS no arquivo singular.properties").setVisible(false);

        panelErrorMsg.add(errorMapJS, errorMapStatic);
        templatePanel.add(verNoMaps, clearButton, currentLocationButton, map, metaData, mapStatic);

        if (StringUtils.isBlank(singularKeyMapStatic)) {
            templatePanel.setVisible(false);
            errorMapStatic.setVisible(true);
        }
        if (StringUtils.isBlank(singularKeyMaps)) {
            templatePanel.setVisible(false);
            errorMapJS.setVisible(true);
        }
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        populateMetaData();

        visitChildren(FormComponent.class, (comp, visit) -> comp.setEnabled(!isVisualization()));
        this.add(WicketUtils.$b.attrAppender("style", "height: " + getHeight() + "px;", ""));

        map.setVisible(!isVisualization() || multipleMarkers);
        clearButton.setVisible(!isVisualization() && !multipleMarkers);
        currentLocationButton.setVisible(SViewCurrentLocation.class.isInstance(view) && !isVisualization());


        mapStatic.setVisible(isVisualization() && !multipleMarkers);
        verNoMaps.setVisible(isVisualization() && !multipleMarkers);
    }

    protected Integer getHeight() {
        return 500;
    }

    private String stringfyId(Component c) {
        return "'" + c.getMarkupId(true) + "'";
    }

    private boolean isVisualization() {
        return visualization;
    }

    private boolean isReadOnly() {
        boolean truth = visualization;
        if (SViewCurrentLocation.class.isInstance(view)) {
            truth |= SViewCurrentLocation.class.cast(view).isDisableUserLocationSelection();
        }
        return truth;
    }

    public void enableMultipleMarkers(String callbackUrl, String tableContainerId) {
        this.multipleMarkers = true;
        this.callbackUrl = callbackUrl;
        this.tableContainerId = tableContainerId;
    }


    private static class ImgMap extends WebComponent {
        public ImgMap(String id, IModel<?> model) {
            super(id, model);
        }

        @Override
        protected void onComponentTag(ComponentTag tag) {
            super.onComponentTag(tag);
            checkComponentTag(tag, "img");
            tag.put("src", StringEscapeUtils.unescapeHtml4(getDefaultModelObjectAsString()));
        }
    }
}
