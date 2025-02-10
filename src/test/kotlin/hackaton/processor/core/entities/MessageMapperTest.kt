package hackaton.processor.core.entities

import com.fasterxml.jackson.databind.ObjectMapper
import hackaton.processor.core.entities.MessageMapper.toMessageIn
import hackaton.processor.core.entities.MessageMapper.toMessageOutString
import hackaton.processor.core.entities.Status.FINISHED
import hackaton.processor.core.entities.Status.IN_PROCESSING
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class MessageMapperTest {

    private val objectMapper = ObjectMapper()

    @Test
    fun `should map JSON string to MessageIn`() {
        val json =
            """{"email":"test@email.com","status":"IN_PROCESSING","title":"Test Video","urlVideo":"http://example.com"}"""

        val messageIn = toMessageIn(json)

        assertEquals("test@email.com", messageIn.email)
        assertEquals(IN_PROCESSING, messageIn.status)
        assertEquals("Test Video", messageIn.title)
        assertEquals("http://example.com", messageIn.url)
    }

    @Test
    fun `should throw exception when mapping invalid JSON`() {
        val invalidJson = """{"email":"test@email.com","status":123,"title":null}"""

        assertThrows<Exception> {
            toMessageIn(invalidJson)
        }
    }

    @Test
    fun `should convert MessageIn to JSON string`() {
        val messageIn = MessageIn("test@email.com", IN_PROCESSING, "Test Video", "http://example.com")
        val resultJson = toMessageOutString(messageIn, FINISHED, "http://zip.com")

        val parsedJson = objectMapper.readTree(resultJson)
        assertEquals("test@email.com", parsedJson["email"].asText())
        assertEquals("FINISHED", parsedJson["status"].asText())
        assertEquals("Test Video", parsedJson["title"].asText())
        assertEquals("http://example.com", parsedJson["urlVideo"].asText())
        assertEquals("http://zip.com", parsedJson["urlZip"].asText())
    }
}
