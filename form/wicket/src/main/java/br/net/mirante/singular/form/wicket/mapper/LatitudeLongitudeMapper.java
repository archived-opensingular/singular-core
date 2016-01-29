package br.net.mirante.singular.form.wicket.mapper;

import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.mform.SInstance2;
import br.net.mirante.singular.form.mform.core.STypeLatitudeLongitude;
import br.net.mirante.singular.form.wicket.IWicketComponentMapper;
import br.net.mirante.singular.form.wicket.WicketBuildContext;
import br.net.mirante.singular.form.wicket.model.SInstanceCampoModel;
import br.net.mirante.singular.form.wicket.model.MInstanciaValorModel;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSControls;
import br.net.mirante.singular.util.wicket.maps.MarkableGoogleMapsPanel;

public class LatitudeLongitudeMapper implements IWicketComponentMapper {

    @Override
    public void buildView(WicketBuildContext ctx) {

        final BSControls formGroup = ctx.getContainer().newFormGroup();
        final IModel<? extends SInstance2> model = ctx.getModel();

        final IModel<SInstance2> latModel = createValorModel(model, STypeLatitudeLongitude.FIELD_LATITUDE);
        final IModel<SInstance2> lngModel = createValorModel(model, STypeLatitudeLongitude.FIELD_LONGITUDE);

        final MarkableGoogleMapsPanel<SInstance2> googleMapsPanel = new MarkableGoogleMapsPanel<>(model.getObject().getNome(), latModel, lngModel);

        googleMapsPanel.setReadOnly(ctx.getViewMode().isVisualization());

        formGroup.appendDiv(googleMapsPanel);
    }

    private IModel<SInstance2> createValorModel(IModel<? extends SInstance2> root, String path) {
        return new MInstanciaValorModel<>(new SInstanceCampoModel<>(root, path));
    }

}
