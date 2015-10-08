package br.net.mirante.singular.flow.core.defaults;

import br.net.mirante.singular.flow.core.ProcessInstance;
import br.net.mirante.singular.flow.core.TaskInstance;
import br.net.mirante.singular.flow.util.view.IViewLocator;
import br.net.mirante.singular.flow.util.view.Lnk;

public class NullViewLocator implements IViewLocator {

    @Override
    public Lnk getDefaultHrefFor(ProcessInstance processInstance) {
        return null;
    }

    @Override
    public Lnk getDefaultHrefFor(TaskInstance taskInstance) {
        return null;
    }

}
