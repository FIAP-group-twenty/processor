package hackaton.processor.core.gateway

interface IProcessVideoGateway {
    suspend fun downloadVideo(bucketName: String, key: String, outputPath: String)
    suspend fun uploadVideo(bucketName: String, key: String, filePath: String)
    suspend fun sendMessage(queueUrl: String, message: String)
}