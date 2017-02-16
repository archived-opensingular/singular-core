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

package org.opensingular.lib.wicket.util.maps;

import org.apache.wicket.Component;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.util.template.PackageTextTemplate;
import org.opensingular.lib.wicket.util.util.WicketUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MarkableGoogleMapsPanel<T> extends Panel {

    private static final Logger LOGGER = LoggerFactory.getLogger(MarkableGoogleMapsPanel.class);
    private static final String PANEL_SCRIPT = "MarkableGoogleMapsPanel.js";
    private static final String METADATA_JSON = "MarkableGoogleMapsPanelMetadata.json";
    private static final Integer DEFAULT_ZOOM = 4;

    private final IModel<String> metadadosModel = new Model<>();
    private final IModel<Boolean> readOnly = Model.of(Boolean.FALSE);

    private final WebMarkupContainer map = new WebMarkupContainer("map");
    private final HiddenField<String> metadados = new HiddenField<>("metadados", metadadosModel);

    private final HiddenField<T> lat = new HiddenField<>("lat");
    private final HiddenField<T> lng = new HiddenField<>("lng");

    @Override
    public void renderHead(IHeaderResponse response) {

        final PackageResourceReference customJS = new PackageResourceReference(getClass(), PANEL_SCRIPT);

        response.render(JavaScriptReferenceHeaderItem.forReference(customJS));
        response.render(OnDomReadyHeaderItem.forScript("createBelverMap(" + stringfyId(metadados) + ");"));

        super.renderHead(response);
    }

    public MarkableGoogleMapsPanel(String id, IModel<T> latModel, IModel<T> lngModel) {
        super(id);
        lat.setModel(latModel);
        lng.setModel(lngModel);
    }

    private void popularMetadados() {

        final Map<String, Object> properties = new HashMap<>();
        try (final PackageTextTemplate metadataJSON = new PackageTextTemplate(getClass(), METADATA_JSON)){
            properties.put("idMap", map.getMarkupId(true));
            properties.put("idLat", lat.getMarkupId(true));
            properties.put("idLng", lng.getMarkupId(true));
            properties.put("zoom", DEFAULT_ZOOM);
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
        add(map, lat, lng, metadados);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        visitChildren(FormComponent.class, (comp, visit) -> comp.setEnabled( !isReadOnly()));
        this.add(WicketUtils.$b.attrAppender("style", "height: " + getHeight() + "px;", ""));
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
}