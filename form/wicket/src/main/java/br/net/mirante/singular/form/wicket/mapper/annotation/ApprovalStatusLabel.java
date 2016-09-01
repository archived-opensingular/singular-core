package br.net.mirante.singular.form.wicket.mapper.annotation;

import static br.net.mirante.singular.util.wicket.util.Shortcuts.*;

import org.apache.wicket.markup.html.basic.Label;

import br.net.mirante.singular.commons.util.FormatUtil;
import br.net.mirante.singular.form.wicket.model.SInstanceValueModel;

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