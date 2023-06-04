package com.raxim.myscoutee.profile.data.document.mongo

import com.fasterxml.jackson.annotation.JsonProperty

data class Slot(
    // slot date from, to
    @field:JsonProperty(value = "range")
    var range: RangeLocal? = RangeLocal(),

    // how many item count is (if someone clones this promotion, the number decreases)
    @field:JsonProperty(value = "numOfItems")
    var numOfItems: Int = 0,

    @field:JsonProperty(value = "capacity")
    var capacity: RangeInt? = RangeInt()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Slot

        if (range != other.range) return false

        return true
    }

    override fun hashCode(): Int {
        return range?.hashCode() ?: 0
    }
}