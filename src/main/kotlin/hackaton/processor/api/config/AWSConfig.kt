package hackaton.processor.api.config

import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.sqs.SqsClient
import aws.sdk.kotlin.runtime.auth.credentials.StaticCredentialsProvider
import aws.smithy.kotlin.runtime.auth.awscredentials.Credentials
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AwsConfig(
    @Value("\${aws.key}") private val accessKeyId: String,
    @Value("\${aws.secret}") private val secretAccessKey: String,
    @Value("\${aws.region}") private val awsRegion: String
) {
    @Bean
    fun createS3Client(): S3Client {
        val credentials = Credentials(accessKeyId = accessKeyId, secretAccessKey = secretAccessKey)

        return S3Client {
            region = awsRegion
            credentialsProvider = StaticCredentialsProvider(credentials)
        }
    }

    @Bean
    fun createSqsClient(): SqsClient {
        val credentials = Credentials(accessKeyId = accessKeyId, secretAccessKey = secretAccessKey)

        return SqsClient {
            region = awsRegion
            credentialsProvider = StaticCredentialsProvider(credentials)
        }
    }
}
