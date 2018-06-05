package org.opensingular.lib.wicket.util.image;

import static java.util.stream.Collectors.*;

import java.io.Serializable;
import java.util.stream.Stream;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.json.JSONArray;
import org.apache.wicket.ajax.json.JSONObject;
import org.apache.wicket.ajax.json.JsonFunction;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.model.IModel;
import org.opensingular.lib.commons.lambda.ISupplier;
import org.opensingular.lib.wicket.util.jquery.JQuery;

public class PhotoSwipeBehavior extends Behavior {

    private final ISupplier<String> imageDataJsFunction;
    private CoreOptions             coreOptions;
    private DefaultUIOptions        defaultUIOptions;
    private Component               component;

    public PhotoSwipeBehavior(ISupplier<String> imageDataJsFunction) {
        this.imageDataJsFunction = imageDataJsFunction;
    }

    public static PhotoSwipeBehavior forURLs(IModel<String[]> ulrs) {
        return new PhotoSwipeBehavior(() -> {
            String[] array = ulrs.getObject();
            if ((array == null) || (array.length == 0))
                return "function() { return []; }";
            return Stream.of(array)
                .map(it -> "{ src:'" + it + "' }")
                .collect(joining(", ", "function() { return [", "]; }"));
        });
    }

    public static PhotoSwipeBehavior forImages(IModel<Image[]> images) {
        return new PhotoSwipeBehavior(() -> {
            Image[] array = images.getObject();
            if ((array == null) || (array.length == 0))
                return "function() { return []; }";
            return "function() { return $.map(" + JQuery.$(array) + ", function(img){ return ({ src:img.src, w:img.naturalWidth||0, h:img.naturalHeight||0 }); }); }";
        });
    }

    public CoreOptions getCoreOptions() {
        if (coreOptions == null)
            coreOptions = new CoreOptions();
        return coreOptions;
    }
    public PhotoSwipeBehavior setCoreOptions(CoreOptions coreOptions) {
        this.coreOptions = coreOptions;
        return this;
    }
    public DefaultUIOptions getDefaultUIOptions() {
        if (defaultUIOptions == null)
            defaultUIOptions = new DefaultUIOptions();
        return defaultUIOptions;
    }
    public PhotoSwipeBehavior setDefaultUIOptions(DefaultUIOptions defaultUIOptions) {
        this.defaultUIOptions = defaultUIOptions;
        return this;
    }

    public String getJavaScriptFunction() {
        JSONObject options = new JSONObject();
        if (coreOptions != null)
            coreOptions.toJSON(options);
        if (defaultUIOptions != null)
            defaultUIOptions.toJSON(options);
        options.put("items", new JsonFunction(imageDataJsFunction.get()));

        StringBuilder $this = JQuery.$(component);

        return "function() { " + $this + ".photoswipe(" + options.toString(1) + "); }";
    }
    public String getJavaScriptCallback() {
        return "(" + getJavaScriptFunction() + ")()";
    }

    @Override
    public void renderHead(Component component, IHeaderResponse response) {
        response.render(CssHeaderItem.forCSS(".pswp{ z-index:20000 !important }", "PhotoSwipeBehavior_style"));
    }
    @Override
    public boolean isEnabled(Component component) {
        return component.isEnabledInHierarchy();
    }
    @Override
    public void bind(Component component) {
        this.component = component;
    }
    @Override
    public void unbind(Component component) {
        this.component = null;
    }

    public static class CoreOptions implements IToJson {
        public Integer      index;
        public JsonFunction getThumbBoundsFn;
        public Boolean      showHideOpacity;
        public Integer      showAnimationDuration;
        public Integer      hideAnimationDuration;
        public Float        bgOpacity;
        public Float        spacing;
        public Boolean      allowPanToNext;
        public Float        maxSpreadZoom;
        public JsonFunction getDoubleTapZoom;
        public Boolean      loop;
        public Boolean      pinchToClose;
        public Boolean      closeOnScroll;
        public Boolean      closeOnVerticalDrag;
        public Boolean      mouseUsed;
        public Boolean      escKey;
        public Boolean      arrowKeys;
        public Boolean      history;
        public Integer      galleryUID;
        public Boolean      galleryPIDs;
        public String       errorMsg;
        public Integer      preloadPrevious;
        public Integer      preloadNext;
        public String       mainClass;
        public JsonFunction getNumItemsFn;
        public Boolean      focus;
        public JsonFunction isClickableElement;
        public Boolean      modal;

