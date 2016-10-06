/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.form.wicket.mapper;

import org.opensingular.form.SInstance;
import org.opensingular.form.type.util.STypeLatitudeLongitude;
import org.opensingular.singular.form.wicket.IWicketComponentMapper;
import org.opensingular.singular.form.wicket.WicketBuildContext;
import org.opensingular.singular.form.wicket.model.SInstanceValueModel;
import org.opensingular.singular.form.wicket.model.SInstanceFieldModel;
import org.opensingular.singular.util.wicket.bootstrap.layout.BSControls;
import org.opensingular.singular.util.wicket.maps.MarkableGoogleMapsPanel;
import org.apache.wicket.model.IModel;

public class LatitudeLongitudeMapper implements IWicketComponentMapper {

    @Override
    public void buildView(WicketBuildContext ctx) {

        final BSControls formGroup = ctx.getContainer().newFormGroup();
        final IModel<? extends SInstance> model = ctx.getModel();

        final IModel<SInstance> latModel = createValorModel(model, STypeLatitudeLongitude.FIELD_LATITUDE);
        final IModel<SInstance> lngModel = createValorModel(model, STypeLatitudeLongitude.FIELD_LONGITUDE);

        final MarkableGoogleMapsPanel<SInstance> googleMapsPanel = new MarkableGoogleMapsPanel<>(model.getObject().getName(), latModel, lngModel);

        googleMapsPanel.setReadOnly(ctx.getViewMode().isVisualization());

        formGroup.appendDiv(googleMapsPanel);
    }

    private IModel<SInstance> createValorModel(IModel<? extends SInstance> root, String path) {
        return new SInstanceValueModel<>(new SInstanceFieldModel<>(root, path));
    }

}
