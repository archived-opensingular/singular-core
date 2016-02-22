package br.net.mirante.singular.pet.module.wicket.view.skin;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.CssReferenceHeaderItem;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.request.http.WebResponse;

import javax.servlet.http.Cookie;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static com.google.common.collect.Lists.newArrayList;

/**
 * This class stores the options of css skins for the showcase
 *
 * @author Fabricio Buzeto
 */
public class SkinOptions implements Serializable{

    private Skin current = null;

    public static class Skin implements Serializable{
        public final String name;
        public final CssHeaderItem ref;

        public Skin(String name, CssHeaderItem ref){
            this.name = name;
            this.ref = ref;
        }

        public String name() {return name ;}

        public String toString() {  return name();}
    }

    private static final ConcurrentHashMap<String, Skin> skinMap = new ConcurrentHashMap<>();

    public static void addSkin(Skin s){  skinMap.put(s.name(), s); }
    public static List<Skin> options(){ return newArrayList(skinMap.values());}

    private SkinOptions(){}

    public static SkinOptions op(){
        SkinOptions skinOptions = new SkinOptions();
        Optional<Skin> skin = skinInCookie(request().getCookie("skin"));
        if(skin.isPresent()){
            skinOptions.current = skin.get();
        }
        return skinOptions;
    }

    public void selectSkin(Skin selection){
        Cookie cookie = new Cookie("skin","");
        cookie.setPath("/");
        if(selection != null)   {
            cookie.setValue(selection.name());
        }
        response().addCookie(cookie);
        current = selection;
    }

    public void clearSelection(){
        selectSkin(null);
    }

    public Optional<Skin> currentSkin(){
        if(current == null) return Optional.empty();
        return Optional.of(current);
    }

    private static Optional<Skin> skinInCookie(Cookie cookie) {
        if(cookie == null || StringUtils.isBlank(cookie.getValue())) return Optional.empty();
        return Optional.of(skinMap.get(cookie.getValue()));
    }

    private static WebRequest request(){
        return (WebRequest) RequestCycle.get().getRequest();
    }

    private static WebResponse response(){
        return (WebResponse)RequestCycle.get().getResponse();
    }

}
