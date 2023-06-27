package com.raxim.myscoutee.algo;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import com.raxim.myscoutee.algo.dto.CNode;
import com.raxim.myscoutee.algo.dto.Edge;

public class CTreeIterator implements Iterator<Edge> {

    private final CTree cTree;

    private final PriorityQueue<CNode> nodesOrdered = new PriorityQueue<>(
            Comparator.comparing(CNode::getWeight).reversed().thenComparing(CNode::getNode));
    private final Set<String> visited = new HashSet<>();

    private Edge currEdge;

    public CTreeIterator(final CTree cTree) {
        this.cTree = cTree;
    }

    @Override
    public boolean hasNext() {
        if (nodesOrdered.isEmpty()) {
            CNode cNode = cTree.getNodesOrdered().poll();

            String nodeFrom = cNode.getNode().getId();
            visited.add(nodeFrom);

            nodesOrdered.add(cNode);
        }

        CNode cNode;
        do {
            cNode = nodesOrdered.peek();
            currEdge = cNode.poll();

            nodesOrdered.remove(cNode);
            if (cNode.getDegree() > 0) {
                nodesOrdered.add(cNode);
            }
        } while (currEdge != null && visited.contains(currEdge.getTo().getId())
                && !nodesOrdered.isEmpty());

        if (currEdge == null
                && (!cTree.isEmpty() || !nodesOrdered.isEmpty())) {
            hasNext();
        }

        return !cTree.isEmpty() || !nodesOrdered.isEmpty();
    }

    @Override
    public Edge next() {
        if (currEdge != null) {
            visited.add(currEdge.getTo().getId());
        }

        ConcurrentMap<String, CNode> nodes = cTree.getNodes();
        if (nodes.containsKey(currEdge.getTo().getId())) {
            CNode cNodeTo = cTree.getNodes().get(currEdge.getTo().getId());
            nodesOrdered.add(cNodeTo);
        }
        return currEdge;
    }

}
