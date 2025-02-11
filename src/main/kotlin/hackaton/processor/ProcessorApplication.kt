package hackaton.processor

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableAsync

@SpringBootApplication
@EnableAsync
class ProcessorApplication

fun main(args: Array<String>) {
    runApplication<ProcessorApplication>(*args)
}