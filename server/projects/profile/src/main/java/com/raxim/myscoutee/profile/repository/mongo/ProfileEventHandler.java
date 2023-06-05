package com.raxim.myscoutee.profile.repository.mongo;

import com.raxim.myscoutee.common.FileUtil;
import com.raxim.myscoutee.profile.data.document.mongo.Profile;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.rest.core.annotation.*;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
@RepositoryEventHandler(Profile.class)
public class ProfileEventHandler {
    private final Log logger = LogFactory.getLog(getClass());

    @HandleBeforeSave
    @HandleBeforeCreate
    public void handleBeforeCreate(Profile profile) {
        //val qProfile = QProfile("profile")
        logger.info("Handle Profile Before Create....");
    }

    @HandleAfterSave
    @HandleAfterCreate
    public void handleAfterCreate(Profile profile) {
        profile.getImages().forEach(img -> {
            String fileName = img.getName();
            try {
                FileUtil.delete(fileName, "");
                FileUtil.delete(fileName, "_orig");
            } catch (Exception e) {
                logger.error("Error deleting file: " + fileName, e);
            }
        });

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
