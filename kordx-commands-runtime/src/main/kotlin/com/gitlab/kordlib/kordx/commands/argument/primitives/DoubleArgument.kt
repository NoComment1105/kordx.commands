package com.gitlab.kordlib.kordx.commands.argument.primitives

import com.gitlab.kordlib.kordx.commands.argument.ParsingContext
import com.gitlab.kordlib.kordx.commands.argument.Result
import com.gitlab.kordlib.kordx.commands.argument.SingleWordArgument
import kotlin.random.Random

open class DoubleArgument(override val name: String = "Number") : SingleWordArgument<Double>() {
    final override val example: String
        get() = Random.nextDouble(-100.0, 100.0).toString()

    final override suspend fun parse(word: String, context: ParsingContext): Result<Double> = when (val number = word.toDoubleOrNull()) {
        null -> failure("Expected a number.")
        else -> success(number)
    }

    companion object : DoubleArgument()
}