        public JSONObject toJSON() {
            return toJSON(new JSONObject());
        }
        public JSONObject toJSON(JSONObject json) {
            //@formatter:off
            if (index                 != null) json.append("index"                , index                );
            if (getThumbBoundsFn      != null) json.append("getThumbBoundsFn"     , getThumbBoundsFn     );
            if (showHideOpacity       != null) json.append("showHideOpacity"      , showHideOpacity      );
            if (showAnimationDuration != null) json.append("showAnimationDuration", showAnimationDuration);
            if (hideAnimationDuration != null) json.append("hideAnimationDuration", hideAnimationDuration);
            if (bgOpacity             != null) json.append("bgOpacity"            , bgOpacity            );
            if (spacing               != null) json.append("spacing"              , spacing              );
            if (allowPanToNext        != null) json.append("allowPanToNext"       , allowPanToNext       );
            if (maxSpreadZoom         != null) json.append("maxSpreadZoom"        , maxSpreadZoom        );
            if (getDoubleTapZoom      != null) json.append("getDoubleTapZoom"     , getDoubleTapZoom     );
            if (loop                  != null) json.append("loop"                 , loop                 );
            if (pinchToClose          != null) json.append("pinchToClose"         , pinchToClose         );
            if (closeOnScroll         != null) json.append("closeOnScroll"        , closeOnScroll        );
            if (closeOnVerticalDrag   != null) json.append("closeOnVerticalDrag"  , closeOnVerticalDrag  );
            if (mouseUsed             != null) json.append("mouseUsed"            , mouseUsed            );
            if (escKey                != null) json.append("escKey"               , escKey               );
            if (arrowKeys             != null) json.append("arrowKeys"            , arrowKeys            );
            if (history               != null) json.append("history"              , history              );
            if (galleryUID            != null) json.append("galleryUID"           , galleryUID           );
            if (galleryPIDs           != null) json.append("galleryPIDs"          , galleryPIDs          );
            if (errorMsg              != null) json.append("errorMsg"             , errorMsg             );
            if (preloadPrevious       != null) json.append("preloadPrevious"      , preloadPrevious      );
            if (preloadNext           != null) json.append("preloadNext"          , preloadNext          );
            if (mainClass             != null) json.append("mainClass"            , mainClass            );
            if (getNumItemsFn         != null) json.append("getNumItemsFn"        , getNumItemsFn        );
            if (focus                 != null) json.append("focus"                , focus                );
            if (isClickableElement    != null) json.append("isClickableElement"   , isClickableElement   );
            if (modal                 != null) json.append("modal"                , modal                );
            //@formatter:on
            return json;
        }
    }
    public static class DefaultUIOptions implements IToJson {
        public Integer       barsSizeTop;
        public Integer       barsSizeBottom;
        public Integer       timeToIdle;
        public Integer       timeToIdleOutside;
        public Integer       loadingIndicatorDelay;
        public JsonFunction  addCaptionHTMLFn;
        public Boolean       closeEl;
        public Boolean       captionEl;
        public Boolean       fullscreenEl;
        public Boolean       zoomEl;
        public Boolean       shareEl;
        public Boolean       counterEl;
        public Boolean       arrowEl;
        public Boolean       preloaderEl;
        public Boolean       tapToClose;
        public Boolean       tapToToggleControls;
        public Boolean       clickToCloseNonZoomable;
        public String[]      closeElClasses;
        public String        indexIndicatorSep;
        public ShareButton[] shareButtons;
        public JsonFunction  getImageURLForShare;
        public JsonFunction  getPageURLForShare;
        public JsonFunction  getTextForShare;
        public JsonFunction  parseShareButtonOut;

