package com.raxim.myscoutee.profile.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.raxim.myscoutee.profile.repository.mongo.EventItemRepository;
import com.raxim.myscoutee.profile.repository.mongo.LikeRepository;
import com.raxim.myscoutee.profile.repository.mongo.ScheduleRepository;

public class EventPriorityService {
    private final LikeRepository likeRepository;
    private final EventItemRepository eventItemRepository;
    private final ObjectMapper objectMapper;

    public EventPriorityService(ScheduleRepository scheduleRepository,
            LikeRepository likeRepository, EventItemRepository eventItemRepository,
            ObjectMapper objectMapper) {
        this.likeRepository = likeRepository;
        this.eventItemRepository = eventItemRepository;
        this.objectMapper = objectMapper;
    }

    //only profiles, with status 'A' or 'F'
    //you can add eventItem to the event, but not necessarily
    /*public List<Set<Profile>> generate() {
        //eventItem.findCandidates()
        //1) if T, save event with status T
        //2) if A, send notification for all the members
        //3) get findLikeGroups (likeGroup.reduce first)
        //   a) filter the likes among members of the union of (candidates and eventItem.members),
        //   b) filter likes, where the rate is less then rule.rate
        //   c) ignore all edges among existing eventItem.members (generate permutation) with members.accepted + members.invited (not overdue rule.memberGrace)
        //4) if P, run BCTree (check rule.balanced, CGroup.size = eventItem.maxCapacity - members.accepted + members.invited (not overdue rule.memberGrace)
        // get first CGroup
        //   add the filtered candidates to eventItem and save and send notification for the invitied members


        //findCandidates needs to be extended with mutual and from last event
        //a) from range.start of last event is just a gte from input parameter (?1)
        //b) group -> merge lookup.eventItems.commonMembers
        //c) eventItem lookup query only match, where the currentItem.createdBy has status 'A'
        //d) list all the events where $$currentItem.members is in the member list also if mutual has been set (rule.mutual)
    }*/
}
