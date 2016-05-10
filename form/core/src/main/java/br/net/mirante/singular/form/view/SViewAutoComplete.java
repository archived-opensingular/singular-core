package br.net.mirante.singular.form.view;

/**
 * This View is used with selection types when an auto complete option should be
 * displayed.
 *
 * Modes establishes how the options should be loades where:
 *  - STATIC (Default) will load options once the page is loaded nd stay so until its
 *      submission.
 *  - DYNAMIC will allow to filter or modify options as string values are typed onto
 *      the field.
 *
 *  @author Fabricio Buzeto
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
