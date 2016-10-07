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

package org.opensingular.flow.core.renderer;

import org.opensingular.flow.core.FlowMap;
import org.opensingular.flow.core.MTask;
import org.opensingular.flow.core.MTransition;
import org.opensingular.flow.core.ProcessDefinition;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.view.GraphComponent;

import java.util.Collection;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;

/**
 * Class responsible for converting a ProcessDefinition into a GraphComponent.
 */
public class YFilesFlowConverter {

    private final ProcessDefinition process;
    private       Map<MTask, INode> nodeMap;
    private       GraphComponent    graphComponent;
    private NodeCreator creator = (task, graph) -> {
        INode node = graph.createNode();
        graph.addLabel(node, task.getName());
        return node;
    };

    private NodeConnector connector = (graph, transition, from, to) -> {
        IEdge edge = graph.createEdge(from, to);
        graph.addLabel(edge, transition.getName());
        return edge;
    };

    public interface NodeCreator {
        INode create(MTask<?> task, IGraph graph);
    }

    public interface NodeConnector {
        IEdge connect(IGraph graph, MTransition transition, INode fromNode, INode toNode);
    }

    public YFilesFlowConverter(ProcessDefinition process) {
        this.process = process;
        nodeMap = newHashMap();
        graphComponent = new GraphComponent();
    }

    public void setConnector(NodeConnector connector) {
        this.connector = connector;
    }

    public void setCreator(NodeCreator creator) {
        this.creator = creator;
    }

    public GraphComponent toGraphComponent() {
        return graphComponent;
    }

    public YFilesFlowConverter build() {
        Collection<MTask<?>> allTasks = flowMap().getAllTasks();
        allTasks.forEach(this::createNode);
        allTasks.forEach(this::connectNodeTransitions);
        return this;
    }

    private void createNode(MTask<?> task){
        IGraph graph = graphComponent.getGraph();
        INode node = creator.create(task, graph);
        nodeMap.put(task, node);
    }

    private void connectNodeTransitions(MTask<?> rootTask){
        rootTask.getTransitions().forEach(this::connectNodes);
    }

    private void connectNodes(MTransition transition){
        INode fromNode = nodeMap.get(transition.getOrigin());
        INode toNode = nodeMap.get(transition.getDestination());
        connector.connect(graphComponent.getGraph(), transition, fromNode, toNode);
    }

    private FlowMap flowMap() {
        return process.getFlowMap();
    }
}
