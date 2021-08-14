package dev.kord.x.commands.argument.pipe

import dev.kord.x.commands.argument.primitive.IntArgument
import dev.kord.x.commands.model.command.AliasInfo
import dev.kord.x.commands.model.command.invoke
import dev.kord.x.commands.model.module.module
import dev.kord.x.commands.model.processor.BaseEventHandler
import dev.kord.x.commands.model.processor.CommandProcessor
import dev.kord.x.commands.model.processor.ProcessorBuilder
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.cancel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.coroutineContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Assertions
import kotlin.coroutines.CoroutineContext
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@Suppress("EXPERIMENTAL_API_USAGE")
class CommandsTest {

    lateinit var processor: CommandProcessor
    lateinit var output: TestOutput
    lateinit var input: TestEventSource

    @BeforeTest
    fun setUp() = runBlockingTest {
        output = TestOutput()
        input = TestEventSource()
        processor = ProcessorBuilder {
            eventSources += input
            eventHandlers[TestContext] = BaseEventHandler(TestContext, TestConverter(output), TestErrorHandler(output))
            dispatcher = object : CoroutineDispatcher() {
                override fun dispatch(context: CoroutineContext, block: Runnable) {
                    block.run()
                }

            }
        }
    }

    @Test
    fun `command gets invoked without arguments`() = runBlocking {
        val response = "a test response"

        processor += module("test", TestContext) {
            command("test") {
                invoke {
                    respond(response)
                }
            }
        }

        input.channel.emit("test")
        Assertions.assertEquals(1, output.events.size)
        val event = output.events[0] as EventType.Response
        Assertions.assertEquals(response, event.text)
    }

    @Test
    fun `alias gets invoked with child type`() = runBlocking {
        val response = "a test response"

        var error: String? = null

        processor += module("test", TestContext) {
            command("test") {
                alias("an-alias")

                invoke {
                    if (command.aliasInfo !is AliasInfo.Child) error = "expected alias info to be a child"
                    respond(response)
                }
            }
        }

        input.channel.emit("an-alias")
        if(error != null) throw IllegalStateException(error)
    }

    @Test
    fun `parent of alias gets invoked with parent type`() = runBlocking {
        val response = "a test response"

        var error: String? = null

        processor += module("test", TestContext) {
            command("test") {
                alias("an-alias")

                invoke {
                    if (command.aliasInfo !is AliasInfo.Child) error = "expected alias info to be a child"
                    respond(response)
                }
            }
        }

        input.channel.emit("an-alias")
        if(error != null) throw IllegalStateException(error)
    }

    @Test
    fun `command gets invoked with alias`() = runBlocking {
        val response = "a test response"

        processor += module("test", TestContext) {
            command("test") {
                alias("an-alias")

                invoke {
                    respond(response)
                }
            }
        }

        input.channel.emit("an-alias")
        Assertions.assertEquals(1, output.events.size)
        val event = output.events[0] as EventType.Response
        Assertions.assertEquals(response, event.text)
    }

    @Test
    fun `command gets invoked when all arguments succeed`() = runBlocking {
        val response = "a test response"

        processor += module("test", TestContext) {
            command("test") {
                invoke(IntArgument, IntArgument) { _, _ ->
                    respond(response)
                }
            }
        }

        input.channel.emit("test 4 5")
        Assertions.assertEquals(1, output.events.size)
        val event = output.events[0] as EventType.Response
        Assertions.assertEquals(response, event.text)
    }

    @Test
    fun `pipe rejects command when too little arguments`() = runBlocking(Dispatchers.IO) {
        val response = "a test response"

        processor += module("test", TestContext) {
            command("test") {
                invoke(IntArgument, IntArgument) { _, _ ->
                    respond(response)
                }
            }
        }

        input.channel.emit("test 4")
        Assertions.assertEquals(1, output.events.size)
        Assertions.assertTrue(output.events[0] is EventType.RejectArgument)
    }

    @Test
    fun `pipe rejects command when wrong argument type`() = runBlocking(Dispatchers.IO) {
        val response = "a test response"

        processor += module("test", TestContext) {
            command("test") {
                invoke(IntArgument, IntArgument) { _, _ ->
                    respond(response)
                }
            }
        }

        input.channel.emit("test 4 cat")
        Assertions.assertEquals(1, output.events.size)
        Assertions.assertTrue(output.events[0] is EventType.RejectArgument)
    }

    @Test
    fun `pipe rejects command when too many arguments`() = runBlocking(Dispatchers.IO) {
        val response = "a test response"

        processor += module("test", TestContext) {
            command("test") {
                invoke(IntArgument, IntArgument) { _, _ ->
                    respond(response)
                }
            }
        }

        input.channel.emit("test 4 cat")
        Assertions.assertEquals(1, output.events.size)
        Assertions.assertTrue(output.events[0] is EventType.RejectArgument)
    }

}
