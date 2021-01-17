package dev.kord.x.commands.argument.extension

import dev.kord.x.commands.argument.*
import dev.kord.x.commands.argument.result.*
import dev.kord.x.commands.argument.result.extension.orDefault
import dev.kord.x.commands.argument.result.extension.orElse
import dev.kord.x.commands.argument.result.extension.orElseSupply

/**
 * Returns an Argument that maps all [ArgumentResult.Failure] to a [ArgumentResult.Success]
 * with the given [default] value.
 */
fun <T : Any, CONTEXT> Argument<T, CONTEXT>.withDefault(
    default: T
): Argument<T, CONTEXT> =
    object : Argument<T, CONTEXT> by this {
        override suspend fun parse(
            text: String,
            fromIndex: Int,
            context: CONTEXT
        ): ArgumentResult<T> {
            return this@withDefault.parse(text, fromIndex, context).orElse(fromIndex, default)
        }
    }

/**
 * Returns an Argument that maps all [ArgumentResult.Failure] to a [ArgumentResult.Success]
 * with a value generated by [default].
 */
fun <T : Any, CONTEXT> Argument<T, CONTEXT>.withDefault(
    default: suspend CONTEXT.() -> T
): Argument<T, CONTEXT> =
    object : Argument<T, CONTEXT> by this {

        override suspend fun parse(
            text: String,
            fromIndex: Int,
            context: CONTEXT
        ): ArgumentResult<T> {
            return this@withDefault.parse(text, fromIndex, context)
                .orElseSupply(fromIndex) { default(context) }
        }
    }

/**
 * Returns an Argument that maps all [ArgumentResult.Success] with a `null` value and [ArgumentResult.Failure]
 * to a [ArgumentResult.Success] with the given [default] value.
 */
@Suppress("UNCHECKED_CAST")
@JvmName("withDefaultNullable")
fun <T : Any, CONTEXT> Argument<T?, CONTEXT>.withDefault(
    default: T
): Argument<T, CONTEXT> = object :
    Argument<T, CONTEXT> by this as Argument<T, CONTEXT> {

    override suspend fun parse(text: String, fromIndex: Int, context: CONTEXT): ArgumentResult<T> {
        return this@withDefault.parse(text, fromIndex, context).orDefault(default)
    }
}

/**
 * Returns an Argument that maps all [ArgumentResult.Success] with a `null` value and [ArgumentResult.Failure]
 * to a [ArgumentResult.Success] with a value generated by [fallback].
 */
@Suppress("UNCHECKED_CAST")
@JvmName("withDefaultNullable")
fun <T : Any, CONTEXT> Argument<T?, CONTEXT>.withDefault(
    fallback: suspend CONTEXT.() -> T
): Argument<T, CONTEXT> = object :
    Argument<T, CONTEXT> by this as Argument<T, CONTEXT> {

    override suspend fun parse(text: String, fromIndex: Int, context: CONTEXT): ArgumentResult<T> {
        return this@withDefault.parse(text, fromIndex, context)
            .orElseSupply(fromIndex) { fallback(context) }
    }
}