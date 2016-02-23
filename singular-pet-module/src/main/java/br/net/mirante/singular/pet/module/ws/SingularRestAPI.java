package br.net.mirante.singular.pet.module.ws;

import javax.xml.ws.WebServiceException;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.net.mirante.singular.flow.core.Flow;
import br.net.mirante.singular.flow.core.MUser;
import br.net.mirante.singular.flow.core.ProcessDefinition;
import br.net.mirante.singular.flow.core.ProcessInstance;

@RestController
@RequestMapping("/r")
public class SingularRestAPI {

    @RequestMapping(path = "/ping", method = RequestMethod.GET)
    public String ping() {
        return "pong";
    }

    @RequestMapping(path = "/startInstance", method = RequestMethod.GET)
    public Long startInstance(@RequestParam(name = "processAbbreviation") String processAbbreviation) {
        ProcessDefinition processo = Flow.getProcessDefinitionWith(processAbbreviation);
        ProcessInstance processInstance = processo.newInstance();
        processInstance.start();
        return processInstance.getEntityCod().longValue();
    }

    @RequestMapping(path = "/executeDefaultTransition", method = RequestMethod.GET)
    public void executeDefaultTransition(@RequestParam(name = "processAbbreviation") String processAbbreviation,
                                         @RequestParam(name = "codProcessInstance") Long codProcessInstance) {
        ProcessInstance processInstance = getProcessInstance(processAbbreviation, codProcessInstance);
        processInstance.executeTransition();
    }

    @RequestMapping(path = "/executeTransition", method = RequestMethod.GET)
    public void executeTransition(@RequestParam(name = "processAbbreviation") String processAbbreviation,
                                  @RequestParam(name = "codProcessInstance") Long codProcessInstance,
                                  @RequestParam(name = "transitionName") String transitionName) {
        ProcessInstance processInstance = getProcessInstance(processAbbreviation, codProcessInstance);
        processInstance.executeTransition(transitionName);
    }

    @RequestMapping(path = "/relocateTask", method = RequestMethod.GET)
    public void relocateTask(@RequestParam(name = "processAbbreviation") String processAbbreviation,
                             @RequestParam(name = "codProcessInstance") Long codProcessInstance,
                             @RequestParam(name = "username") String username) {
        ProcessInstance processInstance = getProcessInstance(processAbbreviation, codProcessInstance);
        MUser user = Flow.getConfigBean().getUserService().saveUserIfNeeded(username);
        if (user == null) {
            throw new WebServiceException("Usuário não encontrado");
        }
        processInstance.getCurrentTask().relocateTask(user, user, false, "");
    }

    private ProcessInstance getProcessInstance(String processAbbreviation, Long codProcessInstance) {
        return Flow.getProcessDefinitionWith(processAbbreviation).getDataService().retrieveInstance(codProcessInstance.intValue());
    }

}
