package hackaton.processor.core.entities

data class MessageIn(
    val email: String,
    val status: Status,
    val title: String,
    val url: String
)
