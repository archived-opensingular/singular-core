package br.net.mirante.singular.server.commons.form;

import br.net.mirante.singular.form.wicket.enums.AnnotationMode;
import br.net.mirante.singular.form.wicket.enums.ViewMode;

public enum FormActions {

    FORM_ANALYSIS(1, ViewMode.VISUALIZATION, AnnotationMode.EDIT),
    FORM_FILL(2, ViewMode.EDITION, AnnotationMode.NONE),
    FORM_VIEW(3, ViewMode.VISUALIZATION, AnnotationMode.NONE),
    FORM_FILL_WITH_ANALYSIS(4, ViewMode.EDITION, AnnotationMode.READ_ONLY),
    FORM_ANALYSIS_VIEW(5, ViewMode.VISUALIZATION, AnnotationMode.READ_ONLY);

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
