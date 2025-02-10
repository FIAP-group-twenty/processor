package hackaton.processor.core.usecase

import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.presigners.presignGetObject
import aws.smithy.kotlin.runtime.http.request.HttpRequest
import hackaton.processor.core.entities.MessageIn
import hackaton.processor.core.entities.Status.STARTED
import hackaton.processor.core.usecases.ProcessVideoUseCase
import hackaton.processor.infrastructure.gateway.ProcessVideoGateway
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.io.File
import java.io.FileNotFoundException
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ProcessVideoUseCaseTest {

    private val processVideoGateway = mockk<ProcessVideoGateway>()
    private val s3Client = mockk<S3Client>()
    private val useCase = ProcessVideoUseCase(processVideoGateway, s3Client, "bucketName", "queueOut")

    @Test
    fun `should process video and send message`(): Unit = runBlocking {
        val messageIn = MessageIn("email@example.com", STARTED, "title", "url")

        val tempDir = File("build/tmp/title")
        if (!tempDir.exists()) {
            tempDir.mkdirs()
        }

        val outputFile = File(tempDir, "title")
        outputFile.writeText("dummy video content")

        coEvery { processVideoGateway.downloadVideo(any(), any(), any()) } just Runs
        coEvery { processVideoGateway.uploadVideo(any(), any(), any()) } just Runs
        coEvery { processVideoGateway.sendMessage(any(), any()) } just Runs

        useCase.processVideo(messageIn)

        coVerify { processVideoGateway.downloadVideo(any(), any(), any()) }
        coVerify { processVideoGateway.uploadVideo(any(), any(), any()) }
        coVerify { processVideoGateway.sendMessage(any(), any()) }

        outputFile.delete()
        tempDir.delete()
    }


    @Test
    fun `should handle exception when video download fails`() = runBlocking {
        val messageIn = MessageIn("email@example.com", STARTED, "title", "url")

        coEvery { processVideoGateway.downloadVideo(any(), any(), any()) } throws FileNotFoundException("File not found")

        try {
            useCase.processVideo(messageIn)
        } catch (e: Exception) {
            assertTrue(e is FileNotFoundException)
        }
    }

    @Test
    fun `should extract frames and zip them`() {
        val videoPath = "/tmp/test-video.mp4"
        val zipFilePath = "/tmp/test-video.zip"
        File(videoPath).createNewFile()

        val process = mockk<Process>()
        every { process.waitFor() } returns 0
        mockkStatic(ProcessBuilder::class)
        every { ProcessBuilder().start() } returns process

        useCase.extractFramesAndZip(videoPath, zipFilePath)

        val zipFile = File(zipFilePath)
        assertTrue(zipFile.exists())

        zipFile.delete()
    }

    @Test
    fun `should generate presigned URL`() = runBlocking {
        val fileName = "test.zip"
        val presignedUrl = "https://example.com/$fileName"
        val mockHttpRequest = mockk<HttpRequest>()
        coEvery { s3Client.presignGetObject(any(), any()) } returns mockHttpRequest

        val result = useCase.generatePresignedUrl("bucketName", fileName)

        assertEquals(presignedUrl, result)
    }

}
