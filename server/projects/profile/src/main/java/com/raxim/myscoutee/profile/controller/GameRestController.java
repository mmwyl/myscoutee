package com.raxim.myscoutee.profile.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.raxim.myscoutee.common.config.firebase.dto.FirebasePrincipal;
import com.raxim.myscoutee.common.util.CommonUtil;
import com.raxim.myscoutee.profile.data.document.mongo.Profile;
import com.raxim.myscoutee.profile.data.dto.rest.ErrorDTO;
import com.raxim.myscoutee.profile.data.dto.rest.PageDTO;
import com.raxim.myscoutee.profile.data.dto.rest.PageParam;
import com.raxim.myscoutee.profile.data.dto.rest.ProfileDTO;
import com.raxim.myscoutee.profile.data.dto.rest.SchoolDTO;
import com.raxim.myscoutee.profile.handler.LikeParamHandler;
import com.raxim.myscoutee.profile.handler.ParamHandlers;
import com.raxim.myscoutee.profile.repository.mongo.EventRepository;
import com.raxim.myscoutee.profile.repository.mongo.LikeRepository;
import com.raxim.myscoutee.profile.repository.mongo.ProfileRepository;
import com.raxim.myscoutee.profile.service.ProfileService;

@RepositoryRestController
@RequestMapping("games")
public class GameRestController {

    private final ProfileRepository profileRepository;
    private final EventRepository eventRepository;
    private final LikeRepository likeRepository;
    private final ProfileService profileService;
    private final ParamHandlers paramHandlers;

    public GameRestController(ProfileRepository profileRepository,
            EventRepository eventRepository,
            LikeRepository likeRepository,
            ProfileService profileService, ParamHandlers paramHandlers) {
        this.profileRepository = profileRepository;
        this.eventRepository = eventRepository;
        this.likeRepository = likeRepository;
        this.profileService = profileService;
        this.paramHandlers = paramHandlers;
    }

    @GetMapping("/rate_none")
    public ResponseEntity<Object> nonRated(PageParam pageParam,
            Authentication auth) {
        // parameter ? man : woman, filter out type = double, filter out from, to field
        // is the session user and type = single
        // (add field for history rated by)
        Profile profile = ((FirebasePrincipal) auth.getPrincipal()).getUser().getProfile();

        pageParam = paramHandlers.handle(profile, pageParam, LikeParamHandler.TYPE);

        if ("F".equals(profile.getStatus())) {
            return ResponseEntity.badRequest().body(new ErrorDTO(450, "err.friends_only"));
        }

        if ("I".equals(profile.getStatus())) {
            return ResponseEntity.badRequest().body(new ErrorDTO(450, "err.invisible"));
        }

        if (profile.getPosition() != null) {
            List<ProfileDTO> profileDTOs = this.profileService
                    .getProfiles(pageParam, profile, "/games/rate_none", 0.0, profile.getScore());

            List<Object> lOffset = CommonUtil.offset(profileDTOs, pageParam.getOffset());

            return ResponseEntity.ok(new PageDTO<>(profileDTOs, lOffset));
        } else {
            return ResponseEntity.badRequest().body(new ErrorDTO(450, "err.no_profile"));
        }
    }

    @GetMapping("/rate_give")
    public ResponseEntity<Object> rateGive(PageParam pageParam,
            Authentication auth) {
        // parameter ? man : woman, filter out type = double, filter out from, to field
        // is the session user and type = single
        // (add field for history rated by)
        Profile profile = ((FirebasePrincipal) auth.getPrincipal()).getUser().getProfile();

        pageParam = paramHandlers.handle(profile, pageParam, LikeParamHandler.TYPE);

        if ("F".equals(profile.getStatus())) {
            return ResponseEntity.badRequest().body(new ErrorDTO(450, "err.friends_only"));
        }

        if ("I".equals(profile.getStatus())) {
            return ResponseEntity.badRequest().body(new ErrorDTO(450, "err.invisible"));
        }

        if (profile.getPosition() != null) {
            List<ProfileDTO> profileDTOs = this.profileService
                    .getProfiles(pageParam, profile, "/games/rate_give", 1.0, 0);

            List<Object> lOffset = CommonUtil.offset(profileDTOs, pageParam.getOffset());

            return ResponseEntity.ok(new PageDTO<>(profileDTOs, lOffset));
        } else {
            return ResponseEntity.badRequest().body(new ErrorDTO(450, "err.no_profile"));
        }
    }

