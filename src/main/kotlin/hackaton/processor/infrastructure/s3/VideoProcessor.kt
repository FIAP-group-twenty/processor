package hackaton.processor.infrastructure.s3

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class VideoProcessor {

    fun extractFramesAndZip(videoPath: String, outputDir: String, intervalSeconds: Int = 20): String {
        val ffmpegPath = "ffmpeg"
        val outputFolder = File(outputDir)
        outputFolder.mkdirs()

        val interval = "00:00:${intervalSeconds.toString().padStart(2, '0')}"
        val outputPattern = "$outputDir/frame_%03d.jpg"
        val process = ProcessBuilder(
            ffmpegPath, "-i", videoPath, "-vf", "fps=1/$interval", outputPattern
        ).start()

        process.waitFor()

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
