/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.flow.core.renderer;

import java.awt.*;
import java.io.*;

import org.opensingular.flow.core.MTask;
import org.opensingular.flow.core.ProcessDefinition;
import org.opensingular.flow.core.renderer.bpmn.view.ActivityNodeStyle;
import org.opensingular.flow.core.renderer.bpmn.view.EventCharacteristic;
import org.opensingular.flow.core.renderer.bpmn.view.TaskType;
import org.opensingular.flow.core.MTransition;
import org.opensingular.flow.core.renderer.bpmn.view.EventNodeStyle;
import com.google.common.base.Throwables;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.LayoutUtilities;
import com.yworks.yfiles.graph.labelmodels.ExteriorLabelModel;
import com.yworks.yfiles.graph.labelmodels.FreeNodeLabelModel;
import com.yworks.yfiles.graph.labelmodels.NinePositionsEdgeLabelModel;
import com.yworks.yfiles.layout.hierarchic.HierarchicLayout;
import com.yworks.yfiles.view.GraphComponent;
import com.yworks.yfiles.view.export.ContextConfigurator;
import com.yworks.yfiles.view.export.PixelImageExporter;

/**
 * https://www.yworks.com/en/products_yfiles_about.html
 */
public class YFilesFlowRenderer implements IFlowRenderer {

    private static YFilesFlowRenderer INSTANCE = new YFilesFlowRenderer();

    public static YFilesFlowRenderer getInstance() {
        return INSTANCE;
    }

    @Override
    public byte[] generateImage(ProcessDefinition<?> definicao) {
        try {
            GraphComponent graphComponent = convert(definicao);
            LayoutUtilities.applyLayout(graphComponent.getGraph(), buildLayouter());
            return exportToPng(graphComponent);
        } catch (IOException e) {
            Throwables.propagate(e);
        }
        return null;

    }

    private GraphComponent convert(ProcessDefinition<?> definicao) {
        YFilesFlowConverter converter = buildConverter(definicao);

        return converter.build().toGraphComponent();
    }

    private YFilesFlowConverter buildConverter(ProcessDefinition<?> definicao) {
        YFilesFlowConverter converter = new YFilesFlowConverter(definicao);
        converter.setCreator((task, graph) -> {
            return createNode(definicao, task, graph);
        });
        converter.setConnector((graph, transition, from, to) -> {
            return connectEdges(graph, transition, from, to);
        });
        return converter;
    }

    private INode createNode(ProcessDefinition<?> definicao, MTask<?> task, IGraph graph) {
        INode node = addNode(task, graph);
        if(definicao.getFlowMap().getStartTask().equals(task)){
            addStartNode(graph, node);
        }
        return node;
    }

    private INode addNode(MTask<?> task, IGraph graph) {
        if(task.isEnd()){
            return addEndNode(task, graph);
        }else{
            return addTaskNode(task, graph);
        }
    }

    private INode addEndNode(MTask<?> task, IGraph graph) {
        INode node;EventNodeStyle endStyle = new EventNodeStyle();
        endStyle.setCharacteristic(EventCharacteristic.END);
        node = graph.createNode(new RectD(PointD.ORIGIN, terminationSize()),
                endStyle);
        graph.addLabel(node, task.getName(), ExteriorLabelModel.SOUTH);
        return node;
    }

    private SizeD terminationSize() {
        return new SizeD(40, 25);
    }

    private SizeD activitySize() {
        return new SizeD(96, 60);
    }

    private INode addTaskNode(MTask<?> task, IGraph graph) {
        INode node = graph.createNode(new RectD(PointD.ORIGIN, activitySize()), createStyle(task));
        graph.addLabel(node, taskLabel(task), FreeNodeLabelModel.INSTANCE.createDefaultParameter());
        return node;
    }

    private String taskLabel(MTask<?> task) {
        String name = task.getName();
        if(name != null ){
            name = name.replace(' ','\n');
        }
        return name;
    }

    private ActivityNodeStyle createStyle(MTask<?> task) {
        ActivityNodeStyle style = new ActivityNodeStyle();
        if(task.getTaskType().isJava()){
            style.setTaskType(TaskType.SCRIPT);
        }else if(task.getTaskType().isPeople()){
            style.setTaskType(TaskType.MANUAL);
        }
        return style;
    }


    private void addStartNode(IGraph graph, INode node) {
        EventNodeStyle startStyle = new EventNodeStyle();
        startStyle.setCharacteristic(EventCharacteristic.START);
        INode start = graph.createNode(new RectD(PointD.ORIGIN, terminationSize()),
                startStyle);
        graph.createEdge(start, node);
    }

    private IEdge connectEdges(IGraph graph, MTransition transition, INode from, INode to) {
        IEdge edge = graph.createEdge(from, to);
        graph.addLabel(edge, transition.getName(), NinePositionsEdgeLabelModel.CENTER_ABOVE);
        return edge;
    }

    private HierarchicLayout buildLayouter() {
        HierarchicLayout bpmnLayout = new HierarchicLayout();
        bpmnLayout.setEdgeToEdgeDistance(100);
        bpmnLayout.setNodeToNodeDistance(100);
        bpmnLayout.setNodeToEdgeDistance(100);
        bpmnLayout.setMinimumLayerDistance(100);
        bpmnLayout.setOrthogonalRoutingEnabled(true);
        bpmnLayout.setLayoutOrientation(com.yworks.yfiles.layout.LayoutOrientation.LEFT_TO_RIGHT);
        return bpmnLayout;
    }

    private byte[] exportToPng(GraphComponent graphComponent) throws IOException {


        graphComponent.setBounds(0,0,500,100);
        graphComponent.fitGraphBounds();
        graphComponent.zoomTo(graphComponent.getContentRect());

        ContextConfigurator configuration = new ContextConfigurator(graphComponent.getContentRect());
        PixelImageExporter exporter = new PixelImageExporter(configuration);
        exporter.setBackgroundFill(Color.WHITE);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        exporter.export(graphComponent, outputStream,"png");
            outputStream.close();

        return outputStream.toByteArray();
    }
}
