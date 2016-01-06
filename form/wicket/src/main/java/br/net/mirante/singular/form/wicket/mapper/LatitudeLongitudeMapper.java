package br.net.mirante.singular.form.wicket.mapper;

import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.core.MTipoLatitudeLongitude;
import br.net.mirante.singular.form.wicket.IWicketComponentMapper;
import br.net.mirante.singular.form.wicket.WicketBuildContext;
import br.net.mirante.singular.form.wicket.model.MInstanciaCampoModel;
import br.net.mirante.singular.form.wicket.model.MInstanciaValorModel;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSControls;
import br.net.mirante.singular.util.wicket.maps.MarkableGoogleMapsPanel;

public class LatitudeLongitudeMapper implements IWicketComponentMapper {

    @Override
    public void buildView(WicketBuildContext ctx) {

        final BSControls formGroup = ctx.getContainer().newFormGroup();
        final IModel<? extends MInstancia> model = ctx.getModel();

        final IModel<MInstancia> latModel = createValorModel(model, MTipoLatitudeLongitude.FIELD_LATITUDE);
        final IModel<MInstancia> lngModel = createValorModel(model, MTipoLatitudeLongitude.FIELD_LONGITUDE);

        final MarkableGoogleMapsPanel<MInstancia> googleMapsPanel = new MarkableGoogleMapsPanel<>(model.getObject().getNome(), latModel, lngModel);

        googleMapsPanel.setReadOnly(ctx.getViewMode().isVisualization());

        formGroup.appendDiv(googleMapsPanel);
    }

    private IModel<MInstancia> createValorModel(IModel<? extends MInstancia> root, String path) {
        return new MInstanciaValorModel<>(new MInstanciaCampoModel<>(root, path));
    }

}
