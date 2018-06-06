package org.opensingular.form.view.richtext;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class RichTextConfiguration {

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
