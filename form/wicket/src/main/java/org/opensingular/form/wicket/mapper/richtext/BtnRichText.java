package org.opensingular.form.wicket.mapper.richtext;

import java.io.Serializable;

public abstract class BtnRichText implements Serializable {


    private String id;
    private String label;
    private String iconUrl;
    private String toolbar;
//    private String form;


    public BtnRichText() {
    }

    public BtnRichText(String id, String label, String iconUrl, String toolbar) {
        this.id = id;
        this.label = label;
        this.iconUrl = iconUrl;
        this.toolbar = toolbar;
    }
    public abstract void getAction(CkEditorContext editorContext);


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getToolbar() {
        return toolbar;
    }

    public void setToolbar(String toolbar) {
        this.toolbar = toolbar;
    }

    //TODO VERIFICAR SE REALMENTE NÃO É POSSIVEL PASSAR VIA JSON.
    @Override
    public final String toString() {
        return id + "-" + label + "-" + iconUrl + "-" + toolbar;
    }

}
