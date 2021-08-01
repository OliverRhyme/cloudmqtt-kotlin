package com.coldfire.cloudmqtt.model

data class CloudMqttUser(
    val username: String,
    val acls: List<CloudMqttACL>
)