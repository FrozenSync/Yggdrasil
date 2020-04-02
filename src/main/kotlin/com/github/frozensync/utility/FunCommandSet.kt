package com.github.frozensync.utility

//import com.github.frozensync.command.Command
//import com.github.frozensync.command.CommandSet
//import reactor.core.publisher.Mono
//import kotlin.random.Random
//
//object FunCommandSet : CommandSet {
//
//    override val commands: Map<String, Command> = mutableMapOf<String, Command>().apply {
//        this["pick"] = { event ->
//            event.message.channel
//                .flatMap { channel ->
//                    Mono.justOrEmpty(event.message.content)
//                        .flatMap { content ->
//                            val args = content.removePrefix("!pick")
//                            val result = args.split('|').random()
//                            channel.createMessage(result)
//                        }
//                }
//                .then()
//        }
//
//        this["flipcoin"] = { event ->
//            event.message.channel
//                .flatMap { channel ->
//                    val n = Random.nextInt(2)
//                    val result = if (n == 0) "Head" else "Tails"
//                    channel.createMessage(result)
//                }
//                .then()
//        }
//    }
//}
