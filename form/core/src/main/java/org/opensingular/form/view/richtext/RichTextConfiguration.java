package org.opensingular.form.view.richtext;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Class that represent some configuration of the RichText.
 */
public class RichTextConfiguration {

    /**
     * The class css of the button that will have the double click of the stopped text.
     */
    private Set<String> doubleClickDisabledClasses = new HashSet<>();

    private SViewByRichTextNewTab sViewByRichTextNewTab;

    public RichTextConfiguration(SViewByRichTextNewTab sViewByRichTextNewTab) {
        this.sViewByRichTextNewTab = sViewByRichTextNewTab;
    }

    public RichTextConfiguration setDoubleClickDisabledForCssClasses(String... classes) {
        this.doubleClickDisabledClasses.addAll(Arrays.asList(classes));
        this.doubleClickDisabledClasses.removeIf(String::isEmpty);
        return this;
    }

    public Set<String> getDoubleClickDisabledClasses() {
        return doubleClickDisabledClasses;
    }

    public SViewByRichTextNewTab getView(){
        return sViewByRichTextNewTab;
    }

}
