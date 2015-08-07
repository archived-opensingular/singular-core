package br.net.mirante.singular.flow.util.view;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.google.common.base.Preconditions;

public class Lnk implements Serializable {

    private final boolean urlAppMissing;
    private final String url_;

    private Lnk(String url) {
        urlAppMissing = true;
        url_ = url;
    }

    public Lnk(String url, boolean urlAppMissing) {
        this.urlAppMissing = urlAppMissing;
        url_ = url;
    }

    public static Lnk of(String path) {
        return new Lnk(path);
    }

    public static Lnk of(String urlApp, String path) {
        return new Lnk(urlApp, path);
    }

    public static Lnk of(String urlApp, Lnk path) {
        return path.addUrlApp(urlApp);
    }

    private Lnk(String urlApp, String path) {
        if (urlApp == null) {
            urlAppMissing = true;
            url_ = path;
        } else {
            urlAppMissing = false;
            url_ = concat(urlApp, path);
        }
    }

    public boolean isUrlAppMissing() {
        return urlAppMissing;
    }

    public Lnk addUrlApp(String urlApp) {
        if (urlApp == null || !urlAppMissing) {
            return this;
        }
        return new Lnk(concat(urlApp, url_), false);
    }

    public Lnk appendPath(String path) {
        return new Lnk(concat(url_, path), urlAppMissing);
    }

    public Lnk and(String parameter, Integer value) {
        if (value == null) {
            return this;
        }
        return and(parameter, value.toString());
    }

    public Lnk and(String parameter, String value) {
        if (value == null) {
            return this;
        }
        try {
            char separador = '&';
            if (url_.indexOf('?') == -1) {
                separador = '?';
            }
            return new Lnk(url_ + separador + parameter + "=" + URLEncoder.encode(value, "UTF-8"), urlAppMissing);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public Lnk addParamSeNaoPresente(String parameter, Object value) {
        if (value == null) {
            return this;
        }
        return addParamSeNaoPresente(parameter, value.toString());
    }

    public Lnk addParamSeNaoPresente(String parameter, String value) {
        if (value == null || url_.contains(parameter + "=")) {
            return this;
        }
        return and(parameter, value);
    }

    public String getHref() {
        return getHref("(ver)");
    }

    public String getHrefUrl() {
        return getHref(url_);
    }

    public String getHref(CharSequence text) {
        return "<a href=\"" + url_ + "\">" + text + "</a>";
    }

    public String getImg() {
        return "<img src=\"" + url_ + "\"/>";
    }

    public String getImg(String title) {
        return "<img src=\"" + url_ + "\" title=\"" + title + "\"/> ";
    }

    public String getHrefBlank(CharSequence text, CharSequence title) {
        return "<a target=\"_blank\" href=\"" + url_ + "\" title=\"" + title + "\">" + text + "</a>";
    }

    public String getHref(CharSequence text, CharSequence title) {
        return "<a href=\"" + url_ + "\" title=\"" + title + "\">" + text + "</a>";
    }

    @Override
    public String toString() {
        return url_;
    }

    public String getUrl(String urlApp) {
        if (urlAppMissing) {
            Preconditions.checkNotNull(urlApp);
            return concat(urlApp, url_);
        }
        return url_;
    }

    public String getUrl() {
        if (urlAppMissing) {
            throw new RuntimeException("UrlApp não definida para '" + url_ + "'");
        }
        return url_;
    }

    public static String concat(String url, String path) {
        Preconditions.checkNotNull(url);
        Preconditions.checkNotNull(path);
        String s = url;
        if (s.charAt(s.length() - 1) == '/') {
            if (path.charAt(0) == '/') {
                s = s.substring(0, s.length() - 1) + path;
            } else {
                s += path;
            }
        } else {
            if (path.charAt(0) == '/') {
                s += path;
            } else {
                s += "/" + path;
            }
        }
        return s;
    }
}