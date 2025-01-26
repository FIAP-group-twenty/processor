package hackaton.processor.api.controller

import hackaton.processor.core.usecases.ProcessVideoUseCase

class ProcessorController(
    private val inputQueueUrl: String? = System.getenv("INPUT_QUEUE_URL"),
    private val outputQueueUrl: String? = System.getenv("OUTPUT_QUEUE_URL"),
    private val bucket: String? = System.getenv("BUCKET"),
    private val processVideoUseCase: ProcessVideoUseCase
)
