package com.raxim.myscoutee.profile.controller;

import static java.lang.Math.asin;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.toRadians;

import java.time.YearMonth;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.raxim.myscoutee.common.config.firebase.dto.FirebasePrincipal;
import com.raxim.myscoutee.common.util.CommonUtil;
import com.raxim.myscoutee.profile.data.document.mongo.Like;
import com.raxim.myscoutee.profile.data.document.mongo.Profile;
import com.raxim.myscoutee.profile.data.document.mongo.School;
import com.raxim.myscoutee.profile.data.dto.rest.LikeDTO;
import com.raxim.myscoutee.profile.data.dto.rest.ProfileDTO;
import com.raxim.myscoutee.profile.repository.mongo.LikeRepository;
import com.raxim.myscoutee.profile.repository.mongo.ProfileRepository;
import com.raxim.myscoutee.profile.repository.mongo.UserRepository;

@RepositoryRestController
@RequestMapping("profiles")
public class ProfileRestController {

    private final ProfileRepository profileRepository;
    private final LikeRepository likeRepository;
    private final UserRepository userRepository;

    private static final String WOMAN = "w";
    private static final String MAN = "m";

    public ProfileRestController(ProfileRepository profileRepository,
            LikeRepository likeRepository,
            UserRepository userRepository) {
        this.profileRepository = profileRepository;
        this.likeRepository = likeRepository;
        this.userRepository = userRepository;
    }

    private static class DistanceUtil {
        private static final double EARTH_RADIUS = 6370986.0; // meters

        public static double haversine(GeoJsonPoint a, GeoJsonPoint b) {
            double dLat = toRadians(b.getX() - a.getX());
            double dLon = toRadians(b.getY() - a.getY());
            double lat1 = toRadians(a.getX());
            double lat2 = toRadians(b.getX());
            double dist = sin(dLat / 2) * sin(dLat / 2) + sin(dLon / 2) * sin(dLon / 2) * cos(lat1) * cos(lat2);
            double c = 2 * asin(sqrt(dist));
            return EARTH_RADIUS * c;
        }

