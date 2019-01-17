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
import org.opensingular.form.type.core.annotation.AnnotationClassifier;
import org.opensingular.form.type.core.annotation.AtrAnnotation;
import org.opensingular.form.type.core.annotation.SIAnnotation;
import org.opensingular.form.view.SViewBooleanSwitch;
import org.opensingular.form.view.SViewTextArea;
import org.opensingular.lib.commons.lambda.IPredicate;
import org.opensingular.lib.commons.lambda.ISupplier;
import org.opensingular.lib.commons.ref.Out;
import org.opensingular.lib.commons.util.HTMLUtil;

import javax.annotation.Nonnull;
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
        return getActions(target, instance, null);
    }

    @Override
    public Iterable<SInstanceAction> getActions(ISInstanceActionCapable target, SInstance instance, AnnotationClassifier annotationClassifier) {
        final boolean annotatable = getAnnotation(instance, annotationClassifier).isAnnotated();
        if (!annotatable || !annotationsVisible.test(instance))
            return Collections.emptyList();

        final boolean editable = annotationsEditable.test(instance);

        SInstanceAction editAction = new SInstanceAction(SInstanceAction.ActionType.NORMAL)
                .setIcon(resolveIcon(instance, annotationClassifier))
                .setText(getEditActionTitle(instance))
                .setPosition(Integer.MAX_VALUE)
                .setPreview(resolvePreview(instance, editable, annotationClassifier))
                .setActionHandler(new EditAnnotationHandler(annotationClassifier));

        return Collections.singletonList(editAction);
    }

    public AtrAnnotation getAnnotation(SInstance instance, AnnotationClassifier annotationClassifier) {
        AtrAnnotation atrAnnotation = instance.asAtrAnnotation();
        if (annotationClassifier == null) {
            return atrAnnotation;
        } else {
            SIAnnotation annotation = atrAnnotation.annotation(annotationClassifier);
            return annotation.asAtrAnnotation();
        }
    }

    private String getEditActionTitle(SInstance instance) {
        String label = instance.asAtr().getLabel();
        if (label != null) {
            return "Comentários sobre " + label;
        } else {
            return "Comentário";
        }
    }

    private Preview resolvePreview(SInstance instance, boolean editable, AnnotationClassifier annotationClassifier) {
        if (isEmpty(instance, annotationClassifier)) {
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
                            HTMLUtil.escapeHtml(Objects.toString(getAnnotation(instance, annotationClassifier).text(), "")),
                            isTrue(getAnnotation(instance, annotationClassifier).approved())
                                    ? "<div class='annotation-status annotation-status-approved'>Aprovado</div>"
                                    : isFalse(getAnnotation(instance, annotationClassifier).approved())
                                    ? "<div class='annotation-status annotation-status-rejected'>Rejeitado</div>"
                                    : ""))
                    .setFormat("html")
                    .setActions(
                            (editable)
                                    ? Arrays.asList(
                                    new SInstanceAction(ActionType.LINK)
                                            .setText("Editar")
                                            .setIcon(SIcon.resolve(SingularFormAnnotationsIconProvider.ANNOTATION_EDIT))
                                            .setActionHandler(new EditAnnotationHandler(annotationClassifier)),
                                    new SInstanceAction(ActionType.LINK)
                                            .setText("Remover")
                                            .setIcon(SIcon.resolve(SingularFormAnnotationsIconProvider.ANNOTATION_REMOVE))
                                            .setActionHandler(new RemoveAnnotationHandler(annotationClassifier)))
                                    : Collections.emptyList());
        }
    }

    private SIcon resolveIcon(SInstance instance, AnnotationClassifier annotationClassifier) {
        if (isApproved(instance, annotationClassifier))
            return SIcon.resolve(SingularFormAnnotationsIconProvider.ANNOTATION_APPROVED).setColors("#7f7", "transparent");
        else if (isRejected(instance, annotationClassifier))
            return SIcon.resolve(SingularFormAnnotationsIconProvider.ANNOTATION_REJECTED).setColors("#f77", "transparent");
        else
            return SIcon.resolve(SingularFormAnnotationsIconProvider.ANNOTATION_EMPTY).setColors("#aaa", "transparent");
    }

    private boolean isEmpty(SInstance instance, AnnotationClassifier annotationClassifier) {
        return !isApproved(instance, annotationClassifier) && !isRejected(instance, annotationClassifier);
    }

    private boolean isRejected(SInstance instance, AnnotationClassifier annotationClassifier) {
        return isFalse(getAnnotation(instance, annotationClassifier).approved());
    }

    private boolean isApproved(SInstance instance, AnnotationClassifier annotationClassifier) {
        return isTrue(getAnnotation(instance, annotationClassifier).approved());
    }

    private static final class EditAnotacaoRefType extends RefType {
        static final String JUSTIFICATION = "justificativa";
        static final String APPROVED = "aprovado";

        @Nonnull
        @Override
        protected SType<?> retrieve() {
            final SDictionary dict = SDictionary.create();
            final PackageBuilder pkg = dict.createNewPackage("anotacoes");
            final STypeComposite<SIComposite> anotacao = pkg.createCompositeType("anotacao");
            final STypeBoolean aprovado = anotacao.addField(APPROVED, STypeBoolean.class);
            final STypeString justificativa = anotacao.addField(JUSTIFICATION, STypeString.class);

            aprovado.asAtr().label("Aprovado?");
            aprovado.asAtrBootstrap().colPreference(12);
            aprovado.withView(() -> new SViewBooleanSwitch<Boolean>()
                    .setColorFunction(it -> (Boolean.TRUE.equals(it)) ? "success" : "danger")
                    .setTextFunction(it -> (Boolean.TRUE.equals(it)) ? "Sim" : "Não"));

            justificativa.withView(SViewTextArea::new);
            justificativa.asAtr().label("Justificativa").maxLength(5000);

            return anotacao;
        }
    }

    private final class EditAnnotationHandler implements ActionHandler {
        private final AnnotationClassifier annotationClassifier;

        public EditAnnotationHandler(AnnotationClassifier annotationClassifier) {
            this.annotationClassifier = annotationClassifier;
        }

        @Override
        public void onAction(SInstanceAction action, ISupplier<SInstance> fieldInstance, Delegate delegate) {
            ISupplier<SInstance> formSupplier = () -> {
                SInstance ins = SDocumentFactory.empty().createInstance(new EditAnotacaoRefType());
                ins.getField(EditAnotacaoRefType.APPROVED)
                        .setValue(getAnnotation(fieldInstance.get(), annotationClassifier).approved());
                ins.getField(EditAnotacaoRefType.JUSTIFICATION)
                        .setValue(getAnnotation(fieldInstance.get(), annotationClassifier).text());
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
                                    .setActionHandler(new ConfirmarEdicaoHandler(fd, annotationClassifier)), //
                            new SInstanceAction(SInstanceAction.ActionType.CANCEL)
                                    .setText("Cancelar")
                                    .setActionHandler(new CloseFormHandler(fd)) //
                    ));
        }
    }

    private final class RemoveAnnotationHandler implements ActionHandler {
        private final AnnotationClassifier annotationClassifier;

        public RemoveAnnotationHandler(AnnotationClassifier annotationClassifier) {
            this.annotationClassifier = annotationClassifier;
        }

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
                                        getAnnotation(d.getInstanceRef().get(), annotationClassifier).clear();
                                        d.refreshFieldForInstance(d.getInstanceRef().get());
                                        fd.close();
                                    }),
                            new SInstanceAction(ActionType.CANCEL)
                                    .setText("Cancelar")
                                    .setActionHandler((a, i, d) -> fd.close())//
                    ));
        }
    }

    private final class ConfirmarEdicaoHandler implements ActionHandler {
        private final FormDelegate formDelegate;
        private final AnnotationClassifier annotationClassifier;

        public ConfirmarEdicaoHandler(FormDelegate formDelegate, AnnotationClassifier annotationClassifier) {
            this.formDelegate = formDelegate;
            this.annotationClassifier = annotationClassifier;
        }

        @Override
        public void onAction(SInstanceAction action, ISupplier<SInstance> actionInstanceSupplier, Delegate delegate) {
            final SInstance formInstance = formDelegate.getFormInstance();
            final SInstance fieldInstance = delegate.getInstanceRef().get();
            final SIAnnotation annotationInstance = getAnnotation(fieldInstance, annotationClassifier).annotation();

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
