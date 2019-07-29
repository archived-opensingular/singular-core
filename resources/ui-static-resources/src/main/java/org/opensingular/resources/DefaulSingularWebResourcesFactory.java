package org.opensingular.resources;

import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.PackageResource;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.resource.JQueryPluginResourceReference;
import org.apache.wicket.resource.JQueryResourceReference;
import org.opensingular.lib.wicket.SingularWebResourcesFactory;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DefaulSingularWebResourcesFactory implements SingularWebResourcesFactory {
    private List<CssHeaderItem>        styles;
    private List<JavaScriptHeaderItem> scripts;

    private final SingularResourceScope[] scopes;

    public DefaulSingularWebResourcesFactory(SingularResourceScope scope) {
        this.scopes = new SingularResourceScope[]{scope, new DefaultSingularResourceScope()};
    }

    public DefaulSingularWebResourcesFactory() {
        this.scopes = new SingularResourceScope[]{new DefaultSingularResourceScope()};
    }

    protected Set<String> getStylePaths() {
        final Set<String> paths = new LinkedHashSet<>();
        paths.add("global/plugins/font-awesome/css/font-awesome.min.css");
        paths.add("global/plugins/simple-line-icons/simple-line-icons.min.css");
        paths.add("global/plugins/bootstrap/css/bootstrap.css");
        paths.add("global/plugins/uniform/css/uniform.default.css");
        paths.add("global/plugins/bootstrap-datepicker/css/bootstrap-datepicker.min.css");
        paths.add("global/plugins/bootstrap-timepicker/css/bootstrap-timepicker.min.css");
        paths.add("global/plugins/bootstrap-select/css/bootstrap-select.min.css");
        paths.add("global/plugins/bootstrap-switch/css/bootstrap-switch.min.css");
        paths.add("global/plugins/jstree/dist/themes/default/style.min.css");
        paths.add("global/plugins/jquery-multi-select/css/multi-select.css");
        paths.add("global/plugins/ion.rangeslider/css/normalize.css");
        paths.add("global/plugins/ion.rangeslider/css/ion.rangeSlider.css");
        paths.add("global/plugins/ion.rangeslider/css/ion.rangeSlider.skinHTML5.css");
        paths.add("global/plugins/datatables/datatables.min.css");
        paths.add("global/plugins/morris/morris.css");
        paths.add("global/css/components-md.css");
        paths.add("global/css/plugins-md.css");
        paths.add("global/css/singular.css");
        paths.add("layout4/css/layout.css");
        paths.add("global/plugins/jquery-file-upload/css/jquery.fileupload.css");
        paths.add("global/plugins/bootstrap-toastr/toastr.min.css");
        paths.add("global/plugins/typeahead/typeahead.css");
        paths.add("plugins/photoswipe/photoswipe.css");
        paths.add("plugins/photoswipe/default-skin/default-skin.css");
        paths.add("layout4/css/custom.css");
        paths.add("css/custom.css");
        paths.add("layout4/css/themes/default.css");
        return paths;
    }

    protected Set<String> getScriptForIEPaths() {
        final Set<String> paths = new LinkedHashSet<>();
        paths.add("global/plugins/respond.min.js");
        paths.add("global/plugins/excanvas.min.js");
        return paths;
    }

    protected Set<String> getScriptPaths() {
        final Set<String> paths = new LinkedHashSet<>();
        paths.add("plugins/jquery-migrate.min.js");
        paths.add("global/plugins/jquery-ui/jquery-ui.min.js");
        paths.add("global/plugins/bootstrap/js/bootstrap.min.js");
        paths.add("global/plugins/bootstrap-hover-dropdown/bootstrap-hover-dropdown.min.js");
        paths.add("global/plugins/jquery-slimscroll/jquery.slimscroll.min.js");
        paths.add("global/plugins/jquery.blockui.min.js");
        paths.add("global/plugins/jquery.cokie.min.js");
        paths.add("global/plugins/uniform/jquery.uniform.min.js");
        paths.add("global/plugins/bootstrap-datepicker/js/bootstrap-datepicker.min.js");
        paths.add("global/plugins/bootstrap-datepicker/locales/bootstrap-datepicker.pt-BR.min.js");
        paths.add("global/plugins/bootstrap-timepicker/js/bootstrap-timepicker.min.js");
        paths.add("global/plugins/bootstrap-select/js/bootstrap-select.min.js");
        paths.add("global/plugins/bootstrap-select/js/i18n/defaults-pt_BR.js");
        paths.add("global/plugins/bootstrap-switch/js/bootstrap-switch.min.js");
        paths.add("global/plugins/jquery-multi-select/js/jquery.multi-select.js");
        paths.add("global/plugins/jquery-inputmask/jquery.inputmask.bundle.min.js");
        paths.add("global/plugins/jquerymask/jquery.mask.min.js");
        paths.add("global/plugins/datatables/datatables.min.js");
        paths.add("global/plugins/datatables/pdfmake.min.js");
        paths.add("global/plugins/datatables/vfs_fonts.js");
        paths.add("global/plugins/morris/morris.min.js");
        paths.add("global/plugins/morris/raphael-min.js");
        paths.add("global/plugins/jquery.sparkline.min.js");
        paths.add("global/plugins/amcharts/amcharts/amcharts.js");
        paths.add("global/plugins/amcharts/amcharts/serial.js");
        paths.add("global/plugins/amcharts/amcharts/pie.js");
        paths.add("global/plugins/amcharts/amcharts/themes/light.js");
        paths.add("global/plugins/bootstrap-maxlength/bootstrap-maxlength.min.js");
        paths.add("global/plugins/ion.rangeslider/js/ion.rangeSlider.min.js");
        paths.add("global/plugins/bootbox/bootbox.min.js");
        paths.add("global/plugins/jquery-file-upload/js/jquery.iframe-transport.js");
        paths.add("global/plugins/jquery-file-upload/js/jquery.fileupload.js");
        paths.add("global/scripts/app.min.js");
        paths.add("layout4/scripts/layout.min.js");
        paths.add("global/plugins/bootstrap-toastr/toastr.min.js");
        paths.add("global/plugins/typeahead/typeahead.bundle.min.js");
        paths.add("global/plugins/jstree/dist/jstree.min.js");
        paths.add("plugins/stringjs/string.min.js");
        paths.add("plugins/jquery-maskmoney/dist/jquery.maskMoney.min.js");
        paths.add("plugins/photoswipe/photoswipe.min.js");
        paths.add("plugins/photoswipe/photoswipe-ui-default.min.js");
        paths.add("plugins/photoswipe/jquery-photoswipe.js");
        paths.add("global/scripts/iframeResizer.contentWindow.min.js");
        paths.add("plugins/ckeditor/ckeditor.js");
        return paths;
    }

    @Override
    public List<CssHeaderItem> getStyleHeaders() {
        if (styles == null) {
            styles = getStylePaths().stream().map(this::newCssHeader).collect(Collectors.toList());
        }
        return styles;
    }

    @Override
    public List<JavaScriptHeaderItem> getScriptHeaders() {
        if (scripts == null) {
            scripts = Stream.concat(getScriptForIEPaths().stream().map(this::newJavaScriptForIEHeader)
                    , getScriptPaths().stream().map(this::newJavaScriptHeader)).collect(Collectors.toList());
        }
        return scripts;
    }

    @Override
    public CssHeaderItem newCssHeader(String path) {
        return CssHeaderItem.forReference(newPackageResourceReference(path));
    }

    @Override
    public JavaScriptHeaderItem newJavaScriptHeader(String path) {
        return JavaScriptHeaderItem.forReference(newJSPackageResourceReference(path));
    }

    @Override
    public IResource getFavicon() {
        return newPackageResourceReference(getFaviconPath()).getResource();
    }

    @Override
    public ResourceReference getJQuery() {
        return newPackageResourceReference(getjQuerypath());
    }

    @Override
    public IResource getLogo() {
        return newPackageResourceReference(getLogoPath()).getResource();
    }

    protected JavaScriptHeaderItem newJavaScriptForIEHeader(String path) {
        for (SingularResourceScope scope : scopes) {
            if (PackageResource.exists(scope.getClass(), scope.resolve(path), null, null, null)) {
                return JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(scope.getClass(),
                        scope.resolve(path), null, null, "lt IE 9"), null);
            }
        }
        throw resourceNotFoundException(path);
    }

    private ResourceReference newPackageResourceReference(String path) {
        for (SingularResourceScope scope : scopes) {
            if (PackageResource.exists(scope.getClass(), scope.resolve(path), null, null, null)) {
                return new PackageResourceReference(scope.getClass(), scope.resolve(path));
            }
        }
        throw resourceNotFoundException(path);
    }

    private ResourceReference newJSPackageResourceReference(String path) {
        for (SingularResourceScope scope : scopes) {
            if (PackageResource.exists(scope.getClass(), scope.resolve(path), null, null, null)) {
                return new JQueryPluginResourceReference(scope.getClass(), scope.resolve(path));
            }
        }
        throw resourceNotFoundException(path);
    }

    private RuntimeException resourceNotFoundException(String path) {
        return new SingularWebResourcesNotFoundException("Não foi possível encontrar o recurso " + path + " em nenhum dos escopos");
    }

    protected String getFaviconPath() {
        return "img/favicon.png";
    }

    protected String getLogoPath() {
        return "img/logo/logo_singular.png";
    }

    protected String getjQuerypath() {
        return "plugins/jquery.min.js";
    }
}