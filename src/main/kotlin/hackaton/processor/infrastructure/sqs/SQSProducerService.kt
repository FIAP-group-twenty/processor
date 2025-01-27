package hackaton.processor.infrastructure.sqs

class SQSProducerService(private val sqsService: SqsService) {

    suspend fun produceToQueue(queueUrl: String, message: String) {
        try {
            sqsService.sendMessage(queueUrl, message)
            println("Mensagem enviada para a fila SQS: $message")
        } catch (e: Exception) {
            println("Erro ao enviar mensagem para a fila SQS: ${e.message}")
        }
    }
}
