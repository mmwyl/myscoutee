package com.raxim.myscoutee.profile.service.iface;

import java.util.List;

import com.raxim.myscoutee.profile.data.document.mongo.Event;
import com.raxim.myscoutee.profile.data.dto.FilteredEdges;

public interface IEventGeneratorService {
    List<Event> generate(FilteredEdges filteredEdges, String flags);
}
