package com.raxim.myscoutee.profile.data.document.mongo;

public class Match {
    private Member member1;
    private Member member2;

    private Integer score1; // e.g.: member1 altal lott gol
    private Integer score2;

    public Member getMember1() {
        return member1;
    }

    public void setMember1(Member member1) {
        this.member1 = member1;
    }

    public Member getMember2() {
        return member2;
    }

    public void setMember2(Member member2) {
        this.member2 = member2;
    }

    public Integer getScore1() {
        return score1;
    }

    public void setScore1(Integer score1) {
        this.score1 = score1;
    }

    public Integer getScore2() {
        return score2;
    }

    public void setScore2(Integer score2) {
        this.score2 = score2;
    }
}
