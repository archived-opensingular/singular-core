package br.net.mirante.singular.showcase.component.validation;

import java.util.Optional;

import org.apache.wicket.ajax.markup.html.form.AjaxButton;

import br.net.mirante.singular.showcase.component.CaseBase;
import br.net.mirante.singular.showcase.component.ResourceRef;
import static br.net.mirante.singular.showcase.component.ResourceRef.forSource;
import static br.net.mirante.singular.util.wicket.util.WicketUtils.$b;

public class CaseValidationPartial extends CaseBase {

    public CaseValidationPartial() {
        super("Partial");
        setDescriptionHtml("É possível validar somente uma parte do formulário, no exemplo a seguir somente o campo \"Obrigatório 1\" será validado ao acionar a validação parcial." );
        getBotoes().add((id, currentInstance) -> {
            final AjaxButton aj = new PartialValidationButton(id, currentInstance);

            aj.add($b.attr("value", "Validação Parcial"));
            aj.add($b.classAppender("grey"));

            return aj;
        });
        final Optional<ResourceRef> ref = forSource(PartialValidationButton.class);
        if (ref.isPresent()) {
            getAditionalSources().add(ref.get());
        }
    }
}
