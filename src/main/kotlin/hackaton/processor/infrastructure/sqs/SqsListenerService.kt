package hackaton.processor.infrastructure.sqs

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
class SqsListenerService(
    private val sqsService: SqsService,
    private val queueUrl: String = System.getenv("INPUT_QUEUE_URL") ?: "https://sqs.us-east-1.amazonaws.com/123456789012/fila-de-entrada"
) {

    private val scope = CoroutineScope(Dispatchers.Default)

    @Async
    fun onApplicationEvent(event: ContextRefreshedEvent?) {
        scope.launch {
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
        //todo: adicionar aqui chamada para controller
        println("Processando mensagem: $message")
    }
}
