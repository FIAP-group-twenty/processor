package hackaton.processor.core.usecases

import hackaton.processor.infrastructure.s3.S3Service
import org.springframework.stereotype.Service

@Service
class UploadZipUseCase(private val s3Service: S3Service) {

    suspend fun execute(bucketName: String, processedKey: String, zipFilePath: String) {
        s3Service.uploadFile(bucketName, processedKey, zipFilePath)
    }
}
