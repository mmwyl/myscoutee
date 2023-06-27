package com.raxim.myscoutee.algo;

import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.raxim.myscoutee.algo.dto.CGraph;
import com.raxim.myscoutee.algo.dto.CNode;
import com.raxim.myscoutee.algo.dto.Edge;

public class CTree implements Iterable<Edge> {
    private final ConcurrentMap<String, CNode> nodes;
    private final PriorityQueue<CNode> nodesOrdered;

    public CTree(CGraph cGraph) {
        this.nodes = new ConcurrentHashMap<>();
        this.nodesOrdered = new PriorityQueue<>(
                Comparator.comparing(CNode::getWeight).reversed().thenComparing(CNode::getNode));
        cGraph.forEach(cNode -> {
            nodes.put(cNode.getNode().getId(), cNode);
            nodesOrdered.add(cNode);
        });
    }

    @Override
    public Iterator<Edge> iterator() {
        return new CTreeIterator(this);
    }

    public ConcurrentMap<String, CNode> getNodes() {
        return nodes;
    }

    public PriorityQueue<CNode> getNodesOrdered() {
        return nodesOrdered;
    }

    public boolean isEmpty() {
        return this.nodesOrdered.isEmpty();
    }
}