    @GetMapping("/rate_receive")
    public ResponseEntity<Object> rateGet(PageParam pageParam,
            Authentication auth) {
        // parameter ? man : woman, filter out type = double, filter out from, to field
        // is the session user and type = single
        // (add field for history rated by)

        Profile profile = ((FirebasePrincipal) auth.getPrincipal()).getUser().getProfile();

        pageParam = paramHandlers.handle(profile, pageParam, LikeParamHandler.TYPE);

        if ("F".equals(profile.getStatus())) {
            return ResponseEntity.badRequest().body(new ErrorDTO(450, "err.friends_only"));
        }

        if ("I".equals(profile.getStatus())) {
            return ResponseEntity.badRequest().body(new ErrorDTO(450, "err.invisible"));
        }

        if (profile.getPosition() != null) {
            List<ProfileDTO> profileDTOs = this.profileService
                    .getProfiles(pageParam, profile, "/games/rate_receive", 2.0, 0);

            List<Object> lOffset = CommonUtil.offset(profileDTOs, pageParam.getOffset());

            return ResponseEntity.ok(new PageDTO<>(profileDTOs, lOffset));
        } else {
            return ResponseEntity.badRequest().body(new ErrorDTO(450, "err.no_profile"));
        }
    }

    @GetMapping("/rate_both")
    public ResponseEntity<?> rateBoth(PageParam pageParam,
            Authentication auth) {
        // parameter ? man : woman, filter out type = double, filter out from, to field
        // is the session user and type = single
        // (add field for history rated by)
        Profile profile = ((FirebasePrincipal) auth.getPrincipal()).getUser().getProfile();

        pageParam = paramHandlers.handle(profile, pageParam, LikeParamHandler.TYPE);

        if ("F".equals(profile.getStatus())) {
            return ResponseEntity.badRequest().body(new ErrorDTO(450, "err.friends_only"));
        }

        if ("I".equals(profile.getStatus())) {
            return ResponseEntity.badRequest().body(new ErrorDTO(450, "err.invisible"));
        }

        if (profile.getPosition() != null) {
            List<ProfileDTO> profileDTOs = this.profileService
                    .getProfiles(pageParam, profile, "/games/rate_both", 1.5, 0);

            List<Object> lOffset = CommonUtil.offset(profileDTOs, pageParam.getOffset());

            return ResponseEntity.ok(new PageDTO<>(profileDTOs, lOffset));
        } else {
            return ResponseEntity.badRequest().body(new ErrorDTO(450, "err.no_profile"));
        }
    }

    @GetMapping("/rate_double/{id}/rated")
    public ResponseEntity<?> rateDouble(
            @PathVariable String id,
            @RequestParam(value = "step", required = false) Integer step,
            @RequestParam(value = "offset", required = false) String[] offset,
            Authentication auth) {
        // parameter ? man : woman, filter out type = double, filter out from, to field
        // is the session user and type = single
        // (add field for history rated by)

        Object[] tOffset;
        if (offset != null && offset.length == 1) {
            tOffset = new Object[] { CommonUtil.decode(offset[0]) };
        } else {
            tOffset = new Object[] { "1900-01-01" };
        }

        Profile profile = ((FirebasePrincipal) auth.getPrincipal()).getUser().getProfile();

        if (profile.getPosition() != null) {
            List<ProfileDTO> profiles = likeRepository.findDoubleById(
                    profile.getId(),
                    UUID.fromString(id),
                    20,
                    step != null ? step : 5,
                    new String[] { "A", "F" },
                    tOffset);

            // http://dolszewski.com/spring/how-to-bind-requestparam-to-object/

            List<Object> lOffset;
            if (!profiles.isEmpty()) {
                lOffset = profiles.get(profiles.size() - 1).getOffset();
            } else {
                lOffset = Arrays.asList(tOffset);
            }

            return ResponseEntity.ok(new PageDTO<>(profiles, lOffset));
        } else {
            return ResponseEntity.badRequest().body(new ErrorDTO(450, "err.no_profile"));
        }
    }

