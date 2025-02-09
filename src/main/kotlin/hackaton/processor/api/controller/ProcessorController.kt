package hackaton.processor.api.controller

import hackaton.processor.core.usecases.ProcessVideoUseCase

class ProcessorController(
    private val inputQueueUrl: String? = System.getenv("INPUT_QUEUE_URL"),
    private val outputQueueUrl: String? = System.getenv("OUTPUT_QUEUE_URL"),
    private val bucket: String? = System.getenv("BUCKET"),
    private val processVideoUseCase: ProcessVideoUseCase
){
    //todo: adicionar função para chamar usecases
    //todo: separar usecasses em:
    // 1.buscar e baixar localmente o video no s3, lançar uma exceção caso não seja encontrado
    // 2.processar o video separando os frames e criando o zip e salvando local
    // 3.chamar o s3 para atualizar o bucket com o zip
    // 4.enviar mensagem para a fila de saída dizendo que o processo foi finalizado


}
