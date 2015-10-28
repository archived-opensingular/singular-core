package br.net.mirante.singular.flow.core.ws;

import br.net.mirante.singular.flow.core.Flow;
import br.net.mirante.singular.flow.core.ProcessDefinition;
import br.net.mirante.singular.flow.core.ProcessInstance;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

@WebService
public class SingularWS {

    @WebMethod(action = "ping")
    public String ping() {
        return "pong";
    }

    @WebMethod(action = "startInstance")
    public Long startInstance(@WebParam(name = "processAbbreviation") String processAbbreviation) {
        ProcessDefinition processo = Flow.getProcessDefinition(processAbbreviation);
        ProcessInstance processInstance = processo.newInstance();
        processInstance.start();
        return processInstance.getEntityCod().longValue();
    }

    @WebMethod(action = "executeDefaultTransition")
    public void executeDefaultTransition(@WebParam(name = "codProcessInstance") Long codProcessInstance) {
        ProcessInstance processInstance = Flow.getProcessInstance(codProcessInstance.intValue());
        processInstance.executeTransition();
    }

    @WebMethod(action = "executeTransition")
    public void executeTransition(@WebParam(name = "codProcessInstance") Long codProcessInstance, @WebParam(name = "transitionName") String transitionName) {
        ProcessInstance processInstance = Flow.getProcessInstance(codProcessInstance.intValue());
        processInstance.executeTransition(transitionName);
    }

}
