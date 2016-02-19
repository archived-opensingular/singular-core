package br.net.mirante.singular.showcase.view.skin;

import javax.servlet.http.Cookie;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.CssReferenceHeaderItem;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.request.http.WebResponse;

import static com.google.common.collect.Lists.newArrayList;

/**
 * This class stores the options of css skins for the showcase
 *
 * @author Fabricio Buzeto
 */
public class SkinOptions implements Serializable{

    private Skin current = null;

    public enum Skin implements Serializable{
        RED("Red",CssReferenceHeaderItem.forUrl("resources/custom/css/red.css")),
        GREEN("Green",CssReferenceHeaderItem.forUrl("resources/custom/css/green.css"));

        public final String name;
        public final CssHeaderItem ref;

        Skin(String name, CssHeaderItem ref){
            this.name = name;
            this.ref = ref;
        }
    }

    public static List<Skin> options(){
        return newArrayList(Skin.values());
    }

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
//            Session.get().setAttribute("skin","");
        }else{
//            Session.get().setAttribute("skin",selection);
        }
        response().addCookie(cookie);
        current = selection;
    }

    public void clearSelection(){
        selectSkin(null);
    }

    public Optional<Skin> currentSkin(){
//        if(Session.get().getAttribute("skin") == "") return Optional.empty();
//        Skin skin = (Skin) Session.get().getAttribute("skin");
//        if(skin != null) return Optional.of(skin);
//        return skinInCookie(request().getCookie("skin"));
        if(current == null) return Optional.empty();
        return Optional.of(current);
    }

    private static Optional<Skin> skinInCookie(Cookie cookie) {
        if(cookie == null || StringUtils.isBlank(cookie.getValue())) return Optional.empty();
        return Optional.of(Skin.valueOf(cookie.getValue()));
    }

    private static WebRequest request(){
        return (WebRequest) RequestCycle.get().getRequest();
    }

    private static WebResponse response(){
        return (WebResponse)RequestCycle.get().getResponse();
    }

}
