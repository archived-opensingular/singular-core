/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.util.wicket.template;

import javax.servlet.http.Cookie;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.json.JSONObject;
import org.apache.wicket.markup.head.CssUrlReferenceHeaderItem;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.request.http.WebResponse;

import br.net.mirante.singular.commons.base.SingularException;

/**
 * This class stores the options of css skins for the showcase
 *
 * @author Fabricio Buzeto
 */
public class SkinOptions implements Serializable {

    private Skin current = null;
    private List<Skin> skins = new ArrayList<>();

    public static class Skin implements Serializable {

        private final String name;
        private final CssUrlReferenceHeaderItem ref;
        private final Boolean defaultSkin;

        private Skin(String name, Boolean defaultSkin, CssUrlReferenceHeaderItem ref) {
            this.name = name;
            this.defaultSkin = defaultSkin;
            this.ref = ref;
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

        public CssUrlReferenceHeaderItem getRef() {
            return ref;
        }

        public Boolean getDefaultSkin() {
            return defaultSkin;
        }
    }

    public void addSkin(String name, CssUrlReferenceHeaderItem ref) {
        skins.add(new Skin(name, false, ref));
    }

    public void addDefaulSkin(String name, CssUrlReferenceHeaderItem ref) {
        skins.add(new Skin(name, true, ref));
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
            json.put("url", selection.getRef().getUrl());
            cookie.setValue(json.toString());
        }
        response().addCookie(cookie);
        current = selection;
    }

    public Skin currentSkin() {
        if (current == null) {
            final Cookie cookie = request().getCookie("skin");
            if (cookie != null) {
                return options()
                        .stream()
                        .filter(s -> s.getName().equals(new JSONObject(cookie.getValue()).get("name")))
                        .findFirst()
                        .orElse(getDefaultSkin());
            }
            return getDefaultSkin();
        }
        return current;
    }

    public Skin getDefaultSkin() {
        return options()
                .stream()
                .filter(s -> s.defaultSkin)
                .findFirst().orElseThrow(() -> new SingularException("Nenhum skin foi configurada como default"));
    }

    private static WebRequest request() {
        return (WebRequest) RequestCycle.get().getRequest();
    }

    private static WebResponse response() {
        return (WebResponse) RequestCycle.get().getResponse();
    }

}
