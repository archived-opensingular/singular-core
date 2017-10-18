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

package org.opensingular.form.decorator.action;

import static org.apache.commons.lang3.StringUtils.*;

public class SingularFormAnnotationsIconProvider implements SIconProvider {

    public static final String ANNOTATION_EMPTY    = "singular-form-icon-annotation-empty";
    public static final String ANNOTATION_APPROVED = "singular-form-icon-annotation-approved";
    public static final String ANNOTATION_REJECTED = "singular-form-icon-annotation-rejected";
    public static final String ANNOTATION_EDIT     = "singular-form-icon-annotation-edit";
    public static final String ANNOTATION_REMOVE   = "singular-form-icon-annotation-remove";

    @Override
    public int order() {
        return 0;
    }

    @Override
    public SIcon resolve(String id) {
        SIcon icon = new SIcon()
            .setContainerCssClasses("annotation-toggle-container");
        switch (defaultString(id)) {
            case ANNOTATION_EMPTY:
                return icon.setIconCssClasses("annotation-icon", "annotation-icon-empty");
            case ANNOTATION_APPROVED:
                return icon.setIconCssClasses("annotation-icon", "annotation-icon-approved");
            case ANNOTATION_REJECTED:
                return icon.setIconCssClasses("annotation-icon", "annotation-icon-rejected");
            case ANNOTATION_EDIT:
                return icon
                    .setContainerCssClasses("annotation-action-edit")
                    .setIconCssClasses("fa", "fa-pencil");
            case ANNOTATION_REMOVE:
                return icon
                    .setContainerCssClasses("annotation-action-remove")
                    .setIconCssClasses("icon-trash");
            default:
                return null;
        }
    }
}
