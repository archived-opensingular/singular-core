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

package org.opensingular.server.commons.form;

import org.opensingular.form.wicket.enums.AnnotationMode;
import org.opensingular.form.wicket.enums.ViewMode;

public enum FormActions {

    FORM_ANALYSIS(1, ViewMode.READ_ONLY, AnnotationMode.EDIT),
    FORM_FILL(2, ViewMode.EDIT, AnnotationMode.NONE),
    FORM_VIEW(3, ViewMode.READ_ONLY, AnnotationMode.NONE),
    FORM_FILL_WITH_ANALYSIS(4, ViewMode.EDIT, AnnotationMode.READ_ONLY),
    FORM_ANALYSIS_VIEW(5, ViewMode.READ_ONLY, AnnotationMode.READ_ONLY);

    private Integer id;
    private ViewMode viewMode;
    private AnnotationMode annotationMode;

    FormActions(Integer id, ViewMode viewMode, AnnotationMode annotationMode) {
        this.id = id;
        this.viewMode = viewMode;
        this.annotationMode = annotationMode;
    }

    public static FormActions getById(Integer id) {
        for (FormActions fa : FormActions.values()) {
            if (fa.id.equals(id)) {
                return fa;
            }
        }
        return null;
    }

    public Integer getId() {
        return id;
    }

    public ViewMode getViewMode() {
        return viewMode;
    }

    public AnnotationMode getAnnotationMode() {
        return annotationMode;
    }
}
