/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.server.module.wicket.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.opensingular.flow.core.ws.BaseSingularRest;

@RestController
@RequestMapping("/rest/flow/")
public class SingularRest extends BaseSingularRest {

    @Override
    @RequestMapping(value = START_INSTANCE, method = RequestMethod.GET)
    public Long startInstance(@RequestParam String processAbbreviation) {
        return super.startInstance(processAbbreviation);
    }

    @Override
    @RequestMapping(value = EXECUTE_DEFAULT_TRANSITION, method = RequestMethod.GET)
    public void executeDefaultTransition(@RequestParam String processAbbreviation,
                                         @RequestParam Long codProcessInstance,
                                         @RequestParam String username) {
        super.executeDefaultTransition(processAbbreviation, codProcessInstance, username);
    }

    @Override
    @RequestMapping(value = EXECUTE_TRANSITION, method = RequestMethod.GET)
    public void executeTransition(@RequestParam String processAbbreviation,
                                  @RequestParam Long codProcessInstance,
                                  @RequestParam String transitionName,
                                  @RequestParam String username) {
        super.executeTransition(processAbbreviation, codProcessInstance, transitionName, username);
    }

    @Override
    @RequestMapping(value = RELOCATE_TASK, method = RequestMethod.GET)
    public void relocateTask(@RequestParam String processAbbreviation,
                             @RequestParam Long codProcessInstance,
                             @RequestParam String username,
                             @RequestParam Integer lastVersion) {
        super.relocateTask(processAbbreviation, codProcessInstance, username, lastVersion);
    }
}
