package br.net.mirante.singular.flow.core.renderer;

import java.awt.Color;
import java.awt.Rectangle;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import y.base.Edge;
import y.base.Node;
import y.base.NodeCursor;
import y.io.IOHandler;
import y.io.ImageIoOutputHandler;
import y.layout.Layouter;
import y.module.LayoutModule;
import y.option.OptionHandler;
import y.view.EdgeLabel;
import y.view.Graph2D;
import y.view.Graph2DLayoutExecutor;
import y.view.Graph2DView;
import y.view.NodeLabel;
import y.view.NodePort;
import y.view.NodePortLayoutConfigurator;
import y.view.NodeRealizer;
import y.view.NodeScaledPortLocationModel;
import y.view.tabular.TableGroupNodeRealizer;
import br.net.mirante.singular.flow.core.EventType;
import br.net.mirante.singular.flow.core.FlowMap;
import br.net.mirante.singular.flow.core.MTask;
import br.net.mirante.singular.flow.core.MTaskEnd;
import br.net.mirante.singular.flow.core.MTransition;
import br.net.mirante.singular.flow.core.ProcessDefinition;

import com.google.common.base.Throwables;
import com.yworks.yfiles.bpmn.layout.BpmnLayouter;
import com.yworks.yfiles.bpmn.view.ActivityTypeEnum;
import com.yworks.yfiles.bpmn.view.BpmnLayoutConfigurator;
import com.yworks.yfiles.bpmn.view.BpmnRealizerFactory;
import com.yworks.yfiles.bpmn.view.BpmnTypeEnum;
import com.yworks.yfiles.bpmn.view.EventCharEnum;
import com.yworks.yfiles.bpmn.view.EventPortSupport;
import com.yworks.yfiles.bpmn.view.TaskTypeEnum;

/**
 * https://www.yworks.com/en/products_yfiles_about.html
 *
 */
public class YFilesFlowRenderer extends LayoutModule implements IFlowRenderer {

    private static final String MINIMUM_NODE_DISTANCE = "Minimum Node Distance";
    private static final String MINIMUM_EDGE_DISTANCE = "Minimum Edge Distance";
    private static final String MINIMUM_LABEL_MARGIN = "Minimum Label Margin";
    private static final String MINIMUM_ICON_MARGIN = "Minimum Icon Margin";
    private static final String MINIMUM_POOL_DISTANCE = "Minimum Pool Distance";
    private static final String LANE_BORDER_INSET = "Lane Border Inset";

    private static final int ORIENTATION_VALUE = 0;
    private static final double MINIMUM_NODE_DISTANCE_VALUE = 50;
    private static final double MINIMUM_EDGE_DISTANCE_VALUE = 20;
    private static final double MINIMUM_POOL_DISTANCE_VALUE = 20;
    private static final double MINIMUM_LABEL_MARGIN_VALUE = 5;
    private static final double MINIMUM_ICON_MARGIN_VALUE = 30;
    private static final double LANE_BORDER_INSET_VALUE = 20;

    private static final String ORIENTATION = "Orientation";
    private static final String LEFT_TO_RIGHT = "Left to Right";
    private static final String TOP_TO_BOTTOM = "Top to Bottom";
    private static final String AUTOMATIC = "Automatic";

    private static final Color START_LINE_COLOR = new Color(39, 174, 39);
    private static final Color INTER_LINE_COLOR = new Color(220, 186, 0);
    private static final Color END_LINE_COLOR = new Color(177, 31, 31);
    private static final Color FILL_1_COLOR = new Color(255, 255, 255, 230);
    private static final Color FILL_2_COLOR = new Color(212, 212, 212, 204);

    private static final double NODE_PORT_SIZE = 25;

    private static final String[] orientEnum = {
            AUTOMATIC,
            LEFT_TO_RIGHT,
            TOP_TO_BOTTOM
    };

    private static YFilesFlowRenderer instance = null;

    private YFilesFlowRenderer() {
        super("BPMN Layouter", "Mirante Tecnologia", "Gerência de leiaute para os diagramas de BPM");
    }

    public static YFilesFlowRenderer getInstance() {
        if (instance == null) {
            synchronized(YFilesFlowRenderer.class){
                if (instance == null) {
                    instance = new YFilesFlowRenderer();
                }
            }
        }
        return instance;
    }

    @Override
    public byte[] generateImage(ProcessDefinition<?> definicao) {
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        drawDiagrama(generateDiagrama(definicao), os);
        return os.toByteArray();
    }

