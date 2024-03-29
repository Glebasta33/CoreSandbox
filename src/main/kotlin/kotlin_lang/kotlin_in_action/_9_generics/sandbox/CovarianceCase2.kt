package kotlin_lang.kotlin_in_action._9_generics.sandbox

/**
 * Ковариантность:
 */
open class Message(val text: String)

class EmailMessage(text: String) : Message(text)

interface MessengerProducer<out T : Message> {
    fun produceMessage(text: String): T //<-исходящая позиция
}

fun changeMessengerToEmail(obj: MessengerProducer<EmailMessage>) {
    // MessengerProducer<EmailMessage> - подтип MessengerProducer<Message>
    val messenger: MessengerProducer<Message> = obj
}


class EmailMessenger : MessengerProducer<EmailMessage> {
    override fun produceMessage(text: String): EmailMessage {
        return EmailMessage("Email: $text")
    }
}


fun main() {
    // MessengerProducer<EmailMessage> - подтип MessengerProducer<Message>
    val messengerProducer: MessengerProducer<Message> = EmailMessenger()
    val message = messengerProducer.produceMessage("Hello Kotlin")
    println(message.text)    // Email: Hello Kotlin
}
