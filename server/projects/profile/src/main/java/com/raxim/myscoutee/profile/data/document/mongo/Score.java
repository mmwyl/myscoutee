package com.raxim.myscoutee.profile.data.document.mongo;

import java.util.UUID;

public class Score {
    private String type;
    private UUID matchId;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public UUID getMatchId() {
        return matchId;
    }

    public void setMatchId(UUID matchId) {
        this.matchId = matchId;
    }
}