    private Graph2DView generateDiagrama(ProcessDefinition<?> definicao) {
        Graph2DView view = new Graph2DView();
        Graph2D graph = view.getGraph2D();

        final FlowMap flow = definicao.getFlowMap();
        final Map<String, Node> mapaVertice = new HashMap<>();
        for (final MTask<?> task : flow.getTasks()) {
            final Node v = adicionarNode(graph, task);
            mapaVertice.put(task.getAbbreviation(), v);
        }
        for (final MTaskEnd task : flow.getEndTasks()) {
            final Node v = adicionarNode(graph, task);
            mapaVertice.put(task.getAbbreviation(), v);
        }
        adicionarStartNode(graph, flow.getStartTask(), mapaVertice);
        for (final MTask<?> task : flow.getTasks()) {
            for (final MTransition transicao : task.getTransitions()) {
                adicionarEdge(graph, transicao, mapaVertice);
            }
        }
        super.start(graph);
        return view;
    }

    private void drawDiagrama(Graph2DView view, OutputStream os) {
        view.setZoom(1.0);

        Rectangle rectangle = view.getGraph2D().getBoundingBox();
        view.setSize((int) rectangle.getWidth() + 2, (int) rectangle.getHeight() + 2);
        view.setCenter(rectangle.getWidth() / 2.0 + rectangle.getX(), rectangle.getHeight() / 2.0 + rectangle.getY());
        view.updateView();
        writeDiagrama(view.getGraph2D(), os);
    }

