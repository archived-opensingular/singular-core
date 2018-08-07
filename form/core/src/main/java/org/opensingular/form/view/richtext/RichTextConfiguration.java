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