        public static double vincenty(GeoJsonPoint a, GeoJsonPoint b) {
            double dLat = toRadians(b.getX() - a.getX());
            double dLon = toRadians(b.getY() - a.getY());
            double lat1 = toRadians(a.getX());
            double lat2 = toRadians(b.getX());
            double dist = sin(dLat / 2) * sin(dLat / 2) + cos(lat1) * cos(lat2) * sin(dLon / 2) * sin(dLon / 2);
            double c = 2 * atan2(sqrt(dist), sqrt(1 - dist));
            return EARTH_RADIUS * c;
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProfileDTO> getProfile(@PathVariable String id) {
        Optional<Profile> profile = profileRepository.findById(UUID.fromString(id));
        if (profile.isPresent()) {
            ProfileDTO profileDto = new ProfileDTO(profile.get());
            return ResponseEntity.ok(profileDto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/like")
    @Transactional
    public ResponseEntity<List<LikeDTO>> like(@RequestBody List<LikeDTO> pfLikes, Authentication auth) {
        Set<UUID> froms = new HashSet<>();
        Set<UUID> tos = new HashSet<>();
        Set<UUID> all = new HashSet<>();

        FirebasePrincipal firebasePrincipal = (FirebasePrincipal) auth.getPrincipal();
        Profile profile = firebasePrincipal.getUser().getProfile();

        List<LikeDTO> pLikes = new ArrayList<>();
        for (LikeDTO like : pfLikes) {
            UUID from = like.getFrom() != null ? UUID.fromString(like.getFrom()) : profile.getId();
            UUID to = UUID.fromString(like.getTo());

            froms.add(from);
            tos.add(to);

            LikeDTO likeDTO = new LikeDTO();
            likeDTO.setFrom(from.toString());
            likeDTO.setTo(to.toString());
            likeDTO.setRate(like.getRate());
            likeDTO.setRef(like.getRef().toString());

            pLikes.add(likeDTO);
        }

        all.addAll(froms);
        all.addAll(tos);
        all.add(profile.getId());

        List<Profile> profiles = profileRepository.findAllById(all);

        Profile createdBy = profiles.stream().filter(p -> p.getId().equals(profile.getId())).findFirst()
                .orElseThrow(SecurityException::new);

        List<Like> dbLikes = likeRepository.findByIds(profile.getId(),
                froms, tos);

        List<Like> likesToSave = new ArrayList<>();

        for (LikeDTO like : pLikes) {
            Like mLike = dbLikes.stream()
                    .filter(dbLike -> Objects.equals(CommonUtil.parseUUID(like.getFrom()), dbLike.getFrom().getId()) &&
                            Objects.equals(CommonUtil.parseUUID(like.getTo()), dbLike.getTo().getId()) &&
                            Objects.equals(dbLike.getRef(), CommonUtil.parseUUID(like.getRef())))
                    .findFirst()
                    .orElse(null);

            if (mLike != null) {
                mLike.setRate(like.getRate());
                mLike.setDistance(calc(mLike.getFrom(), mLike.getTo()));
            } else {
                UUID from = like.getFrom() != null ? UUID.fromString(like.getFrom()) : profile.getId();
                UUID to = UUID.fromString(like.getTo());

                Profile profileFrom = profiles.stream().filter(p -> p.getId().equals(from)).findFirst().orElse(null);
                Profile profileTo = profiles.stream().filter(p -> p.getId().equals(to)).findFirst().orElse(null);

                if (profileFrom.getGender().equals(WOMAN)) {
                    Profile tProfileFrom = profileFrom;
                    profileFrom = profileTo;
                    profileTo = tProfileFrom;
                }

                boolean isDouble = !from.equals(profile.getId());

                mLike = new Like();
                mLike.setId(UUID.randomUUID());
                mLike.setDouble(isDouble);
                mLike.setFrom(profileFrom);
                mLike.setTo(profileTo);
                mLike.setRate(like.getRate());
                mLike.setCreatedBy(createdBy);
                mLike.setCreatedDate(new Date());
                mLike.setDistance(calc(profileFrom, profile));
                mLike.setRef(CommonUtil.parseUUID(like.getRef()));
            }

            likesToSave.add(mLike);
        }

        List<com.raxim.myscoutee.profile.data.document.mongo.Like> likesSaved = likeRepository.saveAll(likesToSave);

        List<LikeDTO> likesAll = likesSaved.stream()
                .map(like -> {
                    LikeDTO likeDTO = new LikeDTO();
                    if (like.getFrom() != null) {
                        likeDTO.setFrom(like.getFrom().getId().toString());
                    }

                    if (like.getTo() != null) {
                        likeDTO.setTo(like.getTo().getId().toString());
                    }

                    likeDTO.setRate(like.getRate());
                    return likeDTO;
                })
                .toList();

        return ResponseEntity.ok(likesAll);
    }

    private long calc(Profile profile1, Profile profile2) {
        double locDist = DistanceUtil.haversine(profile1.getPosition(), profile2.getPosition());

        double ageDist = 0;
        if (profile1.getBirthday() != null && profile2.getBirthday() != null) {
            YearMonth profile1YM = YearMonth.from(profile1.getBirthday().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate());

            YearMonth profile2YM = YearMonth.from(profile2.getBirthday().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate());

            ageDist = Math.pow(Math.abs(ChronoUnit.MONTHS.between(profile1YM, profile2YM)), 3);
        }

        int childDist = 0;
        if (profile1.getHasChild() != null && profile2.getHasChild() != null) {
            childDist = 5 * (profile1.getHasChild().equals(profile2.getHasChild()) ? 0 : 1);
        }

        double heightDist = 0;
        if (profile1.getHeight() != null && profile2.getHeight() != null) {
            heightDist = Math.pow(Math.abs(profile1.getHeight() - profile2.getHeight()), 2);
        }

        int maritalDist = 0;
        if (profile1.getMarital() != null && profile2.getMarital() != null) {
            maritalDist = 10 * (profile1.getMarital().equals(profile2.getMarital()) ? 0 : 1);
        }

        int physiqueDist = 0;
        if (profile1.getPhysique() != null && profile2.getPhysique() != null) {
            physiqueDist = 2 * (profile1.getPhysique().equals(profile2.getPhysique()) ? 0 : 1);
        }

        int professionDist = 0;
        if (profile1.getProfession() != null && profile2.getProfession() != null) {
            professionDist = 6 * (profile1.getProfession().equals(profile2.getProfession()) ? 0 : 1);
        }

        int religionDist = 0;
        if (profile1.getReligion() != null && profile2.getReligion() != null) {
            religionDist = 3 * (profile1.getReligion().equals(profile2.getReligion()) ? 0 : 1);
        }

        int smokerDist = 0;
        if (profile1.getSmoker() != null && profile2.getSmoker() != null) {
            smokerDist = 15 * (profile1.getSmoker().equals(profile2.getSmoker()) ? 0 : 1);
        }

        int voiceDist = 0;
        if (profile1.getVoice() != null && profile2.getVoice() != null) {
            voiceDist = 5 * (profile1.getVoice().equals(profile2.getVoice()) ? 0 : 1);
        }

        int langDist = 0;
        if (profile1.getLanguages() != null && profile2.getLanguages() != null) {
            langDist = profile1.getLanguages().stream()
                    .filter(profile2.getLanguages()::contains)
                    .mapToInt(e -> 30)
                    .sum();
        }

        int schoolDist = 0;
        if (profile1.getSchools() != null && profile2.getSchools() != null) {
            Set<String> schoolNames1 = profile1.getSchools().stream().map(School::getName).collect(Collectors.toSet());
            Set<String> schoolNames2 = profile2.getSchools().stream().map(School::getName).collect(Collectors.toSet());

            schoolDist = schoolNames1.stream()
                    .filter(schoolNames2::contains)
                    .mapToInt(e -> 10)
                    .sum();
        }

        long dist = Math.round(
                1 * locDist +
                        5 * ageDist +
                        5 * childDist +
                        2 * heightDist +
                        10 * maritalDist +
                        1 * physiqueDist +
                        3 * professionDist +
                        3 * religionDist +
                        5 * smokerDist +
                        3 * voiceDist +
                        1 * langDist +
                        1 * schoolDist);

        return Math.max(0, Math.min(100, dist));
    }
}
