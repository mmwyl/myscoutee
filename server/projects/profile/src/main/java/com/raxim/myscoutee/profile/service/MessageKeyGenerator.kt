package com.raxim.myscoutee.profile.service

import org.springframework.cache.interceptor.KeyGenerator
import java.lang.reflect.Method

class MessageKeyGenerator: KeyGenerator {

    // it can be cached by language only, not like en-US, but does not matter
    // params[0]!!.split(",")[0].split("-")[0]
    override fun generate(target: Any, method: Method, vararg params: Any?): Any {
        return params[0]!!
    }
}