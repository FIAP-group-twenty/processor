package hackaton.processor.infrastructure.gateway

import hackaton.processor.infrastructure.s3.S3Service
import hackaton.processor.infrastructure.sqs.SqsService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

class ProcessVideoGatewayTest {

    private val s3Service = mockk<S3Service>()
    private val sqsService = mockk<SqsService>()
    private val gateway = ProcessVideoGateway(s3Service, sqsService)

    @Test
    fun `should download video from S3`() = runBlocking {
        val bucketName = "test-bucket"
        val key = "test-video.mp4"
        val outputPath = "/tmp/test-video.mp4"

        coEvery { s3Service.downloadFile(bucketName, key, outputPath) } returns Unit

        gateway.downloadVideo(bucketName, key, outputPath)

        coVerify { s3Service.downloadFile(bucketName, key, outputPath) }
    }

    @Test
    fun `should upload video to S3`() = runBlocking {
        val bucketName = "test-bucket"
        val key = "uploaded-video.mp4"
        val filePath = "/tmp/uploaded-video.mp4"

        coEvery { s3Service.uploadFile(bucketName, key, filePath) } returns Unit

        gateway.uploadVideo(bucketName, key, filePath)

        coVerify { s3Service.uploadFile(bucketName, key, filePath) }
    }

    @Test
    fun `should send message to SQS`() = runBlocking {
        val queueUrl = "https://sqs.us-east-1.amazonaws.com/123456789012/test-queue"
        val message = "Test message"

        coEvery { sqsService.sendMessage(queueUrl, message) } returns Unit

        gateway.sendMessage(queueUrl, message)

        coVerify { sqsService.sendMessage(queueUrl, message) }
    }
}
