package com.raxim.myscoutee.profile.repository.mongo;

import java.io.File;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.rest.core.annotation.HandleAfterCreate;
import org.springframework.data.rest.core.annotation.HandleAfterDelete;
import org.springframework.data.rest.core.annotation.HandleAfterSave;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeDelete;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import com.google.common.io.Files;
import com.raxim.myscoutee.common.util.FileUtil;
import com.raxim.myscoutee.profile.data.document.mongo.Profile;

@Component
@RepositoryEventHandler(Profile.class)
public class ProfileEventHandler {
    private final Log logger = LogFactory.getLog(getClass());

    private void delete(String fileName, String sub) {
        Pair<String, String> tempPath = FileUtil.tempToPath(fileName, false);
        String sep1 = tempPath.getFirst();
        String tmpDir = tempPath.getSecond();
        File tmpFile = new File(tmpDir + sep1 + "_" + fileName + sub);

        if (tmpFile.exists()) {
            Pair<String, String> fullPath = FileUtil.uuidToPath(fileName, true);
            String sep2 = fullPath.getFirst();
            String fullDir = fullPath.getSecond();
            File sourceFile = tmpFile;
            try {
                Files.copy(sourceFile, new File(fullDir + sep2 + "_" + fileName + sub));
            } catch (IOException e) {
                e.printStackTrace();
            }
            sourceFile.delete();
        }
    }

    @HandleBeforeSave
    @HandleBeforeCreate
    public void handleBeforeCreate(Profile profile) {
        // val qProfile = QProfile("profile")
        logger.info("Handle Profile Before Create....");
    }

    @HandleAfterSave
    @HandleAfterCreate
    public void handleAfterCreate(Profile profile) {
        if (profile.getImages() != null) {
            profile.getImages().forEach(img -> {
                String fileName = img.getName();
                delete(fileName, "");
                delete(fileName, "_orig");
            });
        }

        logger.info("Handle Profile After Create ....");
    }

    @HandleBeforeDelete
    public void handleBeforeDelete(Profile profile) {
        logger.info("Handle Profile Before Delete ....");
    }

    @HandleAfterDelete
    public void handleAfterDelete(Profile profile) {
        logger.info("Handle Profile After Delete ....");
    }
}
