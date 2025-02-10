package hackaton.processor.api.config

import aws.sdk.kotlin.services.s3.S3Client
import hackaton.processor.core.usecases.ProcessVideoUseCase
import hackaton.processor.infrastructure.gateway.ProcessVideoGateway
import hackaton.processor.infrastructure.s3.S3Service
import hackaton.processor.infrastructure.sqs.SqsService
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class BeanConfiguration(
    private val s3Service: S3Service,
    private val s3Client: S3Client,
    private val sqsService: SqsService,
    @Value(value = "\${aws.bucketName}") private var bucketName: String,
    @Value("\${aws.sqs.queue-upload}") private var queueIn: String,
    @Value("\${aws.sqs.queue-update}") private var queueOut: String
) {

    @Bean
    fun processVideoGateway(): ProcessVideoGateway {
        return ProcessVideoGateway(s3Service, sqsService)
    }

    @Bean
    fun processVideoUseCase(): ProcessVideoUseCase {
        return ProcessVideoUseCase(processVideoGateway(), s3Client, bucketName, queueOut)
    }

}