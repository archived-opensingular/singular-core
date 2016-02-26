package br.net.mirante.singular.pet.module.ws;

import br.net.mirante.singular.flow.core.ProcessInstance;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/v")
public class SingularViewAPI extends AbstractSingularViewAPI {

    @RequestMapping(path = "/ping", method = RequestMethod.GET)
    public String ping() {
        return "pong";
    }

    @RequestMapping(path = "/executedefaulttransition", method = RequestMethod.GET)
    public void executeDefaultTransition(@RequestParam(name = "processAbbreviation") String processAbbreviation,
                                         @RequestParam(name = "codProcessInstance") Long codProcessInstance) {
        ProcessInstance processInstance = getProcessInstance(processAbbreviation, codProcessInstance);
        processInstance.executeTransition();
    }

    @RequestMapping(path = "/executetransition", method = RequestMethod.GET)
    public void executeTransition(HttpServletRequest request,
                                  HttpServletResponse response,
                                  @RequestParam(name = "processAbbreviation") String processAbbreviation,
                                  @RequestParam(name = "codProcessInstance") Long codProcessInstance,
                                  @RequestParam(name = "transitionName") String transitionName) {
        ProcessInstance processInstance = getProcessInstance(processAbbreviation, codProcessInstance);
        processInstance.executeTransition(transitionName);
    }


}
