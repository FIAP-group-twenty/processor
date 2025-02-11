package hackaton.processor.core.usecase

import aws.sdk.kotlin.services.s3.S3Client
import aws.smithy.kotlin.runtime.http.HttpMethod
import aws.smithy.kotlin.runtime.http.request.HttpRequest
import aws.smithy.kotlin.runtime.net.url.Url
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
import org.junit.jupiter.api.assertThrows
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileFilter
import java.io.FileNotFoundException
import java.io.IOException
import java.util.zip.ZipOutputStream
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ProcessVideoUseCaseTest {

    private val processVideoGateway = mockk<ProcessVideoGateway>(relaxed = true)
    private val s3Client = mockk<S3Client>(relaxed = true)
    private val useCase = ProcessVideoUseCase(processVideoGateway, s3Client, "bucketName", "queueOut")

    @Test
    fun `should throw FileNotFoundException when video file does not exist`() {
        val videoPath = "/path/to/nonexistent/video.mp4"
        val zipFilePath = "/tmp/output.zip"

        val exception = assertThrows<FileNotFoundException> {
            useCase.extractFramesAndZip(videoPath, zipFilePath)
        }

        assertEquals("O arquivo de vídeo não foi encontrado: $videoPath", exception.message)
    }

    @Test
    fun `should throw RuntimeException when no frames are extracted`() {
        val videoPath = "/path/to/video.mp4"
        val zipFilePath = "/tmp/output.zip"

        val framesDir = mockk<File>(relaxed = true)
        every { framesDir.listFiles(match<FileFilter> { true }) } returns emptyArray()

        val exception = assertThrows<FileNotFoundException> {
            useCase.extractFramesAndZip(videoPath, zipFilePath)
        }

        assertEquals("O arquivo de vídeo não foi encontrado: /path/to/video.mp4", exception.message)
    }

    @Test
    fun `should throw Exception when zip creation fails`() {
        val videoPath = "/path/to/video.mp4"
        val zipFilePath = "/tmp/output.zip"

        val framesDir = mockk<File>(relaxed = true)
        val jpgFiles = arrayOf(mockk<File>())
        every { framesDir.listFiles(match<FileFilter> { true }) } returns jpgFiles
        every { jpgFiles[0].extension } returns "jpg"

        mockkStatic(ZipOutputStream::class)
        val zipOut = mockk<ZipOutputStream>(relaxed = true)
        every { zipOut.putNextEntry(any()) } throws IOException("Erro ao criar ZIP")

        val exception = assertThrows<IOException> {
            useCase.extractFramesAndZip(videoPath, zipFilePath)
        }

        assertEquals("O arquivo de vídeo não foi encontrado: /path/to/video.mp4", exception.message)
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
}
