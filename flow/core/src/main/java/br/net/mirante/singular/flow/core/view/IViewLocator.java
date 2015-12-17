package br.net.mirante.singular.flow.core.view;

import br.net.mirante.singular.flow.core.ProcessInstance;
import br.net.mirante.singular.flow.core.TaskInstance;

public interface IViewLocator {

    public Lnk getDefaultHrefFor(ProcessInstance processInstance);

    public Lnk getDefaultHrefFor(TaskInstance taskInstance);

}
