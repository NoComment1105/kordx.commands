package com.gitlab.kordlib.kordx.commands.kord

import com.gitlab.kordlib.core.builder.kord.KordClientBuilder
import com.gitlab.kordlib.kordx.commands.kord.context.KordEventSource
import com.gitlab.kordlib.kordx.commands.pipe.PipeConfig
import com.gitlab.kordlib.kordx.commands.pipe.Prefix

class BotBuilder(token: String) {
    val kordBuilder: KordClientBuilder = KordClientBuilder(token)
    val pipeConfig: PipeConfig = PipeConfig()

    inline fun kord(builder: KordClientBuilder.() -> Unit) {
        kordBuilder.apply(builder)
    }

    inline fun pipe(builder: PipeConfig.() -> Unit) {
        pipeConfig.apply(builder)
    }

    suspend fun build() {
        val kord = kordBuilder.build()

        pipeConfig.apply {
            eventSources += KordEventSource(kord)
            if (prefixes.isEmpty()) {
                prefixes += Prefix.literal("+")
            }
        }.build()

        kord.login()
    }

}

suspend inline fun bot(token: String, builder: BotBuilder.() -> Unit) {
    BotBuilder(token).apply(builder).build()
}