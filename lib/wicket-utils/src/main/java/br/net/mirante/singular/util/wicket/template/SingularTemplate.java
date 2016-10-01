/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.util.wicket.template;

import br.net.mirante.singular.util.wicket.application.SkinnableApplication;
import com.google.common.collect.ImmutableList;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.HeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.filter.HeaderResponseContainer;
import org.apache.wicket.markup.head.filter.JavaScriptFilteredIntoFooterHeaderResponse;
import org.apache.wicket.markup.html.IHeaderResponseDecorator;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.PackageResourceReference;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class SingularTemplate extends WebPage {

    public static final String                   JAVASCRIPT_CONTAINER = "javascript-container";
    public static final IHeaderResponseDecorator JAVASCRIPT_DECORATOR = (response) -> new JavaScriptFilteredIntoFooterHeaderResponse(response, SingularTemplate.JAVASCRIPT_CONTAINER);

    public List<HeaderItem> getStyles() {
        return Stream.of(
                "/singular-static/resources/" + getCurrentSkinFolder() + "/global/plugins/font-awesome/css/font-awesome.min.css",
                "/singular-static/resources/" + getCurrentSkinFolder() + "/global/plugins/simple-line-icons/simple-line-icons.min.css",
                "/singular-static/resources/" + getCurrentSkinFolder() + "/global/plugins/bootstrap/css/bootstrap.min.css",
                "/singular-static/resources/" + getCurrentSkinFolder() + "/global/plugins/uniform/css/uniform.default.css",
                "/singular-static/resources/" + getCurrentSkinFolder() + "/global/plugins/bootstrap-datepicker/css/bootstrap-datepicker.min.css",
                "/singular-static/resources/" + getCurrentSkinFolder() + "/global/plugins/bootstrap-timepicker/css/bootstrap-timepicker.min.css",
                "/singular-static/resources/" + getCurrentSkinFolder() + "/global/plugins/bootstrap-select/css/bootstrap-select.min.css",
                "/singular-static/resources/" + getCurrentSkinFolder() + "/global/plugins/bootstrap-switch/css/bootstrap-switch.min.css",
                "/singular-static/resources/" + getCurrentSkinFolder() + "/global/plugins/jquery-multi-select/css/multi-select.css",
                "/singular-static/resources/" + getCurrentSkinFolder() + "/global/plugins/ion.rangeslider/css/normalize.css",
                "/singular-static/resources/" + getCurrentSkinFolder() + "/global/plugins/ion.rangeslider/css/ion.rangeSlider.css",
                "/singular-static/resources/" + getCurrentSkinFolder() + "/global/plugins/ion.rangeslider/css/ion.rangeSlider.skinHTML5.css",
                "/singular-static/resources/" + getCurrentSkinFolder() + "/global/plugins/datatables/plugins/bootstrap/datatables.bootstrap.css",
                "/singular-static/resources/" + getCurrentSkinFolder() + "/global/plugins/morris/morris.css",
                "/singular-static/resources/" + getCurrentSkinFolder() + "/global/css/components-md.css",
                "/singular-static/resources/" + getCurrentSkinFolder() + "/global/css/plugins-md.css",
                "/singular-static/resources/" + getCurrentSkinFolder() + "/layout4/css/layout.css",
                "/singular-static/resources/" + getCurrentSkinFolder() + "/global/plugins/jquery-file-upload/css/jquery.fileupload.css",
                "/singular-static/resources/comum/plugins/syntaxHighlighter/css/shCore.css",
                "/singular-static/resources/comum/plugins/syntaxHighlighter/css/shThemeDefault.css",
                "/singular-static/resources/" + getCurrentSkinFolder() + "/global/plugins/bootstrap-toastr/toastr.min.css",
                "/singular-static/resources/" + getCurrentSkinFolder() + "/global/plugins/typeahead/typeahead.css",
                "/singular-static/resources/" + getCurrentSkinFolder() + "/global/css/typhography.css",
                "/singular-static/resources/" + getCurrentSkinFolder() + "/layout4/css/custom.css",
                "/singular-static/resources/" + getCurrentSkinFolder() + "/css/custom.css",
                "/singular-static/resources/" + getCurrentSkinFolder() + "/layout4/css/themes/default.css",
                "resources/custom/css/custom.css").map(CssHeaderItem::forUrl).collect(Collectors.collectingAndThen(Collectors.toList(), ImmutableList::copyOf));
    }

    public List<HeaderItem> getJavaScriptsUrls() {
        return Stream.concat(
                Stream.of(
                        "/singular-static/resources/" + getCurrentSkinFolder() + "/global/plugins/respond.min.js",
                        "/singular-static/resources/" + getCurrentSkinFolder() + "/global/plugins/excanvas.min.js"
                ).map(url -> JavaScriptHeaderItem.forUrl(url, null, false, "UTF-8", "lt IE 9")),
                Stream.of(
                        "/singular-static/resources/" + getCurrentSkinFolder() + "/global/plugins/jquery-migrate.min.js",
                        "/singular-static/resources/" + getCurrentSkinFolder() + "/global/plugins/jquery-ui/jquery-ui.min.js",
                        "/singular-static/resources/" + getCurrentSkinFolder() + "/global/plugins/bootstrap/js/bootstrap.js",
                        "/singular-static/resources/" + getCurrentSkinFolder() + "/global/plugins/bootstrap-hover-dropdown/bootstrap-hover-dropdown.min.js",
                        "/singular-static/resources/" + getCurrentSkinFolder() + "/global/plugins/jquery-slimscroll/jquery.slimscroll.min.js",
                        "/singular-static/resources/" + getCurrentSkinFolder() + "/global/plugins/jquery.blockui.min.js",
                        "/singular-static/resources/" + getCurrentSkinFolder() + "/global/plugins/jquery.cokie.min.js",
                        "/singular-static/resources/" + getCurrentSkinFolder() + "/global/plugins/uniform/jquery.uniform.min.js",
                        "/singular-static/resources/" + getCurrentSkinFolder() + "/global/plugins/bootstrap-datepicker/js/bootstrap-datepicker.min.js",
                        "/singular-static/resources/" + getCurrentSkinFolder() + "/global/plugins/bootstrap-datepicker/locales/bootstrap-datepicker.pt-BR.min.js",
                        "/singular-static/resources/" + getCurrentSkinFolder() + "/global/plugins/bootstrap-timepicker/js/bootstrap-timepicker.min.js",
                        "/singular-static/resources/" + getCurrentSkinFolder() + "/global/plugins/bootstrap-select/js/bootstrap-select.min.js",
                        "/singular-static/resources/" + getCurrentSkinFolder() + "/global/plugins/bootstrap-switch/js/bootstrap-switch.min.js",
                        "/singular-static/resources/" + getCurrentSkinFolder() + "/global/plugins/jquery-multi-select/js/jquery.multi-select.js",
                        "/singular-static/resources/" + getCurrentSkinFolder() + "/global/plugins/jquery-inputmask/jquery.inputmask.bundle.min.js",
                        "/singular-static/resources/" + getCurrentSkinFolder() + "/global/plugins/datatables/datatables.min.js",
                        "/singular-static/resources/" + getCurrentSkinFolder() + "/global/plugins/datatables/plugins/bootstrap/datatables.bootstrap.js",
                        "/singular-static/resources/" + getCurrentSkinFolder() + "/global/plugins/morris/morris.min.js",
                        "/singular-static/resources/" + getCurrentSkinFolder() + "/global/plugins/morris/raphael-min.js",
                        "/singular-static/resources/" + getCurrentSkinFolder() + "/global/plugins/jquery.sparkline.min.js",
                        "/singular-static/resources/" + getCurrentSkinFolder() + "/global/plugins/amcharts/amcharts/amcharts.js",
                        "/singular-static/resources/" + getCurrentSkinFolder() + "/global/plugins/amcharts/amcharts/serial.js",
                        "/singular-static/resources/" + getCurrentSkinFolder() + "/global/plugins/amcharts/amcharts/pie.js",
                        "/singular-static/resources/" + getCurrentSkinFolder() + "/global/plugins/amcharts/amcharts/themes/light.js",
                        "/singular-static/resources/" + getCurrentSkinFolder() + "/global/plugins/bootstrap-maxlength/bootstrap-maxlength.min.js",
                        "/singular-static/resources/" + getCurrentSkinFolder() + "/global/plugins/ion.rangeslider/js/ion.rangeSlider.min.js",
                        "/singular-static/resources/" + getCurrentSkinFolder() + "/global/plugins/bootbox/bootbox.min.js",
                        "/singular-static/resources/" + getCurrentSkinFolder() + "/global/plugins/jquery-file-upload/js/jquery.iframe-transport.js",
                        "/singular-static/resources/" + getCurrentSkinFolder() + "/global/plugins/jquery-file-upload/js/jquery.fileupload.js",
                        "/singular-static/resources/comum/plugins/jquery-maskmoney/dist/jquery.maskMoney.min.js",
                        "/singular-static/resources/comum/plugins/syntaxHighlighter/js/shCore.js",
                        "/singular-static/resources/comum/plugins/syntaxHighlighter/js/shBrushJava.js",
                        "/singular-static/resources/comum/plugins/syntaxHighlighter/js/shBrushJScript.js",
                        "/singular-static/resources/comum/plugins/syntaxHighlighter/js/shBrushXml.js",
                        "/singular-static/resources/comum/plugins/ckeditor/ckeditor.js",
                        "/singular-static/resources/" + getCurrentSkinFolder() + "/global/scripts/app.js",
                        "/singular-static/resources/" + getCurrentSkinFolder() + "/layout4/scripts/layout.js",
                        "/singular-static/resources/" + getCurrentSkinFolder() + "/global/plugins/bootstrap-toastr/toastr.min.js",
                        "/singular-static/resources/" + getCurrentSkinFolder() + "/global/plugins/typeahead/typeahead.bundle.js",
                        "/singular-static/resources/comum/plugins/stringjs/string.min.js"
                ).map(JavaScriptHeaderItem::forUrl)).collect(Collectors.collectingAndThen(Collectors.toList(), ImmutableList::copyOf));
    }

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
        getApplication().setHeaderResponseDecorator(JAVASCRIPT_DECORATOR);

        /*Essa estratégia é utilizada para garantir que o jquery será sempre carregado pois está fixo no html
        * sem esse artificio páginas sem componentes ajax do wicket apresentarão erros de javascript.*/
        getApplication()
                .getJavaScriptLibrarySettings()
                .setJQueryReference(new PackageResourceReference(SingularTemplate.class, "empty.js"));

        add(new Label("pageTitle", new ResourceModel(getPageTitleLocalKey())));
        add(new HeaderResponseContainer(JAVASCRIPT_CONTAINER, JAVASCRIPT_CONTAINER));
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        getStyles().forEach(response::render);
        getJavaScriptsUrls().forEach(response::render);
    }

    protected String getPageTitleLocalKey() {
        return "label.page.title.local";
    }

    protected void initSkins() {
        final WebApplication wa = WebApplication.get();
        if (wa instanceof SkinnableApplication) {
            ((SkinnableApplication) wa).initSkins(skinOptions);
        } else {
            skinOptions.addDefaulSkin("singular");
        }
    }

    public String getCurrentSkinFolder() {
        return skinOptions.currentSkin().getName();
    }

    public SkinOptions getSkinOptions() {
        return skinOptions;
    }

}