package kotlin_lang.kotlin_in_action._9_generics.sandbox

// open class Message(val text: String)
// class EmailMessage(text: String): Message(text)

interface MessengerConsumer<in T : Message> {
    fun consumeMessage(message: T)
}

fun changeMessengerToDefault(obj: MessengerConsumer<Message>) {
    // MessengerConsumer<Message> - подтип MessengerConsumer<EmailMessage>
    val messenger: MessengerConsumer<EmailMessage> = obj
}

class InstantMessenger : MessengerConsumer<Message> {
    override fun consumeMessage(message: Message) {
        println("Consumed message: ${message.text}")
    }
}

fun main() {
    val messenger: MessengerConsumer<EmailMessage> = InstantMessenger()
    val message = EmailMessage("Hi Kotlin")
    messenger.consumeMessage(message)
}