package hackaton.processor.api

import hackaton.processor.api.listener.SqsListener
import hackaton.processor.core.usecases.ProcessVideoUseCase
import hackaton.processor.infrastructure.sqs.SqsService
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean

//@SpringBootTest
//class SqsListenerTest {
//
//    @MockBean
//    private lateinit var sqsService: SqsService
//
//    @MockBean
//    private lateinit var processVideoUseCase: ProcessVideoUseCase
//
//    private lateinit var sqsListener: SqsListener
//
//    @BeforeEach
//    fun setup() {
//        sqsListener = SqsListener(sqsService, processVideoUseCase)
//    }
//
//    @Test
//    fun `should process messages from SQS`() = runBlocking {
//        val message = """{"email":"test@email.com","status":"IN_PROCESSING","title":"Test Video","urlVideo":"http://example.com"}"""
//        coEvery { sqsService.receiveMessages(any()) } returns listOf(message)
//        coEvery { processVideoUseCase.processVideo(any()) } just Runs
//
//        sqsListener.startListening(message)
//
//        coVerify { processVideoUseCase.processVideo(any()) }
//    }
//
//    @Test
//    fun `should handle exception when processing message`() = runBlocking {
//        val message = """{"email":"test@email.com","status":"IN_PROCESSING","title":"Test Video","urlVideo":"http://example.com"}"""
//        coEvery { processVideoUseCase.processVideo(any()) } throws RuntimeException("Processing error")
//
//        sqsListener.startListening(message)
//
//        coVerify { processVideoUseCase.processVideo(any()) }
//    }
//}
