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

import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SDictionary;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.decorator.action.SInstanceAction.ActionHandler;
import org.opensingular.form.decorator.action.SInstanceAction.ActionType;
import org.opensingular.form.decorator.action.SInstanceAction.Delegate;
import org.opensingular.form.decorator.action.SInstanceAction.FormDelegate;
import org.opensingular.form.decorator.action.SInstanceAction.Preview;
import org.opensingular.form.document.RefType;
import org.opensingular.form.document.SDocumentFactory;
import org.opensingular.form.type.core.STypeBoolean;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.type.core.annotation.SIAnnotation;
import org.opensingular.form.view.SViewBooleanSwitch;
import org.opensingular.form.view.SViewTextArea;
import org.opensingular.lib.commons.lambda.IPredicate;
import org.opensingular.lib.commons.lambda.ISupplier;
import org.opensingular.lib.commons.ref.Out;
import org.opensingular.lib.commons.util.HTMLUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

import static org.apache.commons.lang3.BooleanUtils.isFalse;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

/**
 * Provider para a ação de exibição do Help do campo.
 */
public class SInstanceAnnotationActionsProvider implements ISInstanceActionsProvider {

    private final IPredicate<SInstance> annotationsVisible;
    private final IPredicate<SInstance> annotationsEditable;

    public SInstanceAnnotationActionsProvider(IPredicate<SInstance> annotationsVisible, IPredicate<SInstance> annotationsEditable) {
        this.annotationsVisible = annotationsVisible;
        this.annotationsEditable = annotationsEditable;
    }

    @Override
    public Iterable<SInstanceAction> getActions(ISInstanceActionCapable target, SInstance instance) {
        final boolean annotatable = instance.asAtrAnnotation().isAnnotated();
        if (!annotatable || !annotationsVisible.test(instance))
            return Collections.emptyList();

        final boolean editable = annotationsEditable.test(instance);

        SInstanceAction editAction = new SInstanceAction(SInstanceAction.ActionType.NORMAL)
                .setIcon(resolveIcon(instance))
                .setText(getEditActionTitle(instance))
                .setPosition(Integer.MAX_VALUE)
                .setPreview(resolvePreview(instance, editable))
                .setActionHandler(new EditAnnotationHandler());

        return Collections.singletonList(editAction);
    }

    private static String getEditActionTitle(SInstance instance) {
        String label = instance.asAtr().getLabel();
        if (label != null) {
            return "Comentários sobre " + label;
        } else {
            return "Comentário";
        }
    }

    private static Preview resolvePreview(SInstance instance, boolean editable) {
        if (isEmpty(instance)) {
            return (!editable)
                    ? new Preview()
                    .setMessage("<i>Nenhum comentário</i>")
                    .setFormat("html")
                    : null;

        } else {
            return new Preview()
                    .setTitle("Comentário")
                    .setMessage(String.format(""
                                    + "<div class='annotation-toggle-container'>"
                                    + "<p class='annotation-text'>%s</p>"
                                    + "<hr/>"
                                    + "%s"
                                    + "</div>",
                            HTMLUtil.escapeHtml(Objects.toString(instance.asAtrAnnotation().text(), "")),
                            isTrue(instance.asAtrAnnotation().approved())
                                    ? "<div class='annotation-status annotation-status-approved'>Aprovado</div>"
                                    : isFalse(instance.asAtrAnnotation().approved())
                                    ? "<div class='annotation-status annotation-status-rejected'>Rejeitado</div>"
                                    : ""))
                    .setFormat("html")
                    .setActions(
                            (editable)
                                    ? Arrays.asList(
                                    new SInstanceAction(ActionType.LINK)
                                            .setText("Editar")
                                            .setIcon(SIcon.resolve(SingularFormAnnotationsIconProvider.ANNOTATION_EDIT))
                                            .setActionHandler(new EditAnnotationHandler()),
                                    new SInstanceAction(ActionType.LINK)
                                            .setText("Remover")
                                            .setIcon(SIcon.resolve(SingularFormAnnotationsIconProvider.ANNOTATION_REMOVE))
                                            .setActionHandler(new RemoveAnnotationHandler()))
                                    : Collections.emptyList());
        }
    }

    private static SIcon resolveIcon(SInstance instance) {
        if (isApproved(instance))
            return SIcon.resolve(SingularFormAnnotationsIconProvider.ANNOTATION_APPROVED).setColors("#7f7", "transparent");
        else if (isRejected(instance))
            return SIcon.resolve(SingularFormAnnotationsIconProvider.ANNOTATION_REJECTED).setColors("#f77", "transparent");
        else
            return SIcon.resolve(SingularFormAnnotationsIconProvider.ANNOTATION_EMPTY).setColors("#aaa", "transparent");
    }

    private static boolean isEmpty(SInstance instance) {
        return !isApproved(instance) && !isRejected(instance);
    }

    private static boolean isRejected(SInstance instance) {
        return isFalse(instance.asAtrAnnotation().approved());
    }

