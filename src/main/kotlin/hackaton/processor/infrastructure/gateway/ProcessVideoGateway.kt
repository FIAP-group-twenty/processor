package hackaton.processor.infrastructure.gateway

import hackaton.processor.core.gateway.IProcessVideoGateway
import hackaton.processor.infrastructure.s3.S3Service
import hackaton.processor.infrastructure.sqs.SqsService
import org.springframework.stereotype.Component

@Component
class ProcessVideoGateway(
    private val s3Service: S3Service,
    private val sqsService: SqsService
) : IProcessVideoGateway {

    override suspend fun downloadVideo(bucketName: String, key: String, outputPath: String) =
        s3Service.downloadFile(bucketName, key, outputPath)

    override suspend fun uploadVideo(bucketName: String, key: String, filePath: String) =
        s3Service.uploadFile(bucketName, key, filePath)

    override suspend fun sendMessage(queueUrl: String, message: String) =
        sqsService.sendMessage(queueUrl, message)
}