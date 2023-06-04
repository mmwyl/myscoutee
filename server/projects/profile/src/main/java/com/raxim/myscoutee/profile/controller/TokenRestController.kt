package com.raxim.myscoutee.profile.controller

import com.raxim.myscoutee.common.config.firebase.FirebasePrincipal
import com.raxim.myscoutee.profile.data.document.mongo.Token
import com.raxim.myscoutee.profile.repository.mongo.TokenRepository
import org.springframework.data.rest.webmvc.RepositoryRestController
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping

@RepositoryRestController
@RequestMapping("tokens")
class TokenRestController(val tokenRepository: TokenRepository) {

    @PostMapping("")
    fun register(@RequestBody token: Token, auth: Authentication): ResponseEntity<Token> {

        var tokenObj = this.tokenRepository.findByDeviceId(token.deviceKey!!)
        if(tokenObj == null) {
            val user = (auth.principal as FirebasePrincipal).user
            val tokenToSave = token.copy(uuid = user.id)
            tokenObj = this.tokenRepository.save(tokenToSave);
        }

        return ResponseEntity(tokenObj, HttpStatus.CREATED);
    }

}