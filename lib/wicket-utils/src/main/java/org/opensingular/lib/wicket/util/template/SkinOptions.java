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

import org.apache.wicket.ajax.json.JSONObject;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.request.http.WebResponse;
import org.opensingular.lib.commons.util.Loggable;

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

    public Skin addDefaulSkin(String name) {
        Skin skin = new Skin(name, true);
        skins.add(skin);
        return skin;
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
        if (current != null) {
            return current;
        }
        Cookie cookie = request().getCookie("skin");
        if (cookie != null) {
            try {
                String name = (String) new JSONObject(URLDecoder.decode(cookie.getValue(), "UTF-8")).get("name");
                return skins
                        .stream()
                        .filter(s -> s.getName().equals(name))
                        .findFirst()
                        .orElseGet(() -> {
                            Optional<Skin> skin = getDefaultSkin();
                            if (skin.isPresent()) {
                                return skin.get();
                            }
                            return addDefaulSkin(name);
                        });
            } catch (UnsupportedEncodingException e) {
                getLogger().error(e.getMessage(), e);
                return getDefaultSkin().orElse(fallBackSkin());
            }
        }
        return getDefaultSkin().orElse(fallBackSkin());
    }

    public Optional<Skin> getDefaultSkin() {
        return skins.stream().filter(s -> s.defaultSkin).findFirst();
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

