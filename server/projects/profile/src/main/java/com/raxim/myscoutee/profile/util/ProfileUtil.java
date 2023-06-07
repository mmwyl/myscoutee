package com.raxim.myscoutee.profile.util;

import com.raxim.myscoutee.profile.data.document.mongo.Profile;
import com.raxim.myscoutee.profile.data.document.mongo.School;

public class ProfileUtil {
    public static int score(Profile profile) {
        int result = 0;
        result = 31 * result + (profile.getBirthday() != null ? profile.getBirthday().hashCode() : 0);
        result = 31 * result + (profile.getMarital() != null ? profile.getMarital().hashCode() : 0);
        result = 31 * result + (profile.getHeight() != null ? profile.getHeight() : 0);
        result = 31 * result + (profile.getSmoker() != null ? profile.getSmoker().hashCode() : 0);
        result = 31 * result + (profile.getHasChild() != null ? profile.getHasChild().hashCode() : 0);
        if (profile.getSchools() != null) {
            for (School school : profile.getSchools()) {
                result = 31 * result + (school != null ? school.hashCode() : 0);
            }
        }
        result = 31 * result + (profile.getPhysique() != null ? profile.getPhysique().hashCode() : 0);
        result = 31 * result + (profile.getLanguages() != null ? profile.getLanguages().hashCode() : 0);
        result = 31 * result + (profile.getReligion() != null ? profile.getReligion().hashCode() : 0);
        result = 31 * result + (profile.getProfession() != null ? profile.getProfession().hashCode() : 0);
        result = 31 * result + (profile.getVoice() != null ? profile.getVoice().hashCode() : 0);
        return result;
    }
    
}
