package com.raxim.myscoutee.algo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.raxim.myscoutee.algo.dto.CGraph;
import com.raxim.myscoutee.algo.dto.DGraph;
import com.raxim.myscoutee.algo.dto.Edge;
import com.raxim.myscoutee.algo.dto.Graph;
import com.raxim.myscoutee.algo.dto.Node;
import com.raxim.myscoutee.algo.generator.EdgeGenerator;
import com.raxim.myscoutee.algo.generator.IGenerator;
import com.raxim.myscoutee.algo.generator.NodeGenerator;

public class DGraphTest extends AbstractAlgoTest {

    private final static int NODE_NUM = 1000;
    private final static int EDGE_NUM = 1000;

    @Test
    public void shouldLoadDGraph() throws AlgoLoadException {
        Graph graph = load("algo/graph.json");

        DGraph dGraph1 = new DGraph();
        dGraph1.addAll(graph.getEdges());

        assertEquals(2, dGraph1.size());
    }

    @Test
    public void shouldGetMaxFlow() throws AlgoLoadException {
        Graph graph = load("algo/graph.json");

        DGraph dGraph1 = new DGraph();
        dGraph1.addAll(graph.getEdges());

        assertEquals(2, dGraph1.size());

        Iterator<CGraph> itDGraph = dGraph1.iterator();
        CGraph cGraph1 = itDGraph.next();

        CTree cTree1 = new CTree(cGraph1);
        Iterator<Edge> itEdge1 = cTree1.iterator();

        assertTrue(itEdge1.hasNext());
        Edge edge1 = itEdge1.next();
        assertEquals(50, edge1.getWeight());

        assertTrue(itEdge1.hasNext());
        Edge edge2 = itEdge1.next();
        assertEquals(30, edge2.getWeight());

        assertTrue(itEdge1.hasNext());
        Edge edge3 = itEdge1.next();
        assertEquals(20, edge3.getWeight());
    }

    @Test
    void shouldGenIterate() {
        DGraph dGraph = new DGraph();

        IGenerator<Node> nodeGenerator = new NodeGenerator();
        Set<Node> nodes = nodeGenerator.generate(NODE_NUM);

        IGenerator<Edge> edgeGenerator = new EdgeGenerator(nodes);
        Set<Edge> edges = edgeGenerator.generate(EDGE_NUM);
        dGraph.addAll(new ArrayList<>(edges));

        dGraph.forEach(cGraph -> {
            System.out.println("---- partition -----");
            CTree cTree = new CTree(cGraph);
            cTree.forEach(edge -> {
                System.out.println(edge);
            });
        });
    }
}
