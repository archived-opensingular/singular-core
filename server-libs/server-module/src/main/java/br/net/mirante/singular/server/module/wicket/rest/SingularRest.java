/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.server.module.wicket.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import br.net.mirante.singular.flow.core.ws.BaseSingularRest;

@RestController
@RequestMapping("/rest/flow/")
public class SingularRest extends BaseSingularRest {

    @Override
    @RequestMapping(value = START_INSTANCE, method = RequestMethod.GET)
    public Long startInstance(String processAbbreviation) {
        return super.startInstance(processAbbreviation);
    }

    @Override
    @RequestMapping(value = EXECUTE_DEFAULT_TRANSITION, method = RequestMethod.GET)
    public void executeDefaultTransition(String processAbbreviation,
                                         Long codProcessInstance,
                                         String username) {
        super.executeDefaultTransition(processAbbreviation, codProcessInstance, username);
    }

    @Override
    @RequestMapping(value = EXECUTE_TRANSITION, method = RequestMethod.GET)
    public void executeTransition(String processAbbreviation,
                                  Long codProcessInstance,
                                  String transitionName,
                                  String username) {
        super.executeTransition(processAbbreviation, codProcessInstance, transitionName, username);
    }

    @Override
    @RequestMapping(value = RELOCATE_TASK, method = RequestMethod.GET)
    public void relocateTask(String processAbbreviation,
                             Long codProcessInstance,
                             String username,
                             Integer lastVersion) {
        super.relocateTask(processAbbreviation, codProcessInstance, username, lastVersion);
    }
}
