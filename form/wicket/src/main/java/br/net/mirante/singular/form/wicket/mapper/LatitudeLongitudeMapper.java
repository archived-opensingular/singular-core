package br.net.mirante.singular.form.wicket.mapper;

import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.core.MTipoLatitudeLongitude;
import br.net.mirante.singular.form.wicket.IWicketComponentMapper;
import br.net.mirante.singular.form.wicket.WicketBuildContext;
import br.net.mirante.singular.form.wicket.model.MInstanciaCampoModel;
import br.net.mirante.singular.form.wicket.model.MInstanciaValorModel;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSControls;
import br.net.mirante.singular.util.wicket.maps.MarkableGoogleMapsPanel;

/**
 * Created by danilo.mesquita on 04/01/2016.
 */
public class LatitudeLongitudeMapper implements IWicketComponentMapper {

    @Override
    public void buildView(WicketBuildContext ctx) {

        final BSContainer<?> container = ctx.getContainer();
        final BSControls formGroup = container.newFormGroup();

        final MInstanciaValorModel<MInstancia> latModel = createModel(ctx.getModel(), MTipoLatitudeLongitude.FIELD_LATITUDE);
        final MInstanciaValorModel<MInstancia> lngModel = createModel(ctx.getModel(), MTipoLatitudeLongitude.FIELD_LONGITUDE);

        formGroup.appendDiv(new MarkableGoogleMapsPanel<>("googleMapsPanel", latModel, lngModel).setReadOnly(ctx.getViewMode().isVisualization()));
    }

    private MInstanciaValorModel<MInstancia> createModel(IModel<? extends MInstancia> root, String path) {
        return new MInstanciaValorModel<>(new MInstanciaCampoModel<>(root, path));
    }

}
