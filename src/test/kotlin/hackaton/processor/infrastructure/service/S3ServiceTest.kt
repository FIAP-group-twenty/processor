package hackaton.processor.infrastructure.service

import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.model.PutObjectResponse
import aws.smithy.kotlin.runtime.content.ByteStream
import hackaton.processor.infrastructure.s3.S3Service
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

class S3ServiceTest {

    private val s3Client = mockk<S3Client>()
    private val s3Service = S3Service(s3Client)

    @Test
    fun `should download file from S3 and save it locally`() = runBlocking {
        val bucketName = "test-bucket"
        val key = "test-file.mp4"
        val outputPath = "build/tmp"

        val byteStream = ByteStream.fromBytes("file content".toByteArray())
        val response = aws.sdk.kotlin.services.s3.model.GetObjectResponse { body = byteStream }

        coEvery {
            s3Client.getObject(
                any(),
                any<suspend (aws.sdk.kotlin.services.s3.model.GetObjectResponse) -> Unit>()
            )
        } coAnswers {
            val block = arg<suspend (aws.sdk.kotlin.services.s3.model.GetObjectResponse) -> Unit>(1)
            block.invoke(response)
        }

        s3Service.downloadFile(bucketName, key, outputPath)

        val expectedPath = Paths.get(outputPath, key)
        assert(Files.exists(expectedPath)) { "O arquivo não foi criado no local esperado." }

        coVerify {
            s3Client.getObject(
                any(),
                any<suspend (aws.sdk.kotlin.services.s3.model.GetObjectResponse) -> Unit>()
            )
        }
    }

    @Test
    fun `should throw exception when downloading file with empty body`(): Unit = runBlocking {
        val bucketName = "test-bucket"
        val key = "test-file.mp4"
        val outputPath = "build/tmp"

        val response = aws.sdk.kotlin.services.s3.model.GetObjectResponse { body = null }

        coEvery {
            s3Client.getObject(
                any(),
                any<suspend (aws.sdk.kotlin.services.s3.model.GetObjectResponse) -> Unit>()
            )
        } coAnswers {
            val block = arg<suspend (aws.sdk.kotlin.services.s3.model.GetObjectResponse) -> Unit>(1)
            block.invoke(response)
        }

        assertThrows(IllegalStateException::class.java) {
            runBlocking { s3Service.downloadFile(bucketName, key, outputPath) }
        }
    }

    @Test
    fun `should upload file to S3`(): Unit = runBlocking {
        val bucketName = "test-bucket"
        val key = "uploaded-file.mp4"
        val filePath = "build/tmp/uploaded-file.mp4"

        val file = File(filePath).apply {
            parentFile.mkdirs()
            writeText("test content")
        }

        coEvery { s3Client.putObject(any()) } returns PutObjectResponse { }

        s3Service.uploadFile(bucketName, key, filePath)

        coVerify { s3Client.putObject(any()) }

        file.delete()
    }

    @Test
    fun `should throw exception when uploading non-existent file`(): Unit = runBlocking {
        val bucketName = "test-bucket"
        val key = "non-existent-file.mp4"
        val filePath = "build/tmp/non-existent-file.mp4"

        assertThrows(IllegalArgumentException::class.java) {
            runBlocking { s3Service.uploadFile(bucketName, key, filePath) }
        }
    }
}
