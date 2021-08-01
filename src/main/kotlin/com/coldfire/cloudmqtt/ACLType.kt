package com.coldfire.cloudmqtt

import com.fasterxml.jackson.annotation.JsonProperty

enum class ACLType {
    @JsonProperty("topic")
    TOPIC,
    @JsonProperty("pattern")
    PATTERN
}