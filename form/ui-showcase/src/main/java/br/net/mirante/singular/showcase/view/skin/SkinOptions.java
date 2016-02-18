package br.net.mirante.singular.showcase.view.skin;

import org.apache.wicket.Session;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.CssReferenceHeaderItem;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;

/**
 * This class stores the options of css skins for the showcase
 *
 * @author Fabricio Buzeto
 */
public class SkinOptions {

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

    public static void selectSkin(Session session, Skin selection){
        session.setAttribute("skin", selection);
    }

    public static void clearSelection(Session session){
        selectSkin(session,null);
    }

    public static Optional<Skin> currentSkin(Session session){
        SkinOptions.Skin skin = (SkinOptions.Skin) session.getAttribute("skin");
        if(skin != null) return Optional.of(skin);
        return Optional.empty();
    }

}
