/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.util.wicket.template;

import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.filter.HeaderResponseContainer;
import org.apache.wicket.markup.head.filter.JavaScriptFilteredIntoFooterHeaderResponse;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.PackageResourceReference;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class SingularTemplate extends WebPage {

    public static final String JAVASCRIPT_CONTAINER = "javascript-container";

    public final SkinOptions skinOptions = new SkinOptions();

    public SingularTemplate() {
        initSkins();
    }

    public SingularTemplate(IModel<?> model) {
        super(model);
        initSkins();
    }

    public SingularTemplate(PageParameters parameters) {
        super(parameters);
        initSkins();
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        getApplication()
                .setHeaderResponseDecorator(r -> new JavaScriptFilteredIntoFooterHeaderResponse(r, SingularTemplate.JAVASCRIPT_CONTAINER));
        getApplication()
                .getJavaScriptLibrarySettings()
                .setJQueryReference(new PackageResourceReference(SingularTemplate.class, "empty.js"));

        add(new Label("pageTitle", new ResourceModel(getPageTitleLocalKey())));
        add(new HeaderResponseContainer(JAVASCRIPT_CONTAINER, JAVASCRIPT_CONTAINER));
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        getDefaultCSSUrls().forEach(response::render);
        getDefaultJavaScriptsUrls().forEach(response::render);
        response.render(skinOptions.currentSkin().getRef());
    }

    protected String getPageTitleLocalKey() {
        return "label.page.title.local";
    }

    protected void initSkins() {
        skinOptions.addDefaulSkin("Default", CssHeaderItem.forUrl("/singular-static/resources/metronic/layout4/css/themes/default.css"));
        skinOptions.addSkin("Vermelho", CssHeaderItem.forUrl("/singular-static/resources/singular/themes/red.css"));
        skinOptions.addSkin("Verde", CssHeaderItem.forUrl("/singular-static/resources/singular/themes/green.css"));
        skinOptions.addSkin("Anvisa", CssHeaderItem.forUrl("/singular-static/resources/singular/themes/anvisa.css"));
        skinOptions.addSkin("Montreal", CssHeaderItem.forUrl("/singular-static/resources/singular/themes/montreal.css"));
    }

    public List<CssHeaderItem> getDefaultCSSUrls() {

        final List<CssHeaderItem> cssHeaderItens = new ArrayList<>();
        final Consumer<String> add = s -> cssHeaderItens.add(CssHeaderItem.forUrl(s));

        add.accept("/singular-static/resources/metronic/global/plugins/font-awesome/css/font-awesome.min.css");
        add.accept("/singular-static/resources/metronic/global/plugins/simple-line-icons/simple-line-icons.min.css");
        add.accept("/singular-static/resources/metronic/global/plugins/bootstrap/css/bootstrap.min.css");
        add.accept("/singular-static/resources/metronic/global/plugins/uniform/css/uniform.default.css");
        add.accept("/singular-static/resources/metronic/global/plugins/bootstrap-datepicker/css/bootstrap-datepicker.min.css");
        add.accept("/singular-static/resources/metronic/global/plugins/bootstrap-timepicker/css/bootstrap-timepicker.min.css");
        add.accept("/singular-static/resources/metronic/global/plugins/bootstrap-select/css/bootstrap-select.min.css");
        add.accept("/singular-static/resources/metronic/global/plugins/bootstrap-switch/css/bootstrap-switch.min.css");
        add.accept("/singular-static/resources/metronic/global/plugins/jquery-multi-select/css/multi-select.css");
        add.accept("/singular-static/resources/metronic/global/plugins/ion.rangeslider/css/normalize.css");
        add.accept("/singular-static/resources/metronic/global/plugins/ion.rangeslider/css/ion.rangeSlider.css");
        add.accept("/singular-static/resources/metronic/global/plugins/ion.rangeslider/css/ion.rangeSlider.skinHTML5.css");
        add.accept("/singular-static/resources/metronic/global/plugins/datatables/plugins/bootstrap/datatables.bootstrap.css");
        add.accept("/singular-static/resources/metronic/global/plugins/morris/morris.css");
        add.accept("/singular-static/resources/metronic/global/css/components-md.css");
        add.accept("/singular-static/resources/metronic/global/css/plugins-md.css");
        add.accept("/singular-static/resources/metronic/layout4/css/layout.css");
        add.accept("/singular-static/resources/metronic/global/plugins/jquery-file-upload/css/jquery.fileupload.css");
        add.accept("/singular-static/resources/singular/plugins/syntaxHighlighter/css/shCore.css");
        add.accept("/singular-static/resources/singular/plugins/syntaxHighlighter/css/shThemeDefault.css");
        add.accept("/singular-static/resources/metronic/global/plugins/bootstrap-toastr/toastr.min.css");
        add.accept("/singular-static/resources/metronic/global/plugins/typeahead/typeahead.css");
        add.accept("/singular-static/resources/singular/css/custom.css");
        add.accept("resources/custom/css/custom.css");

        return cssHeaderItens;
    }

    public List<JavaScriptHeaderItem> getDefaultJavaScriptsUrls() {

        final List<JavaScriptHeaderItem> scriptHeaderItens = new ArrayList<>();
        final Consumer<String> add = s -> scriptHeaderItens.add(JavaScriptHeaderItem.forUrl(s));
        final Consumer<String> addOnlyIfLtIE9 = s -> scriptHeaderItens.add(JavaScriptHeaderItem.forUrl(s, null, false, "UTF-8", "lt IE 9"));

        addOnlyIfLtIE9.accept("/singular-static/resourcesmetronic/global/plugins/respond.min.js");
        addOnlyIfLtIE9.accept("/singular-static/resourcesmetronic/global/plugins/excanvas.min.js");

        add.accept("/singular-static/resources/metronic/global/plugins/jquery-migrate.min.js");
        add.accept("/singular-static/resources/metronic/global/plugins/jquery-ui/jquery-ui.min.js");
        add.accept("/singular-static/resources/metronic/global/plugins/bootstrap/js/bootstrap.js");
        add.accept("/singular-static/resources/metronic/global/plugins/bootstrap-hover-dropdown/bootstrap-hover-dropdown.min.js");
        add.accept("/singular-static/resources/metronic/global/plugins/jquery-slimscroll/jquery.slimscroll.min.js");
        add.accept("/singular-static/resources/metronic/global/plugins/jquery.blockui.min.js");
        add.accept("/singular-static/resources/metronic/global/plugins/jquery.cokie.min.js");
        add.accept("/singular-static/resources/metronic/global/plugins/uniform/jquery.uniform.min.js");
        add.accept("/singular-static/resources/metronic/global/plugins/bootstrap-datepicker/js/bootstrap-datepicker.min.js");
        add.accept("/singular-static/resources/metronic/global/plugins/bootstrap-datepicker/locales/bootstrap-datepicker.pt-BR.min.js");
        add.accept("/singular-static/resources/metronic/global/plugins/bootstrap-timepicker/js/bootstrap-timepicker.min.js");
        add.accept("/singular-static/resources/metronic/global/plugins/bootstrap-select/js/bootstrap-select.min.js");
        add.accept("/singular-static/resources/metronic/global/plugins/bootstrap-switch/js/bootstrap-switch.min.js");
        add.accept("/singular-static/resources/metronic/global/plugins/jquery-multi-select/js/jquery.multi-select.js");
        add.accept("/singular-static/resources/metronic/global/plugins/jquery-inputmask/jquery.inputmask.bundle.min.js");
        add.accept("/singular-static/resources/metronic/global/plugins/datatables/datatables.min.js");
        add.accept("/singular-static/resources/metronic/global/plugins/datatables/plugins/bootstrap/datatables.bootstrap.js");
        add.accept("/singular-static/resources/metronic/global/plugins/morris/morris.min.js");
        add.accept("/singular-static/resources/metronic/global/plugins/morris/raphael-min.js");
        add.accept("/singular-static/resources/metronic/global/plugins/jquery.sparkline.min.js");
        add.accept("/singular-static/resources/metronic/global/plugins/amcharts/amcharts/amcharts.js");
        add.accept("/singular-static/resources/metronic/global/plugins/amcharts/amcharts/serial.js");
        add.accept("/singular-static/resources/metronic/global/plugins/amcharts/amcharts/pie.js");
        add.accept("/singular-static/resources/metronic/global/plugins/amcharts/amcharts/themes/light.js");
        add.accept("/singular-static/resources/metronic/global/plugins/bootstrap-maxlength/bootstrap-maxlength.min.js");
        add.accept("/singular-static/resources/metronic/global/plugins/ion.rangeslider/js/ion.rangeSlider.min.js");
        add.accept("/singular-static/resources/metronic/global/plugins/bootbox/bootbox.min.js");
        add.accept("/singular-static/resources/metronic/global/plugins/jquery-file-upload/js/jquery.iframe-transport.js");
        add.accept("/singular-static/resources/metronic/global/plugins/jquery-file-upload/js/jquery.fileupload.js");
        add.accept("/singular-static/resources/singular/plugins/jquery-maskmoney/dist/jquery.maskMoney.min.js");
        add.accept("/singular-static/resources/singular/plugins/syntaxHighlighter/js/shCore.js");
        add.accept("/singular-static/resources/singular/plugins/syntaxHighlighter/js/shBrushJava.js");
        add.accept("/singular-static/resources/singular/plugins/syntaxHighlighter/js/shBrushJScript.js");
        add.accept("/singular-static/resources/singular/plugins/syntaxHighlighter/js/shBrushXml.js");
        add.accept("/singular-static/resources/metronic/global/scripts/app.js");
        add.accept("/singular-static/resources/metronic/layout4/scripts/layout.js");
        add.accept("/singular-static/resources/metronic/global/plugins/bootstrap-toastr/toastr.min.js");
        add.accept("/singular-static/resources/metronic/global/plugins/typeahead/typeahead.bundle.min.js");
        add.accept("/singular-static/resources/singular/plugins/stringjs/string.min.js");

        return scriptHeaderItens;
    }

    public SkinOptions getSkinOptions() {
        return skinOptions;
    }
}