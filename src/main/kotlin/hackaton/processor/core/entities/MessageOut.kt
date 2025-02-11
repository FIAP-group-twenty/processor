package hackaton.processor.core.entities

data class MessageOut(
    val email: String,
    val status: Status,
    val title: String,
    val urlVideo: String,
    val urlZip: String?
)
