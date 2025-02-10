package hackaton.processor.api.listener

import hackaton.processor.core.entities.MessageMapper.toMessageIn
import hackaton.processor.core.usecases.ProcessVideoUseCase
import hackaton.processor.infrastructure.gateway.ProcessVideoGateway
import hackaton.processor.infrastructure.sqs.SqsService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
class SqsListener(
    private val sqsService: SqsService,
    private val processVideoUseCase: ProcessVideoUseCase,
    @Value("\${aws.sqs.queue-upload}") private var queueIn: String,
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
            val messages = sqsService.receiveMessages(queueIn)

            for (message in messages) {
                processMessage(message)
            }

            delay(1000)
        }
    }

    suspend fun processMessage(message: String) {
        try {
            processVideoUseCase.processVideo(toMessageIn(message))
        } catch (e: Exception) {
            println("Erro no processamento da mensagem: ${e.message}")
        }
    }

}
