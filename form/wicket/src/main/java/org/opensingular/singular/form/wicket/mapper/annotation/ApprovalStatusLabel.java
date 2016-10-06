package org.opensingular.singular.form.wicket.mapper.annotation;

import static org.opensingular.singular.util.wicket.util.Shortcuts.*;

import org.apache.wicket.markup.html.basic.Label;

import org.opensingular.singular.commons.util.FormatUtil;
import org.opensingular.singular.form.wicket.model.SInstanceValueModel;

final class ApprovalStatusLabel extends Label {
    ApprovalStatusLabel(String id, SInstanceValueModel<Boolean> approvedModel) {
        super(id, $m.get(
            () -> FormatUtil.booleanDescription(approvedModel.getObject(),
                "Aprovado",
                "Rejeitado")));
        this.add($b.classAppender($m.get(
            () -> FormatUtil.booleanDescription(approvedModel.getObject(),
                "annotation-status-approved",
                "annotation-status-rejected"))));
    }
}