package hackaton.processor.infrastructure.sqs

import jakarta.annotation.PostConstruct
import kotlinx.coroutines.*
import org.springframework.stereotype.Service

@Service
class SqsListenerService(
    private val sqsService: SqsService,
    private val queueUrl: String
) {

    @PostConstruct
    fun startListener() {
        GlobalScope.launch {
            listenForMessages()
        }
    }

    suspend fun listenForMessages() {
        while (true) {
            val messages = sqsService.receiveMessages(queueUrl)

            for (message in messages) {
                processMessage(message)
            }
        }
    }

    private suspend fun processMessage(message: String) {
        println("Processando mensagem: $message")
    }
}
