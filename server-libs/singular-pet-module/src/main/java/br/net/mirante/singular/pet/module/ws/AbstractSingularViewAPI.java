package br.net.mirante.singular.pet.module.ws;

import br.net.mirante.singular.flow.core.Flow;
import br.net.mirante.singular.flow.core.ProcessInstance;

public abstract class AbstractSingularViewAPI {

    protected ProcessInstance getProcessInstance(String processAbbreviation, Long codProcessInstance) {
        return Flow.getProcessDefinitionWith(processAbbreviation).getDataService().retrieveInstance(codProcessInstance.intValue());
    }
}