    @GetMapping("/rate_double/{id}/none")
    public ResponseEntity<?> doubleNone(
            @PathVariable String id,
            @RequestParam(value = "step", required = false) Integer step,
            @RequestParam(value = "offset", required = false) String[] offset,
            Authentication auth) {
        // parameter ? man : woman, filter out type = double, filter out from, to field
        // is the session user and type = single
        // (add field for history rated by)

        Optional<Profile> selected = profileRepository.findById(UUID.fromString(id));

        Profile sProfile;
        if (selected.isPresent()) {
            sProfile = selected.get();
        } else {
            return ResponseEntity.badRequest().body(new ErrorDTO(450, "err.no_profile"));
        }

        String sGender;
        if (sProfile.getGender().equals("w")) {
            sGender = "m";
        } else {
            sGender = "w";
        }

        Object[] tOffset;
        if (offset != null && offset.length == 4) {
            tOffset = new Object[] {
                    Double.valueOf(CommonUtil.decode(offset[0])),
                    Double.valueOf(CommonUtil.decode(offset[1])),
                    Double.valueOf(CommonUtil.decode(offset[2])),
                    CommonUtil.decode(offset[3])
            };
        } else {
            tOffset = new Object[] { 0, 0, 0, "1900-01-01" };
        }

        Profile profile = ((FirebasePrincipal) auth.getPrincipal()).getUser().getProfile();

        if (profile.getPosition() != null) {
            List<ProfileDTO> profiles = profileRepository.findProfileNoType(
                    CommonUtil.point(profile.getPosition()),
                    tOffset,
                    20,
                    step != null ? step : 5,
                    sProfile.getId(),
                    sGender,
                    profile.getGroup(),
                    profile.getId(),
                    0.0 // last parameter not used
            );

            // http://dolszewski.com/spring/how-to-bind-requestparam-to-object/

            List<Object> lOffset;
            if (!profiles.isEmpty()) {
                lOffset = profiles.get(profiles.size() - 1).getOffset();
            } else {
                lOffset = Arrays.asList(tOffset);
            }

            return ResponseEntity.ok(new PageDTO<>(profiles, lOffset));
        } else {
            return ResponseEntity.badRequest().body(new ErrorDTO(450, "err.no_profile"));
        }
    }

    @GetMapping(value = { "/rate_none/{id}/schools", "/rate_give/{id}/schools",
            "/rate_give/{id}/schools", "/rate_give/{id}/schools", "/rate_give/{id}/schools",
            "rate_receive/{id}/schools", "/rate_double/{id}/rated/{id}/schools",
            "/rate_double/{id}/none/{id}/schools",
            "/rate_both/{id}/schools", "/rate_met/{id}/schools" })
    public ResponseEntity<PageDTO<SchoolDTO>> getSchools(
            @PathVariable String id, Authentication auth,
            @RequestParam(name = "step", required = false) Integer step,
            @RequestParam(name = "offset", required = false) String[] offset) {
        Object[] tOffset;
        if (offset != null && offset.length == 3) {
            tOffset = new Object[] {
                    CommonUtil.decode(offset[0]),
                    CommonUtil.decode(offset[1]),
                    CommonUtil.decode(offset[2])
            };
        } else {
            tOffset = new Object[] { "a", "1900-01-01", "1900-01-01" };
        }

        List<SchoolDTO> schools = this.profileService
                .getSchools(UUID.fromString(id), step, tOffset);

        // http://dolszewski.com/spring/how-to-bind-requestparam-to-object/
        List<Object> lOffset;
        if (!schools.isEmpty()) {
            lOffset = schools.get(schools.size() - 1).getOffset();
        } else {
            lOffset = Arrays.asList(tOffset);
        }

        PageDTO<SchoolDTO> pageDTO = new PageDTO<>(schools, lOffset);
        return ResponseEntity.ok().body(pageDTO);
    }

}