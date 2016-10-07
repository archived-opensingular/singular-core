/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.lib.wicket.util.template;

import org.opensingular.lib.commons.util.Loggable;
import org.apache.wicket.ajax.json.JSONObject;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.request.http.WebResponse;

import javax.servlet.http.Cookie;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This class stores the options of css skins for the showcase
 *
 * @author Fabricio Buzeto
 */
public class SkinOptions implements Serializable, Loggable {

    private Skin       current = null;
    private List<Skin> skins   = new ArrayList<>();

    public static class Skin implements Serializable {

        private final String  name;
        private final Boolean defaultSkin;

        private Skin(String name, Boolean defaultSkin) {
            this.name = name;
            this.defaultSkin = defaultSkin;
        }

        public String name() {
            return name;
        }

        public String toString() {
            return name();
        }

        public String getName() {
            return name;
        }
    }

    public void addSkin(String name) {
        skins.add(new Skin(name, false));
    }

    public void addDefaulSkin(String name) {
        skins.add(new Skin(name, true));
    }

    public List<Skin> options() {
        return skins;
    }

    public void selectSkin(Skin selection) {
        final Cookie cookie = new Cookie("skin", "");
        cookie.setPath("/");
        if (selection != null) {
            final JSONObject json = new JSONObject();
            json.put("name", selection.getName());
            try {
                cookie.setValue(URLEncoder.encode(json.toString(), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                getLogger().error(e.getMessage(), e);
            }
        }
        response().ifPresent(r -> r.addCookie(cookie));
        current = selection;
    }

    public Skin currentSkin() {
        if (current == null) {
            final Cookie cookie = request().getCookie("skin");
            if (cookie != null) {
                try {
                    String name = (String) new JSONObject(URLDecoder.decode(cookie.getValue(), "UTF-8")).get("name");
                    return options()
                            .stream()
                            .filter(s -> s.getName().equals(name))
                            .findFirst()
                            .orElseGet(() -> {
                                if (!getDefaultSkin().isPresent()) {
                                    addDefaulSkin(name);
                                }
                                return getDefaultSkin().orElse(fallBackSkin());
                            });
                } catch (UnsupportedEncodingException e) {
                    getLogger().error(e.getMessage(), e);
                    return getDefaultSkin().orElse(fallBackSkin());
                }
            }
            return getDefaultSkin().orElse(fallBackSkin());
        }
        return current;
    }

    public Optional<Skin> getDefaultSkin() {
        return options()
                .stream()
                .filter(s -> s.defaultSkin)
                .findFirst();
    }

    private static WebRequest request() {
        return (WebRequest) RequestCycle.get().getRequest();
    }

    private static Optional<WebResponse> response() {
        Response response = RequestCycle.get().getResponse();
        if (response instanceof WebResponse) {
            return Optional.of((WebResponse) response);
        } else {
            return Optional.empty();
        }
    }

    public Skin fallBackSkin() {
        return getDefaultSkin().orElseGet(() -> {
            addDefaulSkin("singular");
            return options().get(0);
        });
    }

}

