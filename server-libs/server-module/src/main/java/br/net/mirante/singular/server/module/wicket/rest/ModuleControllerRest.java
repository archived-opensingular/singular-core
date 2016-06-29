/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.server.module.wicket.rest;

import javax.inject.Inject;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import br.net.mirante.singular.flow.core.Flow;
import br.net.mirante.singular.flow.core.ProcessInstance;
import br.net.mirante.singular.server.commons.config.SingularServerConfiguration;
import br.net.mirante.singular.server.module.config.ActionConfig;
import br.net.mirante.singular.server.module.config.IController;
import br.net.mirante.singular.support.spring.util.AutoScanDisabled;

@AutoScanDisabled
@RequestMapping("/rest/flow")
@RestController
public class ModuleControllerRest {

    @Inject
    private SingularServerConfiguration singularServerConfiguration;

    @RequestMapping(value = "/execute", method = RequestMethod.GET)
    public ActionResponse listMenu(Action action) {

        IController controller = getActionController(action);
        return controller.execute(action);
    }

    private IController getActionController(Action action) {
        final ProcessInstance processInstance = Flow.getProcessInstance(action.getFlowCod().toString());
        final ActionConfig actionConfig = processInstance.getProcessDefinition().getMetaDataValue(ActionConfig.KEY);

        return actionConfig.getCustomAction(action.getName());
    }


}