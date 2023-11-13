package com.raxim.myscoutee.profile.service;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.raxim.myscoutee.profile.data.document.mongo.I18nMessage;
import com.raxim.myscoutee.profile.repository.mongo.I18nMessageRepository;

@Service
public class I18nMessageService {

    private final I18nMessageRepository i18nMessageRepository;

    public I18nMessageService(I18nMessageRepository i18nMessageRepository) {
        this.i18nMessageRepository = i18nMessageRepository;
    }

    @Cacheable(value = "i18n_messages", keyGenerator = "messageKeyGenerator")
    public I18nMessage getMessageByLang(String locale) {
        String lang = locale.split(",")[0].split("-")[0];
        I18nMessage message = i18nMessageRepository.findByLang(lang);

        // fallback
        if (message == null) {
            message = i18nMessageRepository.findByLang("en");
        }

        return message;
    }
}
