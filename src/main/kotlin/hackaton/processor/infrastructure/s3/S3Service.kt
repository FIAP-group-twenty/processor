package hackaton.processor.infrastructure.s3

import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.model.GetObjectRequest
import aws.sdk.kotlin.services.s3.model.PutObjectRequest
import aws.smithy.kotlin.runtime.content.ByteStream
import aws.smithy.kotlin.runtime.content.asByteStream
import aws.smithy.kotlin.runtime.content.toByteArray
import hackaton.processor.api.config.AwsConfig
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

class S3Service(private val s3Client: S3Client) {

    suspend fun downloadFile(bucketName: String, key: String, outputPath: String) {
        val request = GetObjectRequest {
            bucket = bucketName
            this.key = key
        }

        s3Client.getObject(request) { getObjectResponse ->
            val filePath = Paths.get(outputPath)

            getObjectResponse.body?.let { byteStream ->
                val bytes = byteStream.toByteArray()
                Files.write(filePath, bytes)
            } ?: throw IllegalStateException("O corpo do objeto está vazio.")
        }
    }

    suspend fun uploadFile(bucketName: String, key: String, filePath: String) {
        val file = File(filePath)
        if (!file.exists()) {
            throw IllegalArgumentException("Arquivo não encontrado no caminho: $filePath")
        }

        try {
            val inputStream: ByteStream = file.asByteStream()
            val request = PutObjectRequest {
                bucket = bucketName
                this.key = key
                body = inputStream
            }

            s3Client.putObject(request)
        } catch (e: Exception) {
            throw RuntimeException("Erro ao fazer upload do arquivo: ${e.message}", e)
        }
    }

    suspend fun processVideoAndUpload(
        bucketName: String,
        videoKey: String,
        outputDir: String,
        processedKey: String
    ) {
        val tempVideoPath = "$outputDir/video.mp4"
        val s3Service = S3Service(AwsConfig.createS3Client())

        s3Service.downloadFile(bucketName, videoKey, tempVideoPath)

        val processor = VideoProcessor()
        val zipPath = processor.extractFramesAndZip(tempVideoPath, outputDir)

        s3Service.uploadFile(bucketName, processedKey, zipPath)
    }
}