package org.opensingular.form.wicket.mapper.annotation;

import static org.opensingular.lib.wicket.util.util.Shortcuts.*;

import org.apache.wicket.markup.html.basic.Label;

import org.opensingular.lib.commons.util.FormatUtil;
import org.opensingular.form.wicket.model.SInstanceValueModel;

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