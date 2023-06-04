package com.raxim.myscoutee.profile.data.document.mongo

import com.fasterxml.jackson.annotation.JsonProperty

data class FormOption(
    @field:JsonProperty(value = "value")
    var value: String? = null,
    @field:JsonProperty(value = "viewValue")
    var viewValue: String? = null
)