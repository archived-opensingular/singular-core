import org.opensingular.flow.core.renderer.bpmn.view.ActivityNodeStyle;
import org.opensingular.flow.core.renderer.bpmn.view.EventNodeStyle;
import org.opensingular.flow.core.renderer.bpmn.view.TaskType;
import com.yworks.yfiles.geometry.*;
import com.yworks.yfiles.graph.*;
import com.yworks.yfiles.graph.labelmodels.*;
import com.yworks.yfiles.layout.hierarchic.*;
import com.yworks.yfiles.view.GraphComponent;
import com.yworks.yfiles.view.export.ContextConfigurator;
import com.yworks.yfiles.view.export.PixelImageExporter;

import java.awt.*;
import java.io.FileOutputStream;

import static org.opensingular.flow.core.renderer.bpmn.view.EventCharacteristic.END;
import static org.opensingular.flow.core.renderer.bpmn.view.EventCharacteristic.START;


public class YFilesSpike {

    public static void main(String[] args) throws Exception{
        GraphComponent graphComponent = new GraphComponent();
        IGraph graph = graphComponent.getGraph();

        populateGraph(graph);

        HierarchicLayout bpmnLayout = new HierarchicLayout();
        bpmnLayout.setEdgeToEdgeDistance(100);
        bpmnLayout.setNodeToNodeDistance(100);
        bpmnLayout.setNodeToEdgeDistance(100);
        bpmnLayout.setMinimumLayerDistance(100);

        bpmnLayout.setOrthogonalRoutingEnabled(true);
        bpmnLayout.setLayoutOrientation(com.yworks.yfiles.layout.LayoutOrientation.LEFT_TO_RIGHT);

        LayoutUtilities.applyLayout(graphComponent.getGraph(), bpmnLayout);

//        JFrame frame = new JFrame();
//        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//        frame.setExtendedState(Frame.MAXIMIZED_BOTH);

//        Container contentPane = frame.getRootPane().getContentPane();
//        contentPane.add(graphComponent, BorderLayout.CENTER);

        graphComponent.setBounds(0,0,500,100);
        graphComponent.fitGraphBounds();


        ContextConfigurator configuration = new ContextConfigurator(graphComponent.getContentRect());

        PixelImageExporter exporter = new PixelImageExporter(configuration);
        exporter.setBackgroundFill(Color.WHITE);
        exporter.export(graphComponent, new FileOutputStream("teste.png"),"png");
//        exporter.exportToBitmap();
    }

    private static void populateGraph(IGraph graph) {
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
        ActivityNodeStyle style = new ActivityNodeStyle();
//        style.setActivityType(ActivityType.TASK);
        if(Math.random()*100 > 50 ){
            style.setTaskType(TaskType.USER);
        }if(Math.random()*100 > 50 ) {
            style.setTaskType(TaskType.SCRIPT);
        }else{
            style.setTaskType(TaskType.MANUAL);
        }
//        style.setTriggerEventType(EventType.TIMER);
        INode node = graph.createNode(new RectD(PointD.ORIGIN, new SizeD(96, 60)),
                style);

        graph.addLabel(node, s.replace(' ','\n'), FreeNodeLabelModel.INSTANCE.createDefaultParameter());
        return node;
    }

}
