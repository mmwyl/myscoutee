package com.raxim.myscoutee.profile.data.document.mongo;

public class Match {
    private MemberScore member1;
    private MemberScore member2;

    private Integer score1; // e.g.: member1 altal lott gol
    private Integer score2;

    public MemberScore getMember1() {
        return member1;
    }

    public void setMember1(MemberScore member1) {
        this.member1 = member1;
    }

    public MemberScore getMember2() {
        return member2;
    }

    public void setMember2(MemberScore member2) {
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((member1 == null) ? 0 : member1.hashCode());
        result = prime * result + ((member2 == null) ? 0 : member2.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Match other = (Match) obj;
        if (member1 == null) {
            if (other.member1 != null)
                return false;
        } else if (!member1.equals(other.member1))
            return false;
        if (member2 == null) {
            if (other.member2 != null)
                return false;
        } else if (!member2.equals(other.member2))
            return false;
        return true;
    }
}
