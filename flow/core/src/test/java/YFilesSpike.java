import br.net.mirante.singular.flow.core.renderer.bpmn.view.ActivityNodeStyle;
import br.net.mirante.singular.flow.core.renderer.bpmn.view.ChoreographyLabelModel;
import br.net.mirante.singular.flow.core.renderer.bpmn.view.EventNodeStyle;
import com.yworks.yfiles.geometry.*;
import com.yworks.yfiles.graph.*;
import com.yworks.yfiles.graph.labelmodels.*;
import com.yworks.yfiles.layout.hierarchic.*;
import com.yworks.yfiles.view.GraphComponent;
import com.yworks.yfiles.view.export.ContextConfigurator;
import com.yworks.yfiles.view.export.PixelImageExporter;

import javax.swing.*;
import java.awt.*;
import java.io.FileOutputStream;

import static br.net.mirante.singular.flow.core.renderer.bpmn.view.EventCharacteristic.END;
import static br.net.mirante.singular.flow.core.renderer.bpmn.view.EventCharacteristic.START;

/**
 * Created by nuk on 17/06/16.
 */
public class YFilesSpike {

    public static void main(String[] args) throws Exception{
        GraphComponent graphComponent = new GraphComponent();
        IGraph graph = graphComponent.getGraph();

//        IEdgeDefaults edgeDefaults = graph.getEdgeDefaults();
//        BpmnEdgeStyle bpmnEdgeStyle = new BpmnEdgeStyle();
//        bpmnEdgeStyle.setType(EdgeType.DEFAULT_FLOW);
//        edgeDefaults.setStyle(bpmnEdgeStyle);
//        edgeDefaults.setStyleInstanceSharingEnabled(false);

//        edgeDefaults.getLabelDefaults().setLayoutParameter(
//                new EdgeSegmentLabelModel(100, 0, 0, true, EdgeSides.ABOVE_EDGE)
//                        .createDefaultParameter());
        // For nodes we use a CompositeLabelModel that combines label placements inside and outside of the node

/*        FoldingManager manager = new FoldingManager();
        IFoldingView foldingView = manager.createFoldingView();
*/
//        INodeDefaults nodeDefaults = graph.getNodeDefaults();
//        nodeDefaults.getPortDefaults().setAutoCleanupEnabled(false);


        // For nodes we use a CompositeLabelModel that combines label placements inside and outside of the node
//        CompositeLabelModel compositeLabelModel = new CompositeLabelModel();
//        compositeLabelModel.getLabelModels().add(new InteriorLabelModel());
//        ExteriorLabelModel exteriorLabelModel = new ExteriorLabelModel();
//        exteriorLabelModel.setInsets(new InsetsD(10));
//        compositeLabelModel.getLabelModels().add(exteriorLabelModel);
//        nodeDefaults.getLabelDefaults().setLayoutParameter(compositeLabelModel.createDefaultParameter());

//        SmartEdgeLabelModel model = new SmartEdgeLabelModel();
//        nodeDefaults.getLabelDefaults().setLayoutParameter(model.createDefaultParameter());

//        graph.getNodeDefaults().getLabelDefaults().setLayoutParameter(
//                new SmartEdgeLabelModel().createDefaultParameter());

        // use a specialized port candidate provider
        /*GraphDecorator decorator = manager.getMasterGraph().getDecorator();
        decorator.getNodeDecorator().getPortCandidateProviderDecorator().setFactory(
                node -> (node.getStyle() instanceof BpmnNodeStyle || node.getStyle() instanceof GroupNodeStyle),
                BpmnPortCandidateProvider::new);
        // Pools only have a dynamic PortCandidate
        decorator.getNodeDecorator().getPortCandidateProviderDecorator().setFactory(
                node -> (node.getStyle() instanceof PoolNodeStyle),
                node -> {
                    DefaultPortCandidate candidate = new DefaultPortCandidate(node);
                    candidate.setValidity(PortCandidateValidity.DYNAMIC);
                    return IPortCandidateProvider.fromCandidates(candidate);
                });*/

        // allow reconnecting of edges
//        decorator.getEdgeDecorator().getEdgeReconnectionPortCandidateProviderDecorator().setImplementation(
//                IEdgeReconnectionPortCandidateProvider.ALL_NODE_CANDIDATES);

        populateGraph(graph);



        HierarchicLayout bpmnLayout = new HierarchicLayout();
        bpmnLayout.setEdgeToEdgeDistance(100);
        bpmnLayout.setNodeToNodeDistance(100);
        bpmnLayout.setNodeToEdgeDistance(100);
        bpmnLayout.setMinimumLayerDistance(100);
//        bpmnLayout.setAutomaticEdgeGroupingEnabled(true);
//        bpmnLayout.setEdgeRoutingItem(EdgeRoutingStyle.ORTHOGONAL);
//        EdgeLayoutDescriptor descriptor = new EdgeLayoutDescriptor();
//        descriptor.setRoutingStyle(new RoutingStyle(EdgeRoutingStyle.ORTHOGONAL));
//        bpmnLayout.setParallelEdgeRouterEnabled(true);
//        EdgeRouter iLayoutStage = new EdgeRouter();
//        bpmnLayout.setParallelEdgeRouter(iLayoutStage);
        bpmnLayout.setOrthogonalRoutingEnabled(true);
        bpmnLayout.setLayoutOrientation(com.yworks.yfiles.layout.LayoutOrientation.LEFT_TO_RIGHT);
//        ILayerConstraintFactory lcf =
//                LayoutUtilities.createIncrementalLayerConstraintFactory(bpmnLayout, graph);
//        TreeLayout bpmnLayout = new TreeLayout();

       /* OrthogonalLayout bpmnLayout = new OrthogonalLayout();
        bpmnLayout.setIntegratedEdgeLabelingEnabled(true);
//        bpmnLayout.setGridSpacing(80);
        bpmnLayout.setParallelEdgeRouterEnabled(true);
//        bpmnLayout.setNodeToNodeDistance(100);
//        bpmnLayout.setNodeToEdgeDistance(100);
//        bpmnLayout.setMinimumLayerDistance(100);
        bpmnLayout.setLayoutOrientation(com.yworks.yfiles.layout.LayoutOrientation.RIGHT_TO_LEFT);

*/
        /*BpmnLayout bpmnLayout = new BpmnLayout();
//        bpmnLayout.setCompactMessageFlowLayering(true);
        bpmnLayout.setScope(Scope.SELECTED_ELEMENTS);
        bpmnLayout.setLayoutOrientation(LayoutOrientation.LEFT_TO_RIGHT);
        bpmnLayout.setMinimumNodeDistance(100);
//        bpmnLayout.setLayoutMode(LayoutMode.ROUTE_EDGES);
        bpmnLayout.setMinimumEdgeLength(20);*/

        LayoutUtilities.applyLayout(graphComponent.getGraph(), bpmnLayout);

//        JFrame frame = new JFrame();
//        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//        frame.setExtendedState(Frame.MAXIMIZED_BOTH);

//        Container contentPane = frame.getRootPane().getContentPane();
//        contentPane.add(graphComponent, BorderLayout.CENTER);

        graphComponent.setBounds(0,0,500,100);
        graphComponent.fitGraphBounds();


//        graphComponent.zoomTo(graphComponent.getContentRect());
//        frame.setSize(graphComponent.getPreferredSize());
//        frame.setVisible(true);


// The entire content as specified in the CanvasComponent instance.
        ContextConfigurator configuration = new ContextConfigurator(graphComponent.getContentRect());

        PixelImageExporter exporter = new PixelImageExporter(configuration);
        exporter.setBackgroundFill(Color.WHITE);
        exporter.export(graphComponent, new FileOutputStream("teste.png"),"png");
//        exporter.exportToBitmap();
    }

