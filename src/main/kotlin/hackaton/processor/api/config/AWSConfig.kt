package hackaton.processor.api.config

import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.sqs.SqsClient
import software.amazon.awssdk.regions.Region

object AwsConfig {
    private val region: String = System.getenv("AWS_REGION")

    fun createS3Client(): S3Client { return S3Client { region = region } }

    fun createSqsClient(): SqsClient { return SqsClient { region = region } }
}
