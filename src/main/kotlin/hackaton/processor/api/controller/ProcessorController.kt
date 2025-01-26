package hackaton.processor.api.controller

import hackaton.processor.core.usecases.ProcessVideoUseCase
import hackaton.processor.infrastructure.s3.S3Service
import hackaton.processor.infrastructure.sqs.SqsService

class ProcessorController(
    private val inputQueueUrl: String? = System.getenv("INPUT_QUEUE_URL"),
    private val outputQueueUrl: String? = System.getenv("OUTPUT_QUEUE_URL"),
    private val bucket: String? = System.getenv("BUCKET"),
    private val processVideoUseCase: ProcessVideoUseCase
) {

    suspend fun handleMessages() {
        val messages = processVideoUseCase.sqsService.receiveMessages(inputQueueUrl)

        for (message in messages) {
            processVideoUseCase.processMessage(message)
        }
    }
}
