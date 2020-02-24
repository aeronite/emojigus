package com.argus3000.emojigus

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.route
import io.ktor.routing.routing
import kotlinx.serialization.json.json
import org.kodein.di.generic.instance
import org.kodein.di.ktor.kodein

fun Application.routes() {
    val vehicleManager by kodein().instance<VehicleManager>()
    val alertProcessor by kodein().instance<AlertProcessor>()

    routing {
        get("/") {
            call.respondText("Emojigus!", contentType = ContentType.Text.Plain)
        }
        route("api") {
            get("alerts") {
                call.respond(alertProcessor.viewAlerts(6))
            }
            get("vehicleAlerts") {
                val idParam = call.request.queryParameters["id"]?.toInt() ?: 0
                val numAlerts = vehicleManager.viewNumAlertsForVehicle(idParam)
                call.respond(json {
                    "numAlerts" to numAlerts
                })
            }
            get("stats") {
                val latestAlertTime = alertProcessor.viewLatestAlertTime()
                call.respond(json {
                    "numVehiclesMonitored" to vehicleManager.numVehiclesMonitored()
                    "latestAlertTime" to latestAlertTime
                })
            }
        }
    }
}