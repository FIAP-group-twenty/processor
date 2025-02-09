package hackaton.processor.infrastructure.s3

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class VideoProcessor {

    fun extractFramesAndZip(videoPath: String, outputDir: String, intervalSeconds: Int = 20): String {
        val outputFolder = File(outputDir)
        outputFolder.mkdirs()

        val outputPattern = "$outputDir/frame_%03d.jpg"
        val command = arrayOf("ffmpeg", "-i", videoPath, "-vf", "fps=1/$intervalSeconds", outputPattern)

        val process = ProcessBuilder(*command)
            .redirectErrorStream(true)
            .start()

        val exitCode = process.waitFor()
        if (exitCode != 0) {
            throw RuntimeException("Erro ao processar vídeo. Código de saída: $exitCode")
        }

        val zipFilePath = "$outputDir/frames.zip"
        ZipOutputStream(FileOutputStream(zipFilePath)).use { zipOut ->
            outputFolder.listFiles { file -> file.extension == "jpg" }?.forEach { file ->
                FileInputStream(file).use { fis ->
                    val zipEntry = ZipEntry(file.name)
                    zipOut.putNextEntry(zipEntry)
                    fis.copyTo(zipOut)
                }
            }
        }

        return zipFilePath
    }
}
