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

package org.opensingular.server.commons.flow.renderer.remote;

import org.opensingular.server.commons.flow.renderer.remote.dto.Task;
import org.opensingular.server.commons.flow.renderer.remote.dto.Transition;
import org.opensingular.server.commons.flow.renderer.remote.dto.TransitionTask;
import org.opensingular.flow.core.EventType;
import org.opensingular.flow.core.ITaskPredicate;
import org.opensingular.flow.core.MTask;
import org.opensingular.flow.core.MTransition;
import org.opensingular.flow.core.ProcessDefinition;
import org.opensingular.flow.core.renderer.IFlowRenderer;
import org.opensingular.flow.core.renderer.YFilesFlowRenderer;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;

/**
 * https://www.yworks.com/en/products_yfiles_about.html
 */
public class YFilesFlowRemoteRenderer implements IFlowRenderer {

    private String url = "http://singular02.mirante.net.br/yfiles/graph";

    public YFilesFlowRemoteRenderer(String url) {
        if (url != null) {
            this.url = url;
        }
    }

    @Override
    public byte[] generateImage(ProcessDefinition<?> definicao) {
        org.opensingular.server.commons.flow.renderer.remote.dto.ProcessDefinition pd = new org.opensingular.server.commons.flow.renderer.remote.dto.ProcessDefinition();
        pd.setTasks(new ArrayList<>());
        for (MTask<?> task : definicao.getFlowMap().getAllTasks()) {
            pd.getTasks().add(from(task, definicao.getFlowMap().getStartTask()));
        }
        return new RestTemplate().postForObject(url, pd, byte[].class);
    }

    private Task from(MTask<?> task, MTask<?> startTask) {
        Task t = new Task(task.isWait(), task.isJava(), task.isPeople(), task.isEnd(), task.getName(), task.getAbbreviation(), task.equals(startTask), new ArrayList<>(0), task.getMetaDataValue(YFilesFlowRenderer.SEND_EMAIL, false));
        for (MTransition mt : task.getTransitions()) {
            t.getTransitions().add(from(mt));
        }
        return t;
    }

    private Transition from(MTransition mt) {
        Transition t = new Transition(from(mt.getOrigin()), from(mt.getDestination()), mt.getName(), from(mt.getPredicate()));
        return t;
    }

    private EventType from(ITaskPredicate predicate) {
        if (predicate != null) {
            return predicate.getEventType();
        }
        return null;
    }


    private TransitionTask from(MTask<?> origin) {
        return new TransitionTask(origin.getAbbreviation(), origin.getName());
    }
}