    private static void populateGraph(IGraph graph) {
        // Create 10 nodes.
       /* INode[] nodes = new INode[10];
        for (int i = 0; i < nodes.length; i++) {
            if(i % 2 == 0)
                nodes[i] = graph.create(new RectD(PointD.ORIGIN, new SizeD(80, 50)),
                        new ActivityNodeStyle());
            else
                nodes[i] = graph.create(new RectD(PointD.ORIGIN, new SizeD(80, 50)),
                        new EventNodeStyle());
            graph.addLabel(nodes[i], "Node #" + Integer.toString(i));
        }

        // Create 5 edges. Each edge has "even" source node and "odd" target node.
        IEdge[] edges = new IEdge[5];
        for (int i = 0, j = 0; i < nodes.length - 1; i += 2, j++) {
            edges[j] = graph.createEdge(nodes[i], nodes[i + 1]);
            graph.addLabel(edges[j], "Edge #" + Integer.toString(j));
            IEdge e = graph.createEdge(nodes[i], nodes[i % 2]);
            graph.addLabel(e, "Edge %" + Integer.toString(j));
        }*/

        previsaoDeFluxoDeCaixa(graph);

//        canabidiol(graph);

    }

    private static void previsaoDeFluxoDeCaixa(IGraph graph) {
        INode start = addStartNode(graph);

        INode prencher = addActivity(graph, "Preencher Previsão");
        INode notificarEnvio = addActivity(graph, "Notificar Envio");
        INode analisar = addActivity(graph, "Analisar Previsão");
        INode notificarAprovacao = addActivity(graph, "Notificar Aprovação");
        INode notificarRejeicao = addActivity(graph, "Notificar Rejeição");
        INode ajustarPrevisao = addActivity(graph, "Ajustar Previsão");
        INode notificarAjuste = addActivity(graph, "Notificar Ajuste");

        INode aprovado = addEnd(graph, "Aprovado");
        INode cancelado = addEnd(graph, "Cancelado");

        graph.createEdge(start, prencher);
        connect(graph, prencher, cancelado, "Cancelar");
        connect(graph, prencher, notificarEnvio, "Salvar/Enviar");
        connect(graph, notificarEnvio, analisar, "");
        connect(graph, analisar, notificarAprovacao, "Aprovar");
        connect(graph, notificarAprovacao, aprovado, "");
        connect(graph, analisar, notificarRejeicao, "Solicitar Ajuste");
        connect(graph, notificarRejeicao, ajustarPrevisao,"");
        connect(graph, ajustarPrevisao, cancelado, "Cancelar");
        connect(graph, ajustarPrevisao, notificarAjuste, "Salvar/Enviar");
        connect(graph, notificarAjuste, analisar, "");
    }

