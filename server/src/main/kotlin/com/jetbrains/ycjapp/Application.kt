package com.jetbrains.ycjapp

import Greeting
import SERVER_PORT
import com.jetbrains.ycjapp.service.MenuService
import com.yuventius.sample_project.service.UserService
import com.yuventius.sample_project.service.logger
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json

fun main() {
    embeddedServer(Netty, port = SERVER_PORT, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }
    routing {
//        get("/dsl") {
//            val nickname: String? = call.request.queryParameters["nickname"]
//            logger.info("nickname is ${nickname}")
//            call.respond(UserService.getUsersByDSL(nickname))
//        }
//
//        get("/sequence") {
//            val nickname: String? = call.request.queryParameters["nickname"]
//            call.respond(UserService.getUsersBySequence(nickname))
//        }
//
//        get("/nativeQuery") {
//            val nickname: String? = call.request.queryParameters["nickname"]
//            call.respond(UserService.getUserByNativeQuery(nickname))
//        }
        get("/home-menu") {
            call.respond(MenuService.getMenus())
        }
    }
}