package org.opensingular.form.decorator.action;

import static org.apache.commons.lang3.StringUtils.*;

import java.util.Arrays;
import java.util.Collections;

import org.opensingular.form.SInstance;

public class SInstanceHelpActionsProvider implements ISInstanceActionsProvider {

    @Override
    public Iterable<SInstanceAction> getActions(ISInstanceActionCapable target, SInstance instance) {
        return (isBlank(instance.asAtr().getHelp()))
            ? Collections.emptyList()
            : Arrays.asList(new SInstanceAction(
                SInstanceAction.ActionType.NORMAL,
                SIcon.resolve("question"),
                "Ajuda")
                    .setActionHandler((i, d) -> d.showMessage("Ajuda", i.get().asAtr().getHelp())));
    }
}
