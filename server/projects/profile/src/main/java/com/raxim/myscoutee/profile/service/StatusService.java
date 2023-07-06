package com.raxim.myscoutee.profile.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.raxim.myscoutee.profile.data.document.mongo.Event;
import com.raxim.myscoutee.profile.data.document.mongo.Member;
import com.raxim.myscoutee.profile.data.document.mongo.Promotion;
import com.raxim.myscoutee.profile.data.dto.rest.EventDTO;
import com.raxim.myscoutee.profile.exception.EventFullException;
import com.raxim.myscoutee.profile.exception.IllegalAccessException;
import com.raxim.myscoutee.profile.exception.MessageException;
import com.raxim.myscoutee.profile.exception.PromotionFullException;
import com.raxim.myscoutee.profile.repository.mongo.EventItemRepository;
import com.raxim.myscoutee.profile.repository.mongo.EventRepository;
import com.raxim.myscoutee.profile.repository.mongo.MemberRepository;
import com.raxim.myscoutee.profile.repository.mongo.ProfileRepository;
import com.raxim.myscoutee.profile.repository.mongo.PromotionRepository;
import com.raxim.myscoutee.profile.repository.mongo.TokenRepository;

@Service
public class StatusService {
    private final ProfileRepository profileRepository;
    private final EventRepository eventRepository;
    private final EventItemRepository eventItemRepository;
    private final TokenRepository tokenRepository;
    private final MemberRepository memberRepository;
    private final PromotionRepository promotionRepository;

    public StatusService(
            ProfileRepository profileRepository,
            EventRepository eventRepository,
            EventItemRepository eventItemRepository,
            TokenRepository tokenRepository,
            MemberRepository memberRepository,
            PromotionRepository promotionRepository) {
        this.profileRepository = profileRepository;
        this.eventRepository = eventRepository;
        this.eventItemRepository = eventItemRepository;
        this.tokenRepository = tokenRepository;
        this.memberRepository = memberRepository;
        this.promotionRepository = promotionRepository;
    }

    // TODO: to be fixed
    /*
     * public ResponseEntity<?> itemStatus(String itemId, String status, UUID
     * profileUid) {
     * Optional<com.raxim.myscoutee.profile.data.document.mongo.Profile> profileOp =
     * profileRepository
     * .findById(profileUid);
     * if (profileOp.isPresent()) {
     * com.raxim.myscoutee.profile.data.document.mongo.Profile profile =
     * profileOp.get();
     * return eventItemRepository.findById(UUID.fromString(itemId)).map(eventItem ->
     * {
     * {
     * String action = null;
     * 
     * if (status.equals("A") && eventItem.getNum() ==
     * eventItem.getCapacity().getMax()) {
     * return ResponseEntity.badRequest().body(new ErrorDTO(450, "err.event_full"));
     * } else {
     * if (eventItem.getOptional() != null && eventItem.getOptional()) {
     * if (status.equals("A")) {
     * boolean isMember = eventItem.getMembers().stream()
     * .anyMatch(member -> member.getId().equals(profile.getId()));
     * if (!isMember) {
     * Member newMember = new Member();
     * newMember.setId(profileUid);
     * newMember.setProfile(profile);
     * newMember.setStatus(status);
     * newMember.setRole("U");
     * eventItem.getMembers().add(newMember);
     * }
     * action = "joined";
     * eventItem.setNum(eventItem.getNum() + 1);
     * } else if (status.equals("L")) {
     * action = "left";
     * eventItem.setNum(eventItem.getNum() - 1);
     * }
     * 
     * Set<com.raxim.myscoutee.profile.data.document.mongo.Member> eMembers =
     * eventItem
     * .getMembers().stream()
     * .map(member -> {
     * if (member.getId().equals(profile.getId())) {
     * member.setStatus(status);
     * }
     * return member;
     * })
     * .collect(Collectors.toSet());
     * 
     * eventItem.setMembers(eMembers);
     * com.raxim.myscoutee.profile.data.document.mongo.EventItem savedEventItem =
     * eventItemRepository
     * .save(eventItem);
     * 
     * if (action != null) {
     * List<UUID> promoterIds = savedEventItem.getMembers().stream()
     * .filter(member -> member.getRole().equals("P"))
     * .map(member -> member.getProfile().getId())
     * .collect(Collectors.toList());
     * 
     * if (promoterIds.size() > 0) {
     * List<String> deviceKeys = tokenRepository.findByUserIds(promoterIds).stream()
     * .map(token -> token.getDeviceKey())
     * .collect(Collectors.toList());
     * 
     * if (!deviceKeys.isEmpty()) {
     * com.google.firebase.messaging.MulticastMessage message =
     * com.google.firebase.messaging.MulticastMessage
     * .builder()
     * .setNotification(
     * Notification.builder()
     * .setTitle("Member + " + action)
     * .setBody("Member '" + profile.getFirstName() + "' "
     * + action + " the " + eventItem.getName()
     * + " event!")
     * .build())
     * .addAllTokens(deviceKeys)
     * .build();
     * 
     * try {
     * com.google.firebase.messaging.BatchResponse response = FirebaseMessaging
     * .getInstance().sendMulticast(message);
     * System.out.println("Successfully sent message: " + response);
     * } catch (FirebaseMessagingException e) {
     * e.printStackTrace();
     * }
     * }
     * }
     * }
     * 
     * return ResponseEntity.noContent().build();
     * } else {
     * return ResponseEntity.badRequest().build();
     * }
     * }
     * }
     * }).orElse(ResponseEntity.notFound().build());
     * }
     * return ResponseEntity.badRequest().build();
     * }
     */

    // promotion accept event -> message to participant, or cancel event
    public Optional<EventDTO> status(String id, UUID profileUid, String status)
            throws MessageException {

        Optional<Event> eventRes = id != null ? this.eventRepository.findById(UUID.fromString(id)) : Optional.empty();

        if (eventRes.isPresent()) {
            Event event = eventRes.get();

            if ("A".equals(status)
                    && event.getNum() == event.getCapacity().getMax()) {
                throw new EventFullException();
            }

            if (event.getRef() != null) {
                Optional<Promotion> optPromotion = promotionRepository.findPromotionByEvent(event.getRef().getId());
                if (optPromotion.isPresent()) {
                    throw new IllegalAccessException();
                }

                Promotion promotion = optPromotion.get();
                if (promotion.getCnt() == 0) {
                    throw new PromotionFullException();
                }
            }

            Optional<Member> optCurrentMember = event.getMembers().stream()
                    .filter(member -> profileUid.equals(member.getProfile().getId())).findFirst();

            if (!optCurrentMember.isPresent()) {
                throw new IllegalAccessException();
            }

            Member currentMember = optCurrentMember.get();
            currentMember.setStatus(status);
            event.getMembers().add(currentMember);

            event.sync();
            Event dbEvent = eventRepository.save(event);

            // notification sent for any member with status A and any role (promoter also)

            return Optional.of(new EventDTO(dbEvent));
        }

        return Optional.empty();
    }
}
