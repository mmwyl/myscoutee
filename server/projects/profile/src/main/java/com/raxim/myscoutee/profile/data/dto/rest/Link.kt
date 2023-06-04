package com.raxim.myscoutee.profile.data.dto.rest

import com.fasterxml.jackson.annotation.JsonProperty

data class Link(

    @field:JsonProperty(value = "link")
    val link: com.raxim.myscoutee.profile.data.document.mongo.Link? = null,

    @field:JsonProperty(value = "info")
    val info: LinkInfo? = null
)