    private static void canabidiol(IGraph graph) {
        INode start = addStartNode(graph);
        INode aguardando = addActivity(graph, "Aguardando análise");

        INode analise = addActivity(graph, "Em Análise");

        INode gerente = addActivity(graph, "Aguardando Gerente");

        INode deferido = addEnd(graph, "Deferido");

        INode indeferido = addEnd(graph, "Indeferido");

        INode exigencia = addEnd(graph, "Em Exigência");

        graph.createEdge(start, aguardando);
        connect(graph, aguardando, analise, "Iniciar Análise");
        connect(graph, analise, gerente, "Finalizar Análise");
        connect(graph, analise, exigencia, "Enviar Exigência");
        connect(graph, gerente, deferido, "Deferir");
        connect(graph, gerente, indeferido, "Indeferir");
        connect(graph, exigencia, aguardando, "Cumprir Exigência");
    }

    private static INode addStartNode(IGraph graph) {
        EventNodeStyle startStyle = new EventNodeStyle();
        startStyle.setCharacteristic(START);
        return graph.createNode(new RectD(PointD.ORIGIN, new SizeD(40, 25)),
                startStyle);
    }

    private static void connect(IGraph graph, INode aguardando, INode analise, String s) {
        IEdge edge = graph.createEdge(aguardando, analise);
        ILabel label = graph.addLabel(edge, s, NinePositionsEdgeLabelModel.CENTER_ABOVE);
    }

    private static INode addEnd(IGraph graph, String deferido2) {
        EventNodeStyle endStyle = new EventNodeStyle();
//        endStyle.setType(TERMINATE);
        endStyle.setCharacteristic(END);
        INode deferido = graph.createNode(new RectD(PointD.ORIGIN, new SizeD(40, 25)),
                endStyle);
        graph.addLabel(deferido, deferido2, ExteriorLabelModel.SOUTH);
        return deferido;
    }

    private static INode addActivity(IGraph graph, String s) {
        INode aguardando = graph.createNode(new RectD(PointD.ORIGIN, new SizeD(96, 60)),
                new ActivityNodeStyle());
//        graph.addLabel(aguardando, s, InteriorStretchLabelModel.CENTER);
//        graph.addLabel(aguardando, s, InteriorStretchLabelModel.CENTER);
//        InteriorStretchLabelModel model = new InteriorStretchLabelModel();
//        graph.addLabel(aguardando, s, model.createDefaultParameter());
        graph.addLabel(aguardando, s, ChoreographyLabelModel.NORTH_MESSAGE);
//        EdgeSegmentLabelModel model = new EdgeSegmentLabelModel();
//        graph.addLabel(aguardando, s, model.createDefaultParameter());
        return aguardando;
    }

}
