package org.opensingular.lib.wicket.util.template;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableList;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.HeaderItem;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;

public class RecursosStaticosSingularTemplate {

    private RecursosStaticosSingularTemplate(){
        /*Construtor vazio pois os métodos são apenas estaticos.*/
    }

    protected static String skinnableResource(String uri, String skinFolder) {
        return "/singular-static/resources/" + skinFolder + uri;
    }

    public static List<HeaderItem> getStyles(String skinFolder) {
        String skinFolderFormatted = skinFolder == null ? "singular" : skinFolder;
        return Stream.of(skinnableResource("/global/plugins/font-awesome/css/font-awesome.min.css", skinFolderFormatted),
                skinnableResource("/global/plugins/simple-line-icons/simple-line-icons.min.css", skinFolderFormatted),
                skinnableResource("/global/plugins/bootstrap/css/bootstrap.css", skinFolderFormatted),
                skinnableResource("/global/plugins/uniform/css/uniform.default.css", skinFolderFormatted),
                skinnableResource("/global/plugins/bootstrap-datepicker/css/bootstrap-datepicker.min.css", skinFolderFormatted),
                skinnableResource("/global/plugins/bootstrap-timepicker/css/bootstrap-timepicker.min.css", skinFolderFormatted),
                skinnableResource("/global/plugins/bootstrap-select/css/bootstrap-select.min.css", skinFolderFormatted),
                skinnableResource("/global/plugins/bootstrap-switch/css/bootstrap-switch.min.css", skinFolderFormatted),
                skinnableResource("/global/plugins/jstree/dist/themes/default/style.min.css", skinFolderFormatted),
                skinnableResource("/global/plugins/jquery-multi-select/css/multi-select.css", skinFolderFormatted),
                skinnableResource("/global/plugins/ion.rangeslider/css/normalize.css", skinFolderFormatted),
                skinnableResource("/global/plugins/ion.rangeslider/css/ion.rangeSlider.css", skinFolderFormatted),
                skinnableResource("/global/plugins/ion.rangeslider/css/ion.rangeSlider.skinHTML5.css", skinFolderFormatted),
                skinnableResource("/global/plugins/datatables/plugins/bootstrap/datatables.bootstrap.css", skinFolderFormatted),
                skinnableResource("/global/plugins/morris/morris.css", skinFolderFormatted),
                skinnableResource("/global/css/components-md.css", skinFolderFormatted),
                skinnableResource("/global/css/plugins-md.css", skinFolderFormatted),
                skinnableResource("/global/css/singular.css", skinFolderFormatted),
                skinnableResource("/layout4/css/layout.css", skinFolderFormatted),
                skinnableResource("/global/plugins/jquery-file-upload/css/jquery.fileupload.css", skinFolderFormatted),
                skinnableResource("/global/plugins/bootstrap-toastr/toastr.min.css", skinFolderFormatted),
                skinnableResource("/global/plugins/typeahead/typeahead.css", skinFolderFormatted),
                skinnableResource("/layout4/css/custom.css", skinFolderFormatted),
                skinnableResource("/css/custom.css", skinFolderFormatted),
                skinnableResource("/layout4/css/themes/default.css", skinFolderFormatted),
                "resources/custom/css/custom.css")
                .map(CssHeaderItem::forUrl).collect(Collectors.collectingAndThen(Collectors.toList(), ImmutableList::copyOf));
    }

