package hackaton.processor.api.listener

import hackaton.processor.core.entities.Message
import hackaton.processor.core.usecases.ProcessorUseCase
import hackaton.processor.infrastructure.sqs.SqsService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
class SqsListener(
    private val sqsService: SqsService,
    private val processorUseCase: ProcessorUseCase,
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
        try {
            val videoMessage = parseMessage(message)
            processorUseCase.processVideo(videoMessage)
        } catch (e: Exception) {
            println("Erro no processamento da mensagem: ${e.message}")
        }
    }

    private fun parseMessage(message: String) = Message(id = "123", title = "Exemplo de Vídeo", url = message) //todo: ajustar
}
