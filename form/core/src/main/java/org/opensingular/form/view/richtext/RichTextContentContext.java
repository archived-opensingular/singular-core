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

/**
 * This class respresent a stylized button for change all Content of the RichText.
 */
public class RichTextContentContext implements RichTextContext {

    /**
     * The current value of content.
     */
    private String content;

    /**
     * The new value of content.
     */
    private String newValue;

    public RichTextContentContext(String content) {
        this.content = content;
    }

    @Override
    public void setReturnValue(String newValue) {
        this.newValue = newValue;
    }

    @Override
    public String getValue() {
        return this.newValue;
    }

    public String getContent() {
        return this.content;
    }
}
