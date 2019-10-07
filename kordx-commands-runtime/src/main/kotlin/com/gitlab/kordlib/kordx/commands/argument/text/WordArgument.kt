package com.gitlab.kordlib.kordx.commands.argument.text

import com.gitlab.kordlib.kordx.commands.argument.ParsingContext
import com.gitlab.kordlib.kordx.commands.argument.Result
import com.gitlab.kordlib.kordx.commands.argument.SingleWordArgument

open class WordArgument(override val name: String = "Word") : SingleWordArgument<String>() {

    final override val example: String
        get() = listOf("epeolatry", "functionalism", "koan").random()

    final override suspend fun parse(word: String, context: ParsingContext): Result<String> = success(word)

    companion object : WordArgument()
}