package org.opensingular.form.decorator.action;

import static org.apache.commons.lang3.BooleanUtils.*;

import java.util.Arrays;
import java.util.Collections;

import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SDictionary;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.decorator.action.SInstanceAction.ActionHandler;
import org.opensingular.form.decorator.action.SInstanceAction.Delegate;
import org.opensingular.form.decorator.action.SInstanceAction.FormDelegate;
import org.opensingular.form.document.RefType;
import org.opensingular.form.document.SDocumentFactory;
import org.opensingular.form.type.core.STypeBoolean;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.type.core.annotation.SIAnnotation;
import org.opensingular.form.view.SViewTextArea;
import org.opensingular.lib.commons.lambda.ISupplier;
import org.opensingular.lib.commons.ref.Out;

/**
 * Provider para a ação de exibição do Help do campo.
 */
public class SInstanceAnnotationActionsProvider implements ISInstanceActionsProvider {

    @Override
    public Iterable<SInstanceAction> getActions(ISInstanceActionCapable target, SInstance instance) {
        final boolean annotatable = instance.asAtrAnnotation().isAnnotated();
        final boolean approved = isTrue(instance.asAtrAnnotation().approved());
        final boolean rejected = isFalse(instance.asAtrAnnotation().approved());

        if (!annotatable)
            return Collections.emptyList();

        final String title = "Comentários sobre " + instance.asAtr().getLabel();
        final SIcon icon;

        if (approved) {
            icon = SIcon.resolve(SingularFormAnnotationsIconProvider.ANNOTATION_APPROVED).setColors("#7f7", "#fff");

        } else if (rejected) {
            icon = SIcon.resolve(SingularFormAnnotationsIconProvider.ANNOTATION_REJECTED).setColors("#f77", "#fff");

        } else {
            icon = SIcon.resolve(SingularFormAnnotationsIconProvider.ANNOTATION_EMPTY).setColors("#aaa", "#fff");

        }
        return Arrays.asList(new SInstanceAction(SInstanceAction.ActionType.NORMAL)
            .setIcon(icon)
            .setText(title)
            .setPosition(Integer.MAX_VALUE)
            .setActionHandler(new AnnotationHandler(title)));
    }

    private static final class FormAnotacaoRefType extends RefType {
        static final String JUSTIFICATIVA = "justificativa";
        static final String APROVADO      = "aprovado";

        @Override
        protected SType<?> retrieve() {
            final SDictionary dict = SDictionary.create();
            final PackageBuilder pkg = dict.createNewPackage("anotacoes");
            final STypeComposite<SIComposite> anotacao = pkg.createCompositeType("anotacao");
            final STypeBoolean aprovado = anotacao.addField(APROVADO, STypeBoolean.class);
            final STypeString justificativa = anotacao.addField(JUSTIFICATIVA, STypeString.class);

            aprovado.asAtr().label("Aprovado?");

            justificativa.setView(SViewTextArea::new);
            justificativa.asAtr().label("Justificativa");

            return anotacao;
        }
    }

    private static final class AnnotationHandler implements ActionHandler {
        private final String title;
        private AnnotationHandler(String title) {
            this.title = title;
        }
        @Override
        public void onAction(SInstanceAction action, ISupplier<SInstance> fieldInstance, Delegate delegate) {
            ISupplier<SInstance> formSupplier = () -> {
                SInstance ins = SDocumentFactory.empty().createInstance(new FormAnotacaoRefType());
                ins.getField(FormAnotacaoRefType.APROVADO)
                    .setValue(fieldInstance.get().asAtrAnnotation().approved());
                ins.getField(FormAnotacaoRefType.JUSTIFICATIVA)
                    .setValue(fieldInstance.get().asAtrAnnotation().text());
                return ins;
            };
            Out<SInstanceAction.FormDelegate> formDelegate = new Out<>();
            delegate.openForm(formDelegate,
                title,
                formSupplier,
                Arrays.asList(

                    new SInstanceAction(SInstanceAction.ActionType.NORMAL)
                        .setText("Cancelar")
                        .setActionHandler(new CloseFormHandler(formDelegate)),

                    new SInstanceAction(SInstanceAction.ActionType.PRIMARY)
                        .setText("Confirmar")
                        .setActionHandler(new ConfirmarHandler(formDelegate))
                //
                ));
        }
    }

    private static final class ConfirmarHandler implements ActionHandler {
        private final Out<FormDelegate> formDelegate;
        public ConfirmarHandler(Out<FormDelegate> formDelegate) {
            this.formDelegate = formDelegate;
        }
        @Override
        public void onAction(SInstanceAction action, ISupplier<SInstance> actionInstanceSupplier, Delegate delegate) {
            final SInstance formInstance = formDelegate.get().getFormInstance();
            final SInstance fieldInstance = delegate.getInstanceRef().get();
            final SIAnnotation annotationInstance = fieldInstance.asAtrAnnotation().annotation();

            annotationInstance.setApproved(formInstance.getValue(FormAnotacaoRefType.APROVADO));
            annotationInstance.setText(formInstance.getValue(FormAnotacaoRefType.JUSTIFICATIVA));

            delegate.refreshFieldForInstance(fieldInstance);
            formDelegate.get().close();
        }
    }

    private static final class CloseFormHandler implements ActionHandler {
        private final Out<FormDelegate> formDelegate;
        public CloseFormHandler(Out<FormDelegate> formDelegate) {
            this.formDelegate = formDelegate;
        }
        @Override
        public void onAction(SInstanceAction action, ISupplier<SInstance> actionInstanceSupplier, Delegate delegate) {
            formDelegate.get().close();
        }
    }
}
