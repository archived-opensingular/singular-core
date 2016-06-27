/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.flow.core.renderer;

import java.awt.*;
import java.io.*;

import br.net.mirante.singular.flow.core.MTask;
import br.net.mirante.singular.flow.core.MTransition;
import br.net.mirante.singular.flow.core.renderer.bpmn.view.ActivityNodeStyle;
import br.net.mirante.singular.flow.core.renderer.bpmn.view.ChoreographyLabelModel;
import br.net.mirante.singular.flow.core.renderer.bpmn.view.EventNodeStyle;
import br.net.mirante.singular.flow.core.renderer.bpmn.view.TaskType;
import com.google.common.base.Throwables;
import br.net.mirante.singular.flow.core.ProcessDefinition;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.LayoutUtilities;
import com.yworks.yfiles.graph.labelmodels.ExteriorLabelModel;
import com.yworks.yfiles.graph.labelmodels.NinePositionsEdgeLabelModel;
import com.yworks.yfiles.layout.hierarchic.HierarchicLayout;
import com.yworks.yfiles.view.GraphComponent;
import com.yworks.yfiles.view.export.ContextConfigurator;
import com.yworks.yfiles.view.export.PixelImageExporter;

import javax.imageio.ImageIO;

import static br.net.mirante.singular.flow.core.renderer.bpmn.view.EventCharacteristic.END;
import static br.net.mirante.singular.flow.core.renderer.bpmn.view.EventCharacteristic.START;

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
            ByteArrayOutputStream outputStream = exportToPng(graphComponent);
            return outputStream.toByteArray();
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
        endStyle.setCharacteristic(END);
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
//                    graph.addLabel(node, task.getName(), ChoreographyLabelModel.NORTH_MESSAGE);
        graph.addLabel(node, taskLabel(task), ChoreographyLabelModel.NORTH_MESSAGE);
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
        startStyle.setCharacteristic(START);
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

    private ByteArrayOutputStream exportToPng(GraphComponent graphComponent) throws IOException {
        ContextConfigurator configuration = new ContextConfigurator(graphComponent.getContentRect());

        graphComponent.setBounds(0,0,400,400);
        graphComponent.fitGraphBounds();
//            graphComponent.zoomTo(graphComponent.getContentRect());

        PixelImageExporter exporter = new PixelImageExporter(configuration);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        exporter.setBackgroundFill(Color.WHITE);

//            exporter.export(graphComponent, outputStream,"png");
//            outputStream.close();

        ImageIO.write(exporter.exportToBitmap(graphComponent), "png", outputStream);
        return outputStream;
    }
}
