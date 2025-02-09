package hackaton.processor.api.config

import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.sqs.SqsClient
import aws.sdk.kotlin.runtime.auth.credentials.StaticCredentialsProvider
import aws.smithy.kotlin.runtime.auth.awscredentials.Credentials
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AwsConfig {
    private val awsRegion: String = System.getenv("AWS_REGION") ?: "us-east-1"
    private val accessKeyId: String = System.getenv("AWS_ACCESS_KEY_ID") ?: "default-access-key-id"
    private val secretAccessKey: String = System.getenv("AWS_SECRET_ACCESS_KEY") ?: "default-secret-access-key"

    @Bean
    fun createS3Client(): S3Client {
        val credentials: Credentials = Credentials(
            accessKeyId = accessKeyId,
            secretAccessKey = secretAccessKey
        )

        return S3Client {
            region = awsRegion
            credentialsProvider = StaticCredentialsProvider(credentials)
        }
    }

    @Bean
    fun createSqsClient(): SqsClient {
        val credentials: Credentials = Credentials(
            accessKeyId = accessKeyId,
            secretAccessKey = secretAccessKey
        )

        return SqsClient {
            region = awsRegion
            credentialsProvider = StaticCredentialsProvider(credentials)
        }
    }
}
