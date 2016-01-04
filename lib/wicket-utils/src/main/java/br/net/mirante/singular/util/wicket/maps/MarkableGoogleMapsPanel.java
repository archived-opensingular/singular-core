package br.net.mirante.singular.util.wicket.maps;

import org.apache.wicket.Component;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.PackageResourceReference;

import br.net.mirante.singular.util.wicket.util.WicketUtils;

/**
 * Created by danilo.mesquita on 04/01/2016.
 */
public class MarkableGoogleMapsPanel<T> extends Panel {

    private final WebMarkupContainer map = new WebMarkupContainer("map");
    private final HiddenField<T> lat = new HiddenField<>("lat");
    private final HiddenField<T> lng = new HiddenField<>("lng");

    @Override
    public void renderHead(IHeaderResponse response) {

        final PackageResourceReference apiJS = new PackageResourceReference(MarkableGoogleMapsPanel.class, "GoogleMapsApi.js");
        final PackageResourceReference customJS = new PackageResourceReference(MarkableGoogleMapsPanel.class, "MarkableGoogleMapsPanel.js");
        final String initScript = "createBelverMap(%s,%s,%s);";

        response.render(JavaScriptReferenceHeaderItem.forReference(apiJS, true));
        response.render(JavaScriptReferenceHeaderItem.forReference(customJS));
        response.render(OnDomReadyHeaderItem.forScript(String.format(initScript, stringfyId(map), stringfyId(lat), stringfyId(lng))));

        super.renderHead(response);
    }

    public MarkableGoogleMapsPanel(String id, IModel<T> latModel, IModel<T> lngModel) {
        super(id);
        lat.setModel(latModel);
        lng.setModel(lngModel);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(map);
        add(lat);
        add(lng);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        this.add(WicketUtils.$b.attrAppender("style", "height: " + getHeight() + "px;", ""));
    }

    protected Integer getHeight() {
        return 500;
    }

    private String stringfyId(Component c) {
        return "'" + c.getMarkupId(true) + "'";
    }

}
