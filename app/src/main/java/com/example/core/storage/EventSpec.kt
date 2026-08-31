package com.example.core.storage

data class EventSpec(
    val entityId: String,
    val actorId: String,
    val deviceId: String = "DEVICE_LOCAL",
    val eventType: String,
    val amount: Long,
    val reason: String = "",
    val referenceEntityId: String = "",
    val currency: String = "IDR"
)
