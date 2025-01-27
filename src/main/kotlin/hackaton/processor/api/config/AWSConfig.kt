package hackaton.processor.api.config

import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.sqs.SqsClient
import aws.sdk.kotlin.runtime.auth.credentials.StaticCredentialsProvider
import aws.smithy.kotlin.runtime.auth.awscredentials.Credentials

object AwsConfig {
    private val awsRegion: String = System.getenv("AWS_REGION")
    private val accessKeyId: String = System.getenv("AWS_ACCESS_KEY_ID")
    private val secretAccessKey: String = System.getenv("AWS_SECRET_ACCESS_KEY")

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
