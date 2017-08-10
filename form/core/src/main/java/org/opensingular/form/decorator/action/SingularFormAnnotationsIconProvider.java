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
