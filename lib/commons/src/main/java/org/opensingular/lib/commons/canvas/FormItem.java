/*
 *
 *  * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.opensingular.lib.commons.canvas;

public class FormItem {
    private String label;
    private String value;
    private Integer cols;

    public FormItem(String label, String value, Integer cols) {
        this.label = label;
        this.value = value;
        this.cols = cols;
    }

    public String getLabel() {
        return label;
    }

    public String getValue() {
        return value;
    }

    public Integer getCols() {
        return cols;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setCols(Integer cols) {
        this.cols = cols;
    }

    public boolean isValueAndLabelNull(){
        return value == null && label == null;
    }
}
