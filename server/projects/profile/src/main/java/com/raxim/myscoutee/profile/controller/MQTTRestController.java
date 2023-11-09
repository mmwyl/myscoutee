package com.raxim.myscoutee.profile.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.raxim.myscoutee.common.config.firebase.dto.FirebasePrincipal;
import com.raxim.myscoutee.profile.data.document.mongo.Profile;

@RestController
@RequestMapping("mqtt")
public class MQTTRestController {

    @GetMapping("auth")
    public ResponseEntity<Void> auth(Authentication auth) {
        FirebasePrincipal firebasePrincipal = (FirebasePrincipal) auth.getPrincipal();
        Profile profile = firebasePrincipal.getUser().getProfile();
        return ResponseEntity.ok().build();
    }

    @PostMapping("acl")
    public ResponseEntity<Void> acl(Authentication auth, @RequestBody String topics) {
        FirebasePrincipal firebasePrincipal = (FirebasePrincipal) auth.getPrincipal();
        Profile profile = firebasePrincipal.getUser().getProfile();
        try {
            String lTopics = URLDecoder.decode(topics, StandardCharsets.UTF_8.toString());
            System.out.println("topics for ACL: " + lTopics);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("disconnect")
    public ResponseEntity<Void> disconnect(Authentication auth) {
        FirebasePrincipal firebasePrincipal = (FirebasePrincipal) auth.getPrincipal();
        Profile profile = firebasePrincipal.getUser().getProfile();
        return ResponseEntity.ok().build();
    }
}
