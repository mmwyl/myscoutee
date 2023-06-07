package com.raxim.myscoutee.profile.controller;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import com.raxim.myscoutee.common.config.firebase.dto.FirebasePrincipal;
import com.raxim.myscoutee.common.util.CommonUtil;
import com.raxim.myscoutee.profile.data.dto.rest.Page;
import com.raxim.myscoutee.profile.data.dto.rest.School;
import com.raxim.myscoutee.profile.repository.mongo.SchoolRepository;
import com.raxim.myscoutee.profile.service.ProfileService;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RepositoryRestController
@RequestMapping("user")
public class UserSchoolRestController {
    private final ProfileService profileService;
    private final SchoolRepository schoolRepository;

    public UserSchoolRestController(ProfileService profileService, SchoolRepository schoolRepository) {
        this.profileService = profileService;
        this.schoolRepository = schoolRepository;
    }

    @GetMapping("/schools")
    public ResponseEntity<Page<School>> getSchools(
            Authentication auth,
            @RequestParam(value = "step", required = false) Integer step,
            @RequestParam(value = "offset", required = false) String[] offset) {
        FirebasePrincipal principal = (FirebasePrincipal) auth.getPrincipal();
        String profileId = principal.getUser().getProfile().getId();

        Object[] tOffset = (offset != null && offset.length == 3)
                ? new Object[] { CommonUtil.decode(offset[0]), CommonUtil.decode(offset[1]),
                        CommonUtil.decode(offset[2]) }
                : new Object[] { "a", "1900-01-01", "1900-01-01" };

        if (profileId == null) {
            return ResponseEntity.notFound().build();
        } else {
            List<School> schools = profileService.getSchools(profileId, step, tOffset);

            List<Object> lOffset = schools.isEmpty() ? Arrays.asList(tOffset)
                    : schools.get(schools.size() - 1).getOffset();

            return ResponseEntity.ok(new Page<>(schools, lOffset));
        }
    }

    @PostMapping("/schools")
    @Transactional
    public ResponseEntity<List<School>> saveSchool(
            Authentication auth,
            @RequestBody List<com.raxim.myscoutee.profile.data.document.mongo.School> schools) {
        FirebasePrincipal principal = (FirebasePrincipal) auth.getPrincipal();
        String profileId = principal.getUser().getProfile().getId();

        if (profileId == null) {
            return ResponseEntity.notFound().build();
        } else {
            List<School> schoolsDto = profileService.saveSchools(profileId, schools);
            return ResponseEntity.status(HttpStatus.CREATED).body(schoolsDto);
        }
    }

    @PatchMapping("/schools/{id}")
    @Transactional
    public ResponseEntity<List<School>> patchSchool(
            Authentication auth,
            @PathVariable String id,
            @RequestBody List<com.raxim.myscoutee.profile.data.document.mongo.School> schools) {
        FirebasePrincipal principal = (FirebasePrincipal) auth.getPrincipal();
        String profileId = principal.getUser().getProfile().getId();

        if (profileId == null) {
            return ResponseEntity.notFound().build();
        } else {
            List<School> schoolsDto = profileService.saveSchools(profileId, schools);
            return ResponseEntity.ok(schoolsDto);
        }
    }

    @DeleteMapping("/schools/{id}")
    public ResponseEntity<?> deleteSchool(
            Authentication auth,
            @PathVariable String id) {
        FirebasePrincipal principal = (FirebasePrincipal) auth.getPrincipal();
        String profileId = principal.getUser().getProfile().getId();

        if (profileId == null) {
            return ResponseEntity.notFound().build();
        } else {
            Optional<com.raxim.myscoutee.profile.data.document.mongo.School> school = profileService
                    .getSchoolByProfile(profileId, UUID.fromString(id));
            if (school.isPresent()) {
                com.raxim.myscoutee.profile.data.document.mongo.School schoolT = school.get();
                schoolT.setStatus("D");
                schoolRepository.save(schoolT);
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        }
    }

    @PostMapping("/schools/parse")
    public ResponseEntity<List<com.raxim.myscoutee.profile.data.document.mongo.School>> upload(
            Authentication auth,
            @RequestParam("file") MultipartFile file) {
        if (file != null && !file.isEmpty()) {
            try {
                PdfReader pdfReader = new PdfReader(file.getBytes());
                List<com.raxim.myscoutee.profile.data.document.mongo.School> schools = new ArrayList<>();

                for (int i = 1; i <= pdfReader.getNumberOfPages(); i++) {
                    String cnt = PdfTextExtractor.getTextFromPage(pdfReader, i);
                    String[] lines = cnt.split("\n");

                    for (String line : lines) {
                        try {
                            java.util.regex.Matcher matcher = dateRegex.toRegex().matcher(line);
                            if (matcher.find()) {
                                String str = matcher.group(1);
                                com.raxim.myscoutee.profile.data.document.mongo.School school = new com.raxim.myscoutee.profile.data.document.mongo.School(
                                        str.range());
                                schools.add(school);
                            }
                        } catch (Exception ex) {
                            System.out.println(ex);
                        }
                    }
                }

                return ResponseEntity.ok(schools);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } else {
            return ResponseEntity.badRequest().build();
        }
    }
}
