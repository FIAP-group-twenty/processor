package hackaton.processor.api.listener

import hackaton.processor.core.entities.MessageMapper.toMessageIn
import hackaton.processor.core.usecases.ProcessVideoUseCase
import hackaton.processor.infrastructure.sqs.SqsService
import io.awspring.cloud.sqs.annotation.SqsListener
import org.springframework.stereotype.Service

@Service
class SqsListener(
    private val sqsService: SqsService,
    private val processVideoUseCase: ProcessVideoUseCase
) {

    @SqsListener("\${aws.sqs.queue-upload}")
    suspend fun startListening(message: String) {
        processMessage(message)
    }

    suspend fun processMessage(message: String) {
        try {
            processVideoUseCase.processVideo(toMessageIn(message))
        } catch (e: Exception) {
            println("Erro no processamento da mensagem: ${e.message}")
        }
    }
}
