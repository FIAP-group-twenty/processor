package hackaton.processor.core.entities

import com.fasterxml.jackson.databind.ObjectMapper

object MessageMapper {
    fun toMessageIn(json: String): MessageIn {
        val objectMapper = ObjectMapper()
        val map = objectMapper.readValue(json, Map::class.java)

        return MessageIn(
            email = map["email"] as String,
            status = Status.valueOf(map["status"] as String),
            title = map["title"] as String,
            url = map["urlVideo"] as String
        )
    }

    fun toMessageOutString(messageIn: MessageIn, status: Status, urlZip: String?): String{
        val objectMapper = ObjectMapper()

        val messageOut = MessageOut(
            email = messageIn.email,
            status = status,
            title = messageIn.title,
            urlVideo = messageIn.url,
            urlZip = urlZip
        )

        return objectMapper.writeValueAsString(messageOut)
    }
}