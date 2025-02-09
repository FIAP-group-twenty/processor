package hackaton.processor.core.usecases

import hackaton.processor.core.entities.Message
import org.springframework.stereotype.Service

@Service
class ProcessorUseCase(
    private val downloadVideoUseCase: DownloadVideoUseCase,
    private val processVideoUseCase: ProcessVideoUseCase,
    private val uploadZipUseCase: UploadZipUseCase
) {

    suspend fun processVideo(message: Message) {
        try {
            val videoKey = message.url
            val downloadPath = "/tmp/${videoKey}"
            val processedKey = "processed/${videoKey}/frames.zip"

            downloadVideoUseCase.execute("your-bucket-name", videoKey, downloadPath)
            processVideoUseCase.execute(downloadPath)
            uploadZipUseCase.execute("your-bucket-name", processedKey, "Mensagem: ${message.title} - Processado com sucesso!")
        } catch (e: Exception) {
            println("Erro no processamento do vídeo: ${e.message}")
        }
    }
}
