package com.github.zhloba.spring.messaging.postgresql.converter

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.zhloba.spring.messaging.postgresql.core.MessageDataContainer
import com.github.zhloba.spring.messaging.postgresql.eventbus.NotificationEvent
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.springframework.messaging.Message

class JacksonNotificationMessageConverter(private val objectMapper: ObjectMapper) : BaseNotificationMessageConverter() {

    override fun fromNotification(notificationEvent: NotificationEvent): Message<String> =
        notificationEvent.payload?.let { payload ->
            val container = Json.decodeFromString<MessageDataContainer>(payload)
            buildMessage(notificationEvent, container.payload, container.headers)
        } ?: buildMessage(notificationEvent, null, mapOf())

    override fun toNotificationPayload(message: Message<*>): String {
        return objectMapper.writeValueAsString(
            NotificationMessageContainer(
                payload = message.payload?.let { java.lang.String.valueOf(it) },
                headers = normalizeHeaders(message.headers)
            )
        )
    }

    data class NotificationMessageContainer(
        @JsonProperty("p") val payload: String?,
        @JsonProperty("h") val headers: Map<String, Any?>
    )

}

