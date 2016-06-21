package br.net.mirante.singular.flow.core.renderer;

import br.net.mirante.singular.flow.core.FlowMap;
import br.net.mirante.singular.flow.core.MTask;
import br.net.mirante.singular.flow.core.MTransition;
import br.net.mirante.singular.flow.core.ProcessInstance;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.view.GraphComponent;

import java.util.Collection;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;

/**
 * Created by nuk on 21/06/16.
 */
public class YFilesFlowConverter {

    private final ProcessInstance process;
    private Map<MTask, INode> nodeMap;
    private GraphComponent graphComponent;

    public YFilesFlowConverter(ProcessInstance process) {
        this.process = process;
        nodeMap = newHashMap();
        graphComponent = new GraphComponent();
        buildGraph();
    }

    public GraphComponent toGraphComponent() {
        return graphComponent;
    }

    private void buildGraph() {
        Collection<MTask<?>> allTasks = flowMap().getAllTasks();
        allTasks.forEach(this::createNode);
        allTasks.forEach(this::connectNodeTransitions);
    }

    private void createNode(MTask<?> task){
        IGraph graph = graphComponent.getGraph();
        INode node = createNode(task, graph);
        nodeMap.put(task, node);
    }

    private INode createNode(MTask<?> task, IGraph graph) {
        INode node = graph.createNode();
        graph.addLabel(node, task.getName());
        return node;
    }

    private void connectNodeTransitions(MTask<?> rootTask){
        rootTask.getTransitions().forEach(this::connectNodes);
    }

    private void connectNodes(MTransition transition){
        INode fromNode = nodeMap.get(transition.getOrigin());
        INode toNode = nodeMap.get(transition.getDestination());
        connectNodes(graphComponent.getGraph(), transition, fromNode, toNode);
    }

    private IEdge connectNodes(IGraph graph, MTransition transition, INode fromNode, INode toNode) {
        IEdge edge = graph.createEdge(fromNode, toNode);
        graph.addLabel(edge, transition.getName());
        return edge;
    }

    private FlowMap flowMap() {
        return process.getProcessDefinition().getFlowMap();
    }
}
