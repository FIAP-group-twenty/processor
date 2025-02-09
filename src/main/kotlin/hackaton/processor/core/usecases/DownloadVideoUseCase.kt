package hackaton.processor.core.usecases

import hackaton.processor.infrastructure.s3.S3Service
import org.springframework.stereotype.Service

@Service
class DownloadVideoUseCase(private val s3Service: S3Service) {

    suspend fun execute(bucketName: String, videoKey: String, outputPath: String) {
        s3Service.downloadFile(bucketName, videoKey, outputPath)
    }
}
