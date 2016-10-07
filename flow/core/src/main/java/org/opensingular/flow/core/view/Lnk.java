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

package org.opensingular.flow.core.view;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Objects;

import org.opensingular.flow.core.SingularFlowException;

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
            throw new SingularFlowException(e);
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
            Objects.requireNonNull(urlApp);
            return concat(urlApp, url_);
        }
        return url_;
    }

    public String getUrl() {
        if (urlAppMissing) {
            throw new SingularFlowException("UrlApp não definida para '" + url_ + "'");
        }
        return url_;
    }

    public static String concat(String url, String path) {
        Objects.requireNonNull(url);
        Objects.requireNonNull(path);
        String s = url;
        if (s.length() > 0 && s.charAt(s.length() - 1) == '/') {
            if (path.length() > 0 && path.charAt(0) == '/') {
                s = s.substring(0, s.length() - 1) + path;
            } else {
                s += path;
            }
        } else {
            if (path.length() > 0 && path.charAt(0) == '/') {
                s += path;
            } else {
                s += "/" + path;
            }
        }
        return s;
    }
}