    public static List<HeaderItem> getJavaScriptsUrls() {

        return Stream.concat(
                Stream.of(
                        "/singular-static/resources/singular/global/plugins/respond.min.js",
                        "/singular-static/resources/singular/global/plugins/excanvas.min.js"
                ).map(url -> JavaScriptHeaderItem.forUrl(url, null, false, StandardCharsets.UTF_8.name(), "lt IE 9")),
                Stream.of(
                        "/singular-static/resources/singular/global/plugins/jquery-ui/jquery-ui.min.js",
                        "/singular-static/resources/singular/global/plugins/bootstrap/js/bootstrap.min.js",
                        "/singular-static/resources/singular/global/plugins/bootstrap-hover-dropdown/bootstrap-hover-dropdown.min.js",
                        "/singular-static/resources/singular/global/plugins/jquery-slimscroll/jquery.slimscroll.min.js",
                        "/singular-static/resources/singular/global/plugins/jquery.blockui.min.js",
                        "/singular-static/resources/singular/global/plugins/jquery.cokie.min.js",
                        "/singular-static/resources/singular/global/plugins/uniform/jquery.uniform.min.js",
                        "/singular-static/resources/singular/global/plugins/bootstrap-datepicker/js/bootstrap-datepicker.min.js",
                        "/singular-static/resources/singular/global/plugins/bootstrap-datepicker/locales/bootstrap-datepicker.pt-BR.min.js",
                        "/singular-static/resources/singular/global/plugins/bootstrap-timepicker/js/bootstrap-timepicker.min.js",
                        "/singular-static/resources/singular/global/plugins/bootstrap-select/js/bootstrap-select.min.js",
                        "/singular-static/resources/singular/global/plugins/bootstrap-switch/js/bootstrap-switch.min.js",
                        "/singular-static/resources/singular/global/plugins/jquery-multi-select/js/jquery.multi-select.js",
                        "/singular-static/resources/singular/global/plugins/jquery-inputmask/jquery.inputmask.bundle.min.js",
                        "/singular-static/resources/singular/global/plugins/jquerymask/jquery.mask.min.js",
                        "/singular-static/resources/singular/global/plugins/datatables/datatables.min.js",
                        "/singular-static/resources/singular/global/plugins/datatables/plugins/bootstrap/datatables.bootstrap.js",
                        "/singular-static/resources/singular/global/plugins/morris/morris.min.js",
                        "/singular-static/resources/singular/global/plugins/morris/raphael-min.js",
                        "/singular-static/resources/singular/global/plugins/jquery.sparkline.min.js",
                        "/singular-static/resources/singular/global/plugins/amcharts/amcharts/amcharts.js",
                        "/singular-static/resources/singular/global/plugins/amcharts/amcharts/serial.js",
                        "/singular-static/resources/singular/global/plugins/amcharts/amcharts/pie.js",
                        "/singular-static/resources/singular/global/plugins/amcharts/amcharts/themes/light.js",
                        "/singular-static/resources/singular/global/plugins/bootstrap-maxlength/bootstrap-maxlength.min.js",
                        "/singular-static/resources/singular/global/plugins/ion.rangeslider/js/ion.rangeSlider.min.js",
                        "/singular-static/resources/singular/global/plugins/bootbox/bootbox.min.js",
                        "/singular-static/resources/singular/global/plugins/jquery-file-upload/js/jquery.iframe-transport.js",
                        "/singular-static/resources/singular/global/plugins/jquery-file-upload/js/jquery.fileupload.js",
                        "/singular-static/resources/singular/global/scripts/app.min.js",
                        "/singular-static/resources/singular/layout4/scripts/layout.min.js",
                        "/singular-static/resources/singular/global/plugins/bootstrap-toastr/toastr.min.js",
                        "/singular-static/resources/singular/global/plugins/typeahead/typeahead.bundle.min.js",
                        "/singular-static/resources/singular/global/plugins/jstree/dist/jstree.min.js",
                        "/singular-static/resources/singular/plugins/stringjs/string.min.js",
                        "/singular-static/resources/singular/plugins/jquery-maskmoney/dist/jquery.maskMoney.min.js",
                        "/singular-static/resources/singular/plugins/ckeditor/ckeditor.js"
                ).map(JavaScriptHeaderItem::forUrl)).collect(Collectors.collectingAndThen(Collectors.toList(), ImmutableList::copyOf));
    }
}
