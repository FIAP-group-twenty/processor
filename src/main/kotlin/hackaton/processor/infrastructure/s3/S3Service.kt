package hackaton.processor.infrastructure.s3

import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.getObject
import aws.sdk.kotlin.services.s3.putObject
import java.io.File

class S3Service(private val s3Client: S3Client) {

    suspend fun downloadFile(bucketName: String, key: String, outputPath: String) {
        val response = s3Client.getObject {
            bucket = bucketName
            this.key = key
        }

        File(outputPath).outputStream().use { fileOut ->
            response.body?.use { it.copyTo(fileOut) }
        }
    }

    suspend fun uploadFile(bucketName: String, key: String, filePath: String) {
        val file = File(filePath)
        s3Client.putObject {
            bucket = bucketName
            this.key = key
            body = file.readBytes()
        }
    }
}
