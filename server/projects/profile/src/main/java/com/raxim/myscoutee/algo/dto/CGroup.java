package com.raxim.myscoutee.algo.dto;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class CGroup extends LinkedHashSet<Node> {
    private double weight;

    private Set<String> types = new HashSet<>();

    // for rebalance, and normalize (changing nodes between groups - edges on
    // intersection is needed)
    private List<Edge> edges = new ArrayList<>();

    public CGroup() {
        this(new HashSet<>());
    }

    public CGroup(Set<Node> nodes) {
        super(nodes);
        this.weight = 0d;
    }

    public void add(Edge edge) {
        if (edge.getFrom() != null) {
            super.add(edge.getFrom());
            if (edge.getFrom().getType() != null) {
                types.add(edge.getFrom().getType());
            }
        }
        if (edge.getTo() != null) {
            super.add(edge.getTo());

            if (edge.getTo().getType() != null) {
                types.add(edge.getTo().getType());
            }
        }
        edges.add(edge);

        this.weight = edges.stream().mapToDouble(Edge::getWeight).average().getAsDouble();
    }

    public Set<Node> balance(List<String> pTypes) {
        if (pTypes.isEmpty() || pTypes.size() < 2) {
            return this;
        }

        // TODO: behavior review
        if (pTypes.size() != types.size()) {
            super.clear();
            return Set.of();
        }

        Map<String, List<Node>> nodesByType = this.stream()
                .collect(Collectors.groupingBy(Node::getType));

        int minSize = nodesByType.values().stream()
                .mapToInt(List::size)
                .min()
                .orElse(0); // Minimum size of any type

        Set<Node> nodes = nodesByType.entrySet().stream()
                .flatMap(entry -> entry.getValue().stream().limit(minSize))
                .collect(Collectors.toSet());

        super.clear();
        super.addAll(nodes);

        return nodes;
    }

    @Override
    public String toString() {
        return "CGroup [weight=" + weight + ", types=" + types + ", edges=" + edges + "]";
    }
}
