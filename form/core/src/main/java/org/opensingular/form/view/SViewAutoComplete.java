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

package org.opensingular.form.view;

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
