package hackaton.processor.infrastructure.service

import aws.sdk.kotlin.services.sqs.SqsClient
import aws.sdk.kotlin.services.sqs.model.Message
import aws.sdk.kotlin.services.sqs.model.ReceiveMessageResponse
import aws.sdk.kotlin.services.sqs.model.SendMessageResponse
import hackaton.processor.infrastructure.sqs.SqsService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SqsServiceTest {

    private val sqsClient: SqsClient = mockk(relaxed = true)
    private val sqsService = SqsService(sqsClient)

    @Test
    fun `should send message to SQS`() = runBlocking {
        val queueUrl = "https://sqs.us-east-1.amazonaws.com/123456789012/test-queue"
        val message = "Hello, SQS!"

        coEvery { sqsClient.sendMessage(any()) } returns SendMessageResponse {}

        sqsService.sendMessage(queueUrl, message)

        coVerify {
            sqsClient.sendMessage(withArg {
                assertEquals(queueUrl, it.queueUrl)
                assertEquals(message, it.messageBody)
            })
        }
    }

    @Test
    fun `should receive messages from SQS`() = runBlocking {
        val queueUrl = "https://sqs.us-east-1.amazonaws.com/123456789012/test-queue"
        val messages = listOf(
            Message { body = "Message 1" },
            Message { body = "Message 2" }
        )

        coEvery { sqsClient.receiveMessage(any()) } returns ReceiveMessageResponse {
            this.messages = messages
        }

        val receivedMessages = sqsService.receiveMessages(queueUrl)

        assertEquals(listOf("Message 1", "Message 2"), receivedMessages)

        coVerify {
            sqsClient.receiveMessage(withArg {
                assertEquals(queueUrl, it.queueUrl)
                assertEquals(10, it.maxNumberOfMessages)
            })
        }
    }

    @Test
    fun `should return empty list when SQS has no messages`() = runBlocking {
        val queueUrl = "https://sqs.us-east-1.amazonaws.com/123456789012/test-queue"

        coEvery { sqsClient.receiveMessage(any()) } returns ReceiveMessageResponse {
            messages = null
        }

        val receivedMessages = sqsService.receiveMessages(queueUrl)

        assertEquals(emptyList<String>(), receivedMessages)
    }
}
