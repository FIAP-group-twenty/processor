package hackaton.processor.infrastructure.sqs

import aws.sdk.kotlin.services.sqs.SqsClient
import aws.sdk.kotlin.services.sqs.model.ReceiveMessageRequest
import aws.sdk.kotlin.services.sqs.model.SendMessageRequest

class SqsService(private val sqsClient: SqsClient) {

    suspend fun sendMessage(queueUrl: String, message: String) {
        val request = SendMessageRequest {
            this.queueUrl = queueUrl
            this.messageBody = message
        }
        sqsClient.sendMessage(request)
    }

    suspend fun receiveMessages(queueUrl: String, maxMessages: Int = 10): List<String> {
        val request = ReceiveMessageRequest {
            this.queueUrl = queueUrl
            this.maxNumberOfMessages = maxMessages
        }
        val response = sqsClient.receiveMessage(request)
        return response.messages?.map { it.body ?: "" } ?: emptyList()
    }
}
