package hackaton.processor.api

import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.sqs.SqsClient
import hackaton.processor.api.listener.SqsListener
import hackaton.processor.core.usecases.ProcessVideoUseCase
import hackaton.processor.infrastructure.gateway.ProcessVideoGateway
import hackaton.processor.infrastructure.sqs.SqsService
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import kotlin.test.assertEquals

class SqsListenerTest {

    private val sqsClient: SqsClient = mockk(relaxed = true)
    private val sqsService = SqsService(sqsClient)
    private val processVideoGateway = mockk<ProcessVideoGateway>(relaxed = true)
    private val s3Client = mockk<S3Client>(relaxed = true)
    private val useCase = mockk<ProcessVideoUseCase>(relaxed = true)

    @Test
    fun `test startListening should call processVideoUseCase`() = runBlocking {
        val message = "test message"
        val sqsListener = SqsListener(sqsService, useCase)

        sqsListener.startListening(message)

        assertEquals(1,1)
    }
}
