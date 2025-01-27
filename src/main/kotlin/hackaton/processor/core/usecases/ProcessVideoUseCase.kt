package hackaton.processor.core.usecases

import hackaton.processor.infrastructure.s3.S3Service

class ProcessVideoUseCase(private val s3Service: S3Service) {
    suspend fun execute() {
        val bucketName = "meu-bucket"
        val videoKey = "videos/input/video.mp4"
        val outputDir = "/tmp/processing"
        val processedKey = "videos/output/frames.zip"

        s3Service.processVideoAndUpload(bucketName, videoKey, outputDir, processedKey)
    }
}
