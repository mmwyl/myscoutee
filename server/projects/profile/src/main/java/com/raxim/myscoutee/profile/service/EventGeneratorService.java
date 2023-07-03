package com.raxim.myscoutee.profile.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.raxim.myscoutee.algo.BCTree;
import com.raxim.myscoutee.algo.CTree;
import com.raxim.myscoutee.algo.dto.DGraph;
import com.raxim.myscoutee.algo.dto.Edge;
import com.raxim.myscoutee.algo.dto.Node;
import com.raxim.myscoutee.algo.dto.Range;
import com.raxim.myscoutee.common.util.JsonUtil;
import com.raxim.myscoutee.profile.data.document.mongo.Event;
import com.raxim.myscoutee.profile.data.document.mongo.Like;
import com.raxim.myscoutee.profile.data.document.mongo.LikeGroup;
import com.raxim.myscoutee.profile.data.document.mongo.Profile;
import com.raxim.myscoutee.profile.data.document.mongo.Schedule;
import com.raxim.myscoutee.profile.repository.mongo.EventRepository;
import com.raxim.myscoutee.profile.repository.mongo.LikeRepository;
import com.raxim.myscoutee.profile.repository.mongo.ScheduleRepository;
import com.raxim.myscoutee.profile.util.AppConstants;
import com.raxim.myscoutee.profile.util.EventUtil;

@Service
public class EventGeneratorService {

    public static final String SCHEDULE_RANDOM_GROUP = "RANDOM_GROUP";

    private final ScheduleRepository scheduleRepository;
    private final LikeRepository likeRepository;
    private final EventRepository eventRepository;
    private final ObjectMapper objectMapper;

    public EventGeneratorService(ScheduleRepository scheduleRepository,
            LikeRepository likeRepository, EventRepository eventRepository,
            ObjectMapper objectMapper) {
        this.scheduleRepository = scheduleRepository;
        this.likeRepository = likeRepository;
        this.eventRepository = eventRepository;
        this.objectMapper = objectMapper;
    }

    public List<Set<Profile>> generate() {
        System.out.println("---- generate start -----");

        Optional<Schedule> schedule = scheduleRepository.findByKey(SCHEDULE_RANDOM_GROUP);
        long lastIdx = schedule.map(Schedule::getLastIdx).orElse(0L);
        long batchSize = schedule.map(Schedule::getBatchSize).orElse(1000L);
        Range flags = schedule.map(sch -> JsonUtil.jsonToObject(sch.getFlags(), Range.class, objectMapper))
                .orElse(new Range(6, 12));

        // batched version of query does exist -> just to simplify for the time being
        List<LikeGroup> likeGroups = likeRepository.findLikeGroups();

        List<Event> events = eventRepository.findAll();

        List<Set<Edge>> ignoredEdges = events.stream().map(event -> EventUtil.permutate(event))
                .toList();
        System.out.println("ignored edges -----" + ignoredEdges);

        // merge likes
        List<Like> likesBoth = likeGroups.stream().map(group -> {
            return group.reduce();
        }).filter(like -> like != null).toList();

        // nodes
        Map<String, Profile> nodes = new HashMap<>();
        likesBoth.forEach(likeBoth -> {
            nodes.put(likeBoth.getFrom().getId().toString(), likeBoth.getFrom());
            nodes.put(likeBoth.getTo().getId().toString(), likeBoth.getTo());
        });

        System.out.println("Num of nodes " + nodes.keySet().size());

        // edges
        List<Edge> edges = likesBoth.stream().map(likeBoth -> {
            Node fromNode = new Node(likeBoth.getFrom().getId().toString(), likeBoth.getFrom().getGender());
            Node toNode = new Node(likeBoth.getTo().getId().toString(), likeBoth.getTo().getGender());
            double weight = (double) (likeBoth.getRate() * likeBoth.getDistance());
            return new Edge(fromNode, toNode, weight);
        }).toList();

        System.out.println("Num of edges " + edges.size());

        DGraph dGraph = new DGraph();
        dGraph.addAll(edges);

        Range range = new Range(flags.getMin(), flags.getMax());
        List<BCTree> bcTrees = dGraph.stream().map(cGraph -> {
            CTree cTree = new CTree(cGraph, List.of(AppConstants.MAN, AppConstants.WOMAN), ignoredEdges);
            return new BCTree(cTree, range);
        }).toList();

        List<Set<Profile>> profileList = new ArrayList<>();
        bcTrees.forEach(bcTree -> bcTree.forEach(cGroup -> {
            Set<Profile> profiles = cGroup.stream()
                    .map(node -> nodes.get(node.getId()))
                    .collect(Collectors.toSet());
            profileList.add(profiles);
        }));

        System.out.println("---- generate end -----");

        return profileList;
    }
}
