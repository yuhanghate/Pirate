package com.yuhang.novel.pirate.constant

import com.google.gson.reflect.TypeToken
import java.util.regex.Pattern
import javax.script.ScriptEngineManager

object BookResouceConstant {

    /**
     * user-agent头
     */
    const val DEFAULT_USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.142 Safari/537.36"

    var MAP_STRING = object : TypeToken<Map<String, String>>() {
    }.type

    val JS_PATTERN = Pattern.compile("(<js>[\\w\\W]*?</js>|@js:[\\w\\W]*$)", Pattern.CASE_INSENSITIVE)
    val EXP_PATTERN = Pattern.compile("\\{\\{([\\w\\W]*?)\\}\\}")

    val SCRIPT_ENGINE = ScriptEngineManager().getEngineByName("rhino")
}