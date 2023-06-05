package com.raxim.myscoutee.common.config.firebase;

import com.google.api.client.util.ArrayMap;
import com.google.firebase.auth.FirebaseToken;

import java.util.ArrayList;
import java.util.Map;

public class FirebaseTokenHolder {
    private final FirebaseToken token;

    public FirebaseTokenHolder(FirebaseToken token) {
        this.token = token;
    }

    public String getEmail() {
        return token.getEmail();
    }

    public String getIssuer() {
        return token.getIssuer();
    }

    public String getName() {
        return token.getName();
    }

    public String getUid() {
        return token.getUid();
    }

    public String getGoogleId() {
        Map<?, ?> claims = token.getClaims();
        ArrayMap<?, ?> firebaseClaims = (ArrayMap<?, ?>) claims.get("firebase");
        ArrayMap<?, ?> identities = (ArrayMap<?, ?>) firebaseClaims.get("identities");
        ArrayList<String> googleIdentities = (ArrayList<String>) identities.get("google.com");
        return googleIdentities.get(0);
    }
}
