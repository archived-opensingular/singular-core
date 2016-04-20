package br.net.mirante.singular.server.commons.flow;

import br.net.mirante.singular.flow.core.FlowMap;
import br.net.mirante.singular.flow.core.MTask;
import br.net.mirante.singular.flow.core.MTaskEnd;
import br.net.mirante.singular.flow.core.MTransition;
import br.net.mirante.singular.flow.core.ProcessDefinition;
import br.net.mirante.singular.flow.core.renderer.IFlowRenderer;
import com.google.common.base.Throwables;
import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.model.mxICell;
import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public enum JGraphFlowRenderer implements IFlowRenderer {

    INSTANCE;

    private static byte[] renderGraphImpl(ProcessDefinition<?> definicao) {
        final mxGraph graph = renderGraph(definicao);
        final RenderedImage img = mxCellRenderer.createBufferedImage(graph, null, 1, Color.WHITE, false, null);

        final ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            ImageIO.write(img, "png", out);
        } catch (final IOException e) {
            throw Throwables.propagate(e);
        }

        return out.toByteArray();
    }

    private static void style(mxGraph graph) {
        final mxStylesheet foo = new mxStylesheet();

        final Map<String, Object> stil = new HashMap<>();
        stil.put(mxConstants.STYLE_ROUNDED, true);
        stil.put(mxConstants.STYLE_EDGE, mxConstants.EDGESTYLE_ENTITY_RELATION);
        stil.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_CONNECTOR);
        stil.put(mxConstants.STYLE_ENDARROW, mxConstants.ARROW_CLASSIC);
        stil.put(mxConstants.STYLE_VERTICAL_ALIGN, mxConstants.ALIGN_MIDDLE);
        stil.put(mxConstants.STYLE_ALIGN, mxConstants.ALIGN_CENTER);
        stil.put(mxConstants.STYLE_STROKECOLOR, "#6482B9");
        stil.put(mxConstants.STYLE_FONTCOLOR, "#446299");
        foo.setDefaultEdgeStyle(stil);

        addStyleIcone(foo, "TIMER", "timer.png");
        addStyleIcone(foo, "END", "terminate.png");
        addStyleIcone(foo, "MESSAGE", "message_intermediate.png");
        addStyleIcone(foo, "START", "event.png");
        addStyleIcone(foo, "JAVA", "gear.png");

        graph.setStylesheet(foo);
    }

    private static void addStyleIcone(mxStylesheet foo, String nomeEstilo, String nomeImagem) {
        final Map<String, Object> def = new HashMap<>();
        def.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_IMAGE);
        def.put(mxConstants.STYLE_IMAGE, "/com/miranteinfo/alocpro/processo/mbpm/renderer/" + nomeImagem);
        // def.put(mxConstants.STYLE_SPACING_TOP, mxConstants.SHAPE_IMAGE);
        foo.putCellStyle(nomeEstilo, def);
    }

    private static mxGraph renderGraph(ProcessDefinition<?> definicao) {

        final mxGraph graph = new mxGraph();
        final Object parent = graph.getDefaultParent();

        style(graph);

        graph.getModel().beginUpdate();
        graph.setAutoSizeCells(true);

        final FlowMap fluxo = definicao.getFlowMap();

        final Map<String, Object> mapaVertice = new HashMap<>();
        for (final MTask<?> task : fluxo.getTasks()) {
            final Object v = insertVertex(graph, task);
            mapaVertice.put(task.getAbbreviation(), v);
        }
        for (final MTaskEnd task : fluxo.getEndTasks()) {
            final Object v = insertVertex(graph, task);
            mapaVertice.put(task.getAbbreviation(), v);
        }

        addStartTransition(graph, fluxo.getStartTask(), mapaVertice);

        for (final MTask<?> task : fluxo.getTasks()) {
            for (final MTransition transicao : task.getTransitions()) {
                createTransition(graph, transicao, mapaVertice);
            }
        }

        final mxHierarchicalLayout layout = new mxHierarchicalLayout(graph);
        layout.setOrientation(SwingConstants.WEST);
        layout.execute(parent);
        graph.getModel().endUpdate();

        return graph;
    }

    private static void addStartTransition(mxGraph graph, MTask<?> taskInicial, Map<String, Object> mapaVertice) {
        final Object v = graph.insertVertex(graph.getDefaultParent(), null, null, 20, 20, 20, 20);
        setStyle(v, "START");
        final Object destino = mapaVertice.get(taskInicial.getAbbreviation());
        graph.insertEdge(graph.getDefaultParent(), null, null, v, destino);
    }

    private static void createTransition(mxGraph graph, MTransition transicao, Map<String, Object> mapaVertice) {
        final Object origem = mapaVertice.get(transicao.getOrigin().getAbbreviation());
        final Object destino = mapaVertice.get(transicao.getDestination().getAbbreviation());
        String nome = transicao.getName();
        if (transicao.getDestination().getName().equals(nome)) {
            nome = null;
        } else {
            nome = formatarNome(nome);
        }

        graph.insertEdge(graph.getDefaultParent(), null, nome, origem, destino);
    }

    private static Object insertVertex(mxGraph graph, MTask<?> task) {
        final Object v = graph.insertVertex(graph.getDefaultParent(), task.getAbbreviation(), formatarNome(task.getName()),
                20, 20, 20, 20);
        graph.updateCellSize(v);
        if (task.isWait()) {
            setStyle(v, "TIMER");
        } else if (task.isEnd()) {
            setStyle(v, "END");
        } else if (task.isJava()) {
            if (task.getName().startsWith("Notificar")) {
                setStyle(v, "MESSAGE");
            } else {
                setStyle(v, "JAVA");
            }
        }
        return v;
    }

    private static void setStyle(Object v, String style) {
        ((mxICell) v).setStyle(style);
    }

    private static String formatarNome(String nome) {
        return nome.replace(' ', '\n');
    }

    @Override
    public byte[] generateImage(ProcessDefinition<?> definicao) {
        return renderGraphImpl(definicao);
    }
}