        public JSONObject toJSON() {
            return toJSON(new JSONObject());
        }
        public JSONObject toJSON(JSONObject json) {
            if ((barsSizeTop != null) || (barsSizeBottom != null))
                json
                    .append("barsSize", new JSONObject()
                        .append("top", (barsSizeTop != null) ? barsSizeTop : 0)
                        .append("bottom", (barsSizeBottom != null) ? barsSizeBottom : "auto"));
            //@formatter:off
            if (timeToIdle              != null) json.append("timeToIdle"             , timeToIdle               ); 
            if (timeToIdleOutside       != null) json.append("timeToIdleOutside"      , timeToIdleOutside        ); 
            if (loadingIndicatorDelay   != null) json.append("loadingIndicatorDelay"  , loadingIndicatorDelay    ); 
            if (addCaptionHTMLFn        != null) json.append("addCaptionHTMLFn"       , addCaptionHTMLFn         ); 
            if (closeEl                 != null) json.append("closeEl"                , closeEl                  ); 
            if (captionEl               != null) json.append("captionEl"              , captionEl                ); 
            if (fullscreenEl            != null) json.append("fullscreenEl"           , fullscreenEl             ); 
            if (zoomEl                  != null) json.append("zoomEl"                 , zoomEl                   ); 
            if (shareEl                 != null) json.append("shareEl"                , shareEl                  ); 
            if (counterEl               != null) json.append("counterEl"              , counterEl                ); 
            if (arrowEl                 != null) json.append("arrowEl"                , arrowEl                  ); 
            if (preloaderEl             != null) json.append("preloaderEl"            , preloaderEl              ); 
            if (tapToClose              != null) json.append("tapToClose"             , tapToClose               ); 
            if (tapToToggleControls     != null) json.append("tapToToggleControls"    , tapToToggleControls      ); 
            if (clickToCloseNonZoomable != null) json.append("clickToCloseNonZoomable", clickToCloseNonZoomable  ); 
            if (closeElClasses          != null) json.append("closeElClasses"         , jsonArray(closeElClasses)); 
            if (indexIndicatorSep       != null) json.append("indexIndicatorSep"      , indexIndicatorSep        ); 
            if (indexIndicatorSep       != null) json.append("indexIndicatorSep"      , indexIndicatorSep        );
            if (shareButtons            != null) json.append("shareButtons"           , jsonArray(shareButtons)  );
            if (getImageURLForShare     != null) json.append("getImageURLForShare"    , getImageURLForShare      );
            if (getPageURLForShare      != null) json.append("getPageURLForShare"     , getPageURLForShare       );
            if (getTextForShare         != null) json.append("getTextForShare"        , getTextForShare          );
            if (parseShareButtonOut     != null) json.append("parseShareButtonOut"    , parseShareButtonOut      );
            
            //@formatter:on
            return json;
        }
    }

    public static class ImageItem implements IToJson {
        public String src;
        public int    w, h;
        public String pid;

        public ImageItem(String src, int w, int h) {
            this(src, w, h, null);
        }
        public ImageItem(String src, int w, int h, String pid) {
            this.src = src;
            this.w = w;
            this.h = h;
            this.pid = pid;
        }

        public JSONObject toJSON() {
            return toJSON(new JSONObject());
        }
        public JSONObject toJSON(JSONObject json) {
            json
                .append("src", src)
                .append("w", w)
                .append("h", h);
            if (pid != null)
                json.append("pid", pid);
            return json;
        }
    }

    public static class ShareButton implements IToJson {
        //@formatter:off
        public static final ShareButton FACEBOOK  = new ShareButton("facebook" , "Share on Facebook", false, "https://www.facebook.com/sharer/sharer.php?u={{url}}");
        public static final ShareButton TWITTER   = new ShareButton("twitter"  , "Tweet"            , false, "https://twitter.com/intent/tweet?text={{text}}&url={{url}}");
        public static final ShareButton PINTEREST = new ShareButton("pinterest", "Pin it"           , false, "http://www.pinterest.com/pin/create/button/?url={{url}}&media={{image_url}}&description={{text}}");
        public static final ShareButton DOWNLOAD  = new ShareButton("download" , "Download image"   , true , "{{raw_image_url}}");
        //@formatter:on

        public String                   id;
        public String                   label;
        public String                   url;
        public boolean                  download;

        public ShareButton(String id, String label, boolean download, String url) {
            this.id = id;
            this.label = label;
            this.url = url;
            this.download = download;
        }
        public JSONObject toJSON() {
            return toJSON(new JSONObject());
        }
        public JSONObject toJSON(JSONObject json) {
            json
                .append("id", id)
                .append("label", label)
                .append("url", url);
            if (download)
                json.append("download", download);
            return json;
        }
    }

    private interface IToJson extends Serializable {
        JSONObject toJSON();
        JSONObject toJSON(JSONObject json);
    }
    private static JSONArray jsonArray(String[] objs) {
        return new JSONArray(objs);
    }
    private static JSONArray jsonArray(IToJson[] objs) {
        return new JSONArray(Stream.of(objs).map(it -> it.toJSON()).collect(toList()));
    }
}
