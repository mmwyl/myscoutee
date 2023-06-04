package com.raxim.myscoutee.profile.data.document.mongo

import com.fasterxml.jackson.annotation.JsonProperty

data class FormItem (
    @field:JsonProperty(value = "name")
    var name: String? = null,

    // date steps = ds
    @field:JsonProperty(value = "type")
    var type: String? = null,

    // it can be array, object etc. -> dynamic form
    @field:JsonProperty(value = "data")
    val data: Any? = null,

    @field:JsonProperty(value = "options")
    val options: List<FormOption>? = emptyList()

)