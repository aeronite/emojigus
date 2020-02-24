package com.argus3000.emojigus

import kotlinx.serialization.Encoder
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import java.time.Instant
import java.time.format.DateTimeFormatter

data class VehicleEvent(
    val vehicleId: Int,
    val smilesPerSecond: Float,
    val time: Instant = Instant.now()
)

data class VehicleUpgrade(val vehicleId: Int, val softwareVersion: String)

@Serializable
data class VehicleEventEnriched(
    val vehicleId: Int,
    val softwareVersion: String,
    val smilesPerSecond: Float,
    val versionAnalysis: VersionAnalysis,
    @Serializable(with = InstantSerDe::class) val time: Instant
)

enum class VersionAnalysis {
    Awesome, Cool, ok, buggy, terrible, scary, nasty, weird
}

@Serializer(forClass = Instant::class)
object InstantSerDe : KSerializer<Instant> {
    override fun serialize(encoder: Encoder, obj: Instant) =
        encoder.encodeString(DateTimeFormatter.ISO_INSTANT.format(obj))
}