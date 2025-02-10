package hackaton.processor.core.usecases

import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.model.GetObjectRequest
import aws.sdk.kotlin.services.s3.presigners.presignGetObject
import hackaton.processor.core.entities.MessageIn
import hackaton.processor.core.entities.MessageMapper.toMessageOutString
import hackaton.processor.core.entities.Status.FINISHED
import hackaton.processor.infrastructure.gateway.ProcessVideoGateway
import org.springframework.beans.factory.annotation.Value
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlin.time.Duration.Companion.days

class ProcessVideoUseCase(
    private val processVideoGateway: ProcessVideoGateway,
    private val s3Client: S3Client,
    @Value(value = "\${aws.bucketName}") private var bucketName: String,
    @Value("\${aws.sqs.queue-update}") private var queueOut: String
) {

    suspend fun processVideo(messageIn: MessageIn) {
        try {
            val videoDir = "/tmp/${messageIn.title}"
            val zipFilePath = "$videoDir/${messageIn.title}-frames.zip"
            val zipFileName = "${messageIn.title}-frames.zip"

            processVideoGateway.downloadVideo(bucketName, messageIn.title, videoDir)
            extractFramesAndZip(videoDir, zipFilePath)
            processVideoGateway.uploadVideo(bucketName, zipFileName, zipFilePath)

            val presignedUrl = generatePresignedUrl(bucketName, zipFileName)
            val messageOut = toMessageOutString(messageIn, FINISHED, presignedUrl)

            processVideoGateway.sendMessage(queueOut, messageOut)
        } catch (e: Exception) {
            println("Erro no processamento do vídeo: ${e.message}")
        }
    }

    fun extractFramesAndZip(videoPath: String, zipFilePath: String, intervalSeconds: Int = 20) {
        val videoFile = File(videoPath)
        if (!videoFile.exists()) {
            throw FileNotFoundException("O arquivo de vídeo não foi encontrado: $videoPath")
        }

        val framesDir = File(videoFile.parent, "frames")
        framesDir.mkdirs()
        val outputPattern = "${framesDir.absolutePath}/frame_%03d.jpg"
        val command = arrayOf("ffmpeg", "-i", videoPath, "-vf", "fps=1/$intervalSeconds", outputPattern)

        println("Extraindo frames do vídeo: $videoPath")

        try {
            val process = ProcessBuilder(*command)
                .redirectErrorStream(true)
                .start()

            val exitCode = process.waitFor()
            if (exitCode != 0) {
                throw RuntimeException("Erro ao processar vídeo. Código de saída: $exitCode")
            }

            println("Frames extraídos com sucesso em: ${framesDir.absolutePath}")
        } catch (e: Exception) {
            println("Erro ao extrair frames: ${e.message}")
            throw e
        }

        val jpgFiles = framesDir.listFiles { file -> file.extension == "jpg" }

        if (jpgFiles.isNullOrEmpty()) {
            throw RuntimeException("Nenhum frame foi extraído. Verifique se o FFmpeg está instalado e funcionando corretamente.")
        }

        println("Compactando frames em: $zipFilePath")
        try {
            ZipOutputStream(FileOutputStream(zipFilePath)).use { zipOut ->
                jpgFiles.forEach { file ->
                    FileInputStream(file).use { fis ->
                        val zipEntry = ZipEntry(file.name)
                        zipOut.putNextEntry(zipEntry)
                        fis.copyTo(zipOut)
                        zipOut.closeEntry()
                    }
                }
            }
            println("Arquivo ZIP criado com sucesso: $zipFilePath")
        } catch (e: Exception) {
            println("Erro ao criar ZIP: ${e.message}")
            throw e
        } finally {
            println("Removendo arquivos temporários...")
            jpgFiles.forEach { it.delete() }
            framesDir.delete()
            println("Arquivos temporários removidos.")
        }
    }

    suspend fun generatePresignedUrl(bucketName: String, fileName: String): String {
        val getObjectRequest = GetObjectRequest {
            bucket = bucketName
            key = fileName
        }

        return s3Client.presignGetObject(getObjectRequest,  365.days).toString()
    }
}

