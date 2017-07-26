package org.opensingular.form.decorator.action;

import static org.apache.commons.lang3.StringUtils.*;

import java.util.Arrays;
import java.util.Collections;

import org.opensingular.form.SInstance;

/**
 * Provider para a ação de exibição do Help do campo.
 */
public class SInstanceHelpActionsProvider implements ISInstanceActionsProvider {

    @Override
    public Iterable<SInstanceAction> getActions(ISInstanceActionCapable target, SInstance instance) {
        final String helpText = instance.asAtr().getHelp();
        return (isBlank(helpText))
            ? Collections.emptyList()
            : Arrays.asList(new SInstanceAction(SInstanceAction.ActionType.NORMAL)
                .setIcon(SIcon.resolve("question"))
                .setText("Ajuda")
                .setPosition(Integer.MIN_VALUE)
                .setPreview(new SInstanceAction.Preview()
//                    .setTitle("Ajuda")
                    .setMessage(helpText)));
    }
}
