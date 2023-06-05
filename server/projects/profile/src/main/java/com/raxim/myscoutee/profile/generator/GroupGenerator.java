package com.raxim.myscoutee.profile.generator;

import com.raxim.myscoutee.algo.BGroupSet;
import com.raxim.myscoutee.algo.NodeRepository;
import com.raxim.myscoutee.algo.dto.Edge;
import com.raxim.myscoutee.algo.dto.Group;
import com.raxim.myscoutee.algo.dto.Node;
import com.raxim.myscoutee.algo.dto.Range;
import com.raxim.myscoutee.profile.data.document.mongo.Profile;
import com.raxim.myscoutee.profile.data.dto.internal.Bound;
import com.raxim.myscoutee.profile.repository.mongo.LikeRepository;
import com.raxim.myscoutee.profile.repository.mongo.ScheduleRepository;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class GroupGenerator implements IGenerator<Bound, Profile> {
    private static final String RANDOM_GROUP = "RANDOM_GROUP";

    private final ScheduleRepository scheduleRepository;
    private final LikeRepository likeRepository;
    private final Map<String, Profile> nodes;

    public GroupGenerator(ScheduleRepository scheduleRepository, LikeRepository likeRepository) {
        this.scheduleRepository = scheduleRepository;
        this.likeRepository = likeRepository;
        this.nodes = new HashMap<>();
    }

    @Override
    public List<Set<Profile>> generate(Bound flags) {
        Optional<Schedule> schedule = scheduleRepository.findByKey(RANDOM_GROUP);
        Date lastRunningTime = schedule.map(Schedule::getLastRunDate).orElse(new Date());

        List<LikeBoth> likesBoth = likeRepository.findBothAll(lastRunningTime.toString(), 1.5);

        List<Edge> edges = new ArrayList<>();
        for (LikeBoth likeBoth : likesBoth) {
            Node fromNode = new Node(likeBoth.getFrom().getId().toString(), likeBoth.getFrom().getGender());
            Node toNode = new Node(likeBoth.getTo().getId().toString(), likeBoth.getTo().getGender());
            long weight = (long) ((1 / (2 / likeBoth.getRate())) * likeBoth.getDistance());
            Edge edge = new Edge(fromNode, toNode, weight);
            edges.add(edge);
        }

        NodeRepository nodeRepository = new NodeRepository();
        nodeRepository.addAll(edges);

        Range range = new Range(flags.getMinGroupSize(), flags.getMaxGroupSize());
        BGroupSet groupSet = new BGroupSet(nodeRepository, range, Arrays.asList("m", "w"));

        Map<String, Profile> nodeMap = new HashMap<>();
        for (LikeBoth likeBoth : likesBoth) {
            nodeMap.put(likeBoth.getFrom().getId().toString(), likeBoth.getFrom());
            nodeMap.put(likeBoth.getTo().getId().toString(), likeBoth.getTo());
        }
        nodes.putAll(nodeMap);

        List<Group> groups = groupSet.map(Group::getNodes);
        List<Set<Profile>> result = new ArrayList<>();
        for (Group group : groups) {
            Set<Profile> profileSet = new HashSet<>();
            for (Node node : group.getNodes()) {
                Profile profile = nodes.get(node.getId());
                profileSet.add(profile);
            }
            result.add(profileSet);
        }
        return result;
    }
}
