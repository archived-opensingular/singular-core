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

package org.opensingular.lib.wicket.util.template;

import org.opensingular.lib.wicket.util.application.SkinnableApplication;
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

    protected String skinnableResource(String uri) {
        return "/singular-static/resources/" + getCurrentSkinFolder() + uri;
    }

    protected String commonResource(String uri) {
        return "/singular-static/resources/comum" + uri;
    }

    public List<HeaderItem> getStyles() {
        return Stream.of(skinnableResource("/global/plugins/font-awesome/css/font-awesome.min.css" ),
                skinnableResource("/global/plugins/simple-line-icons/simple-line-icons.min.css" ),
                skinnableResource("/global/plugins/bootstrap/css/bootstrap.min.css" ),
                skinnableResource("/global/plugins/uniform/css/uniform.default.css" ),
                skinnableResource("/global/plugins/bootstrap-datepicker/css/bootstrap-datepicker.min.css" ),
                skinnableResource("/global/plugins/bootstrap-timepicker/css/bootstrap-timepicker.min.css" ),
                skinnableResource("/global/plugins/bootstrap-select/css/bootstrap-select.min.css" ),
                skinnableResource("/global/plugins/bootstrap-switch/css/bootstrap-switch.min.css" ),
                skinnableResource("/global/plugins/jquery-multi-select/css/multi-select.css" ),
                skinnableResource("/global/plugins/ion.rangeslider/css/normalize.css" ),
                skinnableResource("/global/plugins/ion.rangeslider/css/ion.rangeSlider.css" ),
                skinnableResource("/global/plugins/ion.rangeslider/css/ion.rangeSlider.skinHTML5.css" ),
                skinnableResource("/global/plugins/datatables/plugins/bootstrap/datatables.bootstrap.css" ),
                skinnableResource("/global/plugins/morris/morris.css" ),
                skinnableResource("/global/css/components-md.css" ),
                skinnableResource("/global/css/plugins-md.css" ),
                skinnableResource("/layout4/css/layout.css" ),
                skinnableResource("/global/plugins/jquery-file-upload/css/jquery.fileupload.css" ),
                skinnableResource("/global/plugins/bootstrap-toastr/toastr.min.css" ),
                skinnableResource("/global/plugins/typeahead/typeahead.css" ),
                skinnableResource("/global/css/typhography.css" ),
                skinnableResource("/layout4/css/custom.css" ),
                skinnableResource("/css/custom.css" ),
                skinnableResource("/layout4/css/themes/default.css" ),
                commonResource("/plugins/syntaxHighlighter/css/shCore.css" ),
                commonResource("/plugins/syntaxHighlighter/css/shThemeDefault.css" ),
                "resources/custom/css/custom.css" )
                .map(CssHeaderItem::forUrl).collect(Collectors.collectingAndThen(Collectors.toList(), ImmutableList::copyOf));
    }

    public List<HeaderItem> getJavaScriptsUrls() {
        return Stream.concat(
                Stream.of(
                        skinnableResource("/global/plugins/respond.min.js" ),
                        skinnableResource("/global/plugins/excanvas.min.js" )
                ).map(url -> JavaScriptHeaderItem.forUrl(url, null, false, "UTF-8", "lt IE 9" )),
                Stream.of(
                        skinnableResource("/global/plugins/jquery-migrate.min.js" ),
                        skinnableResource("/global/plugins/jquery-ui/jquery-ui.min.js" ),
                        skinnableResource("/global/plugins/bootstrap/js/bootstrap.js" ),
                        skinnableResource("/global/plugins/bootstrap-hover-dropdown/bootstrap-hover-dropdown.min.js" ),
                        skinnableResource("/global/plugins/jquery-slimscroll/jquery.slimscroll.min.js" ),
                        skinnableResource("/global/plugins/jquery.blockui.min.js" ),
                        skinnableResource("/global/plugins/jquery.cokie.min.js" ),
                        skinnableResource("/global/plugins/uniform/jquery.uniform.min.js" ),
                        skinnableResource("/global/plugins/bootstrap-datepicker/js/bootstrap-datepicker.min.js" ),
                        skinnableResource("/global/plugins/bootstrap-datepicker/locales/bootstrap-datepicker.pt-BR.min.js" ),
                        skinnableResource("/global/plugins/bootstrap-timepicker/js/bootstrap-timepicker.min.js" ),
                        skinnableResource("/global/plugins/bootstrap-select/js/bootstrap-select.min.js" ),
                        skinnableResource("/global/plugins/bootstrap-switch/js/bootstrap-switch.min.js" ),
                        skinnableResource("/global/plugins/jquery-multi-select/js/jquery.multi-select.js" ),
                        skinnableResource("/global/plugins/jquery-inputmask/jquery.inputmask.bundle.min.js" ),
                        skinnableResource("/global/plugins/datatables/datatables.min.js" ),
                        skinnableResource("/global/plugins/datatables/plugins/bootstrap/datatables.bootstrap.js" ),
                        skinnableResource("/global/plugins/morris/morris.min.js" ),
                        skinnableResource("/global/plugins/morris/raphael-min.js" ),
                        skinnableResource("/global/plugins/jquery.sparkline.min.js" ),
                        skinnableResource("/global/plugins/amcharts/amcharts/amcharts.js" ),
                        skinnableResource("/global/plugins/amcharts/amcharts/serial.js" ),
                        skinnableResource("/global/plugins/amcharts/amcharts/pie.js" ),
                        skinnableResource("/global/plugins/amcharts/amcharts/themes/light.js" ),
                        skinnableResource("/global/plugins/bootstrap-maxlength/bootstrap-maxlength.min.js" ),
                        skinnableResource("/global/plugins/ion.rangeslider/js/ion.rangeSlider.min.js" ),
                        skinnableResource("/global/plugins/bootbox/bootbox.min.js" ),
                        skinnableResource("/global/plugins/jquery-file-upload/js/jquery.iframe-transport.js" ),
                        skinnableResource("/global/plugins/jquery-file-upload/js/jquery.fileupload.js" ),
                        skinnableResource("/global/scripts/app.js" ),
                        skinnableResource("/layout4/scripts/layout.js" ),
                        skinnableResource("/global/plugins/bootstrap-toastr/toastr.min.js" ),
                        skinnableResource("/global/plugins/typeahead/typeahead.bundle.js" ),
                        commonResource("/plugins/stringjs/string.min.js" ),
                        commonResource("/plugins/jquery-maskmoney/dist/jquery.maskMoney.min.js" ),
                        commonResource("/plugins/syntaxHighlighter/js/shCore.js" ),
                        commonResource("/plugins/syntaxHighlighter/js/shBrushJava.js" ),
                        commonResource("/plugins/syntaxHighlighter/js/shBrushJScript.js" ),
                        commonResource("/plugins/syntaxHighlighter/js/shBrushXml.js" ),
                        commonResource("/plugins/ckeditor/ckeditor.js" )
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
                .setJQueryReference(new PackageResourceReference(SingularTemplate.class, "empty.js" ));

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
            skinOptions.addDefaulSkin("singular" );
        }
    }

    public String getCurrentSkinFolder() {
        return skinOptions.currentSkin().getName();
    }

    public SkinOptions getSkinOptions() {
        return skinOptions;
    }

}