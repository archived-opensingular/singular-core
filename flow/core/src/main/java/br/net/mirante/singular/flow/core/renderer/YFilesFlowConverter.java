package br.net.mirante.singular.flow.core.renderer;

import br.net.mirante.singular.flow.core.*;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.view.GraphComponent;

import java.util.Collection;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;

/**
 * Created by nuk on 21/06/16.
 */
public class YFilesFlowConverter {

    private final ProcessDefinition process;
    private Map<MTask, INode> nodeMap;
    private GraphComponent graphComponent;
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