    private static boolean isApproved(SInstance instance) {
        return isTrue(instance.asAtrAnnotation().approved());
    }

    private static final class EditAnotacaoRefType extends RefType {
        static final String JUSTIFICATION = "justificativa";
        static final String APPROVED = "aprovado";

        @Override
        protected SType<?> retrieve() {
            final SDictionary dict = SDictionary.create();
            final PackageBuilder pkg = dict.createNewPackage("anotacoes");
            final STypeComposite<SIComposite> anotacao = pkg.createCompositeType("anotacao");
            final STypeBoolean aprovado = anotacao.addField(APPROVED, STypeBoolean.class);
            final STypeString justificativa = anotacao.addField(JUSTIFICATION, STypeString.class);

            aprovado.asAtr().label("Aprovado?");
            aprovado.asAtrBootstrap().colPreference(12);
            aprovado.setView(() -> new SViewBooleanSwitch<Boolean>()
                    .setColorFunction(it -> (Boolean.TRUE.equals(it)) ? "success" : "danger")
                    .setTextFunction(it -> (Boolean.TRUE.equals(it)) ? "Sim" : "Não"));

            justificativa.setView(SViewTextArea::new);
            justificativa.asAtr().label("Justificativa").maxLength(5000);

            return anotacao;
        }
    }

    private static final class EditAnnotationHandler implements ActionHandler {
        @Override
        public void onAction(SInstanceAction action, ISupplier<SInstance> fieldInstance, Delegate delegate) {
            ISupplier<SInstance> formSupplier = () -> {
                SInstance ins = SDocumentFactory.empty().createInstance(new EditAnotacaoRefType());
                ins.getField(EditAnotacaoRefType.APPROVED)
                        .setValue(fieldInstance.get().asAtrAnnotation().approved());
                ins.getField(EditAnotacaoRefType.JUSTIFICATION)
                        .setValue(fieldInstance.get().asAtrAnnotation().text());
                return ins;
            };
            Out<SInstanceAction.FormDelegate> formDelegate = new Out<>();
            delegate.openForm(formDelegate,
                    getEditActionTitle(fieldInstance.get()),
                    null,
                    formSupplier,
                    fd -> Arrays.asList(
                            new SInstanceAction(SInstanceAction.ActionType.CONFIRM)
                                    .setText("Confirmar")
                                    .setActionHandler(new ConfirmarEdicaoHandler(fd)), //
                            new SInstanceAction(SInstanceAction.ActionType.CANCEL)
                                    .setText("Cancelar")
                                    .setActionHandler(new CloseFormHandler(fd)) //
                    ));
        }
    }

    private static final class RemoveAnnotationHandler implements ActionHandler {
        @Override
        public void onAction(SInstanceAction action, ISupplier<SInstance> fieldInstance, Delegate delegate) {
            Out<FormDelegate> formDelegate = new Out<>();
            delegate.openForm(formDelegate,
                    "Você está prestes a remover este comentário",
                    "Deseja realmente prosseguir e apagá-lo?",
                    () -> null,
                    fd -> Arrays.asList(
                            new SInstanceAction(ActionType.CONFIRM)
                                    .setText("Apagar")
                                    .setActionHandler((a, i, d) -> {
                                        d.getInstanceRef().get().asAtrAnnotation().clear();
                                        d.refreshFieldForInstance(d.getInstanceRef().get());
                                        fd.close();
                                    }),
                            new SInstanceAction(ActionType.CANCEL)
                                    .setText("Cancelar")
                                    .setActionHandler((a, i, d) -> fd.close())//
                    ));
        }
    }

    private static final class ConfirmarEdicaoHandler implements ActionHandler {
        private final FormDelegate formDelegate;

        public ConfirmarEdicaoHandler(FormDelegate formDelegate) {
            this.formDelegate = formDelegate;
        }

        @Override
        public void onAction(SInstanceAction action, ISupplier<SInstance> actionInstanceSupplier, Delegate delegate) {
            final SInstance formInstance = formDelegate.getFormInstance();
            final SInstance fieldInstance = delegate.getInstanceRef().get();
            final SIAnnotation annotationInstance = fieldInstance.asAtrAnnotation().annotation();

            annotationInstance.setApproved(formInstance.getValue(EditAnotacaoRefType.APPROVED));
            annotationInstance.setText(formInstance.getValue(EditAnotacaoRefType.JUSTIFICATION));

            delegate.refreshFieldForInstance(fieldInstance);
            formDelegate.close();
        }
    }

    private static final class CloseFormHandler implements ActionHandler {
        private final FormDelegate formDelegate;

        public CloseFormHandler(FormDelegate formDelegate) {
            this.formDelegate = formDelegate;
        }

        @Override
        public void onAction(SInstanceAction action, ISupplier<SInstance> actionInstanceSupplier, Delegate delegate) {
            formDelegate.close();
        }
    }
}
