package br.net.mirante.singular.form.mform.basic.view;

/**
 * Created by nuk on 21/03/16.
 */
public class SViewAutoComplete extends SView {

    public enum Mode {STATIC, DYNAMIC;}
    protected Mode fetch = Mode.STATIC;

    public Mode fetch() {   return fetch;}

    public SViewAutoComplete(){};

    public SViewAutoComplete(Mode fetch){
        this.fetch = fetch;
    }
}
