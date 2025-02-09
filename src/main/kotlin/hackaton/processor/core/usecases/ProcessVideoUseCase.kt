package hackaton.processor.core.usecases

import hackaton.processor.infrastructure.s3.VideoProcessor
import org.springframework.stereotype.Service

@Service
class ProcessVideoUseCase {

    suspend fun execute(videoPath: String): String {
        val videoProcessor = VideoProcessor()
        return videoProcessor.extractFramesAndZip(videoPath, "/tmp")
    }
}
