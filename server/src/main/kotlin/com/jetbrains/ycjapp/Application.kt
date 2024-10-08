package com.jetbrains.ycjapp

import Greeting
import SERVER_PORT
import com.jetbrains.ycjapp.service.MenuService
import data.Menu
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.request.receive
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
        get("/home-menu") {
            call.respond(MenuService.getMenus())
        }
        get("/menu/bottom") {
            call.respond(MenuService.getBottomMenus())
        }
        post("/insert/menu"){
            val menu = call.receive<Menu>()
            val result = MenuService.insertMenu(menu)
            if (result) {
                call.respondText("Menu successfully inserted.", status = HttpStatusCode.Created)
            } else {
                call.respondText("Failed to insert menu.", status = HttpStatusCode.InternalServerError)
            }
        }
    }
}