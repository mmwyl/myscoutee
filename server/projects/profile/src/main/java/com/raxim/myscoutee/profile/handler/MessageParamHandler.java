package com.raxim.myscoutee.profile.handler;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Component;

import com.raxim.myscoutee.common.util.CommonUtil;
import com.raxim.myscoutee.profile.data.document.mongo.Profile;
import com.raxim.myscoutee.profile.data.dto.rest.PageParam;

@Component
public class MessageParamHandler implements IParamHandler {
    public static final String TYPE = "msg";

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public PageParam handle(Profile profile, PageParam pageParam) {
        LocalDateTime updatedDateFrom = LocalDateTime.now();

        if (pageParam.getOffset() != null && pageParam.getOffset().length == 1) {
            updatedDateFrom = LocalDateTime.parse(CommonUtil.decode((String) pageParam.getOffset()[0]),
                    DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        }

        String updatedDateFromF = updatedDateFrom.atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        Object[] tOffset = new Object[] { updatedDateFromF };

        pageParam.setId(profile.getId());
        pageParam.setOffset(tOffset);
        return pageParam;
    }
}
