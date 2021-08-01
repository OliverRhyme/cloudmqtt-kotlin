package com.coldfire.cloudmqtt.model

data class CloudMqttACL(
    val pattern: String,
    val read: Boolean,
    val write: Boolean
)