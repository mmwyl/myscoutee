package com.raxim.myscoutee.profile.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    public ResponseEntity<Void> acl(Authentication auth, @RequestParam String topic,
            @RequestParam String action) {
        FirebasePrincipal firebasePrincipal = (FirebasePrincipal) auth.getPrincipal();
        Profile profile = firebasePrincipal.getUser().getProfile();

        System.out.println("topic for ACL: " + topic);
        System.out.println("action for ACL: " + action);

        return ResponseEntity.ok().build();
    }

    @GetMapping("disconnect")
    public ResponseEntity<Void> disconnect(Authentication auth) {
        FirebasePrincipal firebasePrincipal = (FirebasePrincipal) auth.getPrincipal();
        Profile profile = firebasePrincipal.getUser().getProfile();
        return ResponseEntity.ok().build();
    }
}