    private void writeDiagrama(Graph2D graph, OutputStream os) {
        try {
            IOHandler ioh = new ImageIoOutputHandler(ImageIO.getImageWritersBySuffix("png").next());
            ioh.write(graph, os);
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }

    @Override
    protected void mainrun() {
        final Graph2D graph = getGraph2D();

        final BpmnLayouter layouter = (BpmnLayouter) createLayouter(graph);
        boolean horizontalLayout = (layouter.getLayoutOrientation() == BpmnLayouter.ORIENTATION_LEFT_TO_RIGHT);

        final Graph2DLayoutExecutor executor = getLayoutExecutor();
        executor.getTableLayoutConfigurator().setHorizontalLayoutConfiguration(horizontalLayout);
        executor.getTableLayoutConfigurator().setTableToTableDistance(layouter.getPoolDistance());
        executor.getTableLayoutConfigurator().setFromSketchModeEnabled(true);
        executor.setConfiguringTableNodeRealizers(true);

        final BpmnLayoutConfigurator configurator = new BpmnLayoutConfigurator();
        if (layouter.getLayoutMode() == BpmnLayouter.MODE_ROUTE_EDGES) {
            final NodePortLayoutConfigurator portConfigurator =
                    new NodePortLayoutConfigurator();
            portConfigurator.setAutomaticEdgeGroupsEnabled(false);
            portConfigurator.setAutomaticPortConstraintsEnabled(true);
            executor.setNodePortConfigurator(portConfigurator);
        } else {
            executor.setNodePortConfigurator(
                    configurator.createNodePortLayoutConfigurator());
        }

        try {
            configurator.prepareAll(graph);
            launchLayouter(layouter);
        } finally {
            configurator.restoreAll(graph);
        }
    }

    @Override
    protected OptionHandler createOptionHandler() {
        OptionHandler op = new OptionHandler(getModuleName());
        op.addEnum(ORIENTATION, orientEnum, ORIENTATION_VALUE);
        op.addDouble(MINIMUM_NODE_DISTANCE, MINIMUM_NODE_DISTANCE_VALUE);
        op.addDouble(MINIMUM_EDGE_DISTANCE, MINIMUM_EDGE_DISTANCE_VALUE);
        op.addDouble(MINIMUM_POOL_DISTANCE, MINIMUM_POOL_DISTANCE_VALUE);
        op.addDouble(MINIMUM_LABEL_MARGIN, MINIMUM_LABEL_MARGIN_VALUE);
        op.addDouble(MINIMUM_ICON_MARGIN, MINIMUM_ICON_MARGIN_VALUE);
        op.addDouble(LANE_BORDER_INSET, LANE_BORDER_INSET_VALUE);
        return op;
    }

    private Layouter createLayouter(Graph2D graph) {
        final OptionHandler op = getOptionHandler();

        BpmnLayouter layouter = new BpmnLayouter();
        layouter.setLayoutMode(BpmnLayouter.MODE_FULL_LAYOUT);
        layouter.setSphereOfAction(BpmnLayouter.LAYOUT_ALL_ELEMENTS);

        layouter.setMinimumNodeDistance(op.getDouble(MINIMUM_NODE_DISTANCE));
        layouter.setMinimumEdgeLength(op.getDouble(MINIMUM_EDGE_DISTANCE));
        layouter.setLaneInsets(op.getDouble(LANE_BORDER_INSET));

        if (op.get(ORIENTATION).equals(AUTOMATIC)) {
            layouter.setLayoutOrientation(BpmnLayouter.ORIENTATION_LEFT_TO_RIGHT);

            for (NodeCursor nc = graph.nodes(); nc.ok(); nc.next()) {
                Node n = nc.node();
                if (graph.getRealizer(n) instanceof TableGroupNodeRealizer) {
                    BpmnTypeEnum poolType = BpmnRealizerFactory.getType(graph.getRealizer(n));
                    if (BpmnTypeEnum.POOL_TYPE_COLUMN.equals(poolType)) {
                        layouter.setLayoutOrientation(BpmnLayouter.ORIENTATION_TOP_TO_BOTTOM);
                    } else {
                        layouter.setLayoutOrientation(BpmnLayouter.ORIENTATION_LEFT_TO_RIGHT);
                    }
                    break;
                }
            }
        } else if (op.get(ORIENTATION).equals(LEFT_TO_RIGHT)) {
            layouter.setLayoutOrientation(BpmnLayouter.ORIENTATION_LEFT_TO_RIGHT);
        } else {
            layouter.setLayoutOrientation(BpmnLayouter.ORIENTATION_TOP_TO_BOTTOM);
        }

        layouter.setPoolDistance(op.getDouble(MINIMUM_POOL_DISTANCE));
        return layouter;
    }

    protected void adicionarStartNode(Graph2D graph, final MTask<?> task, final Map<String, Node> mapaVertice) {
        Node start = graph.createNode(BpmnRealizerFactory.createEvent(
                BpmnTypeEnum.EVENT_TYPE_PLAIN, EventCharEnum.EVENT_CHARACTERISTIC_START));
        graph.getRealizer(start).setLineColor(START_LINE_COLOR);
        graph.getRealizer(start).setFillColor(FILL_1_COLOR);
        graph.getRealizer(start).setFillColor2(FILL_2_COLOR);
        final Node first = mapaVertice.get(task.getAbbreviation());
        graph.createEdge(start, first,
                BpmnRealizerFactory.createConnection(BpmnTypeEnum.CONNECTION_TYPE_SEQUENCE_FLOW));
    }

    protected Node adicionarNode(Graph2D graph, final MTask<?> task) {
        Node node;
        boolean isActivity = false;
        boolean isTask = false;
        if (task.isWait()) {
            node = graph.createNode(BpmnRealizerFactory.createEvent(
                    BpmnTypeEnum.EVENT_TYPE_TIMER,
                    EventCharEnum.EVENT_CHARACTERISTIC_INTERMEDIATE_BOUNDARY_INTERRUPTING));
            graph.getRealizer(node).setLineColor(INTER_LINE_COLOR);
        } else if (task.isEnd()) {
            node = graph.createNode(BpmnRealizerFactory.createEvent(
                    BpmnTypeEnum.EVENT_TYPE_PLAIN, EventCharEnum.EVENT_CHARACTERISTIC_END));
            graph.getRealizer(node).setLineColor(END_LINE_COLOR);
        } else if (task.isJava()) {
            if (task.getName().startsWith("Notificar")) {
                node = graph.createNode(BpmnRealizerFactory.createEvent(
                        BpmnTypeEnum.EVENT_TYPE_MESSAGE,
                        EventCharEnum.EVENT_CHARACTERISTIC_INTERMEDIATE_BOUNDARY_INTERRUPTING));
                graph.getRealizer(node).setLineColor(INTER_LINE_COLOR);
            } else {
                node = graph.createNode(BpmnRealizerFactory.createActivity(ActivityTypeEnum.TASK, TaskTypeEnum.SERVICE));
                isTask = true;
            }
        } else if (task.isPeople()) {
            node = graph.createNode(BpmnRealizerFactory.createActivity(ActivityTypeEnum.TASK, TaskTypeEnum.USER));
            isTask = true;
        } else {
            node = graph.createNode(BpmnRealizerFactory.createActivity(BpmnTypeEnum.ACTIVITY_TYPE));
            isActivity = true;
        }
        graph.getRealizer(node).setFillColor(FILL_1_COLOR);
        graph.getRealizer(node).setFillColor2(FILL_2_COLOR);

        String label = task.getName().replaceAll("\\s", "\n");
        if (isActivity || isTask) {
            final OptionHandler op = getOptionHandler();
            NodeLabel nodeLabel = new NodeLabel(label);
            nodeLabel.setPosition(NodeLabel.CENTER);
            graph.getRealizer(node).addLabel(nodeLabel);
            double labelWidth;
            double labelHeight;
            if (isActivity) {
                labelWidth = nodeLabel.getWidth() + op.getDouble(MINIMUM_LABEL_MARGIN);
                labelHeight = nodeLabel.getHeight() + op.getDouble(MINIMUM_LABEL_MARGIN);
            } else {
                labelWidth = nodeLabel.getWidth() + op.getDouble(MINIMUM_ICON_MARGIN);
                labelHeight = nodeLabel.getHeight() + op.getDouble(MINIMUM_LABEL_MARGIN);
                nodeLabel.setPosition(NodeLabel.RIGHT);
            }
            if (graph.getRealizer(node).getWidth() < labelWidth) {
                graph.getRealizer(node).setWidth(labelWidth);
            }
            if (graph.getRealizer(node).getHeight() < labelHeight) {
                graph.getRealizer(node).setHeight(labelHeight);
            }
        } else {
            graph.getRealizer(node).setLabelText(label);
        }
        return node;
    }

    protected void adicionarEdge(Graph2D graph, final MTransition transicao, final Map<String, Node> mapaVertice) {
        final Node node1 = mapaVertice.get(transicao.getOrigin().getAbbreviation());
        final Node node2 = mapaVertice.get(transicao.getDestination().getAbbreviation());
        Edge edge = graph.createEdge(node1, node2,
                BpmnRealizerFactory.createConnection(BpmnTypeEnum.CONNECTION_TYPE_SEQUENCE_FLOW));
        String nome = transicao.getName();
        if (!transicao.getDestination().getName().equals(nome)) {
            graph.getRealizer(edge).addLabel(new EdgeLabel(nome.replaceAll("\\s", "\n"), EdgeLabel.FREE));
        }
        if (transicao.getPredicate() != null) {
            adicionarNodePort(graph, node1, edge, transicao.getPredicate().getEventType());
        }
    }

    protected void adicionarNodePort(Graph2D graph, Node node, Edge edge, EventType eventType) {
        BpmnTypeEnum type;
        if (EventType.Message.equals(eventType)) {
            type = BpmnTypeEnum.EVENT_TYPE_MESSAGE;
        } else if (EventType.Conditional.equals(eventType)) {
            type = BpmnTypeEnum.EVENT_TYPE_CONDITIONAL;
        } else if (EventType.Error.equals(eventType)) {
            type = BpmnTypeEnum.EVENT_TYPE_ERROR;
        } else if (EventType.Signal.equals(eventType)) {
            type = BpmnTypeEnum.EVENT_TYPE_SIGNAL;
        } else if (EventType.Timer.equals(eventType)) {
            type = BpmnTypeEnum.EVENT_TYPE_TIMER;
        } else {
            type = BpmnTypeEnum.EVENT_TYPE_LINK;
        }

        NodeRealizer portRealizer = BpmnRealizerFactory.createEvent(type, EventCharEnum.EVENT_CHARACTERISTIC_START);
        NodePort port = EventPortSupport.createEventPort(portRealizer);
        portRealizer.setLineColor(START_LINE_COLOR);
        portRealizer.setFillColor(FILL_1_COLOR);
        portRealizer.setFillColor2(FILL_2_COLOR);
        portRealizer.setHeight(NODE_PORT_SIZE);
        portRealizer.setWidth(NODE_PORT_SIZE);

        if (graph.getRealizer(node).portCount() == 0) {
            port.setModelParameter(NodeScaledPortLocationModel.NODE_TOP_ANCHORED);
        } else {
            port.setModelParameter(NodeScaledPortLocationModel.NODE_BOTTOM_ANCHORED);
        }
        graph.getRealizer(node).addPort(port);
        port.bindSourcePort(edge);
    }
}
