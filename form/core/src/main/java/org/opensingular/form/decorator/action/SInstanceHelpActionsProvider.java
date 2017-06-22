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
        return (isBlank(instance.asAtr().getHelp()))
            ? Collections.emptyList()
            : Arrays.asList(new SInstanceAction(SInstanceAction.ActionType.NORMAL)
                .setIcon(SIcon.resolve("question"))
                .setText("Ajuda")
                .setPosition(Integer.MIN_VALUE)
                .setActionHandler((a, i, d) -> d.showMessage("Ajuda", i.get().asAtr().getHelp())));
    }
}
