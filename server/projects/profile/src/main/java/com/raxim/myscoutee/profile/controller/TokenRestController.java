package com.raxim.myscoutee.profile.controller;

import com.raxim.myscoutee.common.config.firebase.dto.FirebasePrincipal;
import com.raxim.myscoutee.profile.data.document.mongo.Token;
import com.raxim.myscoutee.profile.data.document.mongo.User;
import com.raxim.myscoutee.profile.repository.mongo.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("tokens")
public class TokenRestController {

    private final TokenRepository tokenRepository;

    @Autowired
    public TokenRestController(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    @PostMapping("")
    public ResponseEntity<Token> register(@RequestBody Token token, Authentication auth) {
        Token tokenObj = tokenRepository.findByDeviceId(token.getDeviceKey());

        if (tokenObj == null) {
            FirebasePrincipal principal = (FirebasePrincipal) auth.getPrincipal();
            User user = principal.getUser();
            Token tokenToSave = token.copy(user.getId());
            tokenObj = tokenRepository.save(tokenToSave);
        }

        return new ResponseEntity<>(tokenObj, HttpStatus.CREATED);
    }
}
